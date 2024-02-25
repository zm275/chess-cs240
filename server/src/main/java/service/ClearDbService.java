package service;

import dataAccess.*;

public class ClearDbService {


    public void clearAllData(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) throws DataAccessException {
        // Clear data from each DAO
        memoryUserDAO.clear();
        memoryAuthDAO.clear();
        memoryGameDAO.clear();
    }
}
