package service;

import com.google.gson.JsonSyntaxException;
import ResponseTypes.DataAccessException;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.GameData;

import java.util.List;

public class GameService {
    public List<GameData> listGames(GameDAO gameDAO) throws DataAccessException {
        return gameDAO.listGames();
    }
    public int createGame(String gameName, GameDAO gameDAO) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }
    public void joinGame(int gameID, String playerColor, String authToken, GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException, JsonSyntaxException {
        //find the game
        GameData gameData = gameDAO.getGame(gameID);
        GameData newGameData;
        //find the callers username
        String username = authDAO.getAuth(authToken).username();
        switch (playerColor) {
            case "WHITE" -> {
                //check if white already has a player
                if (gameData.whiteUsername() == null) {
                    newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
                } else throw new DataAccessException("Error: WHITE player already exists", 403);
            }
            case "BLACK" -> {
                if (gameData.blackUsername() == null) {
                    newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                } else throw new DataAccessException("Error: BLACK player already exists", 403);
            }
            case "" -> {
                //This mean that a person is just watching.
                return;
            }
            //json was invalid
            case null, default -> throw new JsonSyntaxException("Error: json not WHITE/BLACK");
        }
        gameDAO.updateGame(newGameData);
    }

}
