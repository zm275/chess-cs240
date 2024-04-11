package server;

import ResponseTypes.DataAccessException;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.Leave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@WebSocket
public class WSServer {
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();
    private final ConnectionManager connections = new ConnectionManager();

    public WSServer() {
        gameService = new GameService();
        this.userService = new UserService();
        gameDAO = new SQLGameDAO();
        this.userDAO = new SQLUserDAO();
        this.authDAO = new SQLAuthDAO();
    }
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("WebSocket connected: " + session.getRemoteAddress().getHostName());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message from client: " + message);
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String commandType = json.get("commandType").getAsString();
        switch (commandType) {
            case "JOIN_PLAYER":
                // Handle JOIN_PLAYER command
                handleJoinPlayer(session, json);
                break;
            case "JOIN_OBSERVER":
                // Handle JOIN_OBSERVER command
                handleJoinObserver(session, json);
                break;
            case "MAKE_MOVE":
                // Handle MAKE_MOVE command
                handleMakeMove(session, json);
                break;
            case "LEAVE":
                // Handle LEAVE command
                handleLeave(session, json);
                break;
            case "RESIGN":
                // Handle RESIGN command
                handleResign(session, json);
                break;
            default:
                // Handle unknown command
                handleUnknownCommand(session, json);
                break;
        }


    }

    private void handleJoinPlayer(Session session, JsonObject json) throws DataAccessException, IOException {
        int gameID = json.get("gameID").getAsInt();
        //check if game exists
        try {
            gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: Game not found."));
            session.getRemote().sendString(errorJson);
            return;
        }
        String visitorAuthToken = json.get("authToken").getAsString();

        ChessGame.TeamColor color = gson.fromJson(json.get("playerColor"), ChessGame.TeamColor.class);
        // Check if the chosen color is available
        String colorUsername = gameDAO.getUsernameByColor(gameID, color);
        try {
            String userName = authDAO.getAuth(visitorAuthToken).username();
            if (!Objects.equals(colorUsername, userName)) {
                if (colorUsername == null) {
                    String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: That color is empty."));
                    session.getRemote().sendString(errorJson);
                    return;
                }
                // Color is not available, send an error message to the client
                String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: That color is already taken."));
                session.getRemote().sendString(errorJson);
                return;
            }
            ChessBoard board = gameService.getChessBoard(gameID, gameDAO);
            LoadGame game = new LoadGame(board, color);
            String LoadGameJson = gson.toJson(game);
            session.getRemote().sendString(LoadGameJson);
            connections.add(userName, session);
            //send notification out to everyone that is playing or watching this game.
            Notification notification = new Notification(userName + " has joined game # " + gameID + " as color " + color + ".");
            connections.broadcast(userName, notification);
        } catch (DataAccessException e){
            String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: Invalid authToken."));
            session.getRemote().sendString(errorJson);
            return;
        }

    }
    private void handleJoinObserver(Session session, JsonObject json) throws IOException, DataAccessException {
        int gameID = json.get("gameID").getAsInt();
        //check if game exists
        try {
            gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: Game not found."));
            session.getRemote().sendString(errorJson);
            return;
        }
        String visitorAuthToken = json.get("authToken").getAsString();
        try {
            String userName = authDAO.getAuth(visitorAuthToken).username();
            ChessBoard board = gameService.getChessBoard(gameID, gameDAO);
            LoadGame game = new LoadGame(board, null);
            String LoadGameJson = gson.toJson(game);
            session.getRemote().sendString(LoadGameJson);
            connections.add(userName, session);
            //send notification out to everyone that is playing or watching this game.
            Notification notification = new Notification(userName + " has joined game # " + gameID + "as a spectator");
            connections.broadcast(userName, notification);
        } catch (DataAccessException e) {
            String errorJson = gson.toJson(new webSocketMessages.serverMessages.Error("Error: AuthToken invalid."));
            session.getRemote().sendString(errorJson);
            return;
        }


    }
    private void handleMakeMove(Session session, JsonObject json) {
    }
    private void handleLeave(Session session, JsonObject json) throws DataAccessException, IOException {
        Leave leave = gson.fromJson(json, Leave.class);
        String userName = authDAO.getAuth(leave.getAuthString()).username();
        GameData gameData = gameDAO.getGame(leave.getGameID());
        GameData newGameData;
        if (Objects.equals(gameData.whiteUsername(), userName)){
            newGameData = new GameData(leave.getGameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            newGameData = new GameData(leave.getGameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }
        gameDAO.updateGame(newGameData);
        connections.remove(userName);
        connections.broadcast(leave.getAuthString(), new Notification(userName + "has left the game."));
    }
    private void handleResign(Session session, JsonObject json) {
    }
    private void handleUnknownCommand(Session session, JsonObject json) {
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }
}

