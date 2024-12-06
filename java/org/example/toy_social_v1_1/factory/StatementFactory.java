package org.example.toy_social_v1_1.factory;

import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StatementFactory {
    private static StatementFactory instance;

    private StatementFactory() {}

    public static StatementFactory getInstance() {
        if (instance == null) {
            instance = new StatementFactory();
        }
        return instance;
    }

    public PreparedStatement insertUserStatement(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        return statement;
    }

    public PreparedStatement deleteUserStatement(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
        return statement;
    }

    public PreparedStatement updateUserStatement(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setLong(3, user.getID());
        return statement;
    }

    public PreparedStatement insertFriendshipStatement(PreparedStatement statement, Friendship friendship) throws SQLException {
        statement.setLong(1, friendship.getId1());
        statement.setLong(2, friendship.getId2());
        return statement;
    }

    public PreparedStatement deleteFriendshipStatement(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
        return statement;
    }

    public PreparedStatement updateFriendshipStatement(PreparedStatement statement, Friendship friendship) throws SQLException {
        statement.setLong(1, friendship.getId1());
        statement.setLong(2, friendship.getId2());
        statement.setDate(3, friendship.getSince());
        statement.setLong(4, friendship.getID());
        return statement;
    }

    public PreparedStatement insertFriendReqStatement(PreparedStatement statement, FriendRequest friendReq) throws SQLException {
        statement.setLong(1, friendReq.getUser1());
        statement.setLong(2, friendReq.getUser2());
        statement.setDate(3, friendReq.getSent());
        statement.setBoolean(4, friendReq.getAccepted());
        return statement;
    }

    public PreparedStatement deleteFriendReqStatement(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
        return statement;
    }

    public PreparedStatement updateFriendReqStatement(PreparedStatement statement, FriendRequest friendReq) throws SQLException {
        statement.setLong(1, friendReq.getUser1());
        statement.setLong(2, friendReq.getUser2());
        statement.setDate(3, friendReq.getSent());
        statement.setBoolean(4, friendReq.getAccepted());
        statement.setLong(5, friendReq.getID());
        return statement;
    }

    public PreparedStatement getPendingReqStatement(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
        return statement;
    }

    public PreparedStatement getUsersStartingWithPrefixStatement(PreparedStatement statement, String prefix, String currentUsername) throws SQLException {
        statement.setString(1, prefix + "%");
        statement.setString(2, currentUsername);
        return statement;
    }

    public PreparedStatement getUserFriendsStatement(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
        return statement;
    }

    public PreparedStatement getSentRequestsStatement(PreparedStatement statement, Long userId) throws SQLException {
        statement.setLong(1, userId);
        return statement;
    }

    public PreparedStatement deleteUserFriendshipsStatement(PreparedStatement statement, Long userId) throws SQLException {
        statement.setLong(1, userId);
        statement.setLong(2, userId);
        return statement;
    }

    public PreparedStatement getByUsernameStatement(PreparedStatement statement, String username) throws SQLException {
        statement.setString(1, username);
        return statement;
    }

    public PreparedStatement insertMessageStatement(PreparedStatement statement, Message message) throws SQLException {
        statement.setString(1, message.getText());
        statement.setTimestamp(2, message.getTime());
        statement.setLong(3, message.getFrom().getID());
        if(message.getReply() != null) {
            statement.setLong(4, message.getReply().getID());
        } else {
            statement.setNull(4, Types.BIGINT);
        }
        return statement;
    }

    public PreparedStatement deleteMessageStatement(PreparedStatement statement, Long messageId) throws SQLException {
        statement.setLong(1, messageId);
        return statement;
    }

    public PreparedStatement updateMessageStatement(PreparedStatement statement, Message message) throws SQLException {
        statement.setString(1, message.getText());
        statement.setTimestamp(2, message.getTime());
        statement.setLong(3, message.getID());
        return statement;
    }

    public PreparedStatement getReceiversStatement(PreparedStatement statement, Long messageId) throws SQLException {
        statement.setLong(1, messageId);
        return statement;
    }

    public PreparedStatement insertReceiverStatement(PreparedStatement statement, Long messageId, Long userId) throws SQLException {
        statement.setLong(1, messageId);
        statement.setLong(2, userId);
        return statement;
    }

    public PreparedStatement getConversationStatement(PreparedStatement statement, Long user1Id, Long user2Id) throws SQLException {
        statement.setLong(1, user1Id);
        statement.setLong(2, user2Id);
        statement.setLong(3, user2Id);
        statement.setLong(4, user1Id);
        return statement;
    }

    public PreparedStatement getPagedUserFriendships(PreparedStatement statement, Long userId, int capacity, int start) throws SQLException {
        statement.setLong(1, userId);
        statement.setInt(2, capacity);
        statement.setInt(3, start);
        return statement;
    }
}
