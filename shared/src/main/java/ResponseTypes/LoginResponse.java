package ResponseTypes;

import model.AuthData;


public class LoginResponse extends Response {
    String authToken;
    String username;
    public LoginResponse(boolean success, AuthData authData){
        this.success = success;
        this.authToken = authData.authToken();
        this.username = authData.username();
    }
    public LoginResponse(boolean success, DataAccessException e){
        this.success = success;
        message = e.getLocalizedMessage();
    }

}
