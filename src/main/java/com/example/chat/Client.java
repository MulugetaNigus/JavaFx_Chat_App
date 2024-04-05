package com.example.chat;

import javafx.application.Application;
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
import java.net.Socket;

public class Client extends Application {
    private static final int PORT = 4001;
    private static final String SERVER_ADDRESS = "localhost";
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Client");

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
        sendButton.setOnAction(e -> sendMessage());
        bottomBox.getChildren().addAll(messageField, sendButton);
        root.setBottom(bottomBox);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    private void sendMessage() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = messageField.getText();
            if (!message.isEmpty()) {
                chatArea.appendText("Client: " + message + "\n");

                out.println(message);
                String response = in.readLine();
                chatArea.appendText("Server: " + response + "\n");

                messageField.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
