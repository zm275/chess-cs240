package clientTests;

import ResponseTypes.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Server;
import serverFacade.ServerFacade;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws IOException {
        serverFacade = new ServerFacade( "http://localhost:8080");
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade.clearDB();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }
    @Test
    @DisplayName("good register")
    void register() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        assertEquals("jquelin", response.getAuthData().username());
        assertNotNull(response.getAuthData().authToken());
        assertTrue(response.isSuccess());
        assertNotNull(response);
    }
    @Test
    @DisplayName("bad register")
    void badRegister() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        RegisterResponse response = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        assertNotNull(response);
        assertNotNull(response.getMessage());
    }
    @Test
    @DisplayName("good login")
    void login() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        LoginResponse response = serverFacade.loginUser("jquelin", "1233");
        assertNotNull(response);
        assertNotNull(response.getAuthToken());
    }
    @Test
    @DisplayName("good logout")
    void logout() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        LoginResponse response = serverFacade.logoutUser(response0.getAuthData().authToken());
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNull(response.getMessage());
    }
    @Test
    @DisplayName("bad logout")
    void badLogout() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        LoginResponse response = serverFacade.logoutUser("123435");
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
    }
    @Test
    @DisplayName("good create game")
    void createGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        CreateGameResponse response = serverFacade.createGame("testGame", response0.getAuthData().authToken());
        assertNotNull(response);
        assertNull(response.getMessage());
        assertNotNull(response.getGameID());
    }
    @Test
    @DisplayName("bad create game")
    void createBadGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        CreateGameResponse response = serverFacade.createGame("testGame", "12345");
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertNull(response.getGameID());
    }
    @Test
    @DisplayName("list game")
    void listGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        serverFacade.createGame("game2", response0.getAuthData().authToken());
        ListGamesResponse response = serverFacade.listAllGames(response0.getAuthData().authToken());
        assertNotNull(response.getGames());

    }
    @Test
    @DisplayName("bad list game")
    void badListGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        serverFacade.createGame("game2", response0.getAuthData().authToken());
        ListGamesResponse response = serverFacade.listAllGames("bad_auth");
        assertNotNull(response.getMessage());
        assertNull(response.getGames());

    }
    @Test
    @DisplayName("join game")
    void joinGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        CreateGameResponse createGameResponse = serverFacade.createGame("game2", response0.getAuthData().authToken());
        JoinGameResponse response = serverFacade.joinGame(createGameResponse.getGameID(),"WHITE", response0.getAuthData().authToken());
        assertNull(response.getMessage());
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
    @Test
    @DisplayName("bad join game")
    void badJoinGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        CreateGameResponse createGameResponse = serverFacade.createGame("game2", response0.getAuthData().authToken());
        JoinGameResponse response = serverFacade.joinGame(createGameResponse.getGameID(),"WHITE1", response0.getAuthData().authToken());
        assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }
    @Test
    @DisplayName("bad join game")
    void badJoinGameTest1() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        CreateGameResponse createGameResponse = serverFacade.createGame("game2", response0.getAuthData().authToken());
        JoinGameResponse response = serverFacade.joinGame(createGameResponse.getGameID(),"WHITE21", response0.getAuthData().authToken());
        assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }
    @Test
    @DisplayName("watch game")
    void watchGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        CreateGameResponse createGameResponse = serverFacade.createGame("game2", response0.getAuthData().authToken());
        JoinGameResponse response = serverFacade.joinGame(createGameResponse.getGameID(),"WHITE", response0.getAuthData().authToken());
        assertNull(response.getMessage());
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }
    @Test
    @DisplayName("bad watch game")
    void badWatchGameTest() throws IOException {
        serverFacade.clearDB();
        RegisterResponse response0 = serverFacade.registerUser("jquelin", "1233", "1@1.com");
        serverFacade.createGame("testGame", response0.getAuthData().authToken());
        CreateGameResponse createGameResponse = serverFacade.createGame("game2", response0.getAuthData().authToken());
        JoinGameResponse response = serverFacade.joinGame(createGameResponse.getGameID(),"WHITE1", response0.getAuthData().authToken());
        assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }


}
