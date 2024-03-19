import ResponseTypes.LoginResponse;
import ResponseTypes.RegisterResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.AuthData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private static final String ENDPOINT_URL = "http://localhost:8080";

    public static LoginResponse Login(String username, String password) throws IOException {

        URL url = new URL(ENDPOINT_URL + "/session");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Create request body
        String requestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        connection.connect();

        try (OutputStream requestBodyStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            requestBodyStream.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String jsonResponse = readResponseAsString(responseBody);

            // Parse JSON response into LoginResponse object
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, LoginResponse.class);

        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);

            // Parse JSON response into LoginResponse object
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, LoginResponse.class);
        }
    }
    public static RegisterResponse registerUser(String username, String password, String email) throws IOException {
        URL url = new URL(ENDPOINT_URL + "/user");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String requestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\", \"email\":\"" + email + "\"}";

        connection.connect();

        try (OutputStream requestBodyStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            requestBodyStream.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String jsonResponse = readResponseAsString(responseBody);
            //convert json response to authdata format
            Gson gson = new Gson();
            AuthData authData = gson.fromJson(jsonResponse, AuthData.class);
            RegisterResponse registerResponse = new RegisterResponse(true, authData);
            return registerResponse;

        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, RegisterResponse.class);
        }
    }


    private static String processErrorResponseBody(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                StringBuilder responseBodyBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseBodyBuilder.append(line);
                    responseBodyBuilder.append("\n");
                }
                String responseBody = responseBodyBuilder.toString();

                return extractErrorContentFromJson(responseBody);

            }
        } else {
            return "No error response body available.";
        }
    }
    private static String extractErrorContentFromJson(String jsonResponse) {
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        return jsonElement.getAsJsonObject().get("message").getAsString();
    }
    private static String readResponseAsString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        return responseBuilder.toString();
    }
}
