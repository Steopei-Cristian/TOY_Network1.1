package org.example.toy_social_v1_1.repository;


import javafx.collections.ObservableList;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;

import java.util.List;

public interface FriendRequestRepo extends Repo<Long, FriendRequest> {
    Long getLastId();

    List<FriendRequest> getPendingRequests(Long idUser);
    List<FriendRequest> getSentRequests(Long idUser);
}
