package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NebulaApp extends Application {

    // Linking our logic engines
    private final GeminiChatbot chatbot = new GeminiChatbot();
    private final FileReader engine = new FileReader();

    @Override
    public void start(Stage primaryStage) {
        // --- 1. SIDEBAR NAVIGATION ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 20, 20));
        sidebar.setStyle("-fx-background-color: #0f172a;"); // Deep space navy
        sidebar.setPrefWidth(240);

        Label logo = new Label("NEBULA");
        logo.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 22px; -fx-font-weight: bold;");

        Button btnSync = createSidebarButton("Sync Archive");
        Button btnSearch = createSidebarButton("File Explorer");
        Button btnChat = createSidebarButton("AI Assistant");

        // Visual indicator of active tab
        btnChat.setStyle(btnChat.getStyle() + "-fx-background-color: #1e293b; -fx-text-fill: white;");

        sidebar.getChildren().addAll(logo, new Separator(), btnSync, btnSearch, btnChat);

        // --- 2. MAIN CHAT INTERFACE ---
        VBox chatContainer = new VBox(15);
        chatContainer.setPadding(new Insets(20));
        chatContainer.setStyle("-fx-background-color: #1e293b;"); // Slate background

        Label header = new Label("Gemini AI Assistant");
        header.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-font-weight: bold;");

        TextArea chatHistory = new TextArea();
        chatHistory.setText("Nebula Intelligence Online. How can I help you today, Anushka?\n");
        chatHistory.setEditable(false);
        chatHistory.setWrapText(true);
        // Styling the text area to look dark and modern
        chatHistory.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #e2e8f0; -fx-font-family: 'Consolas';");
        VBox.setVgrow(chatHistory, Priority.ALWAYS);

        HBox inputBar = new HBox(10);
        TextField userPrompt = new TextField();
        userPrompt.setPromptText("Type your message to Gemini...");
        userPrompt.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8;");
        HBox.setHgrow(userPrompt, Priority.ALWAYS);

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-cursor: hand;");

        inputBar.getChildren().addAll(userPrompt, sendBtn);
        chatContainer.getChildren().addAll(header, chatHistory, inputBar);

        // --- 3. LOGIC HANDLERS (The "Magic") ---

        // Send Button Action
        sendBtn.setOnAction(e -> {
            String message = userPrompt.getText().trim();
            if (!message.isEmpty()) {
                chatHistory.appendText("\nYou: " + message);
                userPrompt.clear();
                chatHistory.appendText("\nGemini is thinking...");

                // Run AI call in a background thread so GUI doesn't freeze
                new Thread(() -> {
                    String response = chatbot.askGemini(message);
                    // Return to GUI thread to update the text area
                    Platform.runLater(() -> {
                        chatHistory.appendText("\nGemini: " + response + "\n");
                    });
                }).start();
            }
        });

        // Allow pressing 'Enter' to send
        userPrompt.setOnAction(e -> sendBtn.fire());

        // Sync Button Action
        btnSync.setOnAction(e -> {
            chatHistory.appendText("\nSystem: Starting bulk sync of 'to_be_sorted'...");
            new Thread(() -> {
                engine.processDirectory("to_be_sorted");
                Platform.runLater(() -> chatHistory.appendText("\nSystem: Sync Complete. Files moved to vault."));
            }).start();
        });

        // --- 4. FINAL SCENE SETUP ---
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(chatContainer);

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("NebulaArchive Desktop v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to keep sidebar buttons consistent
    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 15, 10, 15));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-cursor: hand;");

        // Hover effects
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#1e293b")) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: #1e293b; -fx-text-fill: white;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!text.contains("Assistant")) { // Don't remove style from active button
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-cursor: hand;");
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}