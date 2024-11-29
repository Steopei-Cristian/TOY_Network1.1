package org.example.toy_social_v1_1.domain.ui;

import javafx.scene.control.ListCell;
import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.service.entity.FriendshipService;

import java.util.Optional;

public class FriendListCell extends ListCell<User> {
    private FriendshipService friendshipService;
    private User currentUser;

    public FriendListCell(FriendshipService friendshipService,
                          User currentUser) {
        super();

        this.friendshipService = friendshipService;
        this.currentUser = currentUser;

        setStyle("-fx-background-color: #00246B;" +
                "-fx-text-fill: #A1D6E2;" +
                "-fx-alignment: CENTER;" +
                "-fx-font-size: 15px;" +
                "-fx-border-color: #A1D6E2;");
    }


    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (!empty && user != null) {
            Optional<Friendship> fr = friendshipService.getFriendship(currentUser.getID(), user.getID());
            fr.ifPresent(friendship -> setText(
                    user.getUsername() + " - since " + friendship.getSince()));
        }
    }
}
