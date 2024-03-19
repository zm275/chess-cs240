package server;

import ResponseTypes.DataAccessException;
import dataAccess.*;
import server.Handlers.ClearDbHandler;
import server.Handlers.GameHandler;
import server.Handlers.UserHandler;
import spark.*;


import static spark.Spark.staticFiles;

public class Server {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public Server() {
        this.userDAO = new SQLUserDAO();
        this.gameDAO = new SQLGameDAO();
        this.authDAO = new SQLAuthDAO();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        staticFiles.location("web");
        try {
            // Initialize the database and create tables if they do not exist
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return -1;
        }
        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", (req, res) -> new ClearDbHandler().clearDatabases(req,res,userDAO, authDAO, gameDAO));
        Spark.post("/user", (req, res) -> new UserHandler().registerNewUser(req, res, userDAO, authDAO));
        Spark.post("/session", (req, res) -> new UserHandler().loginUser(req, res, userDAO, authDAO));
        Spark.delete("/session", (req, res) -> new UserHandler().logoutUser(req, res, userDAO, authDAO));
        Spark.get("/game", (req, res) -> new GameHandler().listGames(req, res, authDAO, gameDAO));
        Spark.post("/game", (req, res) -> new GameHandler().createGame(req, res, authDAO, gameDAO));
        Spark.put("/game", (req, res) -> new GameHandler().joinGame(req, res, authDAO, gameDAO));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

