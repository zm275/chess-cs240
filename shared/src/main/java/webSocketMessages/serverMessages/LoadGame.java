package webSocketMessages.serverMessages;

import chess.ChessBoard;

import java.util.Objects;

public final class LoadGame extends ServerMessage {
    private final ChessBoard chessBoard;

    public LoadGame(ChessBoard chessBoard) {
        super(ServerMessageType.LOAD_GAME);
        this.chessBoard = chessBoard;
    }

    public ChessBoard chessBoard() {
        return chessBoard;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LoadGame) obj;
        return Objects.equals(this.chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessBoard);
    }

    @Override
    public String toString() {
        return "LoadGame[" +
                "chessBoard=" + chessBoard + ']';
    }

}
