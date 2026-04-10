package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;

public class NebulaApp extends Application {

    private final NebulaChatbot chatbot = new NebulaChatbot();
    private final FileReader engine = new FileReader();

    @Override
    public void start(Stage primaryStage) {
        // --- 1. SIDEBAR ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 20, 20, 20));
        sidebar.setStyle("-fx-background-color: #0f172a;");
        sidebar.setPrefWidth(240);

        Label logo = new Label("NEBULA");
        logo.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 22px; -fx-font-weight: bold;");

        Button btnSync = createSidebarButton("Sync Archive");
        Button btnSearch = createSidebarButton("File Explorer");
        Button btnChat = createSidebarButton("AI Assistant");
        btnChat.setStyle(btnChat.getStyle() + "-fx-background-color: #1e293b; -fx-text-fill: white;");

        sidebar.getChildren().addAll(logo, new Separator(), btnSync, btnSearch, btnChat);

        // --- 2. MAIN CHAT AREA ---
        VBox chatContainer = new VBox(15);
        chatContainer.setPadding(new Insets(20));
        chatContainer.setStyle("-fx-background-color: #1e293b;");

        Label header = new Label("Nebula AI Intelligence Core");
        header.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-font-weight: bold;");

        TextArea chatHistory = new TextArea();
        chatHistory.setText("Nebula Intelligence Online. Drag files here to auto-sort.\n");
        chatHistory.setEditable(false);
        chatHistory.setWrapText(true);
        chatHistory.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #e2e8f0; -fx-font-family: 'Consolas';");
        VBox.setVgrow(chatHistory, Priority.ALWAYS);

        HBox inputBar = new HBox(10);
        TextField userPrompt = new TextField();
        userPrompt.setPromptText("Type your message to Nebula...");
        userPrompt.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8;");
        HBox.setHgrow(userPrompt, Priority.ALWAYS);

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-cursor: hand;");

        inputBar.getChildren().addAll(userPrompt, sendBtn);
        chatContainer.getChildren().addAll(header, chatHistory, inputBar);

        // --- 3. DRAG & DROP LOGIC ---
        chatContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != chatContainer && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        chatContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    chatHistory.appendText("\n[System] File detected: " + file.getName());
                    new Thread(() -> {
                        engine.processSingleFile(file); // Smart-sorts the file
                        Platform.runLater(() ->
                                chatHistory.appendText("\nNebula: I've analyzed and vaulted " + file.getName() + ".\n")
                        );
                    }).start();
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        // --- 4. ACTION HANDLERS ---
        sendBtn.setOnAction(e -> {
            String message = userPrompt.getText().trim();
            if (!message.isEmpty()) {
                chatHistory.appendText("\nYou: " + message);
                userPrompt.clear();
                chatHistory.appendText("\nNebula is thinking...");
                new Thread(() -> {
                    String response = chatbot.askNebula(message);
                    Platform.runLater(() -> {
                        String currentText = chatHistory.getText().replace("Nebula is thinking...", "");
                        chatHistory.setText(currentText);
                        chatHistory.appendText("Nebula: " + response + "\n");
                    });
                }).start();
            }
        });

        userPrompt.setOnAction(e -> sendBtn.fire());

        btnSync.setOnAction(e -> {
            chatHistory.appendText("\n[System]: Starting bulk sync of 'to_be_sorted'...");
            new Thread(() -> {
                engine.startSystem();
                engine.processDirectory("to_be_sorted");
                Platform.runLater(() -> chatHistory.appendText("\n[System]: Sync Complete."));
            }).start();
        });

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(chatContainer);

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("NebulaArchive Desktop v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 15, 10, 15));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-cursor: hand;");
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}