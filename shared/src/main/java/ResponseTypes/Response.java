package ResponseTypes;

import com.google.gson.Gson;

class Response {
    transient boolean success;
    String message;
    public String toJson() {
        Gson gson = new Gson();
        if (success) {
            return gson.toJson(this);
        }
        return gson.toJson(this);

    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

