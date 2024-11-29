package org.example.toy_social_v1_1.controller.logic;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.service.network.Network;


public class FriendActionsController {
    private User currentUser;
    private User friend;
    private Network network;

    private ObservableList<User> friends;

    @FXML
    private Button unfriendButton;
    @FXML
    private Button openChatButton;
    @FXML
    private Button inspectButton;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private VBox chatBox;

    public FriendActionsController(Stage stage,
                                   User currentUser,
                                   User friend,
                                   Network network,
                                   ObservableList<User> friends,
                                   VBox chatBox) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.friend = friend;
        this.network = network;
        this.friends = friends;
        this.chatBox = chatBox;
    }

    @FXML
    public void initialize() {
        unfriendButton.setText(unfriendButton.getText() + " " + friend.getUsername());
    }

    @FXML
    public void handleUnfriendReq(ActionEvent action) {
        network.removeFriend(currentUser.getID(), friend.getID());
        friends.remove(friend);
        stage.close();
    }

    @FXML
    public void handleOpenChatReq(ActionEvent action) {
        chatBox.setVisible(true);
        stage.close();
    }

    @FXML
    public void handleInspectReq(ActionEvent action) {

    }

    @FXML
    public void handleCancelReq(ActionEvent action) {
        chatBox.setVisible(false);
        stage.close();
    }
}
