package dataAccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    //We will map the AuthToken to the AuthData to make it easy to find the AuthData or check if it is already there
    private final Map<String, AuthData> authDataMap;

    public MemoryAuthDAO() {
        this.authDataMap = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        //check if an identical authData is already there
        if (authDataMap.containsKey(authData.authToken())){
            throw new DataAccessException("Error: AuthToken already exists");
        }
        authDataMap.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found");
        }
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found");
        }
        authDataMap.remove(authToken);

    }

    @Override
    public void clear() throws DataAccessException {
        authDataMap.clear();
    }
}
