package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor color;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //get the piecetype, so we can decide what moves are valid
        switch (this.type) {
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
                for (int i = 1; i <= 8; i++) {
                    for (int j = 1; j <= 8; j++) {
                        //check if the move is diagonal from the start
                        if ((Math.abs(row - i)) == (Math.abs(col - j))) {
                            //if the end position != start position, i.e. the piece needs to move
                            if (row != i && col != j){
                                //add the move to teh collection
                                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                            }
                        }
                    }

                }
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }

        
        return moves;
    }
}
