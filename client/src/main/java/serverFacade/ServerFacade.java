package serverFacade;

import ResponseTypes.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.AuthData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    public static final String ENDPOINT_URL = "http://localhost:8080";
    public static void clearDB() throws IOException {
        URL url = new URL(ENDPOINT_URL + "/db");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");
        connection.connect();
        if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
            throw new IOException("falied to clear DB");
        }

    }
    public static LoginResponse loginUser(String username, String password) throws IOException {

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
    public static LoginResponse logoutUser(String authToken) throws IOException {
        URL url = new URL(ENDPOINT_URL + "/session");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        // Connect to the server
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Logout successful, return login response with empty authData to indicate success
            return new LoginResponse(true, new AuthData(null, null));
        } else {
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, LoginResponse.class);
        }
    }
    public static CreateGameResponse createGame(String gameName, String authToken) throws IOException {
        URL url = new URL(ENDPOINT_URL + "/game");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        String requestBody = "{\"gameName\":\"" + gameName + "\"}";

        // Connect to the server
        connection.connect();
        try (OutputStream requestBodyStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            requestBodyStream.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String jsonResponse = readResponseAsString(responseBody);

            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, CreateGameResponse.class);


        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, CreateGameResponse.class);
        }
    }
    public static ListGamesResponse listAllGames(String authToken) throws IOException {
        URL url = new URL(ENDPOINT_URL + "/game");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        // Connect to the server
        connection.connect();


        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String jsonResponse = readResponseAsString(responseBody);

            Gson gson = new Gson();
            ListGamesResponse response = gson.fromJson(jsonResponse, ListGamesResponse.class);
            response.setSuccess(true);
            return response;

        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, ListGamesResponse.class);
        }
    }
    public static JoinGameResponse joinGame(int gameNumber, String color, String authToken) throws IOException{
        URL url = new URL(ENDPOINT_URL + "/game");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        // Create request body
        String requestBody = "{\"playerColor\":\"" + color + "\",\"gameID\":\"" + gameNumber + "\"}";

        connection.connect();

        try (OutputStream requestBodyStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            requestBodyStream.write(input, 0, input.length);
        }
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String jsonResponse = readResponseAsString(responseBody);

            Gson gson = new Gson();
            JoinGameResponse response = gson.fromJson(jsonResponse, JoinGameResponse.class);
            response.setSuccess(true);
            return response;

        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            String jsonResponse = readResponseAsString(responseBody);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, JoinGameResponse.class);
        }
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
