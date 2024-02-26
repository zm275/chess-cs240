package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.AuthenticationService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;

public class UserHandler {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final Gson gson;


    public UserHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
        this.authenticationService = new AuthenticationService();
    }

    public Object registerNewUser(Request request, Response response, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) {
        try {
            //deserialize request
            UserData userData = gson.fromJson(request.body(), UserData.class);
            //throws error if there are null values or syntax errors
            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                throw new JsonSyntaxException("Error: Null values in UserData");
            }
            AuthData authData = userService.registerNewUser(userData, memoryUserDAO, memoryAuthDAO);
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
    public Object loginUser(Request request, Response response, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            if (userData.username() == null || userData.password() == null) {
                throw new JsonSyntaxException("Error: Null values in UserData");
            }
            AuthData authData = userService.loginUser(userData, memoryUserDAO, memoryAuthDAO);
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
    public Object logoutUser(Request request, Response response, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) {
        String authToken = request.headers("authorization");
        try {
            userService.logoutUser(authToken, memoryAuthDAO);
            response.status(200);
            return "";
        }
        catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new LoginResponse(false, e).toJson();
        }
    }


}
