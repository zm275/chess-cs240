package server;

import com.google.gson.Gson;

// Define a class for success response
public class SuccessResponse {
    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
