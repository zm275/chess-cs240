package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameData;

import java.util.List;

public class GameService {
    public List<GameData> listGames(MemoryGameDAO memoryGameDAO) throws DataAccessException {
        return memoryGameDAO.listGames();
    }
}
