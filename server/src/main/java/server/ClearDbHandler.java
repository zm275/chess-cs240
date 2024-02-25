package server;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import spark.Request;
import spark.Response;
import service.ClearDbService;
// Handler for DELETE /db endpoint
public class ClearDbHandler {
    private final ClearDbService clearDbService;

    public ClearDbHandler() {
        this.clearDbService = new ClearDbService();
    }

    public Object clearDatabases(Request request, Response response, MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        try {
            clearDbService.clearAllData(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
            response.status(200);
            return new SuccessResponse("Successfully cleared databases").toJson();
        } catch (DataAccessException e){
            response.status(500);
            return new ErrorResponse(e.toString()).toJson();
        }

    }

}

