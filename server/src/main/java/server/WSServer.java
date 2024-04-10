package server;

import ResponseTypes.DataAccessException;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.GameDAO;
import dataAccess.SQLGameDAO;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import webSocketMessages.serverMessages.LoadGame;

import java.io.IOException;

@WebSocket
public class WSServer {
    private final GameDAO gameDAO;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public WSServer() {
        gameService = new GameService();
        gameDAO = new SQLGameDAO();
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
        ChessGame.TeamColor color = gson.fromJson(json.get("playerColor"), ChessGame.TeamColor.class);
        ChessBoard board = gameService.getChessBoard(gameID, gameDAO);
        LoadGame game = new LoadGame(board, color);
        String LoadGameJson = gson.toJson(game);
        session.getRemote().sendString(LoadGameJson);
    }
    private void handleJoinObserver(Session session, JsonObject json) {
    }private void handleMakeMove(Session session, JsonObject json) {
    }private void handleLeave(Session session, JsonObject json) {
    }private void handleResign(Session session, JsonObject json) {
    }private void handleUnknownCommand(Session session, JsonObject json) {
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

