package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

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
            throw new DataAccessException(e.getMessage(), 401);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public Map<String, UserData> getUserDataMap() {
        return null;
    }

    @Override
    public boolean authenticate(UserData userData) throws DataAccessException {
        return false;
    }

    @Override
    public void updateUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void deleteUser(String username) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 401);
        }
    }
}
