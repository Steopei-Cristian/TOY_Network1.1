package org.example.toy_social_v1_1.service.network;


import org.example.toy_social_v1_1.domain.entities.Entity;
import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.observer.Observable;

import java.util.List;
import java.util.Optional;

public interface NetworkService {
    int groupCount();
    List<User> maxComp();

    Optional<User> addUser(String username, String password);
    Optional<User> removeUser(Long id);

    Optional<Friendship> addFriend(Long id1, Long id2);
    Optional<Friendship> removeFriend(Long id1, Long id2);

    List<User> getUserFriends(Long id);
}
