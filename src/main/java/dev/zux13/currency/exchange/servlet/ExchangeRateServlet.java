package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.JsonResponseWriter;
import dev.zux13.currency.exchange.validation.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pair = getCurrencyPairFromPath(req);
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);

        ExchangeRateResponseDto dto = exchangeRateService.findByCurrencyCodes(baseCode, targetCode);
        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, dto);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pair = getCurrencyPairFromPath(req);
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);

        String rateStr = req.getReader().lines()
                .findFirst()
                .map(line -> line.split("=")[1])
                .orElse(null);
        BigDecimal rate = Validator.validateRate(rateStr);

        ExchangeRateResponseDto updated = exchangeRateService.updateRate(baseCode, targetCode, rate);
        JsonResponseWriter.write(resp, HttpServletResponse.SC_OK, updated);
    }

    private String getCurrencyPairFromPath(HttpServletRequest req) {
        String pair = Validator.validateAndExtractPath(req.getPathInfo());
        Validator.validateCurrencyPair(pair);
        return pair;
    }
}
