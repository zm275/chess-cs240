import ResponseTypes.*;
import chess.*;
import model.GameData;
import serverFacade.ServerFacade;
import serverFacade.WebsocketClient;
import ui.EscapeSequences;

import java.io.*;

import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class Main {
    private static String authToken;
    private static String userName;
    private static String playerColor;
    private static final ServerFacade serverFacade  = new ServerFacade(8080);
    private static boolean loggedIn = false;
    private static boolean inGame = false;
    private static boolean observer = false;
    private static WebsocketClient websocketClient;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 240 Chess! Enter 1 to the console to access help menu.");
        while (true) {
            if (!loggedIn) {
                System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

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
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume invalid input
                }
            } else if (!inGame) {
                loggedInPhase(scanner);
            }
            else {
                gamePlayPhase(scanner);
            }
        }
    }
    private static void gamePlayPhase(Scanner scanner) throws IOException {
        System.out.println(playerColor + ">>>");
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Help - Displays text informing the user what actions they can take.");
                    System.out.println("Redraw Chess Board - Redraws the chess board upon the user’s request.");
                    System.out.println("Leave - Removes the user from the game (whether they are playing or observing the game). The client transitions back to the Post-Login UI.");
                    System.out.println("Make Move - Allow the user to input what move they want to make. The board is updated to reflect the result of the move, and the board automatically updates on all clients involved in the game.");
                    System.out.println("Resign - Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over. Does not cause the user to leave the game.");
                    System.out.println("Highlight Legal Moves - Allows the user to input what piece for which they want to highlight legal moves. The selected piece’s current square and all squares it can legally move to are highlighted. This is a local operation and has no effect on remote users’ screens.");
                    break;
                case 2:
                    //redraw the chessboard
                    websocketClient.redrawChessBoard();
                    break;
                case 3:
                    websocketClient.leave();
                    System.out.println("Leaving the game.");
                    inGame = false;
                    observer = false;
                    break;
                case 4:
                    makeMove(scanner);
                    break;
                case 5:
                    websocketClient.resign();
                    break;
                case 6:
                    highlightLegalMoves(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume invalid input
        }
    }

    private static void highlightLegalMoves(Scanner scanner) throws IOException {
        System.out.println("Enter the start position (e.g., 'a2'): ");
        String startPosition = scanner.nextLine().trim();

        // Parse start position
        int startRow = Integer.parseInt(startPosition.substring(1));
        char startChar = startPosition.substring(0).charAt(0);
        int startCol = charToNumber(startChar);// Convert column label to index
        websocketClient.highlightLegalMoves(new ChessPosition(startRow, startCol));
    }

    private static void makeMove(Scanner scanner) throws IOException {
        System.out.println("Enter the start position (e.g., 'a2'): ");
        String startPosition = scanner.nextLine().trim();

        System.out.println("Enter the end position (e.g., 'a4'): ");
        String endPosition = scanner.nextLine().trim();

        // Parse start position
        int startRow = Integer.parseInt(startPosition.substring(1));
        char startChar = startPosition.substring(0).charAt(0);
        int startCol = charToNumber(startChar);// Convert column label to index

        // Parse end position
        int endRow = Integer.parseInt(endPosition.substring(1));
        int endCol = charToNumber(endPosition.substring(0).charAt(0)); // Convert column label to index
        ChessPiece.PieceType upgradedPiece = null; // Initialize upgradedPiece to null

        if ((endRow == 1) || ((endRow == 8))){
            System.out.println("What would you like to upgrade your pawn too?");

            // Loop until a valid upgrade choice is made
            while (upgradedPiece == null) {
                System.out.println("What would you like to upgrade your pawn to? (Enter 'queen', 'knight', 'rook', or 'bishop'): ");
                String upgradeChoice = scanner.nextLine().trim().toLowerCase();
                ChessGame.TeamColor color;
                if (playerColor == "WHITE") {
                    color = WHITE;
                } else {
                    color = BLACK;
                }

                switch (upgradeChoice) {
                    case "queen":
                        upgradedPiece = ChessPiece.PieceType.QUEEN; // Assuming playerColor is already defined
                        break;
                    case "knight":
                        upgradedPiece = ChessPiece.PieceType.KNIGHT;
                        break;
                    case "rook":
                        upgradedPiece = ChessPiece.PieceType.ROOK;
                        break;
                    case "bishop":
                        upgradedPiece = ChessPiece.PieceType.BISHOP;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 'queen', 'knight', 'rook', or 'bishop'.");
                        // The loop will continue and prompt the user again
                }
            }
        }
        // Create ChessMove object
        ChessMove move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), upgradedPiece);
        websocketClient.makeMove(move);

    }
    private static void loggedInPhase(Scanner scanner) throws Exception {
        System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newlin

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
                    int gameNumber = 0;
                    boolean validInput = false;
                    while (!validInput) {
                        System.out.println("Enter the number of the game you want to join:");
                        try {
                            gameNumber = Integer.parseInt(scanner.nextLine());
                            validInput = true; // Set to true if parsing succeeds
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid integer.");
                            continue; // Restart the loop to ask for input again
                        }
                    }
                    System.out.println("Enter the color you want to play (WHITE or BLACK):");
                    String color = scanner.nextLine().toUpperCase();
                    // Call server join API to join the user to the specified game with the specified color
                    joinGame(gameNumber, color, authToken);
                    break;
                case 6:
                    int observeGameNumber = 0;
                    boolean validObserveInput = false;
                    while (!validObserveInput) {
                        System.out.println("Enter the number of the game you want to observe:");
                        try {
                            observeGameNumber = Integer.parseInt(scanner.nextLine());
                            validObserveInput = true; // Set to true if parsing succeeds
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid integer.");
                            continue; // Restart the loop to ask for input again
                        }
                    }
                    // Call server join API to verify that the specified game exists
                    watchGame(observeGameNumber, authToken);
                    inGame = true;
                    observer = true;
                    gamePlayPhase(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume invalid input
        }
    }

    private static void watchGame(int observeGameNumber, String authToken) throws Exception {
        JoinGameResponse response = serverFacade.joinGame(observeGameNumber, "", authToken);
        if (response.isSuccess()){
            playerColor = "Observer";
            System.out.println("Player: " + userName + " is now watching game: " + observeGameNumber);
            websocketClient = new WebsocketClient(false, 8080, observeGameNumber,null, authToken, userName);
        } else {
            System.out.println(response.getMessage());
        }
    }

    private static void joinGame(int gameNumber, String color, String authToken) throws Exception {
        JoinGameResponse response = serverFacade.joinGame(gameNumber, color, authToken);
        if (response.isSuccess()){
            System.out.println("Player: " + userName + " is now playing game: " + gameNumber + " as color: " + color);
            inGame = true;
            // Convert color string to TeamColor enum
            ChessGame.TeamColor teamColor = null;
            if (color.equalsIgnoreCase("WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                System.out.println("Invalid color: " + color);
            }
            if (teamColor != null){
                playerColor = teamColor.toString();
            } else {
                playerColor = "Observer";
            }
            websocketClient = new WebsocketClient(true, 8080, gameNumber, teamColor, authToken, userName);
        } else {
            System.out.println(response.getMessage());
        }

    }
    private static void listAllGames() throws IOException {
        ListGamesResponse response = serverFacade.listAllGames(authToken);
        if (response.isSuccess()){
            List<GameData> games = response.getGames();
            System.out.println("You will use the gameID to join or watch a game.");
            for (GameData game : games) {
                System.out.println(game.gameID() + ": " + game.gameName() + ", WHITE: " + game.whiteUsername() + ", BLACK: " + game.blackUsername());
            }
        } else {
            System.out.println(response.getMessage());
        }
    }
    private static void createGame(String gameName) throws IOException {
        CreateGameResponse response = serverFacade.createGame(gameName, authToken);
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
        LoginResponse result = serverFacade.logoutUser(authToken);
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
        LoginResponse result = serverFacade.loginUser(username, password);
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
        RegisterResponse result = serverFacade.registerUser(username, password, email);
        if (result != null && result.getAuthData() != null && result.getAuthData().authToken() != null && !result.getAuthData().authToken().isEmpty()) {
            loggedIn =  true;
            userName = result.getAuthData().username();
            authToken = result.getAuthData().authToken();
            System.out.println("Registration successful!");
        } else {
            System.out.println(result != null ? result.getMessage() : "Unknown error occurred.");
        }

    }
    public static int charToNumber(char character) {
        switch (Character.toLowerCase(character)) {
            case 'h':
                return 1;
            case 'g':
                return 2;
            case 'f':
                return 3;
            case 'e':
                return 4;
            case 'd':
                return 5;
            case 'c':
                return 6;
            case 'b':
                return 7;
            case 'a':
                return 8;
            default:
                return -1; // Return -1 for characters not in the mapping
        }
    }

    private static void displayHelp() {
        System.out.println("Help - Displaying available commands:");
        System.out.println("1. Help - Displays this message");
        System.out.println("2. Quit - Exits the program");
        System.out.println("3. Login - Prompts the user to input login information");
        System.out.println("4. Register - Prompts the user to input registration information");
    }


}