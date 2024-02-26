package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.util.log.Log;

class Response {
    transient boolean success;
    String message;
}
class BadRequestResponse extends Response {
    public BadRequestResponse(String message) {
        this.message = message;
        this.success = false;
    }
    public String toJson() {
        Gson gson = new Gson();
        if (success) {
            //return authtoken
            return gson.toJson(this);
        }
        return gson.toJson(this);

    }

}

class RegisterResponse extends Response {
    AuthData authData;
    public RegisterResponse(boolean success, AuthData authData){
        this.authData = new AuthData(authData.authToken(), authData.username());
        this.success = success;
    }
    public RegisterResponse(boolean success, DataAccessException e){
        this.success = success;
        message = e.getLocalizedMessage();
    }
    public String toJson() {
        Gson gson = new Gson();
        if (success) {
            //return authdata
            return gson.toJson(this.authData);
        }
        return gson.toJson(this);

    }
}
class LoginResponse extends Response {
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
    public String toJson() {
        Gson gson = new Gson();
        if (success) {
            //return authtoken
            return gson.toJson(this);
        }
        return gson.toJson(this);

    }
}
