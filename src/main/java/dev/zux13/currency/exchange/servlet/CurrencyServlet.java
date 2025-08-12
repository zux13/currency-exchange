package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.service.CurrencyService;
import dev.zux13.currency.exchange.util.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (ServletUtils.isEmptyPathInfo(pathInfo)) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Currency code is required in path."),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            String code = pathInfo.substring(1).toUpperCase();

            var currencyOpt = currencyService.findByCode(code);
            if (currencyOpt.isEmpty()) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Currency with code %s not found.".formatted(code)),
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            var currency = currencyOpt.get();
            ServletUtils.writeJsonResponse(resp, currency, HttpServletResponse.SC_OK);

        } catch (Exception e) {
            ServletUtils.writeJsonResponse(resp,
                    Map.of("message", "Internal error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
