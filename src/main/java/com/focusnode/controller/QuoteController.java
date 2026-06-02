package com.focusnode.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class QuoteController {

    @FXML private Label quoteText;
    @FXML private Label quoteAuthor;

    private static final String ZEN_QUOTES_API = "https://zenquotes.io/api/today";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        fetchQuote();
    }

    private void fetchQuote() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ZEN_QUOTES_API))
                .GET()
                .build();

        CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
            } catch (Exception e) {
                System.err.println("[Cảnh báo] Không thể tải Quote mới từ Internet do lỗi mạng. Đang dùng Quote dự phòng. Lỗi: " + e.getMessage());
            }
            return null;
        }).thenAccept(jsonResponse -> {
            if (jsonResponse != null) {
                try {
                    JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
                    if (!jsonArray.isEmpty()) {
                        JsonObject quoteObj = jsonArray.get(0).getAsJsonObject();
                        String quote = quoteObj.get("q").getAsString();
                        String author = quoteObj.get("a").getAsString();

                        Platform.runLater(() -> {
                            quoteText.setText("\"" + quote + "\"");
                            quoteAuthor.setText("— " + author);
                        });
                    }
                } catch (Exception e) {
                    System.err.println("[Cảnh báo] Lỗi định dạng dữ liệu Quote trả về. Đang dùng Quote dự phòng.");
                    showFallbackQuote();
                }
            } else {
                showFallbackQuote();
            }
        });
    }

    private void showFallbackQuote() {
        Platform.runLater(() -> {
            quoteText.setText("\"Discipline is the bridge between goals and accomplishment.\"");
            quoteAuthor.setText("— Jim Rohn");
        });
    }
}
