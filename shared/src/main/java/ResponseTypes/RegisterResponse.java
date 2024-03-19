package ResponseTypes;

import com.google.gson.Gson;

import model.AuthData;


public class RegisterResponse extends Response {
    AuthData authData;
    public RegisterResponse(boolean success, AuthData authData){
        this.authData = new AuthData(authData.authToken(), authData.username());
        this.success = success;
    }
    public RegisterResponse(boolean success, DataAccessException e){
        this.success = success;
        message = e.getLocalizedMessage();
    }

    public AuthData getAuthData() {
        return authData;
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
