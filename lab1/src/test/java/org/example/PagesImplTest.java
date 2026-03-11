package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PagesImplTest {

  private ByteArrayOutputStream out;
  private IOHandler ioHandler;
  private GameSettings settings;

  @BeforeEach
  void setUp() {
    out = new ByteArrayOutputStream();
    settings = new GameSettings();
    PagesImpl.initPages(ioHandler, settings);
  }

  @Test
  void pagesAreInitialized() {
    assertNotNull(PagesImpl.mainPage);
    assertNotNull(PagesImpl.settingsPage);
    assertNotNull(PagesImpl.gamePage);
    assertEquals(PagesImpl.mainPageName, PagesImpl.mainPage.toString());
    assertEquals(PagesImpl.settingsPageName, PagesImpl.settingsPage.toString());
    assertEquals(PagesImpl.gamePageName, PagesImpl.gamePage.toString());
  }

  @Test
  void gamePage_run_winScenario() {
    settings.applyChange(GameSettings.SettingOption.SECRET_LENGTH, 4);
    settings.applyChange(GameSettings.SettingOption.MAX_ATTEMPTS, 3);
    settings.applyChange(GameSettings.SettingOption.TIMER_MODE, 0);

    String input = "q\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    PagesImpl.gamePage.run(ioHandler, null);

    String output = out.toString();
    assertTrue(output.contains("Game started!"));
  }

  @Test
  void gamePage_run_quitViaQ() {
    settings.applyChange(GameSettings.SettingOption.TIMER_MODE, 0);
    String input = "q\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    PagesImpl.gamePage.run(ioHandler, null);

    String output = out.toString();
    assertTrue(output.contains("Game started!"));
  }

  @Test
  void settingsPage_run_andExit() {
    String input = "5\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    PagesImpl.settingsPage.run(ioHandler, null);

    assertTrue(out.toString().contains("Settings"));
  }

  @Test
  void mainPage_run_andExit() {
    String input = "3\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    PagesImpl.mainPage.run(ioHandler, null);

    assertTrue(out.toString().contains("Bulls&Cows"));
  }
}