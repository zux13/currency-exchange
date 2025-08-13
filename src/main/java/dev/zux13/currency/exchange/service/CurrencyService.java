package dev.zux13.currency.exchange.service;

import dev.zux13.currency.exchange.dao.CurrencyDao;
import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.entity.Currency;
import dev.zux13.currency.exchange.exception.CurrencyNotFoundException;
import dev.zux13.currency.exchange.exception.DuplicateCurrencyCodeException;
import dev.zux13.currency.exchange.mapper.CurrencyMapper;
import dev.zux13.currency.exchange.util.SQLExceptionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll()
                .stream()
                .map(CurrencyMapper::toDto)
                .toList();
    }

    public CurrencyDto findByCode(String code) {
        return currencyDao.findByCode(code)
                .map(CurrencyMapper::toDto)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code %s not found".formatted(code)));
    }

    public CurrencyDto findById(Long id) {
        return currencyDao.findById(id)
                .map(CurrencyMapper::toDto)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with id %d not found".formatted(id)));
    }

    public CurrencyDto save(CurrencyDto dto) {
        try {
            Currency saved = currencyDao.save(CurrencyMapper.toEntity(dto));
            return CurrencyMapper.toDto(saved);
        } catch (RuntimeException ex) {
            if (SQLExceptionUtils.isUniqueConstraintViolation(ex)) {
                throw new DuplicateCurrencyCodeException("Currency with code %s already exists".formatted(dto.code()));
            }
            throw ex;
        }
    }

}
