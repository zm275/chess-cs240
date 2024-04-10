package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand{
    private final Integer gameID;

    public JoinObserver(int gameID, String authToken)  {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
