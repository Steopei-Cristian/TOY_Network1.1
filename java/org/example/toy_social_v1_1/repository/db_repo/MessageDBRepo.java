package org.example.toy_social_v1_1.repository.db_repo;

import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.dto.MessageDTO;
import org.example.toy_social_v1_1.factory.StatementFactory;
import org.example.toy_social_v1_1.repository.MessageRepo;
import org.example.toy_social_v1_1.repository.UserRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepo extends AbstractDBRepo<Long, Message> implements MessageRepo {
    private UserRepo userRepo;

    public MessageDBRepo(String sqlConnection, String userName, String password,
                         UserRepo userRepo) {
        super(sqlConnection, userName, password);
        this.userRepo = userRepo;
        read();
    }

    public void refreshLastID() {
        try {
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            String lastIdMessageQuery = "SELECT setval(pg_get_serial_sequence('messages', 'id'), (SELECT MAX(id) FROM messages))";
            ResultSet res = connection.prepareStatement(lastIdMessageQuery).executeQuery();
            if (res.next()) {
                lastID = res.getLong("setval");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getLastId() {
        return lastID;
    }

    @Override
    protected void read() {
        try (Connection connection = DriverManager.getConnection(connectionString, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM messages")) {

            while (resultSet.next()) {
                Long messageId = resultSet.getLong("id");
                String text = resultSet.getString("text");
                Timestamp time = resultSet.getTimestamp("time");
                Long from_id = resultSet.getLong("from_id");
                Long reply_id = resultSet.getLong("reply_id");
                if(userRepo.find(from_id).isPresent()) {
                    User from = userRepo.find(from_id).get();
                    Message reply = null;
                    if (find(reply_id).isPresent()) {
                        reply = find(reply_id).get();
                    }
                    List<User> to = getReceivers(messageId, connection);
                    Message newMessage = new Message(from, to, text, time, reply);
                    newMessage.setID(messageId);
                    super.add(newMessage);
                }
            }

            refreshLastID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> getReceivers(Long messageId, Connection connection) throws SQLException {
        List<User> receivers = new ArrayList<>();
        String getReceiversQuery = "SELECT to_id FROM receivers WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(getReceiversQuery);
        statement = StatementFactory.getInstance().getReceiversStatement(statement, messageId);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Long userId = resultSet.getLong("to_id");
            userRepo.find(userId).ifPresent(receivers::add);
        }

        return receivers;
    }

    @Override
    public Optional<Message> add(Message message) {
        Optional<Message> result = super.add(message);
        result.ifPresent(existingMessage -> {
            throw new RuntimeException("Add: Message already exists");
        });

        try {
            String insertMessageScript = "INSERT INTO messages (text, time, from_id, reply_id) VALUES (?, ?, ?, ?)";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(insertMessageScript);
            statement = StatementFactory.getInstance().insertMessageStatement(statement, message);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected in messages");

            refreshLastID();

            String insertReceiversScrip = "INSERT INTO receivers (message_id, to_id) VALUES (?, ?)";
            statement = connection.prepareStatement(insertReceiversScrip);
            int rowCount = 0;
            for(User user : message.getTo()) {
                statement = StatementFactory.getInstance().insertReceiverStatement(statement, message.getID(), user.getID());
                rowCount += statement.executeUpdate();
            }
            System.out.println(rowCount + " rows affected in receivers");

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long messageId) {
        Optional<Message> result = super.delete(messageId);
        if(result.isEmpty())
            throw new RuntimeException("Delete: Id not found");

        try {
            String sqlScript = "DELETE FROM messages WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().deleteMessageStatement(statement, messageId);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message message) {
        Optional<Message> result = super.update(message);
        result.ifPresent(existingMessage -> {
            throw new RuntimeException("Update: Message having invalid id");
        });

        try {
            String sqlScript = "UPDATE messages " +
                    "SET text = ?, time = ? " +
                    "WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().updateMessageStatement(statement, message);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MessageDTO> getConversation(Long user1Id, Long user2Id) {
        List<MessageDTO> conversation = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            String conversationQuery = "SELECT * FROM messages m " +
                    "JOIN receivers r ON m.id = r.message_id " +
                    "WHERE (m.from_id = ? AND r.to_id = ?) OR (m.from_id = ? AND r.to_id = ?) " +
                    "ORDER BY m.time";
            PreparedStatement statement = connection.prepareStatement(conversationQuery);
            statement = StatementFactory.getInstance().getConversationStatement(statement, user1Id, user2Id);
            ResultSet res = statement.executeQuery();

            while(res.next()) {
                Long from_id = res.getLong("from_id");
                Optional<User> from = userRepo.find(from_id);
                if(from.isPresent()) {
                    String text = res.getString("text");
                    Timestamp time = res.getTimestamp("time");
                    Long messageId = res.getLong("id");
                    Long reply_id = res.getLong("reply_id");
                    find(reply_id).ifPresentOrElse(
                            (replyMessage) -> {
                                MessageDTO temp = new MessageDTO(messageId, from.get(),
                                        text, time, replyMessage);
                                conversation.add(temp);
                            },
                            () -> {
                                MessageDTO temp = new MessageDTO(messageId, from.get(),
                                        text, time, null);
                                conversation.add(temp);
                            }
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return conversation;
    }

    @Override
    public List<List<MessageDTO>> getSplitConversation(User currentUser, User chattingUser) {
        List<MessageDTO> conversation = getConversation(currentUser.getID(), chattingUser.getID());
        conversation.forEach(System.out::println);

        List<List<MessageDTO>> splittedConversation = new ArrayList<>();
        splittedConversation.add(new ArrayList<>());
        splittedConversation.add(new ArrayList<>());

        conversation.forEach(messageDTO -> {
            if(messageDTO.getFrom().equals(currentUser)) {
                splittedConversation.getFirst().add(messageDTO);
            } else {
                splittedConversation.getLast().add(messageDTO);
            }
        });

        return splittedConversation;
    }
}
