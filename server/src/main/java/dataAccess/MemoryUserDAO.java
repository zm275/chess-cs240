package dataAccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    // map the username to the UserData to make it easy to find users or check if they are already registered
    private final Map<String, UserData> userDataMap;

    public MemoryUserDAO() {
        this.userDataMap = new HashMap<>();
    }
    public Map<String, UserData> getUserDataMap() {
        return this.userDataMap;
    }
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())){
            throw new DataAccessException("Error: Username already taken", 403);
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("Error: User not found", 401);
        }
        return userDataMap.get(username);
    }
    @Override
    public boolean authenticate(UserData userData) throws DataAccessException {
        String providedUsername = userData.username();
        String providedPassword = userData.password();
        if (!userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("Error: User not found", 401);
        }
        String storedPassword = userDataMap.get(providedUsername).password();
        if (Objects.equals(storedPassword, providedPassword)) {
            return true;
        }
        else {
            throw new DataAccessException("Error: Incorrect password.", 401);
        }
    }


    @Override
    public void updateUser(UserData userData) throws DataAccessException {
        if (!userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("Error: User not found", 401);
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new DataAccessException("Error: User not found", 401);
        }
        userDataMap.remove(username);
    }

    @Override
    public void clear() throws DataAccessException {
        userDataMap.clear();
    }

}

