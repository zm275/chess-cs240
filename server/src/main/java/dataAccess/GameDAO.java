package dataAccess;

import java.util.List;
import model.GameData;

public interface GameDAO {

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}
