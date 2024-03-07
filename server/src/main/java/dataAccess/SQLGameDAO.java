package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLGameDAO implements GameDAO{
    private int nextGameId = 0;

    public int getNextGameId() {
        this.nextGameId += 1;
        return this.nextGameId;
    }
    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()){
            throw new DataAccessException("Blank Game Name", 400);
        }
        ChessGame game = new ChessGame();
        Gson gson = new Gson();
        String chessGameJson = gson.toJson(game); //serialize game
        int gameID = getNextGameId();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES ( ?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, gameID);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, null);
            preparedStatement.setString(4, gameName);
            preparedStatement.setString(5, chessGameJson);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), 403);
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM game WHERE gameID = ?")) {
            preparedStatement.setInt(1, gameID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                //checks if there is a row in the result set
                if (resultSet.next()) {
                    int gameId = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameJson = resultSet.getString("game");
                    //deserialize game
                    Gson gson = new Gson();
                    ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                    return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);

                } else {
                    throw new DataAccessException("Error: Game not found in database", 400);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(),400);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<GameData>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM game")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                //checks if there is a row in the result set
                while (resultSet.next()) {
                    int gameId = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameJson = resultSet.getString("game");
                    //deserialize game
                    Gson gson = new Gson();
                    ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                    games.add(new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(),404);
        }
        return games;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        Gson gson = new Gson();
        String chessGameJson = gson.toJson(gameData.game());
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?")) {
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, chessGameJson);
            preparedStatement.setInt(5, gameData.gameID());

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected == 0) {
                throw new DataAccessException("Error: game not found.", 400);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), 401);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM game")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), 401);
        }
    }
}
