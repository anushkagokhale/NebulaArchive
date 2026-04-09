package org.example;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiChatbot {
    // Double-check this key is exactly what Google gave you!
    private static final String API_KEY = "AIzaSyC3-bC4NtAg7zWWLt5hKAPVdcLChlnMW-E";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + "AIzaSyC3-bC4NtAg7zWWLt5hKAPVdcLChlnMW-E";

    private final OkHttpClient client = new OkHttpClient();

    public String askGemini(String prompt) {
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
                    // This will tell us EXACTLY what Google is complaining about
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