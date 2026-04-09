package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NebulaController {
    // These link to the UI elements we'll build next
    private GeminiChatbot chatbot = new GeminiChatbot();
    private FileReader engine = new FileReader();

    public void onSyncClicked() {
        System.out.println("🚀 GUI Trigger: Starting Folder Sync...");
        engine.processDirectory("to_be_sorted");
    }

    public String onSendMessage(String userText) {
        if (userText.isEmpty()) return "Please type something first!";
        return chatbot.askGemini(userText);
    }
}