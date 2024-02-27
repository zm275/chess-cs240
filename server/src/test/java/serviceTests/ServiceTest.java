package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ClearDbService;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    UserDAO testUserDAO = new MemoryUserDAO();
    AuthDAO testAuthDAO =  new MemoryAuthDAO();
    GameDAO testGameDAO = new MemoryGameDAO();
    ClearDbService clearDbService = new ClearDbService();
    UserService userService = new UserService();
    UserData testUser = new UserData("dave", "1234", "dave@dave.com");
    UserData badTestUser = new UserData("dave", "", "dave@dave.com");


    public void start() throws DataAccessException {
        this.clearDbService.clearAllData(this.testUserDAO, testAuthDAO, testGameDAO);
    }

    @Test
    public void testClearAllData() throws DataAccessException {
        testUserDAO.createUser(new UserData("dave", "1234", "dave@gmail.com"));
        testAuthDAO.createAuth("dave");
        testGameDAO.createGame("game1");

        assertDoesNotThrow(() -> clearDbService.clearAllData(testUserDAO,testAuthDAO,testGameDAO));
        assertTrue(testUserDAO.getUserDataMap().isEmpty());
        assertTrue(testAuthDAO.getAuthDataMap().isEmpty());
        assertTrue(testGameDAO.getGameDataMap().isEmpty());

    }
    @Test
    @DisplayName("valid registration")
    public void testRegisterNewUser() throws DataAccessException {
        start();
        UserData user = new UserData("dave", "1234", "dave@gmail.com");
        AuthData authData = userService.registerNewUser(user, testUserDAO, testAuthDAO);
        assertNotNull(authData);

        // Assert that authToken and username in authData are valid
        assertTrue(authData.authToken() != null && !authData.authToken().isEmpty());
        assertTrue(authData.username() != null && !authData.username().isEmpty());
    }
    @Test
    @DisplayName("invalid registration")
    public void failRegisterNewUser() throws DataAccessException {
        start();
        UserData user = new UserData(null,null,null);
        AuthData authData = userService.registerNewUser(user, testUserDAO, testAuthDAO);
        assertFalse(authData.username() != null && !authData.username().isEmpty());
    }

    @Test
    @DisplayName("valid login")
    public void Login() throws DataAccessException {
        start();
        userService.registerNewUser(testUser,testUserDAO,testAuthDAO);
        AuthData authData = userService.loginUser(testUser, testUserDAO,testAuthDAO);
        assertNotNull(authData.authToken());
        assertNotNull(authData.username());
    }
    @Test
    @DisplayName("invalid login")
    public void BadLogin() throws DataAccessException {
        start();
        userService.registerNewUser(testUser,testUserDAO,testAuthDAO);
        assertThrows(DataAccessException.class, () -> {
            userService.loginUser(badTestUser, testUserDAO, testAuthDAO);
        });
    }

    @Test
    @DisplayName("valid logout")
    public void Logout() throws DataAccessException {
        start();
        AuthData authData = userService.registerNewUser(testUser,testUserDAO,testAuthDAO);
        userService.logoutUser(authData.authToken(), testAuthDAO);
        assertThrows(DataAccessException.class, () -> {
            testAuthDAO.getAuth(authData.authToken());
        });
    }

    @Test
    @DisplayName("invalid logout")
    public void BadLogout() throws DataAccessException {
        start();
        AuthData authData = userService.registerNewUser(testUser,testUserDAO,testAuthDAO);
        assertThrows(DataAccessException.class, () -> {
            userService.logoutUser("1234badauthtoken", testAuthDAO);
        });
    }



}
