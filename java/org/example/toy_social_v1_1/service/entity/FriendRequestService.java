package org.example.toy_social_v1_1.service.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.repository.FriendRequestRepo;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.event.EntityChangeEventType;
import org.example.toy_social_v1_1.util.observer.Observable;
import org.example.toy_social_v1_1.util.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendRequestService implements Observable<EntityChangeEvent<FriendRequest>> {
    private FriendRequestRepo friendRequestRepo;
    private List<Observer<EntityChangeEvent<FriendRequest>>> observers = new ArrayList<>();

    public FriendRequestService(FriendRequestRepo friendshipRequestRepo) {
        this.friendRequestRepo = friendshipRequestRepo;
    }

    @Override
    public void addObserver(Observer<EntityChangeEvent<FriendRequest>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent<FriendRequest>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EntityChangeEvent<FriendRequest> event) {
        observers.forEach(observer -> observer.update(event));
    }

    public Optional<FriendRequest> addRequest(FriendRequest request) {
        Optional<FriendRequest> addedRequest = friendRequestRepo.add(request);
        if(addedRequest.isEmpty())
            notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.ADD, request));
        return addedRequest;
    }

    public Optional<FriendRequest> deleteRequest(Long requestId) {
        Optional<FriendRequest> deletedRequest = friendRequestRepo.delete(requestId);
        deletedRequest.ifPresent(request -> notifyObservers(new EntityChangeEvent<>(
                EntityChangeEventType.DELETE, null, request)));
        return deletedRequest;
    }

    public Optional<FriendRequest> updateRequest(FriendRequest newState) {
        Optional<FriendRequest> oldRequest = friendRequestRepo.find(newState.getID());
        return oldRequest.flatMap(old -> {
            Optional<FriendRequest> updatedRequest = friendRequestRepo.update(newState);
            if(updatedRequest.isEmpty())
                notifyObservers(new EntityChangeEvent<>(
                        EntityChangeEventType.UPDATE, newState, old));
            return updatedRequest;
        });
    }

    public List<FriendRequest> getPendingRequests(Long userId) {
        return friendRequestRepo.getPendingRequests(userId);
    }

    public List<FriendRequest> getSentRequests(Long userId) {
        return friendRequestRepo.getSentRequests(userId);
    }

    public Optional<FriendRequest> alreadySentRequest(Long senderId, Long recipientId) {
        for(FriendRequest request : getSentRequests(senderId)) {
            if(request.getUser2().equals(recipientId))
                return Optional.of(request);
        }
        return Optional.empty();
    }

    public Long getLastId(){
        return friendRequestRepo.getLastId();
    }
}
