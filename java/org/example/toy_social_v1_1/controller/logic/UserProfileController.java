package org.example.toy_social_v1_1.controller.logic;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.factory.AlertFactory;
import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.observer.Observer;

import java.sql.Date;
import java.time.LocalDate;


public class UserProfileController implements Observer<EntityChangeEvent<FriendRequest>> {
    @FXML
    private Stage stage;

    private UserService userService;
    private Network network;
    private User currentUser;
    private User searchedUser;
    private FriendRequestService friendRequestService;

    private ObservableList<User> friends;
    private ObservableList<FriendRequest> friendRequests;

    @FXML
    private Label username;
    @FXML
    private Button actionButton;
    @FXML
    private Button cancelButton;

    public UserProfileController(Stage stage,
                                 UserService userService,
                                 Network network,
                                 User currentUser,
                                 User searchedUser,
                                 ObservableList<User> friends,
                                 FriendRequestService friendRequestService,
                                 ObservableList<FriendRequest> userFriendRequests) {
        this.stage = stage;
        this.userService = userService;
        this.network = network;
        this.currentUser = currentUser;
        this.searchedUser = searchedUser;
        this.friends = friends;

        this.friendRequestService = friendRequestService;
        friendRequestService.addObserver(this);

        this.friends = friends;
        friendRequests = userFriendRequests;
    }

    private void initFriendRequests() {

    }

    @Override
    public void update(EntityChangeEvent<FriendRequest> event) {
        setActionButtonText();
    }

    @FXML
    private void initialize() {
        username.setText(searchedUser.getUsername());
        setActionButtonText();
    }

    @FXML
    private void setActionButtonText() {
        if(network.getUserFriends(currentUser.getID()).contains(searchedUser)) {
            actionButton.setText("Unfriend");
        }
        else {
            friendRequestService.alreadySentRequest(currentUser.getID(), searchedUser.getID())
                    .ifPresentOrElse(
                            (_) -> actionButton.setText("Pending request"),
                            () -> actionButton.setText("Send friend request")
                    );
        }
    }

    @FXML
    public void handleActionReq(ActionEvent event) {
        if(actionButton.getText().equals("Send friend request")) {
            //TODO send request
            FriendRequest newReq = new FriendRequest(currentUser.getID(),
                    searchedUser.getID(),
                    Date.valueOf(LocalDate.now()),
                    false);
            newReq.setID(friendRequestService.getLastId() + 1);
            friendRequestService.addRequest(newReq);
            Alert notification = AlertFactory.getInstance().createNotificationAlert("Request sent");
            notification.show();
            stage.close();
        }
        else if(actionButton.getText().equals("Unfriend")) {
            network.removeFriend(currentUser.getID(), searchedUser.getID());
            friends.remove(searchedUser);
            stage.close();
        }
    }

    @FXML
    public void handleCancelReq(ActionEvent event) {
        stage.close();
    }
}
