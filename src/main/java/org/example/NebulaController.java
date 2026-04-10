package org.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class NebulaController {

    @FXML
    private TextArea chatArea; // Ensure this ID matches your FXML

    @FXML
    private TextField inputField; // Ensure this ID matches your FXML

    @FXML
    private Button sendButton;

    // This uses the new Groq-powered high-speed engine
    private final NebulaChatbot chatbot = new NebulaChatbot();

    @FXML
    public void initialize() {
        chatArea.setText("Nebula Intelligence Online. How can I help you today, Anushka?\n");
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
    }

    @FXML
    private void handleSendAction() {
        String userText = inputField.getText().trim();

        if (!userText.isEmpty()) {
            chatArea.appendText("\nYou: " + userText + "\n");
            inputField.clear();
            chatArea.appendText("Nebula is thinking...\n");

            // Background thread to keep the UI responsive
            new Thread(() -> {
                try {
                    String response = chatbot.askNebula(userText);

                    Platform.runLater(() -> {
                        // Remove "thinking" and show real response
                        String currentText = chatArea.getText().replace("Nebula is thinking...\n", "");
                        chatArea.setText(currentText);
                        chatArea.appendText("Nebula: " + response + "\n");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> chatArea.appendText("Error: Nebula Core connection failed.\n"));
                }
            }).start();
        }
    }

    @FXML
    private void handleSyncArchive() {
        chatArea.appendText("\n[System] Starting vault synchronization...\n");
        new Thread(() -> {
            FileReader reader = new FileReader();
            reader.startSystem();
            reader.processDirectory("to_be_sorted");

            Platform.runLater(() -> chatArea.appendText("[System] Vault synchronization complete.\n"));
        }).start();
    }
}