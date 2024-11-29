package org.example.toy_social_v1_1.repository.db_repo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.toy_social_v1_1.domain.entities.FriendRequest;
import org.example.toy_social_v1_1.factory.StatementFactory;
import org.example.toy_social_v1_1.repository.FriendRequestRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepo extends AbstractDBRepo<Long, FriendRequest> implements FriendRequestRepo {
    public FriendRequestDBRepo(String connectionString, String user, String password ) {
        super(connectionString, user, password);
        read();
    }

    public void refreshLastID() {
        try {
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            String lastID_query = "SELECT setval(pg_get_serial_sequence('friend_requests', 'id'), (SELECT MAX(id) FROM friend_requests))";
            ResultSet res = connection.prepareStatement(lastID_query).executeQuery();
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
             ResultSet resultSet = statement.executeQuery("SELECT * FROM friend_requests")) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("user1");
                Long id2 = resultSet.getLong("user2");
                Date sent = resultSet.getDate("sent");
                Boolean accepted = resultSet.getBoolean("accepted");
                FriendRequest temp = new FriendRequest(id1, id2, sent, accepted);
                temp.setID(id);
                super.add(temp);
            }

            refreshLastID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> add(FriendRequest friendReq) {
        System.out.println("PRE ADD LAST ID: " + lastID);
        Optional<FriendRequest> result = super.add(friendReq);
        result.ifPresent(_ -> {
            throw new RuntimeException("Add: Friend request already exists");
        });

        try {
            String sqlScript = "INSERT INTO friend_requests (user1, user2, sent, accepted) VALUES (?, ?, ?, ?)";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().insertFriendReqStatement(statement, friendReq);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            System.out.println("POST ADD LAST ID: " + lastID);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Long id) {
        System.out.println("PRE DELETE LAST ID: " + lastID);
        Optional<FriendRequest> result = super.delete(id);
        if(result.isEmpty())
            throw new RuntimeException("Delete: Id not found");

        try {
            String sqlScript = "DELETE FROM friend_requests WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().deleteFriendReqStatement(statement, id);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            System.out.println("POST DELETE LAST ID: " + lastID);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest friendReq) {
        Optional<FriendRequest> result = super.update(friendReq);
        result.ifPresent(_ -> {
            throw new RuntimeException("Update: Friend request having invalid id");
        });

        try {
            String sqlScript = "UPDATE friend_requests " +
                    "SET user1 = ?, user2 = ?, sent = ?, accepted = ? " +
                    "WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().updateFriendReqStatement(statement, friendReq);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FriendRequest> getPendingRequests(Long idUser) {
        List<FriendRequest> res = new ArrayList<>();

        try {
            String sqlScript = "SELECT * FROM friend_requests WHERE user2 = ? and accepted = false";
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().getPendingReqStatement(statement, idUser);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println("1");
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("user1");
                Long id2 = resultSet.getLong("user2");
                Date sent = resultSet.getDate("sent");
                Boolean accepted = resultSet.getBoolean("accepted");
                FriendRequest temp = new FriendRequest(id1, id2, sent, accepted);
                temp.setID(id);
                res.add(temp);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public List<FriendRequest> getSentRequests(Long userId) {
        List<FriendRequest> res = new ArrayList<>();

        try {
            String sqlScript = "SELECT * FROM friend_requests WHERE user1 = ?";
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().getSentRequestsStatement(statement, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("user1");
                Long id2 = resultSet.getLong("user2");
                Date sent = resultSet.getDate("sent");
                Boolean accepted = resultSet.getBoolean("accepted");
                FriendRequest temp = new FriendRequest(id1, id2, sent, accepted);
                temp.setID(id);
                res.add(temp);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
