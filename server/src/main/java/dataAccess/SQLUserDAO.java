package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class SQLUserDAO implements UserDAO{
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        //hash the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(userData.password());
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO user (username, password, email) VALUES (?,?,?)")) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3,userData.email());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), 403);
        }
    }

    @Override
    public boolean authenticate(UserData userData) throws DataAccessException {
        String providedUsername = userData.username();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT password FROM user WHERE username = ?")) {
            preparedStatement.setString(1, providedUsername);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                //checks if there is a row in the result set
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    if (encoder.matches(userData.password(),storedPassword)) {
                        return true;
                    }
                    else {
                        throw new DataAccessException("Error: invalid password", 401);
                    }
                } else {
                    throw new DataAccessException("Error: User not found in database", 401);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), 401);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), 401);
        }
    }
}
