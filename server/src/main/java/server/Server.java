package server;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import spark.*;
import service.*;

import static spark.Spark.staticFiles;

public class Server {
    private final MemoryUserDAO userDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public Server() {
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", (req, res) -> new ClearDbHandler().clearDatabases(req,res,userDAO, authDAO, gameDAO));
//        Spark.post("/user", (req, res) -> new UserHandler().registerNewUser(req, res, userDAO));
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

