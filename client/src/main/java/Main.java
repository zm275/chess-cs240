import chess.*;

import java.util.Scanner;

public class Main {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private static boolean loggedIn = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (!loggedIn) {
                System.out.println("Welcome to 240 Chess!");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        preLogin(scanner);
                        break;
                    case 2:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                loggedInPhase(scanner);
            }
        }
    }

    private static void preLogin(Scanner scanner) {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        if (username.equals(USERNAME) && password.equals(PASSWORD)) {
            System.out.println("Login successful!");
            loggedIn = true;
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private static void loggedInPhase(Scanner scanner) {
        System.out.println("1. help");
        System.out.println("2. Logout");
        System.out.println(loggedIn ? "[Logged_in] >>>" : "[Logged_out] >>>");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("This is a help string.");
                break;
            case 2:
                loggedIn = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

}