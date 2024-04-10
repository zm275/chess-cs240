package serverFacade;

import chess.ChessGame;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebsocketClient extends Endpoint{

    private Session session;

    public WebsocketClient(boolean join, int port, int gameID, ChessGame.TeamColor playerColor, String authToken) throws Exception{
        try {
            // Connect to WebSocket server
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8080/connect");
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    System.out.println(message);
                }
            });

            if (join) {
                // Send JOIN_PLAYER message to server
                sendJoinPlayerMessage(new JoinPlayer(gameID, playerColor, authToken));
            } else {
                sendObservePlayerMessage(new JoinObserver(gameID, authToken));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message from server: " + message);

        // Parse incoming message and process accordingly
        // You'll need to implement the logic to handle different types of messages here
    }

    @OnClose
    public void onClose() {
        System.out.println("WebSocket connection closed.");
    }

    private void sendJoinPlayerMessage(JoinPlayer joinPlayer) throws IOException {
        String joinPlayerMessage = "{\"commandType\":\"JOIN_PLAYER\",\"authToken\":\"yourAuthToken\",\"gameID\":123,\"playerColor\":\"WHITE\"}";
        session.getBasicRemote().sendText(joinPlayerMessage);
    }
    private void sendObservePlayerMessage(JoinObserver joinObserver) {
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}


