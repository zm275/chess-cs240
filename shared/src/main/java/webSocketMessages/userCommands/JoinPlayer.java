package webSocketMessages.userCommands;

import chess.ChessGame;

import java.awt.desktop.UserSessionListener;
import java.util.Objects;

public class JoinPlayer extends UserGameCommand {
    private final Integer gameID;
    private final ChessGame.TeamColor playerColor;


    public JoinPlayer(Integer gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public Integer gameID() {
        return gameID;
    }

    public ChessGame.TeamColor playerColor() {
        return playerColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JoinPlayer) obj;
        return Objects.equals(this.gameID, that.gameID) &&
                Objects.equals(this.playerColor, that.playerColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, playerColor);
    }

    @Override
    public String toString() {
        return "JoinPlayer[" +
                "gameID=" + gameID + ", " +
                "playerColor=" + playerColor + ']';
    }

}
