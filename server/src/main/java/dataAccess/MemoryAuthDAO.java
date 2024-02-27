package dataAccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    //We will map the AuthToken to the AuthData to make it easy to find the AuthData or check if it is already there
    private final Map<String, AuthData> authDataMap;

    public MemoryAuthDAO() {
        this.authDataMap = new HashMap<>();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        if (authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken already exists", 403);
        }
        authDataMap.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found",401);
        }
        return authDataMap.get(authToken);
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found",401);
        }
        return true;
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found",401);
        }
        authDataMap.remove(authToken);

    }

    @Override
    public void clear() throws DataAccessException {
        authDataMap.clear();
    }
}
