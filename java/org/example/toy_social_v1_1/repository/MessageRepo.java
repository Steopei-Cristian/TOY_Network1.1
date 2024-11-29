package org.example.toy_social_v1_1.repository;

import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.dto.MessageDTO;

import java.util.List;

public interface MessageRepo extends Repo<Long, Message> {
    Long getLastId();

    List<MessageDTO> getConversation(Long user1Id, Long user2Id);
    List<List<MessageDTO>> getSplitConversation(User currentUser, User chattingUser);
}
