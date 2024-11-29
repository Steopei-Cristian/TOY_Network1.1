package org.example.toy_social_v1_1.repository.db_repo;

import org.example.toy_social_v1_1.factory.StatementFactory;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.repository.UserRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class UserDBRepo extends AbstractDBRepo<Long, User> implements UserRepo {

    public UserDBRepo(String sqlConnection, String userName, String password) {
        super(sqlConnection, userName, password);
        read();
    }

    public void refreshLastID() {
        try {
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            String lastID_query = "SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users))";
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String passwordFromDb = resultSet.getString("password");
                User temp = new User(username, passwordFromDb);
                temp.setID(id);
                super.add(temp);
            }

            refreshLastID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> add(User user) {
        Optional<User> result = super.add(user);
        result.ifPresent(existingUser -> {
            throw new RuntimeException("Add: User already exists");
        });

        try {
            String sqlScript = "INSERT INTO users (username, password) VALUES (?, ?)";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().insertUserStatement(statement, user);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        Optional<User> result = super.delete(id);
        if(result.isEmpty())
            throw new RuntimeException("Delete: Id not found");

        try {
            String sqlScript = "DELETE FROM users WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().deleteUserStatement(statement, id);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> result = super.update(user);
        result.ifPresent(existingUser -> {
            throw new RuntimeException("Update: User having invalid id");
        });

        try {
            String sqlScript = "UPDATE users " +
                    "SET username = ?, password = ? " +
                    "WHERE id = ?";
            Connection connection = DriverManager.getConnection(connectionString, this.user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().updateUserStatement(statement, user);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            refreshLastID();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> getByUsername(String inputUsername) {
        List<User> res = new ArrayList<>();

        try {
            String sqlScript = "SELECT * FROM users WHERE username = ?";
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().getByUsernameStatement(statement, inputUsername);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String passwordFromDb = resultSet.getString("password");
                User temp = new User(username, passwordFromDb);
                temp.setID(id);
                res.add(temp);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res.isEmpty() ? Optional.empty() : Optional.of(res.getFirst());
    }

    @Override
    public Optional<User> loginRequest(String username, String password) {
        Optional<User> user = getByUsername(username);
        if(user.isPresent() && user.get().getPassword().equals(password))
            return user;
        return Optional.empty();
    }

    @Override
    public List<User> getUsersStartingWith(String prefix, String currentUsername) {
        List<User> res = new ArrayList<>();

        try {
            String sqlScript = "SELECT * FROM users WHERE username LIKE ? AND username != ?";
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            PreparedStatement statement = connection.prepareStatement(sqlScript);
            statement = StatementFactory.getInstance().getUsersStartingWithPrefixStatement(statement, prefix, currentUsername);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String passwordFromDb = resultSet.getString("password");
                User temp = new User(username, passwordFromDb);
                temp.setID(id);
                res.add(temp);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
