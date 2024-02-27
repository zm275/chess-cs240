package server.Handlers;

import dataAccess.*;
import server.ResponseTypes.ErrorResponse;
import server.SuccessResponse;
import spark.Request;
import spark.Response;
import service.ClearDbService;
// Handler for DELETE /db endpoint
public class ClearDbHandler {
    private final ClearDbService clearDbService;

    public ClearDbHandler() {
        this.clearDbService = new ClearDbService();
    }

    public Object clearDatabases(Request request, Response response, UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        try {
            clearDbService.clearAllData(userDAO, authDAO, gameDAO);
            response.status(200);
            return new SuccessResponse("Successfully cleared databases").toJson();
        } catch (DataAccessException e){
            response.status(500);
            return new ErrorResponse(e.toString()).toJson();
        }

    }

}

