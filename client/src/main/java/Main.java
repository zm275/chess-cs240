import ResponseTypes.*;
import model.GameData;
import serverFacade.ServerFacade;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.io.*;

import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;

public class Main {
    private static String authToken;
    private static String userName;
    private static final String ENDPOINT_URL = "http://localhost:8080";

    private static boolean loggedIn = false;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 240 Chess! Enter 1 to the console to access help menu.");
        while (true) {
            if (!loggedIn) {
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
                        preRegister(scanner);
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
    private static void loggedInPhase(Scanner scanner) throws IOException {
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
                logout(authToken);
                // Transition to Prelogin UI
                break;
            case 3:
                System.out.println("Enter the name for the new game:");
                String gameName = scanner.nextLine();
                createGame(gameName);
                break;
            case 4:
                // Call server API to get all game data
                listAllGames();
                break;
            case 5:
                System.out.println("Enter the number of the game you want to join:");
                int gameNumber = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the color you want to play (WHITE or BLACK):");
                String color = scanner.nextLine().toUpperCase();
                // Call server join API to join the user to the specified game with the specified color
                joinGame(gameNumber, color, authToken);
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

    private static void joinGame(int gameNumber, String color, String authToken) throws IOException {
        JoinGameResponse response = ServerFacade.joinGame(gameNumber, color, authToken);
        if (response.isSuccess()){
            System.out.println("Player: " + userName + " is now playing game: " + gameNumber + " as color: " + color);
            printBoard();
        } else {
            System.out.println(response.getMessage());
        }

    }

    private static void listAllGames() throws IOException {
        ListGamesResponse response = ServerFacade.listAllGames(authToken);
        if (response.isSuccess()){
            List<GameData> games = response.getGames();
            System.out.println("You will use the gameID to join or watch a game.");
            for (GameData game : games) {
                System.out.println(game.gameID() + ": " + game.gameName());
            }
        } else {
            System.out.println(response.getMessage());
        }
    }

    private static void createGame(String gameName) throws IOException {
        CreateGameResponse response = ServerFacade.createGame(gameName, authToken);
        if (response != null && response.getGameID() != null) {
            System.out.println("Successfully created game " + gameName);
        } else if(response != null) {
            System.out.println(response.getMessage());
        }
        else {
            System.out.println("Unknown error.");
        }
    }

    private static void logout(String authToken) throws IOException {
        LoginResponse result = ServerFacade.logoutUser(authToken);
        if (result.isSuccess()) {
            loggedIn = false;
            System.out.println("logout successful");
        }
        else {
           System.out.println(result.getMessage());
        }
    }

    private static void preLogin(Scanner scanner) throws IOException {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        LoginResponse result = ServerFacade.loginUser(username, password);
        if (result != null && result.getAuthToken() != null && !result.getAuthToken().isEmpty()) {
            loggedIn = true;
            userName = result.getUsername();
            authToken = result.getAuthToken();
            System.out.println("Login Successful");
        } else {
            System.out.println(result != null ? result.getMessage() : "Unknown error occurred.");
        }
    }
    private static void preRegister(Scanner scanner) throws IOException {
        System.out.println("Enter your desired username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your desired password: ");
        String password = scanner.nextLine();

        System.out.println("Enter your desired email: ");
        String email = scanner.nextLine();

        // If registration successful, login user and transition to Postlogin UI
        RegisterResponse result = ServerFacade.registerUser(username, password, email);
        if (result != null && result.getAuthData() != null && result.getAuthData().authToken() != null && !result.getAuthData().authToken().isEmpty()) {
            loggedIn =  true;
            userName = result.getAuthData().username();
            authToken = result.getAuthData().authToken();
            System.out.println("Registration successful!");
        } else {
            System.out.println(result != null ? result.getMessage() : "Unknown error occurred.");
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
}