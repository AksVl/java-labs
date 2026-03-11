package org.example;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

public class PagesImpl {
  public static Page game;
  public static MenuPage main;
  public static MenuPage settingsPage;

  public static void initPages(IOHandler ioHandler, GameSettings settings) {

    game = new Page() {
      @Override
      public void run(IOHandler ioHandler, Page previous) {
        this.previous = previous;
        BullsAndCows game = new BullsAndCows(ioHandler, settings);
        int attemptsLeft = settings.getMaxAttempts();

        ioHandler.display("Game started! Try to guess the " + settings.getSecretLength() + "-digit secret.\n");

        while (attemptsLeft > 0) {
          ioHandler.display("\nAttempts left: " + attemptsLeft + "\n");
          ioHandler.display("Enter your guess (or 'q' to quit): ");

          String guess = null;
          try {
            guess = ioHandler.readLineWithTimeout(settings.getAttemptTime(), TimeUnit.SECONDS);
          } catch (IOException e) {
            // Handle I/O error (should not happen with System.in)
            e.printStackTrace();
            break;
          }

          if (guess == null) {
            ioHandler.display("\nTime's up! You took too long.\n");
            attemptsLeft--;
            continue;
          }

          guess = guess.trim();
          BullsAndCows.GuessValidationResult result = game.validateGuess(guess);
          switch (result) {
            case QUIT:
              this.exit(ioHandler);
              return;
            case INVALID:
              ioHandler.display("Invalid guess. Please enter a " + settings.getSecretLength() + "-digit number.\n");
              continue;
            case MATCH:
              ioHandler.display("Congratulations! You guessed the secret: " + game.getSecret() + "\n");
              ioHandler.readLine(); // wait for Enter
              this.exit(ioHandler);
              return;
            case MISMATCH:
              int[] bc = game.getBullsAndCows(guess);
              ioHandler.display("Bulls: " + bc[0] + ", Cows: " + bc[1] + "\n");
              attemptsLeft--;
              break;
          }
        }

        if (attemptsLeft == 0) {
          ioHandler.display("No attempts left. The secret was: " + game.getSecret() + "\n");
        }
        ioHandler.readLine(); // wait for Enter
        this.exit(ioHandler);
      }
    };

    settingsPage = new MenuPage(
            //display actual settings
            "Settings\n\n1)Combination length\n2)Attempt time\n3)Max attempts\n4)Back\n\ntype a number of chosen option:\n",
            Map.of(
                    1, () -> {
                      ioHandler.display("type in new value for chosen parameter:\n");
                      int newValue = -1;
                      while (newValue == -1) {
                        try {
                          newValue = ioHandler.getInt();
                        } catch (Exception e) {
                          ioHandler.display("Invalid input. Please enter a number.\n");
                          ioHandler.getString();  // consume the invalid line
                        }
                      }
                      settings.applyChange(GameSettings.SettingOption.SECRET_LENGTH, newValue);
                      settingsPage.refresh(ioHandler);
                    },
                    2, () -> {
                      ioHandler.display("type in new value for chosen parameter:\n");
                      int newValue = -1;
                      while (newValue == -1) {
                        try {
                          newValue = ioHandler.getInt();
                        } catch (Exception e) {
                          ioHandler.display("Invalid input. Please enter a number.\n");
                          ioHandler.getString();  // consume the invalid line
                        }
                      }
                      settings.applyChange(GameSettings.SettingOption.ATTEMPT_TIME, newValue);
                      settingsPage.refresh(ioHandler);
                    },
                    3, () -> {
                      ioHandler.display("type in new value for chosen parameter:\n");
                      int newValue = -1;
                      while (newValue == -1) {
                        try {
                          newValue = ioHandler.getInt();
                        } catch (Exception e) {
                          ioHandler.display("Invalid input. Please enter a number.\n");
                          ioHandler.getString();  // consume the invalid line
                        }
                      }
                      settings.applyChange(GameSettings.SettingOption.MAX_ATTEMPTS, newValue);
                      settingsPage.refresh(ioHandler);
                    },
                    4, () -> {
                      try {
                        settings.saveToFile(GameSettings.defaultFilePath);
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                      settingsPage.exit(ioHandler);
                    }
            )
    );

    main = new MenuPage(
            "\nBulls&Cows: \n\n1)Play\n2)Settings\n3)Exit\n\ntype a number of chosen option:\n",
            Map.of(
                    1, () -> {
                      // Start the game
                      game.run(ioHandler, main);
                    },
                    2, () -> {
                      // Go to settings page
                      settingsPage.run(ioHandler, main);
                    },
                    3, () -> {
                      main.exit(ioHandler);
                    }
            )
    );
  }
}