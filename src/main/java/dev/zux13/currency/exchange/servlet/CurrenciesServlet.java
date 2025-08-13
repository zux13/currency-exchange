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
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyDto> currencies = currencyService.findAll();
        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        Validator.validateFormField(name, "name");
        Validator.validateCurrencyCode(code);
        Validator.validateFormField(sign, "sign");

        CurrencyDto dto = new CurrencyDto(null, name, code, sign);

        CurrencyDto saved = currencyService.save(dto);

        JsonResponseWriter.write(resp, HttpServletResponse.SC_CREATED, saved);
    }
}
