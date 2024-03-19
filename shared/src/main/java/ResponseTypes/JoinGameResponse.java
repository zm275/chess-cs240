package ResponseTypes;



public class JoinGameResponse extends Response {
    public JoinGameResponse(boolean success) {
        this.success = success;
    }
    public JoinGameResponse(boolean success, DataAccessException e) {
        this.success = success;
        this.message = e.getLocalizedMessage();
    }
}
