package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.ExchangeResponseDto;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.JsonResponseWriter;
import dev.zux13.currency.exchange.validation.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fromCode = req.getParameter("from");
        String toCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        Validator.validateCurrencyCode(fromCode);
        Validator.validateCurrencyCode(toCode);
        BigDecimal amount = Validator.validateAmount(amountStr);

        ExchangeResponseDto exchangeResponse = exchangeRateService.exchange(fromCode, toCode, amount);

        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, exchangeResponse);
    }
}

