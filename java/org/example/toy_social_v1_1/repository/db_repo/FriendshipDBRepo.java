package org.example.toy_social_v1_1.repository.db_repo;

import org.example.toy_social_v1_1.factory.StatementFactory;
import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.repository.FriendshipRepo;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepo extends AbstractDBRepo<Long, Friendship> implements FriendshipRepo {

    public FriendshipDBRepo(String connectionString, String user, String password ) {
        super(connectionString, user, password);
        read();
    }

    public void refreshLastID() {
        try {
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            String lastID_query = "SELECT setval(pg_get_serial_sequence('friendships', 'id'), (SELECT MAX(id) FROM friendships))";
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
             ResultSet resultSet = statement.executeQuery("SELECT * FROM friendships")) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Date since = resultSet.getDate("since");
                Friendship temp = new Friendship(id1, id2, since);
                temp.setID(id);
                super.add(temp);
            }

            refreshLastID();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> add(Friendship friendship) {
        Optional<Friendship> result = super.add(friendship);
        result.ifPresent(existingFriendship -> {
            throw new RuntimeException("Add: Friendship already exists");
        });

        try {
            String sqlScript = "INSERT INTO friendships (id1, id2) VALUES (?, ?)";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().insertFriendshipStatement(statement, friendship);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Long id) {
        Optional<Friendship> result = super.delete(id);
        if(result.isEmpty())
            throw new RuntimeException("Delete: Id not found");

        try {
            String sqlScript = "DELETE FROM friendships WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().deleteFriendshipStatement(statement, id);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        Optional<Friendship> result = super.update(friendship);
        result.ifPresent(existingFriendship -> {
            throw new RuntimeException("Update: Friendship having invalid id");
        });

        try {
            String sqlScript = "UPDATE friendships " +
                    "SET id1 = ?, id2 = ?, since = ? " +
                    "WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().updateFriendshipStatement(statement, friendship);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserFriendships(Long userId) {
        try {
            String sqlScript = "DELETE FROM friendships " +
                        "WHERE id1 = ? OR id2 = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().deleteUserFriendshipsStatement(statement, userId);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> getFriendshipByUsers(Long id1, Long id2){
        Iterable<Friendship> friendships = elems.values();
        for(Friendship f : friendships){
            if(f.getId1().equals(id1) && f.getId2().equals(id2))
                return Optional.of(f);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> removeFriendship(Long userID, Long friendID) {
        Optional<Friendship> temp = getFriendshipByUsers(userID, friendID);
        temp.ifPresent(friendship -> delete(friendship.getID()));
        return temp;
    }

    @Override
    public List<Long> getUserFriends(Long userID) {
        List<Long> res = new ArrayList<>();

        try {
            String sqlScript = "SELECT id2 FROM friendships WHERE id1 = ?";
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().getUserFriendsStatement(statement, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long idFriend = resultSet.getLong("id2");
                res.add(idFriend);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private int countUserFriendships(Connection connection, Long userId) throws SQLException {
        try {
            String sql = "select count(*) as count from friendships where id1 = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement = StatementFactory.getInstance().getUserFriendsStatement(statement, userId);
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfFriends = 0;
                if (result.next()) {
                    totalNumberOfFriends = result.getInt("count");
                }
                return totalNumberOfFriends;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Friendship> getPagedUserFriendships(Connection connection, Pageable pageable, Long userId) {
        List<Friendship> friendshipsOnPage = new ArrayList<>();
        try {
            String sql = "select * from friendships where id1 = ? limit ? offset ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement = StatementFactory.getInstance().getPagedUserFriendships(statement, userId, pageable.getPageSize(), pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    Long id1 = resultSet.getLong("id1");
                    Long id2 = resultSet.getLong("id2");
                    Date since = resultSet.getDate("since");
                    Friendship friendship = new Friendship(id1, id2, since);
                    friendship.setID(id);
                    friendshipsOnPage.add(friendship);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendshipsOnPage;
    }

    @Override
    public Page<Friendship> findAllUserFriendshipsOnPage(Pageable pageable, Long userId) {
        try (Connection connection = DriverManager.getConnection(connectionString, user, password)) {
            int totalNumberOfUserFriendships = countUserFriendships(connection, userId);
            List<Friendship> friendshipsOnPage;
            if (totalNumberOfUserFriendships > 0) {
                friendshipsOnPage = getPagedUserFriendships(connection, pageable, userId);
            } else {
                friendshipsOnPage = new ArrayList<>();
            }
            return new Page<>(friendshipsOnPage, totalNumberOfUserFriendships);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return findAllUserFriendshipsOnPage(pageable, null);
    }
}
