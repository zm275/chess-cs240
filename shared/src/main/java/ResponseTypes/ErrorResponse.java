package ResponseTypes;

import com.google.gson.Gson;

// Define a class for error response
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
