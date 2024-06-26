package webSocketMessages.serverMessages;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Objects;

public final class LoadGame extends ServerMessage {
    private final ChessBoard game;
    private final ChessGame.TeamColor color;

    public LoadGame(ChessBoard chessBoard, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = chessBoard;
        this.color = color;
    }

    public ChessBoard chessBoard() {
        return game;
    }
    public ChessGame.TeamColor teamColor(){
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LoadGame) obj;
        return Objects.equals(this.game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }

    @Override
    public String toString() {
        return "LoadGame[" +
                "chessBoard=" + game + ']';
    }

}
