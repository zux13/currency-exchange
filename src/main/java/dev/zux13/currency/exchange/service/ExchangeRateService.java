package dev.zux13.currency.exchange.service;

import dev.zux13.currency.exchange.dao.ExchangeRateDao;
import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.dto.ExchangeRateSaveDto;
import dev.zux13.currency.exchange.entity.ExchangeRate;
import dev.zux13.currency.exchange.exception.CurrencyNotFoundException;
import dev.zux13.currency.exchange.exception.DuplicateExchangeRateException;
import dev.zux13.currency.exchange.exception.ExchangeRateNotFoundException;
import dev.zux13.currency.exchange.mapper.ExchangeRateMapper;
import dev.zux13.currency.exchange.util.SQLExceptionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
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
                        findByCurrencyIds(base.getId(), target.getId()),
                        base,
                        target);
    }

    public Optional<ExchangeRateResponseDto> save(ExchangeRateSaveDto dto)  {
        try {
            CurrencyDto base = getCurrencyDtoByCode(dto.getBaseCurrencyCode());
            CurrencyDto target = getCurrencyDtoByCode(dto.getTargetCurrencyCode());
            ExchangeRate saved = exchangeRateDao.save(ExchangeRateMapper.toEntity(dto, base.getId(), target.getId()));
            return Optional.of(ExchangeRateMapper.toDto(saved, base, target));
        } catch (RuntimeException ex) {
            if (SQLExceptionUtils.isUniqueConstraintViolation(ex)) {
                throw new DuplicateExchangeRateException(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());
            }
            throw ex;
        }
    }

    public ExchangeRateResponseDto updateRate(String baseCode, String targetCode, double rate) {
        CurrencyDto base = getCurrencyDtoByCode(baseCode);
        CurrencyDto target = getCurrencyDtoByCode(targetCode);
        ExchangeRate entity = findByCurrencyIds(base.getId(), target.getId());
        entity.setRate(rate);
        exchangeRateDao.update(entity);
        return ExchangeRateMapper.toDto(entity, base, target);
    }

    public ExchangeRate findByCurrencyIds(Long baseCurrencyId, Long targetCurrencyId) {
        return exchangeRateDao.findByCurrencyIds(baseCurrencyId, targetCurrencyId)
                .orElseThrow(() -> new ExchangeRateNotFoundException(baseCurrencyId, targetCurrencyId));
    }

    private CurrencyDto getCurrencyDtoByCode(String code) {
        return currencyService.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException(code));
    }

    private CurrencyDto getCurrencyDtoById(Long id) {
        return currencyService.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException(id));
    }

    public Optional<BigDecimal> findRateForExchange(String baseCode, String targetCode) {

        try {
            ExchangeRateResponseDto direct = findByCurrencyCodes(baseCode, targetCode);
            return Optional.of(BigDecimal.valueOf(direct.getRate()));
        } catch (ExchangeRateNotFoundException e) {
        }

        try {
            ExchangeRateResponseDto inverse = findByCurrencyCodes(targetCode, baseCode);
            double inverseRate = inverse.getRate();
            if (inverseRate == 0) {
                return Optional.empty();
            }
            return Optional.of(BigDecimal.valueOf(1.0 / inverseRate));
        } catch (ExchangeRateNotFoundException e) {
        }

        try {
            ExchangeRateResponseDto baseToUsd = findByCurrencyCodes(baseCode, "USD");
            ExchangeRateResponseDto usdToTarget = findByCurrencyCodes("USD", targetCode);

            double rate = baseToUsd.getRate() * usdToTarget.getRate();
            return Optional.of(BigDecimal.valueOf(rate));
        } catch (ExchangeRateNotFoundException e) {
        }

        return Optional.empty();
    }

}
