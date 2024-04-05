package com.example.chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Application {
    private static final int PORT = 4001;
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;
    private List<PrintWriter> clientWriters = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Server");

        BorderPane root = new BorderPane();

        chatArea = new TextArea();
        chatArea.setEditable(false);
        root.setCenter(chatArea);

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(10));
        bottomBox.setSpacing(10);

        messageField = new TextField();
        messageField.setPrefWidth(200);

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessageToClients());
        bottomBox.getChildren().addAll(messageField, sendButton);
        root.setBottom(bottomBox);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

        startServer(); // Start the server when the GUI starts
    }

    private void sendMessageToClients() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendToChatArea("Server: " + message);
            sendToAllClients(message);
            messageField.clear();
        }
    }

    private void sendToAllClients(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                appendToChatArea("Server started...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    appendToChatArea("Client connected: " + clientSocket.getInetAddress());

                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.add(writer);

                    // Handle client communication
                    handleClient(clientSocket, writer);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket, PrintWriter writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String message;
        while ((message = reader.readLine()) != null) {
            appendToChatArea("Client: " + message);
        }
    }

    private void appendToChatArea(String message) {
        Platform.runLater(() -> {
            // Append message to the TextArea
            // This method is required to ensure thread-safe updates to the UI component
            // Since UI updates should happen on the JavaFX Application Thread
            chatArea.appendText("\n" + message);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
