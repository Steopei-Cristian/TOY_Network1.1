package org.example.toy_social_v1_1.controller.logic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.domain.ui.PendingRequestListCell;
import org.example.toy_social_v1_1.service.entity.FriendRequestService;
import org.example.toy_social_v1_1.service.entity.FriendshipService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.service.network.Network;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.event.EntityChangeEventType;
import org.example.toy_social_v1_1.util.observer.Observer;

public class PendingRequestsController implements Observer<EntityChangeEvent<?>> {
    private User currentUser;
    private FriendRequestService friendRequestService;
    private UserService userService;
    private FriendshipService friendshipService;
    private Network network;

    @FXML
    private ListView<FriendRequest> reqListView;

    private ObservableList<FriendRequest> friendRequests;
    private ObservableList<User> friends;

    public PendingRequestsController(User currentUser,
                                     FriendRequestService friendRequestService,
                                     UserService userService,
                                     FriendshipService friendshipService,
                                     Network network,
                                     ObservableList<User> friends,
                                     ObservableList<FriendRequest> userFriendRequests) {
        this.currentUser = currentUser;

        this.friendRequestService = friendRequestService;
        friendRequestService.addObserver(this);
        friendRequests = userFriendRequests;

        this.userService = userService;
        this.friendshipService = friendshipService;
        this.network = network;

        this.friends = friends;
    }

    @Override
    public void update(EntityChangeEvent<?> entityChangeEvent) {
        if(entityChangeEvent.getData() instanceof FriendRequest ||
            entityChangeEvent.getOldData() instanceof FriendRequest) {
            if(entityChangeEvent.getType().equals(EntityChangeEventType.ADD)) {
                handleAddEvent((EntityChangeEvent<FriendRequest>) entityChangeEvent);
            } else if (entityChangeEvent.getType().equals(EntityChangeEventType.DELETE)) {
                handleDeleteEvent((EntityChangeEvent<FriendRequest>) entityChangeEvent);
            } else { // update
                // TODO handle update
            }
        }
    }

    private void handleAddEvent(EntityChangeEvent<FriendRequest> entityChangeEvent) {
        // TODO order by most recent
        FriendRequest addedRequest = entityChangeEvent.getData();
        if (addedRequest.getUser1().equals(currentUser.getID()) ||
                addedRequest.getUser2().equals(currentUser.getID())) {
            friendRequests.add(addedRequest);
        }
    }

    private void handleDeleteEvent(EntityChangeEvent<FriendRequest> entityChangeEvent) {
        FriendRequest deletedRequest = entityChangeEvent.getOldData();
        if (deletedRequest.getUser1().equals(currentUser.getID()) ||
                deletedRequest.getUser2().equals(currentUser.getID())) {
            friendRequests.remove(deletedRequest);
        }
    }

    @FXML
    public void initialize() {
        reqListView.setItems(friendRequests);
        reqListView.setCellFactory(_ -> new PendingRequestListCell(currentUser,
                userService,
                friendRequestService,
                network,
                friends,
                friendRequests));
        reqListView.setPrefHeight(Math.min(2, friendRequests.size()) * 50);

        friendRequests.addListener((ListChangeListener<FriendRequest>) _ -> {
            reqListView.setPrefHeight(Math.min(2, friendRequests.size()) * 50);
        });
        friendRequests.addListener((ListChangeListener<FriendRequest>) _ -> {
            reqListView.setCellFactory(_ -> new PendingRequestListCell(currentUser,
                    userService,
                    friendRequestService,
                    network,
                    friends,
                    friendRequests));
        });
    }
}
