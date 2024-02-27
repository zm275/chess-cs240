package server.ResponseTypes;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.util.log.Log;

import javax.xml.crypto.Data;
import java.util.List;

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
}

