package dataAccess;
import ResponseTypes.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    boolean authenticate(UserData userData) throws DataAccessException;
    void clear() throws DataAccessException;


}
