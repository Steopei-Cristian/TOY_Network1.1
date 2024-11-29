package org.example.toy_social_v1_1.repository;

import org.example.toy_social_v1_1.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends Repo<Long, User> {
    Long getLastId();
    Optional<User> getByUsername(String username);
    Optional<User> loginRequest(String username, String password);

    List<User> getUsersStartingWith(String prefix, String currentUsername);
}
