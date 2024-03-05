package server.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dataAccess.*;
import model.AuthData;
import model.UserData;
import server.ResponseTypes.BadRequestResponse;
import server.ResponseTypes.LoginResponse;
import server.ResponseTypes.RegisterResponse;
import service.AuthenticationService;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final Gson gson;


    public UserHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
        this.authenticationService = new AuthenticationService();
    }

    public Object registerNewUser(Request request, Response response, UserDAO userDAO, AuthDAO authDAO) {
        try {
            //deserialize request
            UserData userData = gson.fromJson(request.body(), UserData.class);
            //throws error if there are null values or syntax errors
            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                throw new JsonSyntaxException("Error: Null values in UserData");
            }
            AuthData authData = userService.registerNewUser(userData, userDAO, authDAO);
            response.status(200);
            return new RegisterResponse(true, authData).toJson();
        } catch (JsonSyntaxException j) {
            response.status(400);
            return new BadRequestResponse("Error: Bad Request").toJson();
        } catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new RegisterResponse(false, e).toJson();
        }
    }
    //returns a new authtoken
    public Object loginUser(Request request, Response response, UserDAO userDAO, AuthDAO authDAO) {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            if (userData.username() == null || userData.password() == null) {
                throw new JsonSyntaxException("Error: Null values in UserData");
            }
            AuthData authData = userService.loginUser(userData, userDAO, authDAO);
            response.status(200);
            return new LoginResponse(true, authData).toJson();
        } catch (JsonSyntaxException j) {
            response.status(400);
            return new BadRequestResponse("Error: Bad Request").toJson();
        } catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new LoginResponse(false, e).toJson();
        }
    }
    public Object logoutUser(Request request, Response response, UserDAO userDAO, AuthDAO authDAO) {
        String authToken = request.headers("authorization");
        try {
            userService.logoutUser(authToken, authDAO);
            response.status(200);
            return gson.toJson(new JsonObject());
        }
        catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new LoginResponse(false, e).toJson();
        }
    }


}
