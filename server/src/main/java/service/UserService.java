package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
    //returns UserAuth
    public AuthData registerNewUser(UserData userData, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        userDAO.createUser(userData);
        return authDAO.createAuth(userData.username());
    }
    // returns authToken String
    public AuthData loginUser(UserData userData, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        boolean auth = userDAO.authenticate(userData);
        if (auth){
            return authDAO.createAuth(userData.username());
        }
        return null;
    }
    public void logoutUser(String authToken, AuthDAO authDAO) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }


}
