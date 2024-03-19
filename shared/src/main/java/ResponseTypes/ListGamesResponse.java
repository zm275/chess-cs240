package ResponseTypes;

import model.GameData;

import java.util.List;

public class ListGamesResponse extends Response {
    List<GameData> games;

    public ListGamesResponse(Boolean success, List<GameData> games) {
        this.success = success;
        this.games = games;
    }
    public ListGamesResponse(Boolean success, DataAccessException e){
        this.success = success;
        this.message = e.getLocalizedMessage();
    }

}
