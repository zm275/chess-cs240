package dataAccess;

import java.util.List;

import ResponseTypes.DataAccessException;
import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;

    String getUsernameByColor(int gameID, ChessGame.TeamColor color) throws DataAccessException;
}
