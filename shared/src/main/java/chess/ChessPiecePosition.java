package chess;
public class ChessPiecePosition {

    private ChessPiece piece;
    private ChessPosition position;

    public ChessPiecePosition(ChessPiece piece, ChessPosition position) {
        this.piece = piece;
        this.position = position;
    }

    public ChessPosition getPosition(){
        return this.position;
    }
    public ChessPiece getPiece(){
        return this.piece;
    }
}
