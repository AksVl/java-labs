package org.example;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class GameSettings {

  public static final String defaultFilePath = "settings.txt";
  public static final String PROPERTIES_HEADER = "GAME SETTINGS";
  public static final String SECRET_LENGTH_PROPERTY_NAME = "secret_length";
  public static final String ATTEMPT_TIME_PROPERTY_NAME = "attempt_time";
  public static final String MAX_ATTEMPTS_PROPERTY_NAME = "max_attempts";
  public static final String IS_WITH_TIMER_PROPERTY_NAME = "is_with_timer";


  public enum SettingOption {
    SECRET_LENGTH,
    ATTEMPT_TIME,
    MAX_ATTEMPTS,
    IS_WITH_TIMER
  }

  public static final Map<Integer, SettingOption> options = Map.ofEntries(
          Map.entry(1, SettingOption.SECRET_LENGTH),
          Map.entry(2, SettingOption.MAX_ATTEMPTS),
          Map.entry(3, SettingOption.IS_WITH_TIMER),
          Map.entry(4, SettingOption.ATTEMPT_TIME)
  );

  private int secretLength;
  private int maxAttempts;
  private boolean isWithTimer;
  private int attemptTime;

  public GameSettings() {
    this.secretLength = 4;
    this.maxAttempts = 10;
    this.isWithTimer = true;
    this.attemptTime = 60;
  }

  public GameSettings(String filePath) throws IOException {
    this();
    loadFromFile(filePath);
  }

  public void applyChange(SettingOption setting, int newValue) {
    switch (setting) {
      case SECRET_LENGTH -> secretLength = newValue;
      case MAX_ATTEMPTS -> maxAttempts = newValue;
      case IS_WITH_TIMER -> isWithTimer = newValue == 1;
      case ATTEMPT_TIME -> attemptTime = newValue;
    }
  }

  public void saveToFile(String filePath) throws IOException {
    Properties props = new Properties();
    props.setProperty(SECRET_LENGTH_PROPERTY_NAME, String.valueOf(secretLength));
    props.setProperty(MAX_ATTEMPTS_PROPERTY_NAME, String.valueOf(maxAttempts));
    props.setProperty(IS_WITH_TIMER_PROPERTY_NAME, String.valueOf(isWithTimer));
    props.setProperty(ATTEMPT_TIME_PROPERTY_NAME, String.valueOf(attemptTime));

    try (Writer writer = new FileWriter(filePath)) {
      props.store(writer, PROPERTIES_HEADER);
    }
  }

  public void loadFromFile(String filePath) throws IOException {
    Properties props = new Properties();
    try (Reader reader = new FileReader(filePath)) {
      props.load(reader);
    } catch (IOException e) {
      return;
    }

    String val = props.getProperty(SECRET_LENGTH_PROPERTY_NAME);
    if (val != null) {
      try {
        secretLength = Integer.parseInt(val.trim());
      } catch (NumberFormatException ignored) {
      }
    }

    val = props.getProperty(MAX_ATTEMPTS_PROPERTY_NAME);
    if (val != null) {
      try {
        maxAttempts = Integer.parseInt(val.trim());
      } catch (NumberFormatException ignored) {
      }
    }

    val = props.getProperty(IS_WITH_TIMER_PROPERTY_NAME);
    if (val != null) {
      try {
        isWithTimer = Integer.parseInt(val.trim()) == 1;
      } catch (NumberFormatException ignored) {
      }
    }

    val = props.getProperty(ATTEMPT_TIME_PROPERTY_NAME);
    if (val != null) {
      try {
        attemptTime = Integer.parseInt(val.trim());
      } catch (NumberFormatException ignored) {
      }
    }
  }

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

  public int getSecretLength() {
    return secretLength;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public boolean getTimerMode(){
    return isWithTimer;
  }

  public int getAttemptTime() {
    return attemptTime;
  }
}