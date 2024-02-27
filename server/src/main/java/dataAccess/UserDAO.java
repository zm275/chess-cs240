package dataAccess;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Map;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    Map<String, UserData> getUserDataMap();
    boolean authenticate(UserData userData) throws DataAccessException;
    void updateUser(UserData userData) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;


}
