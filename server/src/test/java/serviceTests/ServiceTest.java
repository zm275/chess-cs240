package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.ClearDbService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServiceTest {

    UserDAO testUserDAO = new MemoryUserDAO();
    AuthDAO testAuthDAO =  new MemoryAuthDAO();
    GameDAO testGameDAO = new MemoryGameDAO();
    @Test
    public void testClearAllData() throws DataAccessException {
        testUserDAO.createUser(new UserData("dave", "1234", "dave@gmail.com"));
        testAuthDAO.createAuth("dave");
        testGameDAO.createGame("game1");

        ClearDbService clearDbService = new ClearDbService();
        assertDoesNotThrow(() -> clearDbService.clearAllData(testUserDAO,testAuthDAO,testGameDAO));
    }


}
