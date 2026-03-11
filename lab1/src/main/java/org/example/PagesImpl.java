package org.example;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;

import static org.example.Main.logger;

public class PagesImpl {
  public static Page gamePage;
  public static final String gamePageName = "Game page";
  public static MenuPage mainPage;
  public static final String mainPageName = "Main page";
  public static MenuPage settingsPage;
  public static final String settingsPageName = "Settings page";

  public static void initPages(IOHandler ioHandler, GameSettings settings) {

    gamePage = new Page(gamePageName) {
      @Override
      public void run(IOHandler ioHandler, Page previous) {
        logger.info(gamePageName + " : " + "game started");
        this.previous = previous;
        String secret = BullsAndCows.generateSecret(settings.getSecretLength());
        logger.info(gamePageName + " : " + "Generated a secret = " + secret);
        int attemptsLeft = settings.getMaxAttempts();

        ioHandler.display("Game started! Try to guess the " + settings.getSecretLength() + "-digit secret.\n");

        while (attemptsLeft > 0) {
          ioHandler.display("Attempts left: " + attemptsLeft + "\n");
          logger.info(gamePageName + " : " + "attempt " + (settings.getMaxAttempts() - attemptsLeft + 1) + "/" + settings.getMaxAttempts());
          String s = settings.getTimerMode() ? "You have " + settings.getAttemptTime() +
                  " seconds to enter your guess (or 'q' to quit): \n" : "Enter your guess (or 'q' to quit): \n";
          ioHandler.display(s);

          String guess;
          try {
            if (settings.getTimerMode()) {
              guess = ioHandler.readLineWithTimeout(settings.getAttemptTime(), TimeUnit.SECONDS);
            } else {
              guess = ioHandler.readLine();
            }
          } catch (IOException e) {
            logger.log(Level.SEVERE, gamePageName + " : " + "IO error while reading guess", e);
            break;
          }

          if (guess == null) {
            logger.info(gamePageName + " : " + "timeout");
            ioHandler.display("Time's up!\n");
            attemptsLeft--;
            continue;
          }

          guess = guess.trim();
          BullsAndCows.GuessValidationResult result = BullsAndCows.validateGuess(guess, secret);
          switch (result) {
            case QUIT:
              logger.info(gamePageName + " : " + "quit");
              this.exit(ioHandler);
              return;
            case INVALID:
              logger.info(gamePageName + " : " + "invalid input");
              ioHandler.display("Invalid guess. Please enter a " + settings.getSecretLength() + "-digit number.\n");
              attemptsLeft--;
              continue;
            case MATCH:
              logger.info(gamePageName + " : " + "guessed right");
              ioHandler.display("Congratulations! You guessed the secret!\n(press enter to exit)\n");
              ioHandler.consumeBuffered();
              this.exit(ioHandler);
              return;
            case MISMATCH:
              logger.info(gamePageName + " : " + "guessed wrong");
              int[] bc = BullsAndCows.getBullsAndCows(guess, secret);
              ioHandler.display("Bulls: " + bc[0] + ", Cows: " + bc[1] + "\n");
              attemptsLeft--;
              break;
          }
        }

        if (attemptsLeft == 0) {
          logger.info(gamePageName + " : " + "no attempts left");
          ioHandler.display("No attempts left. The secret was: " + secret + "\n");
        }
        ioHandler.consumeBuffered();
        this.exit(ioHandler);
      }
    };

    settingsPage = new MenuPage(settingsPageName,
            settings.getMenuString(),
            Map.of(
                    1, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.SECRET_LENGTH, settingsPage),
                    2, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.MAX_ATTEMPTS, settingsPage),
                    3, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.TIMER_MODE, settingsPage),
                    4, () -> changeSetting(ioHandler, settings, GameSettings.SettingOption.ATTEMPT_TIME, settingsPage),
                    5, () -> {
                      settings.saveToFile(GameSettings.defaultFilePath);
                      settingsPage.exit(ioHandler);
                    }
            )
    );


    mainPage = new MenuPage(mainPageName,
            "\nBulls&Cows: " +
                    "\n\n" +
                    "1)Play\n" +
                    "2)Settings\n" +
                    "3)Exit" +
                    "\n\n" +
                    "type a number of chosen option:\n",
            Map.of(
                    1, () -> {
                      gamePage.run(ioHandler, mainPage);
                    },
                    2, () -> {
                      settingsPage.run(ioHandler, mainPage);
                    },
                    3, () -> mainPage.exit(ioHandler)
            )
    );
    logger.info("Pages initialised");
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
    logger.info(settingsPageName + " : " + option + " changed to " + newValue);
    settings.applyChange(option, newValue);
    page.updateMSG(settings.getMenuString());
    page.refresh(ioHandler);
  }
}
