package org.example.toy_social_v1_1.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.toy_social_v1_1.domain.entities.User;

public class SearchBarController {
    private Iterable<User> allUsers;

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> options;

    @FXML
    public void initialize() {

    }
}
