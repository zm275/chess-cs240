package webSocketMessages.userCommands;

import chess.ChessPosition;

public class LegalMoves extends UserGameCommand{
    private final ChessPosition position;
    private final Integer gameID;

    public LegalMoves(ChessPosition position, String authToken, int gameID) {
        super(authToken);
        this.position = position;
        this.commandType = CommandType.LEGAL_MOVES;
        this.gameID = gameID;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public Integer getGameID() {
        return gameID;
    }
}
