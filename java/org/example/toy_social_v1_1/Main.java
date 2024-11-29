package org.example.toy_social_v1_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.toy_social_v1_1.controller.logic.SessionController;
import org.example.toy_social_v1_1.repository.db_repo.FriendshipDBRepo;
import org.example.toy_social_v1_1.repository.db_repo.FriendRequestDBRepo;
import org.example.toy_social_v1_1.repository.db_repo.MessageDBRepo;
import org.example.toy_social_v1_1.repository.db_repo.UserDBRepo;

import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.FriendshipService;
import org.example.toy_social_v1_1.service.entity.MessageService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;

import org.example.toy_social_v1_1.validator.PasswordValidator;
import org.example.toy_social_v1_1.validator.StringLongValidator;
import org.example.toy_social_v1_1.validator.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String url = "jdbc:postgresql://localhost:5432/toy_network";
        String user = "postgres";
        String password = "postgres";

        UserDBRepo dbUserRepo = new UserDBRepo(url, user, password);
        FriendshipDBRepo dbFriendRepo = new FriendshipDBRepo(url, user, password);
        FriendRequestDBRepo dbFriendRequestRepo = new FriendRequestDBRepo(url, user, password);
        MessageDBRepo dbMessageRepo = new MessageDBRepo(url, user, password, dbUserRepo);

        dbMessageRepo.getAll().forEach(System.out::println);

        UserService userService = new UserService(dbUserRepo);
        FriendshipService frService = new FriendshipService(dbFriendRepo);
        FriendRequestService frRequestService = new FriendRequestService(dbFriendRequestRepo);
        Network networkService = new Network(userService, frService);
        MessageService messageService = new MessageService(dbMessageRepo);

        //FXMLLoader loader1 = new FXMLLoader(Main.class.getResource("views/login-view.fxml"));
        FXMLLoader loader1 = new FXMLLoader(Main.class.getResource("views/session-view.fxml"));
//        loader1.setControllerFactory(param -> new LoginController(userService,
//                networkService,
//                frService,
//                frRequestService,
//                stage));
        loader1.setControllerFactory(_ -> new SessionController(stage,
                userService.getByUsername("cristi").get(),
                networkService,
                userService,
                frService,
                frRequestService,
                messageService));
        Scene scene = new Scene(loader1.load(), 700, 400);
        stage.setTitle("Session");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

//        Stage stage1 = new Stage();
//        FXMLLoader loader2 = new FXMLLoader(Main.class.getResource("views/session-view.fxml"));
//        loader2.setControllerFactory(_ -> new SessionController(stage,
//                userService.getByUsername("sami").get(),
//                networkService,
//                userService,
//                frService,
//                frRequestService,
//                messageService));
//        Scene scene1 = new Scene(loader2.load(), 700, 400);
//        stage1.setTitle("Session");
//        stage1.setResizable(false);
//        stage1.setScene(scene1);
//        stage1.show();

        //userService.getUsersStartingWith("t").forEach(System.out::println);
    }
}
