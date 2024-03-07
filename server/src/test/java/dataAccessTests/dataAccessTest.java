package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ClearDbService;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("create auth")
    public void createAuth() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        assertDoesNotThrow(() -> testAuthDAO.createAuth(testUser.username()));
    }
    @Test
    @DisplayName("bad create auth")
    public void badCreateAuth() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testAuthDAO.createAuth(null));
        assertEquals(403, exception.getStatusCode());
    }
    @Test
    @DisplayName("get auth")
    public void getAuth() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
        assertDoesNotThrow(() -> testAuthDAO.getAuth(authToken));
    }
    @Test
    @DisplayName("bad get auth")
    public void badGetAuth() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testAuthDAO.getAuth(null));
        assertEquals(404, exception.getStatusCode());
    }
    @Test
    @DisplayName("auth authToken")
    public void isAuthorized() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
         boolean auth = testAuthDAO.isAuthorized(authToken);
         assertTrue(auth);
    }
    @Test
    @DisplayName("auth bad authToken")
    public void isNotAuthorized() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testAuthDAO.isAuthorized(null));
        assertEquals(401, exception.getStatusCode());
    }
    @Test
    @DisplayName("delete authToken")
    public void logout() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
        boolean auth = testAuthDAO.isAuthorized(authToken);
        assertTrue(auth);
        testAuthDAO.deleteAuth(authToken);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testAuthDAO.isAuthorized(authToken));
        assertEquals(401, exception.getStatusCode());
    }
    @Test
    @DisplayName("logout bad authToken")
    public void badLogout() throws DataAccessException {
        start();
        testUserDAO.createUser(testUser);
        String authToken = testAuthDAO.createAuth(testUser.username()).authToken();
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testAuthDAO.deleteAuth("12345"));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    @DisplayName("add game to db")
    public void addGame() throws DataAccessException {
        start();
        assertDoesNotThrow(() -> testGameDAO.createGame("chicken"));
    }
    @Test
    @DisplayName("add game to db fail")
    public void failAddGame() throws DataAccessException {
        start();
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testGameDAO.createGame(""));
        assertEquals(400, exception.getStatusCode());
    }
    @Test
    @DisplayName("get game")
    public void getGame() throws DataAccessException {
        start();
        int gameID = testGameDAO.createGame("chicken");
        assertDoesNotThrow(() -> testGameDAO.getGame(gameID));
    }
    @Test
    @DisplayName("get game fail")
    public void failGetGame() throws DataAccessException {
        start();
        DataAccessException exception = assertThrows(DataAccessException.class, () -> testGameDAO.getGame(0));
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    @DisplayName("List multiple games")
    public void listGames() throws DataAccessException {
        start();
        List<GameData> games = new ArrayList<GameData>();
        testGameDAO.createGame("game1");
        games.add(new GameData(1,null,null,"game1", new ChessGame()));
        testGameDAO.createGame("game2");
        games.add(new GameData(2,null,null,"game2", new ChessGame()));
        testGameDAO.createGame("game3");
        games.add(new GameData(3,null,null,"game3", new ChessGame()));
        testGameDAO.createGame("game4");
        games.add(new GameData(4,null,null,"game4", new ChessGame()));

        List<GameData> dbGames = testGameDAO.listGames();
        assertEquals(games, dbGames);
    }
    @Test
    @DisplayName("Bad List multiple games")
    public void badListGames() throws DataAccessException {
        start();
        List<GameData> games = new ArrayList<GameData>();
        testGameDAO.createGame("game1");
        games.add(new GameData(1,null,null,"game1", new ChessGame()));
        testGameDAO.createGame("game2");
        games.add(new GameData(2,null,null,"game2", new ChessGame()));
        testGameDAO.createGame("game3");
        games.add(new GameData(3,null,null,"game3", new ChessGame()));
        games.add(new GameData(4,null,null,"game4", new ChessGame()));
        List<GameData> dbGames = testGameDAO.listGames();
        assertNotEquals(games, dbGames);
    }

}
