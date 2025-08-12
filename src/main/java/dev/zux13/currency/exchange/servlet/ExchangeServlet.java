package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.dto.ExchangeResponseDto;
import dev.zux13.currency.exchange.exception.CurrencyNotFoundException;
import dev.zux13.currency.exchange.exception.ExchangeRateNotFoundException;
import dev.zux13.currency.exchange.service.CurrencyService;
import dev.zux13.currency.exchange.service.ExchangeRateService;
import dev.zux13.currency.exchange.util.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String fromCode = req.getParameter("from");
            String toCode = req.getParameter("to");
            String amountStr = req.getParameter("amount");

            if (!ServletUtils.isValidParams(fromCode, toCode, amountStr)) {
                writeError(resp, "Missing required query parameters", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    writeError(resp, "Amount must be non-negative", HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } catch (NumberFormatException e) {
                writeError(resp, "Invalid amount format", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            CurrencyDto baseCurrency = currencyService.findByCode(fromCode.toUpperCase())
                    .orElseThrow(() -> new CurrencyNotFoundException(fromCode));
            CurrencyDto targetCurrency = currencyService.findByCode(toCode.toUpperCase())
                    .orElseThrow(() -> new CurrencyNotFoundException(toCode));

            var rateOpt = exchangeRateService.findRateForExchange(baseCurrency.getCode(), targetCurrency.getCode());

            if (rateOpt.isEmpty()) {
                writeError(resp, "Exchange rate not found for given currencies", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            BigDecimal rate = rateOpt.get();
            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            ExchangeResponseDto dto = ExchangeResponseDto.builder()
                    .baseCurrency(baseCurrency)
                    .targetCurrency(targetCurrency)
                    .rate(rate.doubleValue())
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();

            ServletUtils.writeJsonResponse(resp, dto, HttpServletResponse.SC_OK);

        } catch (ExchangeRateNotFoundException | CurrencyNotFoundException e) {
            writeError(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            writeError(resp, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void writeError(HttpServletResponse resp, String message, int status) throws IOException {
        ServletUtils.writeJsonResponse(resp, Map.of("message", message), status);
    }
}
