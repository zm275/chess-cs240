import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import spark.Request;
import ui.EscapeSequences;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;

public class Main {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    private static final String ENDPOINT_URL = "http://localhost:8080";


    private static boolean loggedIn = false;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 240 Chess! Enter 1 to the console to access help menu.");
        while (true) {
            if (!loggedIn) {
                System.out.println("Enter 1 to the console to access help menu.");
                System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        displayHelp();
                        break;
                    case 2:
                        System.out.println("Exiting...");
                        System.exit(0);
                    case 3:
                        preLogin(scanner);
                        break;
                    case 4:
                        register(scanner);
                        break;
                    case 5:
                        printBoard();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                loggedInPhase(scanner);
            }
        }
    }

    private static void preLogin(Scanner scanner) throws IOException {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

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
            System.out.println("Login successful!");
            loggedIn = true;

        } else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream responseBody = connection.getErrorStream();
            try {
                // Process the error response body
                processErrorResponseBody(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void loggedInPhase(Scanner scanner) {
        System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("1. Help - Displays text informing the user what actions they can take.");
                System.out.println("2. Logout - Logs out the user. Calls the server logout API to logout the user. After logging out with the server, the client should transition to the Prelogin UI.");
                System.out.println("3. Create Game - Allows the user to input a name for the new game. Calls the server create API to create the game. This does not join the player to the created game; it only creates the new game in the server.");
                System.out.println("4. List Games - Lists all the games that currently exist on the server. Calls the server list API to get all the game data, and displays the games in a numbered list, including the game name and players (not observers) in the game. The numbering for the list should be independent of the game IDs.");
                System.out.println("5. Join Game - Allows the user to specify which game they want to join and what color they want to play. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to join the user to the game.");
                System.out.println("6. Join Observer - Allows the user to specify which game they want to observe. They should be able to enter the number of the desired game. Your client will need to keep track of which number corresponds to which game from the last time it listed the games. Calls the server join API to verify that the specified game exists.");
                break;
            case 2:
                loggedIn = false;
                // Call server logout API here
                // Transition to Prelogin UI
                break;
            case 3:
                System.out.println("Enter the name for the new game:");
                String gameName = scanner.nextLine();
                // Call server create API here
                break;
            case 4:
                // Call server list API to get all game data
                // Display games with numbering
                // Handle join/join observer based on user choice
                break;
            case 5:
                System.out.println("Enter the number of the game you want to join:");
                int gameNumber = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the color you want to play (WHITE or BLACK):");
                String color = scanner.nextLine().toUpperCase();
                // Call server join API to join the user to the specified game with the specified color
                break;
            case 6:
                System.out.println("Enter the number of the game you want to observe:");
                int observeGameNumber = scanner.nextInt();
                scanner.nextLine();
                // Call server join API to verify that the specified game exists
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void register(Scanner scanner) throws IOException {
        System.out.println("Enter your desired username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your desired password: ");
        String password = scanner.nextLine();

        System.out.println("Enter your desired email: ");
        String email = scanner.nextLine();

        // If registration successful, login user and transition to Postlogin UI
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

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            System.out.println("Registration successful!");
            loggedIn = true;
        } else {
            InputStream responseBody = connection.getErrorStream();
            try {
                processErrorResponseBody(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static void displayHelp() {
        System.out.println("Help - Displaying available commands:");
        System.out.println("1. Help - Displays this message");
        System.out.println("2. Quit - Exits the program");
        System.out.println("3. Login - Prompts the user to input login information");
        System.out.println("4. Register - Prompts the user to input registration information");
    }

    public static void printBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        printWhiteOrientation(board);
        printBlackBorder();
        printBlackOrientation(board);

    }

    private static void printBlackBorder() {
        for (int i = 0; i <= 9; i++ ){
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            System.out.print("   ");
        }
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    public static void printWhiteOrientation(ChessBoard board) {
        // Draw the chessboard with escape sequences
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(EscapeSequences.moveCursorToLocation(1, 1));

        boolean blackSquare = false;
        for (int row = 0; row <= 9; row++) {
            //This creates the letter row on top and bottom
            if (row == 0 || row == 9) {
                for (int edge_col = 9; edge_col >= 0; edge_col -= 1) {
                    if (edge_col == 0 || edge_col == 9) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                        System.out.print("   ");
                        continue;
                    }
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + mapNumberToChar(edge_col) + " ");
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.println();
                continue;
            }
            for (int col = 0; col <= 9; col++) {
                if (col == 0 || col == 9) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + row + " ");
                    continue;

                }
                if (blackSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                }

                System.out.print(" " + getChessPiece(board, row, col) + " "); // Add spaces around the piece
                blackSquare = !blackSquare;
            }
            blackSquare = !blackSquare;
            System.out.print(EscapeSequences.RESET_BG_COLOR);

            System.out.println();

        }

        // Reset colors to default
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.ERASE_LINE);
    }
    public static void printBlackOrientation(ChessBoard board) {
        // Draw the chessboard with escape sequences
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(EscapeSequences.moveCursorToLocation(1, 1));

        boolean blackSquare = false;
        for (int row = 9; row >= 0; row--) {
            //This creates the letter row on top and bottom
            if (row == 0 || row == 9) {
                for (int edge_col = 0; edge_col <= 9; edge_col += 1) {
                    if (edge_col == 0 || edge_col == 9) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                        System.out.print("   ");
                        continue;
                    }
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + mapNumberToChar(edge_col) + " ");
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.println();
                continue;
            }
            for (int col = 9; col >= 0; col--) {
                if (col == 0 || col == 9) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GRAY);
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    System.out.print(" " + row + " ");
                    continue;

                }
                if (blackSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                }

                System.out.print(" " + getChessPiece(board, row, col) + " "); // Add spaces around the piece
                blackSquare = !blackSquare;
            }
            blackSquare = !blackSquare;
            System.out.print(EscapeSequences.RESET_BG_COLOR);

            System.out.println();

        }

        // Reset colors to default
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.ERASE_LINE);
    }

    private static String getChessPiece(ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return " ";
        } else {
            String pieceSymbol = String.valueOf(piece);
            if (piece.getTeamColor() == BLACK) {
                return EscapeSequences.SET_TEXT_COLOR_BLUE + pieceSymbol;
            }
            else {
                return EscapeSequences.SET_TEXT_COLOR_RED + pieceSymbol.toUpperCase();
            }

        }
    }
    public static char mapNumberToChar(int number) {
        return (char) ('a' + number - 1);
    }

    private static void processErrorResponseBody(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                StringBuilder responseBodyBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseBodyBuilder.append(line);
                    responseBodyBuilder.append("\n");
                }
                String responseBody = responseBodyBuilder.toString();

                // Print or process the error response body
                String errorContent = extractErrorContentFromJson(responseBody);
                System.out.println(errorContent);
            }
        } else {
            System.out.println("No error response body available.");
        }
    }
    private static String extractErrorContentFromJson(String jsonResponse) {
        JsonElement jsonElement = JsonParser.parseString(jsonResponse);
        return jsonElement.getAsJsonObject().get("message").getAsString();
    }
}