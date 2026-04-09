package org.example;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiChatbot {
    // SECURITY FIX: No more hardcoded key.
    // We pull it from the system environment variables.
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    public String askGemini(String prompt) {
        // Safety check to make sure the environment variable is actually set
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "Setup Error: GEMINI_API_KEY not found in Environment Variables.";
        }

        try {
            JSONObject textObj = new JSONObject().put("text", prompt);
            JSONArray partsArray = new JSONArray().put(textObj);
            JSONObject contentObj = new JSONObject().put("contents", new JSONArray().put(new JSONObject().put("parts", partsArray)));

            RequestBody body = RequestBody.create(
                    contentObj.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    System.err.println("Google Debug: " + responseBody);
                    return "Error from Google: " + response.code();
                }

                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            }
        } catch (Exception e) {
            return "Connection Error: " + e.getMessage();
        }
    }
}