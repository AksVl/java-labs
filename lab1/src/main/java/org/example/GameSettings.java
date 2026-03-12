package org.example;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import static org.example.Main.logger;

/**
 * stores and manages game settings, interacts with storage file
 */
public class GameSettings {

  public static final String defaultFilePath = "settings.txt";
  public static final String PROPERTIES_HEADER = "GAME SETTINGS";
  public static final String SECRET_LENGTH_PROPERTY_NAME = "secret_length";
  public static final String ATTEMPT_TIME_PROPERTY_NAME = "attempt_time";
  public static final String MAX_ATTEMPTS_PROPERTY_NAME = "max_attempts";
  public static final String TIMER_MODE_PROPERTY_NAME = "TIMER_MODE";


  public enum SettingOption {
    SECRET_LENGTH,
    ATTEMPT_TIME,
    MAX_ATTEMPTS,
    TIMER_MODE
  }

  private int secretLength = 4;
  private int maxAttempts = 10;
  private boolean timerMode = true;
  private int attemptTime = 60;

  /**
   * creates a new GameSettings object with default values
   */
  public GameSettings() {
    //
  }

  /**
   * creates a new GameSettings object and loads settings from the specified file
   * if the file does not exist or contains errors, default values are used
   *
   * @param filePath path to the settings file
   */
  public GameSettings(String filePath) {
    loadFromFile(filePath);
  }

  /**
   * applies a change to a specific setting
   *
   * @param setting  the setting to change
   * @param newValue the new value of a setting
   */
  public void applyChange(SettingOption setting, int newValue) {
    switch (setting) {
      case SECRET_LENGTH -> secretLength = newValue;
      case MAX_ATTEMPTS -> maxAttempts = newValue;
      case TIMER_MODE -> timerMode = newValue == 1;
      case ATTEMPT_TIME -> attemptTime = newValue;
    }
  }

  /**
   * saves the current settings to a file
   *
   * @param filePath the path to file
   * @throws RuntimeException if unable to access the file
   */
  public void saveToFile(String filePath) {
    Properties props = new Properties();
    props.setProperty(SECRET_LENGTH_PROPERTY_NAME, String.valueOf(secretLength));
    props.setProperty(MAX_ATTEMPTS_PROPERTY_NAME, String.valueOf(maxAttempts));
    props.setProperty(TIMER_MODE_PROPERTY_NAME, String.valueOf(timerMode));
    props.setProperty(ATTEMPT_TIME_PROPERTY_NAME, String.valueOf(attemptTime));

    try {
      Writer writer = new FileWriter(filePath);
      props.store(writer, PROPERTIES_HEADER);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * loads settings from a file if able to
   *
   * @param filePath the file path to load from
   */
  public void loadFromFile(String filePath) {
    Properties props = new Properties();
    try {
      Reader reader = new FileReader(filePath);
      props.load(reader);
    } catch (IOException e) {
      return;
    }

    String val = props.getProperty(SECRET_LENGTH_PROPERTY_NAME);
    if (val != null) {
      try {
        secretLength = Integer.parseInt(val.trim());
      } catch (NumberFormatException e) {
        logger.log(Level.SEVERE, "unable to read secretLength, default value is used");
      }
    }

    val = props.getProperty(MAX_ATTEMPTS_PROPERTY_NAME);
    if (val != null) {
      try {
        maxAttempts = Integer.parseInt(val.trim());
      } catch (NumberFormatException e) {
        logger.log(Level.SEVERE, "unable to read maxAttempts, default value is used");
      }
    }

    val = props.getProperty(TIMER_MODE_PROPERTY_NAME);
    if (val != null) {
      timerMode = Boolean.parseBoolean(val.trim());
    }

    val = props.getProperty(ATTEMPT_TIME_PROPERTY_NAME);
    if (val != null) {
      try {
        attemptTime = Integer.parseInt(val.trim());
      } catch (NumberFormatException e) {
        logger.log(Level.SEVERE, "unable to read attemptTime, default value is used");
      }
    }
  }

  /**
   * returns the menu string that displays the current settings
   *
   * @return a formatted string suitable for display in a settings menu
   */
  public String getMenuString() {
    return "Settings\n" +
            "\n" +
            "1)Secret length (" + this.getSecretLength() + " digits) \n" +
            "2)Max attempts (" + this.getMaxAttempts() + " attempts) \n" +
            "3)Timer mode (" + (this.getTimerMode() ? "enabled" : "disabled") + ") \n" +
            "4)Attempt time (" + this.getAttemptTime() + " seconds) \n" +
            "5)Back\n" +
            "\n" +
            "type a number of chosen option:\n";
  }

  /**
   * returns a string representation of the current settings for logging
   *
   * @return a string containing all setting values
   */
  @Override
  public String toString() {
    return "{" +
            "secretLength=" + this.getSecretLength() +
            ", maxAttempts=" + this.getMaxAttempts() +
            ", timerMode=" + this.getTimerMode() +
            ", attemptMode=" + this.getAttemptTime() +
            "}";
  }

  public int getSecretLength() {
    return secretLength;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public boolean getTimerMode() {
    return timerMode;
  }

  public int getAttemptTime() {
    return attemptTime;
  }
}