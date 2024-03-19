package chess;

import java.util.*;

import static java.lang.Character.toUpperCase;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            boardString.append("|");
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = squares[i][j];
                char pieceChar = (piece != null) ? pieceToChar(piece) : ' ';
                boardString.append(pieceChar).append("|");
            }
            boardString.append("\n");
        }

        return boardString.toString();
    }

    private char pieceToChar(ChessPiece piece) {
        char c = typeToCharMap.get(piece.getPieceType());
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            c = toUpperCase(c);
        }
        return c;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(squares);
    }
    private final ChessPiece[][]  squares = new ChessPiece[8][8];
    public ChessBoard() {

    }
    final static Map<ChessPiece.PieceType, Character> typeToCharMap = new HashMap<>();

    static {
        typeToCharMap.put(ChessPiece.PieceType.PAWN, 'p');
        typeToCharMap.put(ChessPiece.PieceType.KNIGHT, 'n');
        typeToCharMap.put(ChessPiece.PieceType.ROOK, 'r');
        typeToCharMap.put(ChessPiece.PieceType.QUEEN, 'q');
        typeToCharMap.put(ChessPiece.PieceType.KING, 'k');
        typeToCharMap.put(ChessPiece.PieceType.BISHOP, 'b');
    }
    final static Map<Character, ChessPiece.PieceType> charToTypeMap = Map.of(
            'p', ChessPiece.PieceType.PAWN,
            'n', ChessPiece.PieceType.KNIGHT,
            'r', ChessPiece.PieceType.ROOK,
            'q', ChessPiece.PieceType.QUEEN,
            'k', ChessPiece.PieceType.KING,
            'b', ChessPiece.PieceType.BISHOP);

    public ChessPiece[][] getSquares(){
        return squares;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }
    public void setPiece(ChessPosition position){
        squares[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    public Collection<ChessPiecePosition> getAllChessPiecesPositionForTeam(ChessGame.TeamColor teamColor){
        Collection<ChessPiecePosition> teamPieces = new ArrayList<>();
        for (int i = 0; i <= 7; i++){
            for (int j = 0; j <= 7; j++){
                if (squares[i][j] != null && squares[i][j].getTeamColor() == teamColor){
                    teamPieces.add(new ChessPiecePosition(squares[i][j], new ChessPosition(i + 1,j + 1)));
                }
            }
        }
        return teamPieces;
    }

    public boolean isValidPosition(ChessPosition position){
        return position.getRow() <= 8 && position.getRow() >= 1 && position.getColumn() <= 8 && position.getColumn() >= 1;
    }
    public boolean isOccupied(ChessPosition position){
        ChessPiece piece = getPiece(position);
        return piece != null; //return true if there is a piece
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i <=7; i++){
            for (int j = 0; j <= 7; j++){
                squares[i][j] = null;
            }
        }
        int row = 8;
        int column = 1;
        var boardText = """
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |P|P|P|P|P|P|P|P|
                |R|N|B|Q|K|B|N|R|
                """;

        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
                    var type = charToTypeMap.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    this.addPiece(position, piece);
                    column++;
                }
            }

        }
    }
}
