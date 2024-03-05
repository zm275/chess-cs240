package dataAccess;

import java.util.List;
import java.util.Map;

import model.GameData;

public interface GameDAO {

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;
    Map<Integer, GameData> getGameDataMap();
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;
}
