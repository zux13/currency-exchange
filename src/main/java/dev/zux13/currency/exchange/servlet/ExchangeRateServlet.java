package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.exception.CurrencyNotFoundException;
import dev.zux13.currency.exchange.exception.ExchangeRateNotFoundException;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (ServletUtils.isEmptyPathInfo(pathInfo)) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Currency codes is required in path."),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        String pair = pathInfo.substring(1).toUpperCase();

        if (!ServletUtils.isValidCurrencyPairCodesFormat(pair)) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Currency pair must be exactly 6 characters (two 3-letter codes)."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3, 6);

        try {
            ExchangeRateResponseDto dto = exchangeRateService.findByCurrencyCodes(baseCode, targetCode);
            ServletUtils.writeJsonResponse(resp, dto, HttpServletResponse.SC_OK);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", e.getMessage()),
                    HttpServletResponse.SC_NOT_FOUND
            );
        } catch (Exception e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String pathInfo = req.getPathInfo();

        if (!ServletUtils.isValidCurrencyPairCodesFormat(pathInfo)) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Currency pair must be exactly 6 characters (two 3-letter codes)."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String pair = pathInfo.substring(1).toUpperCase();
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3, 6);

        String rateStr = req.getParameter("rate");
        if (!ServletUtils.isValidParams(rateStr)) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Missing required field: rate"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException ex) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Invalid rate format"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            ExchangeRateResponseDto updated = exchangeRateService.updateRate(baseCode, targetCode, rate);
            ServletUtils.writeJsonResponse(resp, updated, HttpServletResponse.SC_OK);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", e.getMessage()),
                    HttpServletResponse.SC_NOT_FOUND
            );
        } catch (Exception e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
