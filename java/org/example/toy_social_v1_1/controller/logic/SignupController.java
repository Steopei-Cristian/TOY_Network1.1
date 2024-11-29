package org.example.toy_social_v1_1.controller.logic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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


public class SignupController {
    private UserService userService;
    private FriendshipService friendshipService;
    private Network network;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private Stage mainStage;

    private PasswordValidator passwordValidator;

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    TextField confirmPasswordField;
    @FXML
    Button signUpButton;
    @FXML
    Button loginLink;

    public SignupController(UserService userService,
                            FriendshipService friendshipService,
                            Network network,
                            FriendRequestService friendRequestService,
                            MessageService messageService,
                            Stage mainStage) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.network = network;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.mainStage = mainStage;

        passwordValidator = new PasswordValidator();
    }

    @FXML
    public void handleSignupRequest(ActionEvent event) {
        if(usernameField.getText().isEmpty()) {
            Alert alert = AlertFactory.getInstance().createErrorAlert("Invalid credentials", "Username cannot be empty");
            alert.show();
            return;
        }

        Optional<User> resultReq = userService.getByUsername(usernameField.getText());
        if (resultReq.isEmpty()) {
            try {
                passwordValidator.validate(passwordField.getText());
            } catch (Exception e) {
                Alert alert = AlertFactory.getInstance().createExceptionAlert("Invalid Password", e);
                alert.show();
                return;
            }

            if(!passwordField.getText().equals(confirmPasswordField.getText())) {
                Alert alert = AlertFactory.getInstance().createErrorAlert("Invalid credentials", "Passwords do not match");
                alert.show();
            } else {
                Optional<User> user = network.addUser(usernameField.getText(), passwordField.getText());
                if(user.isPresent()) {
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/session-view.fxml"));
                    loader.setControllerFactory(param -> new SessionController(mainStage,
                            user.get(),
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

                        System.out.println("OK");
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }

            }
        }
        else {
            Alert alert = AlertFactory.getInstance().createErrorAlert("Invalid credentials", "Username already exists");
            alert.show();
        }
    }

    @FXML
    public void handleLoginRequest(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/login-view.fxml"));
        loader.setControllerFactory(param -> new LoginController(userService,
                network,
                friendshipService,
                friendRequestService,
                messageService,
                mainStage));

        try {
            Scene scene = new Scene(loader.load(), 500, 300);
            mainStage.setTitle("Login");
            mainStage.setResizable(false);
            mainStage.setScene(scene);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
