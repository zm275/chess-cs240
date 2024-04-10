package serverFacade;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ui.EscapeSequences;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class WebsocketClient extends Endpoint{

    private Session session;
    private final Gson gson = new Gson();

    public WebsocketClient(boolean join, int port, int gameID, ChessGame.TeamColor playerColor, String authToken) throws Exception{
        try {
            // Connect to WebSocket server
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8080/connect");
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                    String messageType = json.get("serverMessageType").getAsString();

                    // Handle different message types
                    switch (messageType) {
                        case "LOAD_GAME":
                            // Handle LOAD_GAME message
                            handleLoadGame(json);
                            break;
                        case "NOTIFICATION":
                            // Handle NOTIFICATION message
                            handleNotification(json);
                            break;
                        case "ERROR":
                            // Handle ERROR message
                            handleError(json);
                            break;
                        default:
                            // Handle unknown message type
                            handleUnknownMessage(json);
                            break;
                    }
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

    private void handleLoadGame(JsonObject json) {
        LoadGame game = gson.fromJson(json, LoadGame.class);
        ChessBoard board = game.chessBoard();
        printBoard(board, game.teamColor());

    }
    public static void printBoard(ChessBoard board, ChessGame.TeamColor color) {
        if (color == WHITE){
            printWhiteOrientation(board);
        }
        else {
            printBlackOrientation(board);
        }
    }
    private static void printBlackBorder() {
        for (int i = 0; i <= 9; i++ ){
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            System.out.print("   ");
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }
    public static void printWhiteOrientation(ChessBoard board) {
        // Draw the chessboard with escape sequences
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(EscapeSequences.moveCursorToLocation(1, 1));

        boolean blackSquare = false;
        for (int row = 0; row <= 9; row++) {
            //This creates the letter row on top and bottom
            if (row == 0 || row == 9) {
                for (int edge_col = 9; edge_col >= 0; edge_col -= 1) {
                    if (edge_col == 0 || edge_col == 9) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                        System.out.print("   ");
                        continue;
                    }
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + mapNumberToChar(edge_col) + " ");
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.println();
                continue;
            }
            for (int col = 0; col <= 9; col++) {
                if (col == 0 || col == 9) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + row + " ");
                    continue;

                }
                if (blackSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                }

                System.out.print(" " + getChessPiece(board, row, col) + " "); // Add spaces around the piece
                blackSquare = !blackSquare;
            }
            blackSquare = !blackSquare;
            System.out.print(EscapeSequences.RESET_BG_COLOR);

            System.out.println();

        }

        // Reset colors to default
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.ERASE_LINE);
    }
    public static void printBlackOrientation(ChessBoard board) {
        // Draw the chessboard with escape sequences
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(EscapeSequences.moveCursorToLocation(1, 1));

        boolean blackSquare = false;
        for (int row = 9; row >= 0; row--) {
            //This creates the letter row on top and bottom
            if (row == 0 || row == 9) {
                for (int edge_col = 0; edge_col <= 9; edge_col += 1) {
                    if (edge_col == 0 || edge_col == 9) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                        System.out.print("   ");
                        continue;
                    }
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + mapNumberToChar(edge_col) + " ");
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.println();
                continue;
            }
            for (int col = 9; col >= 0; col--) {
                if (col == 0 || col == 9) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + row + " ");
                    continue;

                }
                if (blackSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                }

                System.out.print(" " + getChessPiece(board, row, col) + " "); // Add spaces around the piece
                blackSquare = !blackSquare;
            }
            blackSquare = !blackSquare;
            System.out.print(EscapeSequences.RESET_BG_COLOR);

            System.out.println();

        }

        // Reset colors to default
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.ERASE_LINE);
    }
    private void handleNotification(JsonObject json) {
    }

    private void handleError(JsonObject json) {
    }
    private void handleUnknownMessage(JsonObject json) {
    }

    private static String getChessPiece(ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return " ";
        } else {
            String pieceSymbol = String.valueOf(piece);
            if (piece.getTeamColor() == BLACK) {
                return EscapeSequences.SET_TEXT_COLOR_BLUE + pieceSymbol;
            }
            else {
                return EscapeSequences.SET_TEXT_COLOR_RED + pieceSymbol.toUpperCase();
            }

        }
    }
    public static char mapNumberToChar(int number) {
        return (char) ('a' + number - 1);
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
        String joinPlayerMessage = gson.toJson(joinPlayer);
        session.getBasicRemote().sendText(joinPlayerMessage);
    }
    private void sendObservePlayerMessage(JoinObserver joinObserver) {
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}


