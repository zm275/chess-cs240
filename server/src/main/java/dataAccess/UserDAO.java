package dataAccess;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData userData) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;


}
