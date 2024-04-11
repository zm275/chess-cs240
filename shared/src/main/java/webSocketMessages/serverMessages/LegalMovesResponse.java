package webSocketMessages.serverMessages;

import chess.ChessMove;

import java.util.Collection;

public class LegalMovesResponse extends ServerMessage{
    private final Collection<ChessMove> legalMoves;
    public LegalMovesResponse(ServerMessageType type, Collection<ChessMove> legalMoves) {
        super(ServerMessageType.LEGAL_MOVES_RESPONSE);
        this.legalMoves = legalMoves;
    }

    public Collection<ChessMove> getLegalMoves() {
        return legalMoves;
    }
}
