package dataAccess;
import ResponseTypes.DataAccessException;
import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> gameDataMap;
    private int nextGameId = 1;

    public MemoryGameDAO(){
        this.gameDataMap = new HashMap<>();
    }

    public int getNextGameId() {
        this.nextGameId += 1;
        return this.nextGameId;
    }
    @Override
    public int createGame(String gameName) throws DataAccessException {
        //gameId that increments
        if (gameName == null || gameName.isEmpty()){
            throw new DataAccessException("Blank Game Name", 400);
        }
        GameData gameData = new GameData(getNextGameId(),null, null, gameName, new ChessGame());
        gameDataMap.put(gameData.gameID(), gameData);
        return gameData.gameID();

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!gameDataMap.containsKey(gameID)) {
            throw new DataAccessException("Error: Game not found",400);
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
            throw new DataAccessException("Error: Game not found",401);
        }
        // Update the game data
        gameDataMap.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataMap.clear();
    }

    @Override
    public String getUsernameByColor(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        return "WHITE";
    }

}
