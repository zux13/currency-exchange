package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.dto.ExchangeRateSaveDto;
import dev.zux13.currency.exchange.exception.CurrencyNotFoundException;
import dev.zux13.currency.exchange.exception.DuplicateExchangeRateException;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateResponseDto> rates = exchangeRateService.findAll();
            ServletUtils.writeJsonResponse(resp, rates, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String baseCode = req.getParameter("baseCurrencyCode");
            String targetCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");

            if (!ServletUtils.isValidParams(baseCode, targetCode, rateStr)) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Missing required form fields"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            double rate;
            try {
                rate = Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Invalid rate format"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            ExchangeRateSaveDto dto = ExchangeRateSaveDto.builder()
                    .baseCurrencyCode(baseCode)
                    .targetCurrencyCode(targetCode)
                    .rate(rate)
                    .build();

            Optional<ExchangeRateResponseDto> saved;
            try {
                saved = exchangeRateService.save(dto);
            } catch (DuplicateExchangeRateException e) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", e.getMessage()),
                        HttpServletResponse.SC_CONFLICT
                );
                return;
            } catch (CurrencyNotFoundException e) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", e.getMessage()),
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            if (saved.isPresent()) {
                ServletUtils.writeJsonResponse(resp, saved.get(), HttpServletResponse.SC_CREATED);
            } else {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Failed to save exchange rate"),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
            }

        } catch (Exception e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
