package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ClearDbService;
import service.GameService;
import service.UserService;

public class dataAccessTest {
    UserDAO testUserDAO = new SQLUserDAO();
    AuthDAO testAuthDAO =  new SQLAuthDAO();
    GameDAO testGameDAO = new SQLGameDAO();
    ClearDbService clearDbService = new ClearDbService();
    UserService userService = new UserService();
    GameService gameService = new GameService();
    UserData testUser = new UserData("dave", "1234", "dave@dave.com");
    UserData badTestUser = new UserData("dave", "", "dave@dave.com");

    public void start() throws DataAccessException {
        this.clearDbService.clearAllData(this.testUserDAO, testAuthDAO, testGameDAO);
    }

    @Test
    @DisplayName("add game to db")
    public void addGame() throws DataAccessException {
        testGameDAO.createGame("chicken");
    }
}
