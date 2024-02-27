package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.util.log.Log;

import javax.xml.crypto.Data;
import java.util.List;

class Response {
    transient boolean success;
    String message;
    public String toJson() {
        Gson gson = new Gson();
        if (success) {
            return gson.toJson(this);
        }
        return gson.toJson(this);

    }
}
class BadRequestResponse extends Response {
    public BadRequestResponse(String message) {
        this.message = message;
        this.success = false;
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
    @Override
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

}

class ListGamesResponse extends Response {
    List<GameData> games;

    ListGamesResponse(Boolean success, List<GameData> games) {
        this.success = success;
        this.games = games;
    }
    ListGamesResponse(Boolean success, DataAccessException e){
        this.success = success;
        this.message = e.getLocalizedMessage();
    }

}

class CreateGameResponse extends Response {
    Integer gameID;

    CreateGameResponse(boolean success, int gameID) {
        this.success = success;
        this.gameID = gameID;
    }

    CreateGameResponse(boolean success, DataAccessException e) {
        this.success = success;
        this.message = e.getLocalizedMessage();
    }
}

class JoinGameResponse extends Response {
    JoinGameResponse(boolean success) {
        this.success = success;
    }
    JoinGameResponse(boolean success, DataAccessException e) {
        this.success = success;
        this.message = e.getLocalizedMessage();
    }
}

