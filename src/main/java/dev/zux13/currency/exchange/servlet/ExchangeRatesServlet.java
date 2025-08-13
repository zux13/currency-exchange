package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.dto.ExchangeRateSaveDto;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.JsonResponseWriter;
import dev.zux13.currency.exchange.validation.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> rates = exchangeRateService.findAll();
        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, rates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        Validator.validateCurrencyCode(baseCode);
        Validator.validateCurrencyCode(targetCode);
        BigDecimal rate = Validator.validateRate(rateStr);

        ExchangeRateSaveDto dto = new ExchangeRateSaveDto(baseCode, targetCode, rate);

        ExchangeRateResponseDto saved = exchangeRateService.save(dto);

        JsonResponseWriter.write(resp, HttpServletResponse.SC_CREATED, saved);
    }
}
