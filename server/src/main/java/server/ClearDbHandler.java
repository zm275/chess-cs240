package server;

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
        boolean result = clearDbService.clearAllData(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        if (result){
            response.status(200);
            return new SuccessResponse("Successfully cleared databases").toJson();
        }
        else {
            response.status(500);
            return new ErrorResponse("Failed to clear databases").toJson();
        }

    }

}

