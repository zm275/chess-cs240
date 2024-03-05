package dataAccess;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Map;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    boolean authenticate(UserData userData) throws DataAccessException;
    void clear() throws DataAccessException;


}
