package org.example.toy_social_v1_1.domain.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;

import java.util.Optional;

public class PendingRequestListCell extends ListCell<FriendRequest> {
    private VBox reqBox = new VBox();
    private Label text = new Label();
    private HBox buttonBox = new HBox();
    private Button accept = new Button("Accept");
    private Button reject = new Button("Reject");

    private User currentUser;
    private UserService userService;
    private FriendRequestService friendRequestService;
    private Network network;

    private ObservableList<User> friends;
    private ObservableList<FriendRequest> requests;

    public PendingRequestListCell(User currentUser,
                                  UserService userService,
                                  FriendRequestService friendRequestService,
                                  Network network,
                                  ObservableList<User> friends,
                                  ObservableList<FriendRequest> requests) {
        super();

        this.currentUser = currentUser;
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.network = network;

        this.requests = requests;
        this.friends = friends;

        text.setStyle("-fx-background-color: #00246B;" +
                "-fx-text-fill: #A1D6E2;" +
                "-fx-alignment: CENTER;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-alignment: CENTER");
        reqBox.getChildren().add(text);

        accept.setStyle("-fx-background-color: #00246B;" +
                "-fx-text-fill: #A1D6E2;" +
                "-fx-alignment: CENTER;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #A1D6E2;" +
                "-fx-border-radius: 2px;" +
                "-fx-text-alignment: CENTER");

        reject.setStyle("-fx-background-color: #00246B;" +
                "-fx-text-fill: #A1D6E2;" +
                "-fx-alignment: CENTER;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #A1D6E2;" +
                "-fx-border-radius: 2px;" +
                "-fx-text-alignment: CENTER");

        accept.setOnAction(_ -> {
            FriendRequest req = getItem();
            if(req != null) {
                System.out.println(req.getID());
                network.addFriend(req.getUser1(), req.getUser2());
                friendRequestService.deleteRequest(req.getID());
                requests.remove(req);
            }
        });

        reject.setOnAction(_ -> {
            FriendRequest req = getItem();
            if(req != null) {
                friendRequestService.deleteRequest(req.getID());
                requests.remove(req);
            }
        });

        buttonBox.getChildren().addAll(accept, reject);
        buttonBox.setStyle("-fx-alignment: CENTER");
        buttonBox.setSpacing(5);

        reqBox.setStyle("-fx-alignment: CENTER");
        setStyle("-fx-background-color: #00246B;" +
                "-fx-border-color: #A1D6E2;");
    }

    @Override
    protected void updateItem(FriendRequest req, boolean empty) {
        super.updateItem(req, empty);

        if(!empty || req != null) {
            System.out.println(req);

            Optional<User> from = userService.find(req.getUser1());
            Optional<User> to = userService.find(req.getUser2());

            if(from.isEmpty() || to.isEmpty()) {
                return;
            }

            boolean sentToCurrentUser = to.get().equals(currentUser);
            if(sentToCurrentUser) {
                text.setText("From " + from.get().getUsername() + " on " + req.getSent());
                if(!reqBox.getChildren().contains(buttonBox)) {
                    reqBox.getChildren().add(buttonBox);
                }
            } else {
                text.setText("To " + to.get().getUsername() + " on " + req.getSent()
                        + "\nPending confirmation");
            }

            setGraphic(reqBox);
        }
    }
}
