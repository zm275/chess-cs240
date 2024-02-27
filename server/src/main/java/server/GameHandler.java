package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import model.UserData;
import service.AuthenticationService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.List;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson;
    public GameHandler() {
        this.gameService = new GameService();
        this.gson = new Gson();
    }

    public Object listGames(Request request, Response response, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        String authToken = request.headers("authorization");
        try {
            memoryAuthDAO.isAuthorized(authToken);
            response.status(200);
            List<GameData> games = gameService.listGames(memoryGameDAO);
            return new ListGamesResponse(true, games).toJson();
        }
        catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new ListGamesResponse(false, e).toJson();
        }
    }
    public Object createGame(Request request, Response response, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        try {
            String authToken = request.headers("authorization");
            JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
            String gameName = jsonObject.get("gameName").getAsString();
            memoryAuthDAO.isAuthorized(authToken);
            int gameID = gameService.createGame(gameName, memoryGameDAO);
            response.status(200);
            return new CreateGameResponse(true, gameID).toJson();
        }
        catch (JsonSyntaxException j) {
            response.status(400);
            return new BadRequestResponse("Error: Bad Request").toJson();
        }
        catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new CreateGameResponse(false, e).toJson();
        }
        catch (Exception any) {
            response.status(400);
            return new BadRequestResponse("Error: Bad Request.").toJson();
        }
    }

    public Object joinGame(Request request, Response response, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        try {
            String authToken = request.headers("authorization");

            //get the json data into string and int that we can use
            JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
            JsonElement playerColorElement = jsonObject.get("playerColor");
            String playerColor = (playerColorElement != null && !playerColorElement.isJsonNull()) ? playerColorElement.getAsString() : "";
            int gameID = jsonObject.get("gameID").getAsInt();

            memoryAuthDAO.isAuthorized(authToken);
            gameService.joinGame(gameID, playerColor,authToken, memoryGameDAO,memoryAuthDAO);
            response.status(200);
            return new JoinGameResponse(true).toJson();
        }
        catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new JoinGameResponse(false, e).toJson();
        }
        catch (JsonSyntaxException j) {
            response.status(400);
            return new BadRequestResponse("Error: Bad Request. Invalid Json").toJson();
        }

    }

}
