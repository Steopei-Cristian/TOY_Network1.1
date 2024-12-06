package org.example.toy_social_v1_1.service.entity;

import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.repository.FriendshipRepo;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.event.EntityChangeEventType;
import org.example.toy_social_v1_1.util.observer.Observable;
import org.example.toy_social_v1_1.util.observer.Observer;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipService implements Observable<EntityChangeEvent<?>> {
    private FriendshipRepo friendshipRepo;
    private List<Observer<EntityChangeEvent<?>>> observers = new ArrayList<>();

    public FriendshipService(FriendshipRepo friendshipRepo) {
        this.friendshipRepo = friendshipRepo;
    }

    @Override
    public void addObserver(Observer<EntityChangeEvent<?>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent<?>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EntityChangeEvent<?> event) {
        observers.forEach(observer -> observer.update(event));
    }

    public Iterable<Friendship> getAll() {
        return friendshipRepo.getAll();
    }

    public Long getLastId() {
        return friendshipRepo.getLastId();
    }

    public Optional<Friendship> add(Friendship friendship) {
        Optional<Friendship> addedFriendship = friendshipRepo.add(friendship);
        if(addedFriendship.isEmpty())
            notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.ADD, friendship));
        return addedFriendship;
    }

    public Optional<Friendship> removeFriendship(Long userId1, Long userId2) {
        Optional<Friendship> removedFriendship = friendshipRepo.removeFriendship(userId1, userId2);
        removedFriendship.ifPresent(friendship -> notifyObservers(new EntityChangeEvent<>(
                EntityChangeEventType.DELETE, null, friendship)));
        return removedFriendship;
    }

    public void removeUserFriendships(Long userId) {
        friendshipRepo.removeUserFriendships(userId);
    }

    public Optional<Friendship> getFriendship(Long userId1, Long userId2) {
        return friendshipRepo.getFriendshipByUsers(userId1, userId2);
    }

    public List<Long> getUserFriends(Long userId) {
        return friendshipRepo.getUserFriends(userId);
    }

    public Page<Friendship> findAllUserFriendshipsOnPage(Pageable pageable, Long userId) {
        return friendshipRepo.findAllUserFriendshipsOnPage(pageable, userId);
    }
}
