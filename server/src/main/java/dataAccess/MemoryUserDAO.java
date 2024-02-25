package dataAccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    // map the username to the UserData to make it easy to find users or check if they are already registered
    private final Map<String, UserData> userDataMap;

    public MemoryUserDAO() {
        this.userDataMap = new HashMap<>();
    }
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())){
            throw new DataAccessException("Error: Username already taken");
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("Error: User not found");
        }
        return userDataMap.get(username);
    }

    @Override
    public void updateUser(UserData userData) throws DataAccessException {
        if (!userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("Error: User not found");
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("Error: User not found");
        }
        userDataMap.remove(username);
    }

    @Override
    public void clear() throws DataAccessException {
        userDataMap.clear();
    }
}
