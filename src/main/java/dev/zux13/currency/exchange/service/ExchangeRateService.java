package dev.zux13.currency.exchange.service;

import dev.zux13.currency.exchange.dao.ExchangeRateDao;
import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.dto.ExchangeRateSaveDto;
import dev.zux13.currency.exchange.dto.ExchangeResponseDto;
import dev.zux13.currency.exchange.entity.ExchangeRate;
import dev.zux13.currency.exchange.exception.DuplicateExchangeRateException;
import dev.zux13.currency.exchange.exception.ExchangeRateNotFoundException;
import dev.zux13.currency.exchange.mapper.ExchangeRateMapper;
import dev.zux13.currency.exchange.util.SQLExceptionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private static final String CROSS_CURRENCY_CODE = "USD";
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateResponseDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> {
                        CurrencyDto base = getCurrencyDtoById(exchangeRate.getBaseCurrencyId());
                        CurrencyDto target = getCurrencyDtoById(exchangeRate.getTargetCurrencyId());
                        return ExchangeRateMapper.toDto(exchangeRate, base, target);
                })
                .toList();
    }

    public ExchangeRateResponseDto findByCurrencyCodes(String baseCode, String targetCode) {
        CurrencyDto base = getCurrencyDtoByCode(baseCode);
        CurrencyDto target = getCurrencyDtoByCode(targetCode);
        return ExchangeRateMapper.toDto(
                        findByCurrencyIds(base.id(), target.id()),
                        base,
                        target);
    }

    public ExchangeRateResponseDto save(ExchangeRateSaveDto dto)  {
        try {
            CurrencyDto base = getCurrencyDtoByCode(dto.baseCurrencyCode());
            CurrencyDto target = getCurrencyDtoByCode(dto.targetCurrencyCode());
            ExchangeRate saved = exchangeRateDao.save(ExchangeRateMapper.toEntity(dto, base.id(), target.id()));
            return ExchangeRateMapper.toDto(saved, base, target);
        } catch (RuntimeException ex) {
            if (SQLExceptionUtils.isUniqueConstraintViolation(ex)) {
                throw new DuplicateExchangeRateException(
                        "Exchange rate for pair %s/%s already exists"
                                .formatted(dto.baseCurrencyCode(), dto.targetCurrencyCode())
                );
            }
            throw ex;
        }
    }

    public ExchangeRateResponseDto updateRate(String baseCode, String targetCode, BigDecimal rate) {
        CurrencyDto base = getCurrencyDtoByCode(baseCode);
        CurrencyDto target = getCurrencyDtoByCode(targetCode);
        ExchangeRate entity = findByCurrencyIds(base.id(), target.id());
        entity.setRate(rate);
        exchangeRateDao.update(entity);
        return ExchangeRateMapper.toDto(entity, base, target);
    }

    public ExchangeRate findByCurrencyIds(Long baseCurrencyId, Long targetCurrencyId) {
        return exchangeRateDao.findByCurrencyIds(baseCurrencyId, targetCurrencyId)
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "Exchange rate for baseCurrencyId: %d and targetCurrencyId: %d not found"
                                .formatted(baseCurrencyId, targetCurrencyId)
                ));
    }

    public ExchangeResponseDto exchange(String fromCode, String toCode, BigDecimal amount) {
        CurrencyDto baseCurrency = getCurrencyDtoByCode(fromCode);
        CurrencyDto targetCurrency = getCurrencyDtoByCode(toCode);

        BigDecimal rate = findRateForExchange(baseCurrency.code(), targetCurrency.code())
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "Exchange rate for pair %s/%s not found".formatted(fromCode, toCode)
                ));

        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new ExchangeResponseDto(
                baseCurrency,
                targetCurrency,
                rate,
                amount,
                convertedAmount
        );
    }

    public Optional<BigDecimal> findRateForExchange(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            return Optional.of(BigDecimal.ONE);
        }

        return findDirectRate(baseCode, targetCode)
                .or(() -> findInverseRate(baseCode, targetCode))
                .or(() -> findCrossRate(baseCode, targetCode));
    }

    private Optional<BigDecimal> findDirectRate(String baseCode, String targetCode) {
        try {
            return Optional.of(findByCurrencyCodes(baseCode, targetCode).rate());
        } catch (ExchangeRateNotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<BigDecimal> findInverseRate(String baseCode, String targetCode) {
        return findDirectRate(targetCode, baseCode)
                .map(rate -> {
                    if (rate.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO;
                    }
                    return BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
                });
    }

    private Optional<BigDecimal> findCrossRate(String baseCode, String targetCode) {
        Optional<BigDecimal> baseToCross = findDirectRate(baseCode, CROSS_CURRENCY_CODE);
        Optional<BigDecimal> crossToTarget = findDirectRate(CROSS_CURRENCY_CODE, targetCode);

        if (baseToCross.isPresent() && crossToTarget.isPresent()) {
            BigDecimal rate = baseToCross.get().multiply(crossToTarget.get());
            return Optional.of(rate);
        }
        return Optional.empty();
    }

    private CurrencyDto getCurrencyDtoByCode(String code) {
        return currencyService.findByCode(code);
    }

    private CurrencyDto getCurrencyDtoById(Long id) {
        return currencyService.findById(id);
    }
}
