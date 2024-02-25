package dataAccess;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> gameDataMap;

    public MemoryGameDAO(){
        this.gameDataMap = new HashMap<>();
    }


    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        if (gameDataMap.containsKey(gameData.gameID())){
            throw new DataAccessException("Error: Game with this ID already exists.");
        }
        gameDataMap.put(gameData.gameID(), gameData);

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!gameDataMap.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        return gameDataMap.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(gameDataMap.values());
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (!gameDataMap.containsKey(gameData.gameID())) {
            throw new DataAccessException("Error: Game not found");
        }
        // Update the game data
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        if (!gameDataMap.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found");
        }
        gameDataMap.remove(gameID);
    }
}
