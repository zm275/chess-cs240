package service;

import dataAccess.*;

public class ClearDbService {
    public void clearAllData(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        // Clear data from each DAO
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
