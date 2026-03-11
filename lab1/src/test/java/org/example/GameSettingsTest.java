package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameSettingsTest {

  private GameSettings settings;

  @BeforeEach
  void setUp() {
    settings = new GameSettings();
  }

  @Test
  void defaultValuesAreCorrect() {
    assertEquals(4, settings.getSecretLength());
    assertEquals(10, settings.getMaxAttempts());
    assertTrue(settings.getTimerMode());
    assertEquals(60, settings.getAttemptTime());
  }

  @Test
  void applyChange_updatesCorrectSetting() {
    settings.applyChange(GameSettings.SettingOption.SECRET_LENGTH, 5);
    assertEquals(5, settings.getSecretLength());

    settings.applyChange(GameSettings.SettingOption.MAX_ATTEMPTS, 7);
    assertEquals(7, settings.getMaxAttempts());

    settings.applyChange(GameSettings.SettingOption.TIMER_MODE, 0);
    assertFalse(settings.getTimerMode());

    settings.applyChange(GameSettings.SettingOption.ATTEMPT_TIME, 30);
    assertEquals(30, settings.getAttemptTime());
  }

  @Test
  void saveAndLoadFromFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("settings.txt");
    settings.applyChange(GameSettings.SettingOption.SECRET_LENGTH, 6);
    settings.applyChange(GameSettings.SettingOption.MAX_ATTEMPTS, 3);
    settings.applyChange(GameSettings.SettingOption.TIMER_MODE, 0);
    settings.applyChange(GameSettings.SettingOption.ATTEMPT_TIME, 45);
    settings.saveToFile(file.toString());

    GameSettings loaded = new GameSettings(file.toString());
    assertEquals(6, loaded.getSecretLength());
    assertEquals(3, loaded.getMaxAttempts());
    assertFalse(loaded.getTimerMode());
    assertEquals(45, loaded.getAttemptTime());
  }

  @Test
  void loadFromNonExistentFile_keepsDefaults() throws IOException {
    GameSettings loaded = new GameSettings("nonexistent.txt");
    assertEquals(4, loaded.getSecretLength());
    assertEquals(10, loaded.getMaxAttempts());
    assertTrue(loaded.getTimerMode());
    assertEquals(60, loaded.getAttemptTime());
  }

  @Test
  void getMenuString_containsCurrentValues() {
    String menu = settings.getMenuString();
    assertTrue(menu.contains("Secret length (4 digits)"));
    assertTrue(menu.contains("Max attempts (10 attempts)"));
    assertTrue(menu.contains("Timer mode (enabled)"));
    assertTrue(menu.contains("Attempt time (60 seconds)"));
  }
}