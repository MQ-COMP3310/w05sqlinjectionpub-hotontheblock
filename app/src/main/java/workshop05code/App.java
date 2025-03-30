package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            System.err.println("Could not load logging configuration");
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Wordle created and connected.");
            System.out.println("Wordle created and connected.");
        } else {
            logger.log(Level.SEVERE, "Not able to connect to database");
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "Wordle tables created successfully");
            System.out.println("Wordle structures in place.");
        } else {
            logger.log(Level.SEVERE, "Not able to create Wordle tables");
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file
        logger.log(Level.INFO, "Loading words from data.txt");
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("^[a-z]{4}$")) {
                    logger.log(Level.INFO, "Adding valid word: " + line);
                    wordleDatabaseConnection.addValidWord(i, line);
                    i++;
                } else {
                    logger.log(Level.SEVERE, "Invalid word in data file: " + line);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data.txt", e);
            System.out.println("Not able to load words. Sorry!");
            return;
        }

        // let's get them to enter a word
        logger.log(Level.INFO, "Starting word guessing game");
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine().trim().toLowerCase();

            while (!guess.equals("q")) {
                if (guess.matches("^[a-z]{4}$")) {
                    logger.log(Level.INFO, "User guessed: " + guess);
                    System.out.println("You've guessed '" + guess + "'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        logger.log(Level.INFO, "Valid word found: " + guess);
                        System.out.println("Success! It is in the list.\n");
                    } else {
                        logger.log(Level.INFO, "Invalid word guessed: " + guess);
                        System.out.println("Sorry. This word is NOT in the list.\n");
                    }
                } else {
                    logger.log(Level.WARNING, "Invalid input format: " + guess);
                    System.out.println("Please enter exactly 4 lowercase letters (a-z)\n");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine().trim().toLowerCase();
            }
            logger.log(Level.INFO, "User quit the game");
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Scanner error occurred", e);
            System.out.println("An error occurred. Please restart the game.");
        }
        logger.log(Level.INFO, "Game session ended");
    }
}