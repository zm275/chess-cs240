package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    private final Integer gameID;

    public Resign(int gameID, String authToken) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
