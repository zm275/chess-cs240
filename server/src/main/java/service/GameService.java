package service;

import chess.ChessGame;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Objects;

public class GameService {
    public List<GameData> listGames(MemoryGameDAO memoryGameDAO) throws DataAccessException {
        return memoryGameDAO.listGames();
    }
    public int createGame(String gameName, MemoryGameDAO memoryGameDAO) throws DataAccessException {
        return memoryGameDAO.createGame(gameName);
    }
    public void joinGame(int gameID, String playerColor, String authToken, MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO) throws DataAccessException, JsonSyntaxException {
        //find the game
        GameData gameData = memoryGameDAO.getGame(gameID);
        GameData newGameData;
        //find the callers username
        String username = memoryAuthDAO.getAuth(authToken).username();
        if (Objects.equals(playerColor, "WHITE")) {
            //check if white already has a player
            if (gameData.whiteUsername() == null) {
                newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
            else throw new DataAccessException("Error: WHITE player already exists", 403);
        }
        else if(Objects.equals(playerColor, "BLACK")) {
                if (gameData.blackUsername() == null) {
                    newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                }
                else throw new DataAccessException("Error: BLACK player already exists", 403);
        }
        else if(Objects.equals(playerColor, "")){
            return;
        }
        //json was invalid
        else throw new JsonSyntaxException("Error: json not WHITE/BLACK");

        memoryGameDAO.updateGame(newGameData);
    }

}
