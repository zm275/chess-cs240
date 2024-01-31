package chess;

import java.util.Objects;

public class ChessPiecePosition {

    private ChessPiece piece;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiecePosition that = (ChessPiecePosition) o;
        return Objects.equals(piece, that.piece) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, position);
    }

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
