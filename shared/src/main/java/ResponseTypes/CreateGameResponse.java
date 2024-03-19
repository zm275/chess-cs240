package ResponseTypes;

public class CreateGameResponse extends Response {
    Integer gameID;

    public CreateGameResponse(boolean success, int gameID) {
        this.success = success;
        this.gameID = gameID;
    }

    public CreateGameResponse(boolean success, DataAccessException e) {
        this.success = success;
        this.message = e.getLocalizedMessage();
    }
}
