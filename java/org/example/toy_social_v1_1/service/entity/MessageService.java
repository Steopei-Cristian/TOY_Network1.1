package org.example.toy_social_v1_1.service.entity;

import javafx.collections.FXCollections;
import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.dto.MessageDTO;
import org.example.toy_social_v1_1.repository.MessageRepo;
import org.example.toy_social_v1_1.util.event.EntityChangeEvent;
import org.example.toy_social_v1_1.util.event.EntityChangeEventType;
import org.example.toy_social_v1_1.util.observer.Observable;
import org.example.toy_social_v1_1.util.observer.Observer;

import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<EntityChangeEvent<?>> {
    private MessageRepo messageRepo;
    private List<Observer<EntityChangeEvent<?>>> observers = FXCollections.observableArrayList();

    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
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

    public Long getLastId() {
        return messageRepo.getLastId();
    }

    public Optional<Message> find(Long messageId) {
        return messageRepo.find(messageId);
    }

    public Optional<Message> addMessage(Message message) {
        message.setID(getLastId() + 1);
        Optional<Message> addedMessage = messageRepo.add(message);
        if(addedMessage.isEmpty()) {
            notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.ADD, message));
        }
        return addedMessage;
    }

    public List<MessageDTO> getConversation(Long user1Id, Long user2Id) {
        return messageRepo.getConversation(user1Id, user2Id);
    }

    public List<List<MessageDTO>> getSplitConversation(User currentUser, User chattingUser) {
        return messageRepo.getSplitConversation(currentUser, chattingUser);
    }
}
