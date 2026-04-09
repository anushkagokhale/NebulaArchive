package org.example;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class GeminiChatbot {
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");

    // Stable 2026 Production Endpoint
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-pro:generateContent?key=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String askGemini(String userPrompt) {
        if (API_KEY == null || API_KEY.isEmpty()) return "Setup Error: API Key missing.";

        // Get file context from DB
        String vaultData = DatabaseManager.getVaultSummary();
        String context = "You are Nebula, Anushka's archive assistant. " +
                "The current files in the archive are:\n" + vaultData +
                "\n\nUser Question: " + userPrompt;

        try {
            JSONObject textPart = new JSONObject().put("text", context);
            JSONArray contents = new JSONArray().put(new JSONObject().put("parts", new JSONArray().put(textPart)));
            JSONObject jsonBody = new JSONObject().put("contents", contents);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(API_URL).post(body).build();

            try (Response response = client.newCall(request).execute()) {
                String rawResponse = response.body().string();

                if (response.code() == 429) return "Nebula: Rate limit hit. Wait 30 seconds!";
                if (response.code() == 503) return "Nebula: Server busy. Try again in a moment!";
                if (!response.isSuccessful()) return "Google Error: " + response.code();

                JSONObject resJson = new JSONObject(rawResponse);
                if (resJson.has("candidates") && !resJson.getJSONArray("candidates").isEmpty()) {
                    return resJson.getJSONArray("candidates")
                            .getJSONObject(0).getJSONObject("content")
                            .getJSONArray("parts").getJSONObject(0).getString("text");
                }
                return "Nebula: No response generated. Try rephrasing.";
            }
        } catch (Exception e) {
            return "Connection Error: " + e.getMessage();
        }
    }
}