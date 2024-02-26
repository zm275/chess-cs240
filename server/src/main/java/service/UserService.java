package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import spark.Response;

public class UserService {
    //returns UserAuth
    public AuthData registerNewUser(UserData userData, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) throws DataAccessException {
        memoryUserDAO.createUser(userData);
        return memoryAuthDAO.createAuth(userData.username());
    }
    // returns authToken String
    public AuthData loginUser(UserData userData, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) throws DataAccessException {
        boolean auth = memoryUserDAO.authenticate(userData);
        if (auth){
            return memoryAuthDAO.createAuth(userData.username());
        }
        return null;
    }


}
