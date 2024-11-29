package org.example.toy_social_v1_1.domain.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.dto.MessageDTO;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MessageListCell extends ListCell<MessageDTO> {
    private User currentUser;
    private VBox container = new VBox();
    private Region rightMessage = new Region();
    private Region verticalSpacer = new Region();
    private Label replyLabel = new Label();
    private HBox messageWrapper = new HBox();
    private Label text = new Label();

    public MessageListCell(User currentUser) {
        super();

        this.currentUser = currentUser;

        container.setStyle("-fx-background-color: #00246B;" +
                "-fx-border-style: none;");

        text.setStyle("-fx-text-fill: #00246B;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: #A1D6E2;" +
                "-fx-background-radius: 2px;");
        text.setMaxWidth(200);

        HBox.setHgrow(rightMessage, Priority.ALWAYS);
        verticalSpacer.setPrefHeight(1);
        replyLabel.setStyle(text.getStyle() + "-fx-font-size: 10px;"
                + "-fx-text-fill: #1994AD;" + "-fx-padding: 2px;");

        messageWrapper.getChildren().add(text);
        container.getChildren().add(messageWrapper);
        setStyle("-fx-background-color: #00246B;");
    }

    @Override
    protected void updateItem(MessageDTO message, boolean empty) {
        super.updateItem(message, empty);

        if(!empty || message != null) {
            text.setText(" " + message.getText() + " ");


            if(message.getFrom().equals(currentUser)) {
                messageWrapper.getChildren().remove(rightMessage);
            }
            else {
                if(!messageWrapper.getChildren().contains(rightMessage)) {
                    messageWrapper.getChildren().addFirst(rightMessage);
                }
            }

            if(message.getReply() != null) {
                if(!container.getChildren().contains(verticalSpacer)) {
                    container.getChildren().addFirst(verticalSpacer);
                }
                if(!container.getChildren().contains(replyLabel)) {
                    replyLabel.setText("Replied to:\n" + message.getReply().getText());
                    container.getChildren().addFirst(replyLabel);
                }
            } else {
                container.getChildren().removeAll(verticalSpacer, replyLabel);
            }

            setGraphic(container);
        }
    }
}
