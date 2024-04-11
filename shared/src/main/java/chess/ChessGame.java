package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private boolean gameOver = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    public ChessGame() {

    }
    public void setGameOver() {gameOver = true;}
    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    private static ChessPiece[][] deepCopy(ChessPiece[][] original) {
        if (original == null) {
            return null;
        }

        int rows = original.length;
        int cols = original[0].length;

        ChessPiece[][] copy = new ChessPiece[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (original[i][j] != null) {
                    copy[i][j] = original[i][j].copy(); // Assuming ChessPiece has a clone method
                }
            }
        }

        return copy;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessPiece tempPiece = this.testMove(move);
            if (this.isInCheck(piece.getTeamColor())){
                this.undoTestMove(move, tempPiece);
                continue;
            }
            validMoves.add(move);
            this.undoTestMove(move, tempPiece);
        }
        return validMoves;
    }
    public ChessPiece testMove(ChessMove move) {
        ChessPiece[][] squares = board.getSquares();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece tempPiece = board.getPiece(move.getEndPosition());
        //make the move
        squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = piece;
        return tempPiece;
    }
    public void undoTestMove(ChessMove move, ChessPiece tempPiece) {
        ChessPiece[][] squares = board.getSquares();
        ChessPiece piece = board.getPiece(move.getEndPosition());
        squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = tempPiece;
        squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = piece;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameOver) {
            throw new InvalidMoveException();
        }
        if (!this.validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException();
        }
        if (this.teamTurn != board.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException();
        }
        ChessPiece[][] squares = board.getSquares();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        //check for promotions
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = piece;

        toggleTeamTurn();
    }
    public void toggleTeamTurn() {
        if (this.teamTurn == TeamColor.WHITE)
        {this.teamTurn = TeamColor.BLACK;
        } else {
            this.teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find the king
        ChessPiece king = null;
        ChessPosition kingPosition = null;
        ChessPiece[][] squares = this.board.getSquares();
        //for each piece, is it the king of teamColor
        findKing:
        for (int i = 0; i <=7; i++){
            for (int j = 0; j <=7; j++){
                if (Objects.equals(squares[i][j], new ChessPiece(teamColor, ChessPiece.PieceType.KING))){
                    king = squares[i][j];
                    kingPosition = new ChessPosition(i + 1,j + 1);
                    break findKing;
                }
            }
        }
        if (king == null){
            return false;
        }
        Collection<ChessMove> moves = new ArrayList<>();
        //can an opposing piece get to the kings position
        for (int i = 0; i <= 7; i++){
            for (int j = 0; j <= 7; j++){
                if (squares[i][j] != null && squares[i][j].getTeamColor() != teamColor){
                    //just get the end_pos of each of the moves
                    ChessPiece piece = board.getPiece(new ChessPosition(i + 1,j + 1));
                    Collection<ChessPosition> validEndPositions = piece.endPositionsOnlyFromPieceMoves(board, new ChessPosition(i + 1,j + 1));
                    if (validEndPositions.contains(kingPosition)){
                        return true;
                    }
                }
            }
        }
        //check each opposing piece on the board and see if it can reach the king
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //first are they in check
        if (!isInCheck(teamColor)){
            return false;
        }
        //simulate each possible move for you team to see if you are no longer in check
        Collection<ChessPiecePosition> teamPiecePosition = board.getAllChessPiecesPositionForTeam(teamColor);
        for (ChessPiecePosition piecePosition : teamPiecePosition){
            for (ChessMove move : validMoves(piecePosition.getPosition())){
                ChessPiece tempPiece = this.testMove(move);
                if (this.isInCheck(teamColor)){
                    this.undoTestMove(move, tempPiece);
                    return false;
                }
                this.undoTestMove(move, tempPiece);
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for ( ChessPiecePosition piecePosition : board.getAllChessPiecesPositionForTeam(teamColor)){
            if (!validMoves(piecePosition.getPosition()).isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
