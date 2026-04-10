package org.example;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class NebulaChatbot {
    private static final String API_KEY = System.getenv("Nebula");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    public String askNebula(String userPrompt) {
        if (API_KEY == null) return "Error: API Key 'Nebula' missing.";

        // 1. FETCH THE VAULT LIST FROM THE DATABASE
        String vaultData = DatabaseManager.getVaultSummary();

        // 2. CREATE THE SYSTEM BRAIN
        String systemContext = "You are Nebula, Anushka's AI archive assistant. " +
                "You have access to the file vault. Current files in vault:\n" + vaultData +
                "\nUse this list to answer questions about her files.";

        try {
            JSONObject json = new JSONObject();
            json.put("model", "llama-3.3-70b-versatile");

            JSONArray messages = new JSONArray();
            // We add the 'system' message so Nebula knows what files exist
            messages.put(new JSONObject().put("role", "system").put("content", systemContext));
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            json.put("messages", messages);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                JSONObject resJson = new JSONObject(response.body().string());
                return resJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            }
        } catch (Exception e) { return "System Latency. Retry!"; }
    }
}