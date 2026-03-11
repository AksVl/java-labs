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
      public void run(IOHandler ioHandler, Page previous) throws IOException {
        this.previous = previous;
        String secret = BullsAndCows.generateSecret(settings.getSecretLength());
        int attemptsLeft = settings.getMaxAttempts();

        ioHandler.display("Game started! Try to guess the " + settings.getSecretLength() + "-digit secret.\n");

        while (attemptsLeft > 0) {
          ioHandler.display("Attempts left: " + attemptsLeft + "\n");
          String s = settings.getTimerMode() ? "You have " + settings.getAttemptTime() +
                  " seconds to enter your guess (or 'q' to quit): \n" : "Enter your guess (or 'q' to quit): \n";
          ioHandler.display(s);

          String guess = null;
          try {
            if (settings.getTimerMode()) {
              guess = ioHandler.readLineWithTimeout(settings.getAttemptTime(), TimeUnit.SECONDS);
            } else {
              guess = ioHandler.readLine();
            }
          } catch (IOException e) {
            e.printStackTrace();
            break;
          }

          if (guess == null) {
            ioHandler.display("Time's up!\n");
            attemptsLeft--;
            continue;
          }

          guess = guess.trim();
          BullsAndCows.GuessValidationResult result = BullsAndCows.validateGuess(guess, secret);
          switch (result) {
            case QUIT:
              this.exit(ioHandler);
              return;
            case INVALID:
              ioHandler.display("Invalid guess. Please enter a " + settings.getSecretLength() + "-digit number.\n");
              attemptsLeft--;
              continue;
            case MATCH:
              ioHandler.display("Congratulations! You guessed the secret!\n");
              ioHandler.readLine();
              this.exit(ioHandler);
              return;
            case MISMATCH:
              int[] bc = BullsAndCows.getBullsAndCows(guess,secret);
              ioHandler.display("Bulls: " + bc[0] + ", Cows: " + bc[1] + "\n");
              attemptsLeft--;
              break;
          }
        }

        if (attemptsLeft == 0) {
          ioHandler.display("No attempts left. The secret was: " + secret + "\n");
        }
        ioHandler.readLine(); // wait for Enter
        this.exit(ioHandler);
      }
    };

    settingsPage = new MenuPage(
            //display actual settings
            settings.getMenuString(),
            Map.of(
                    1, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.SECRET_LENGTH, settingsPage),
                    2, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.MAX_ATTEMPTS, settingsPage),
                    3, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.IS_WITH_TIMER, settingsPage),
                    4, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.ATTEMPT_TIME, settingsPage),
                    5, () -> {
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
                      try {
                        game.run(ioHandler, main);
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    },
                    2, () -> {
                      // Go to settings page
                      settingsPage.run(ioHandler, main);
                    },
                    3, () -> main.exit(ioHandler)
            )
    );
  }

  private static void changeSetting(IOHandler ioHandler, GameSettings settings,
                                    GameSettings.SettingOption option, MenuPage page) {
    ioHandler.display("type in new value for chosen parameter:\n");
    int newValue = -1;
    while (newValue == -1) {
      try {
        newValue = ioHandler.getInt();
      } catch (Exception e) {
        ioHandler.display("Invalid input. Please enter a number.\n");
        ioHandler.consumeBuffered();
      }
    }
    settings.applyChange(option, newValue);
    page.updateMSG(settings.getMenuString());
    page.refresh(ioHandler);
  }
}
