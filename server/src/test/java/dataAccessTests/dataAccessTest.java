package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ClearDbService;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class dataAccessTest {
    UserDAO testUserDAO = new SQLUserDAO();
    AuthDAO testAuthDAO =  new SQLAuthDAO();
    GameDAO testGameDAO = new SQLGameDAO();
    ClearDbService clearDbService = new ClearDbService();
    UserService userService = new UserService();
    GameService gameService = new GameService();
    UserData testUser = new UserData("dave", "1234", "dave@dave.com");
    UserData badTestUser = new UserData("dave", "", "dave@dave.com");
    UserData nullTestUser = new UserData("dave", null, "dave@dave.com");


    public void start() throws DataAccessException {
        testGameDAO.clear();
        testAuthDAO.clear();
        testUserDAO.clear();
    }
    @Test
    @DisplayName("clear database")
    public void clearDatabase() throws DataAccessException {
        testGameDAO.clear();
        testAuthDAO.clear();
        testUserDAO.clear();
    }
    @Test
    @DisplayName("create user")
    public void createUser() throws DataAccessException {
        start();
        assertDoesNotThrow(() -> testUserDAO.createUser(testUser));
    }
    @Test
    @DisplayName("create user twice")
    public void doubleCreateUser() throws DataAccessException {
        start();
        assertDoesNotThrow(() -> testUserDAO.createUser(testUser));
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testUserDAO.createUser(testUser));
        assertEquals(403, exception.getStatusCode());
    }
    @Test
    @DisplayName("authenticate user")
    public void userLogin() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        assertDoesNotThrow(() -> testUserDAO.authenticate(testUser));
    }
    @Test
    @DisplayName("authenticate user wrong password")
    public void userLoginWrongPassword() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testUserDAO.authenticate(badTestUser));
        assertEquals(401, exception.getStatusCode());
    }


    @Test
    @DisplayName("add game to db")
    public void addGame() throws DataAccessException {
        testGameDAO.createGame("chicken");
    }
}
