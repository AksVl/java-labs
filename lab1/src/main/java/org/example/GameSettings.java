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


  public enum SettingOption {
    SECRET_LENGTH,
    ATTEMPT_TIME,
    MAX_ATTEMPTS,
  }

  public static final Map<Integer, SettingOption> options = Map.ofEntries(
          Map.entry(1, SettingOption.SECRET_LENGTH),
          Map.entry(2, SettingOption.ATTEMPT_TIME),
          Map.entry(3, SettingOption.MAX_ATTEMPTS)
  );

  private int secretLength;
  private int attemptTime;
  private int maxAttempts;

  public GameSettings() {
    this.secretLength = 4;
    this.attemptTime = 60;
    this.maxAttempts = 10;
  }

  public GameSettings(String filePath) throws IOException {
    this();
    loadFromFile(filePath);
  }

  public void applyChange(SettingOption setting, int newValue) {
    switch (setting) {
      case SECRET_LENGTH -> secretLength = newValue;
      case ATTEMPT_TIME -> attemptTime = newValue;
      case MAX_ATTEMPTS -> maxAttempts = newValue;
    }
  }

  public void saveToFile(String filePath) throws IOException {
    Properties props = new Properties();
    props.setProperty(SECRET_LENGTH_PROPERTY_NAME, String.valueOf(secretLength));
    props.setProperty(ATTEMPT_TIME_PROPERTY_NAME, String.valueOf(attemptTime));
    props.setProperty(MAX_ATTEMPTS_PROPERTY_NAME, String.valueOf(maxAttempts));

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
      } catch (NumberFormatException ignored) {}
    }

    val = props.getProperty(ATTEMPT_TIME_PROPERTY_NAME);
    if (val != null) {
      try {
        attemptTime = Integer.parseInt(val.trim());
      } catch (NumberFormatException ignored) {}
    }

    val = props.getProperty(MAX_ATTEMPTS_PROPERTY_NAME);
    if (val != null) {
      try {
        maxAttempts = Integer.parseInt(val.trim());
      } catch (NumberFormatException ignored) {}
    }
  }


  public int getSecretLength() { return secretLength; }
  public int getAttemptTime() { return attemptTime; }
  public int getMaxAttempts() { return maxAttempts; }
}