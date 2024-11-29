package org.example.toy_social_v1_1.controller.logic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.toy_social_v1_1.Main;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.factory.AlertFactory;
import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.FriendshipService;
import org.example.toy_social_v1_1.service.entity.MessageService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;
import org.example.toy_social_v1_1.validator.PasswordValidator;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private UserService userService;
    private FriendshipService friendshipService;
    private Network network;
    private Stage mainStage;
    private FriendRequestService friendRequestService;
    private MessageService messageService;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label signupLabel;


    public LoginController(UserService userService,
                           Network network,
                           FriendshipService friendshipService,
                           FriendRequestService friendRequestService,
                           MessageService messageService,
                           Stage mainStage) {
        this.userService = userService;
        this.network = network;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.mainStage = mainStage;
    }

    @FXML
    public void handleLoginRequest(ActionEvent actionEvent) {
        Optional<User> resultReq1 = userService.getByUsername(usernameField.getText());
        if(resultReq1.isPresent()) {
            try {
                PasswordValidator validator = new PasswordValidator();
                validator.validate(passwordField.getText());
            } catch (Exception e) {
                Alert alert = AlertFactory.getInstance().createExceptionAlert("Invalid credentials", e);
                alert.show();
                return;
            }
        }
        else {
            Alert alert = AlertFactory.getInstance().createErrorAlert("Invalid credentials", "Invalid username");
            alert.show();
            return;
        }

        if (resultReq1.get().getPassword().equals(passwordField.getText())) {
            System.out.println(resultReq1.get());
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/session-view.fxml"));
            loader.setControllerFactory(param -> new SessionController(mainStage,
                    resultReq1.get(),
                    network,
                    userService,
                    friendshipService,
                    friendRequestService,
                    messageService));

            try {
                Scene scene = new Scene(loader.load(), 700, 500);
                mainStage.setTitle("Welcome");
                mainStage.setResizable(false);
                mainStage.setScene(scene);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("OK");
        } else {
            Alert alert = AlertFactory.getInstance().createErrorAlert("Invalid credentials", "Passwords do not match");
            alert.show();
        }
    }

    @FXML
    public void handleSignupRequest(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/signup-view.fxml"));
        loader.setControllerFactory(param -> new SignupController(userService,
                friendshipService,
                network,
                friendRequestService,
                messageService,
                mainStage));

        try {
            Scene scene = new Scene(loader.load(), 550, 350);
            mainStage.setTitle("Signup");
            mainStage.setResizable(false);
            mainStage.setScene(scene);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
