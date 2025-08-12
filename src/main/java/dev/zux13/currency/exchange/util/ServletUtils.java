package dev.zux13.currency.exchange.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.PrintWriter;

@UtilityClass
public class ServletUtils {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @SneakyThrows
    public void writeJsonResponse(HttpServletResponse resp, Object obj, int status) {
        resp.setContentType("application/json");
        resp.setStatus(status);
        PrintWriter writer = resp.getWriter();
        writer.write(GSON.toJson(obj));
    }

    public boolean isValidParams(String... params) {
        if (params == null || params.length == 0) {
            return false;
        }
        boolean isValid = true;
        for (String param : params) {
            if (param == null || param.isBlank()) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    public boolean isEmptyPathInfo(String pathInfo) {
        return pathInfo == null || "/".equals(pathInfo);
    }

    public boolean isEngLetters(String s) {
        boolean isValid = true;
        for (char ch : s.toLowerCase().toCharArray()) {
            if (ch < 'a' || ch > 'z') {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    public boolean isValidCurrencyPairCodesFormat(String pair) {
        return isValidParams(pair)
                && isEngLetters(pair)
                && pair.length() == 6;
    }

}

