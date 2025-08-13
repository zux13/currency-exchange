package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.service.CurrencyService;
import dev.zux13.currency.exchange.util.JsonResponseWriter;
import dev.zux13.currency.exchange.validation.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = Validator.validateAndExtractPath(req.getPathInfo());
        Validator.validateCurrencyCode(code);

        CurrencyDto currency = currencyService.findByCode(code);

        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, currency);
    }
}
