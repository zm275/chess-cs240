package server;

import com.google.gson.Gson;
import dataAccess.MemoryUserDAO;
import model.UserData;
import service.AuthenticationService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final Gson gson;


    public UserHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
        this.authenticationService = new AuthenticationService();
    }

//    public Object registerNewUser(Request request, Response response, MemoryUserDAO memoryUserDAO) {
//        //deserialize request
//        UserData userData = gson.fromJson(request.body(), UserData.class);
//        Object result = userService.registerNewUser(userData, memoryUserDAO);
//
//    }

}
