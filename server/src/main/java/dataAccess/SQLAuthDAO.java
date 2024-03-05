package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        // Insert the authentication token and username into the database
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, authToken);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 403);
        }
        return new AuthData(authToken, username);
    }
    //returns authdata
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String username;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT username FROM auth WHERE authToken = ?")) {
            preparedStatement.setString(1, authToken);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                //checks if there is a row in the result set
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                } else {
                    throw new DataAccessException("Error: User not found in database", 404);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(),404);
        }
        return new AuthData(authToken, username);
    }

        @Override
    public Map<String, AuthData> getAuthDataMap() {
        return null;
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM auth")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 401);
        }
    }
}
