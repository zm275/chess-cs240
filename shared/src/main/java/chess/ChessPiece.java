package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", color=" + color +
                '}';
    }

    private final ChessGame.TeamColor color;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

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
        //get the piecetype, so we can decide what moves are valid
        switch (this.type) {
            case KING:
                addSingleHorizontalAndVerticalMoves(board, myPosition, moves);
                addSingleDiagonalMoves(board, myPosition, moves);
                break;
            case QUEEN:
                addDiagonalMoves(board,myPosition,moves);
                addHorizontalAndVerticalMoves(board, myPosition, moves);
                break;
            case BISHOP:
                addDiagonalMoves(board,myPosition,moves);
                break;
            case KNIGHT:
                addKnightMoves(board, myPosition, moves);
                break;
            case ROOK:
                addHorizontalAndVerticalMoves(board, myPosition, moves);
                break;
            case PAWN:
                addPawnMoves(board,myPosition,moves);
                break;
        }

        
        return moves;
    }
    public void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        List<int[]> directions = new ArrayList<>();
        List<int[]> diagonals = new ArrayList<>();

        if (myColor == ChessGame.TeamColor.WHITE){
            if (myPosition.getRow() == 2) {
                //first check if 1,0 is occupied
                if (board.isValidPosition(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()))){
                    if (!board.isOccupied(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()))){
                        //if 1, 0 is not occupied add 2,0
                        directions.add(new int[]{2,0});
                    }
                }
            }
            directions.add(new int[]{1,0});
            diagonals.add(new int[]{1,1});
            diagonals.add(new int[]{1,-1});

        } else if (myColor == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7) {
                //first check if -1,0 is occupied
                if (board.isValidPosition(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()))){
                    if (!board.isOccupied(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()))){
                        //if -1, 0 is not occupied add -2,0
                        directions.add(new int[]{-2,0});
                    }
                }
            }

            directions.add(new int[]{-1,0});
            diagonals.add(new int[]{-1,1});
            diagonals.add(new int[]{-1,-1});
        }
        //check the diagonals if there is an opposing piece to overtake
        for (int[] diagonal : diagonals){
            int row = myPosition.getRow() + diagonal[0];
            int col = myPosition.getColumn() + diagonal[1];

            if (board.isValidPosition(new ChessPosition(row, col))){
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor) {
                        addMovesPawn(myPosition, moves, row, col);
                    }
                }
            }
        }


        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            validIfStatement:
            if (board.isValidPosition(new ChessPosition(row, col))){
                if (board.isOccupied(new ChessPosition(row, col))) {
                    break validIfStatement;
                }
                addMovesPawn(myPosition, moves, row, col);
            }
        }


    }

    private void addMovesPawn(ChessPosition myPosition, Collection<ChessMove> moves, int row, int col) {
        if (row == 8 || row == 1){
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.ROOK));
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), PieceType.BISHOP));
        }
        else {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
    }

    public void addSingleHorizontalAndVerticalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        List<int[]> directions = List.of(
                new int[]{1, 0},
                new int[]{-1, 0},
                new int[]{0, 1},
                new int[]{0, -1}
        );

        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            validIfStatement:
            if (board.isValidPosition(new ChessPosition(row, col))){
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break validIfStatement;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }
    }

    public void addHorizontalAndVerticalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        List<int[]> directions = List.of(
                new int[]{1, 0},
                new int[]{-1, 0},
                new int[]{0, 1},
                new int[]{0, -1}
        );

        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            whileLoop:
            while (board.isValidPosition(new ChessPosition(row, col))){
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break whileLoop;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));

                row += direction[0];
                col += direction[1];
            }

        }
    }
    public void addSingleDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        List<int[]> directions = List.of(
                new int[]{-1, -1},
                new int[]{-1, 1},
                new int[]{1, -1},
                new int[]{1, 1}
        );

        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            validIfStatement:
            if (board.isValidPosition(new ChessPosition(row, col))){
                //if position is occupied by a friendly piece
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));
                    }
                    break validIfStatement;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));
            }
        }
    }
    public void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        //get myPiece color
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        List<int[]> directions = List.of(
                new int[]{-1, -1},
                new int[]{-1, 1},
                new int[]{1, -1},
                new int[]{1, 1}
        );

        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            whileLoop:
            while (board.isValidPosition(new ChessPosition(row, col))){
                //if position is occupied by a friendly piece
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));
                    }
                    break whileLoop;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));

                row += direction[0];
                col += direction[1];
            }
        }
    }

    public void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves){
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        List<int[]> directions = List.of(
                new int[]{1, 2},
                new int[]{1, -2},
                new int[]{2, 1},
                new int[]{2, -1},
                new int[]{-1, 2},
                new int[]{-1, -2},
                new int[]{-2, 1},
                new int[]{-2, -1}
                );

        for (int[] direction : directions){
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            validIfStatement:
            if (board.isValidPosition(new ChessPosition(row, col))){
                //if position is occupied by a friendly piece
                if (board.isOccupied(new ChessPosition(row, col))){
                    if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != myColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));
                    }
                    break validIfStatement;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row,col), null));
            }
        }
    }
}

