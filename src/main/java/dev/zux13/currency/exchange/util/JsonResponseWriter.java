package dev.zux13.currency.exchange.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.PrintWriter;

@UtilityClass
public class JsonResponseWriter {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public void write(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(GSON.toJson(data));
    }
}

