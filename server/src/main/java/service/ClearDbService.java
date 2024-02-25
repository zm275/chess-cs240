package service;

import dataAccess.*;

public class ClearDbService {


    public boolean clearAllData(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        try {
            // Clear data from each DAO
            memoryUserDAO.clear();
            memoryAuthDAO.clear();
            memoryGameDAO.clear();
            return true;
        } catch (Exception e) {
            // Log any exceptions
            e.printStackTrace();
            return false;
        }
    }
}
