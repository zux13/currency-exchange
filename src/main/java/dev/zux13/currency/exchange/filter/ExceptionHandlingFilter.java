package dev.zux13.currency.exchange.filter;

import dev.zux13.currency.exchange.dto.ErrorResponseDto;
import dev.zux13.currency.exchange.exception.*;
import dev.zux13.currency.exchange.util.JsonResponseWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            JsonResponseWriter.write(
                    (HttpServletResponse) response,
                    HttpServletResponse.SC_NOT_FOUND,
                    new ErrorResponseDto(e.getMessage())
            );
        } catch (DuplicateCurrencyCodeException | DuplicateExchangeRateException e) {
            JsonResponseWriter.write(
                    (HttpServletResponse) response,
                    HttpServletResponse.SC_CONFLICT,
                    new ErrorResponseDto(e.getMessage())
            );
        } catch (ValidationException | IllegalArgumentException e) {
            JsonResponseWriter.write(
                    (HttpServletResponse) response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponseDto(e.getMessage())
            );
        } catch (Exception e) {
            JsonResponseWriter.write(
                    (HttpServletResponse) response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorResponseDto("An unexpected error occurred")
            );
        }
    }
}
