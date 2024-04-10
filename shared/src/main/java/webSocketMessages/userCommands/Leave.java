package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    private final Integer gameID;

    public Leave(int gameID, String authToken) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
