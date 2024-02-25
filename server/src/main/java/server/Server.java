package server;

import spark.*;

import static spark.Spark.staticFiles;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/hello", (req, res) -> "Hello Spark!");
        Spark.get("/db", (req, res) -> {
            // Handle the DELETE request to /db endpoint
            // For example:
            return "Deleted something from the database";
        });


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
