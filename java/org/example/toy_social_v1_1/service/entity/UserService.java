package org.example.toy_social_v1_1.service.entity;

import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.repository.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService {
    private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAll() {
        return StreamSupport.stream(userRepo.getAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Long getLastId() {
        return userRepo.getLastId();
    }

    public Optional<User> find(Long id) {
        return userRepo.find(id);
    }

    public Optional<User> add(User user) {
        return userRepo.add(user);
    }

    public Optional<User> delete(Long id) {
        return userRepo.delete(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepo.getByUsername(username);
    }

    public Optional<User> loginRequest(String username, String password) {
        return userRepo.loginRequest(username, password);
    }

    public List<User> getUsersStartingWith(String prefix, String currentUsername) {
        return userRepo.getUsersStartingWith(prefix, currentUsername);
    }
}
