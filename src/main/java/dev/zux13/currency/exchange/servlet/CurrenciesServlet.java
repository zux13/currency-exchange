package dev.zux13.currency.exchange.servlet;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.exception.DuplicateCurrencyCodeException;
import dev.zux13.currency.exchange.service.CurrencyService;
import dev.zux13.currency.exchange.util.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/currencies/*")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CurrencyDto> currencies = currencyService.findAll();
        ServletUtils.writeJsonResponse(resp, currencies, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");

            if (!ServletUtils.isValidParams(name, code, sign)) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Missing required form fields"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            CurrencyDto dto = CurrencyDto.builder()
                    .name(name)
                    .code(code)
                    .sign(sign)
                    .build();

            Optional<CurrencyDto> saved;
            try {
                saved = currencyService.save(dto);
            } catch (DuplicateCurrencyCodeException e) {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Currency with this code already exists"),
                        HttpServletResponse.SC_CONFLICT
                );
                return;
            }

            if (saved.isPresent()) {
                ServletUtils.writeJsonResponse(resp, saved.get(), HttpServletResponse.SC_CREATED);
            } else {
                ServletUtils.writeJsonResponse(resp,
                        Map.of("message", "Failed to save currency"),
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
