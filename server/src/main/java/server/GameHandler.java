package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import service.AuthenticationService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.List;

public class GameHandler {
    private final GameService gameService;
    private final AuthenticationService authenticationService;
    private final Gson gson;

    public GameHandler() {
        this.gameService = new GameService();
        this.authenticationService = new AuthenticationService();
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

}
