package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class MenuPageTest {

  private ByteArrayOutputStream out;
  private IOHandler ioHandler;
  private MenuPage menuPage;
  private Map<Integer, Runnable> options;
  private AtomicBoolean option1Executed;
  private AtomicBoolean option2Executed;

  @BeforeEach
  void setUp() {
    out = new ByteArrayOutputStream();
    options = new HashMap<>();
    option1Executed = new AtomicBoolean(false);
    option2Executed = new AtomicBoolean(false);

    options.put(1, () -> option1Executed.set(true));
    options.put(2, () -> option2Executed.set(true));

    menuPage = new MenuPage("Menu", "Choose:\n", options);
  }

  @Test
  void run_displaysMessageAndProcessesValidChoice() {
    options.put(1, () -> {
      option1Executed.set(true);
      menuPage.exit(ioHandler);
    });

    String input = "1\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    menuPage.run(ioHandler, null);

    assertTrue(option1Executed.get());
    assertFalse(option2Executed.get());
    assertTrue(out.toString().contains("Choose:\n"));
  }

  @Test
  void run_handlesInvalidInput() {
    options.put(2, () -> {
      option2Executed.set(true);
      menuPage.exit(ioHandler);
    });

    String input = "abc\n2\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    menuPage.run(ioHandler, null);

    assertTrue(option2Executed.get());
    assertTrue(out.toString().contains(MenuPage.unknownOptionMSG));
  }

  @Test
  void run_handlesUnknownOption() {
    options.put(1, () -> {
      option1Executed.set(true);
      menuPage.exit(ioHandler);
    });

    String input = "99\n1\n";
    ioHandler = new IOHandler(new ByteArrayInputStream(input.getBytes()), new PrintStream(out));

    menuPage.run(ioHandler, null);

    assertTrue(option1Executed.get());
    assertTrue(out.toString().contains(MenuPage.unknownOptionMSG));
  }

  @Test
  void refresh_clearsAndDisplaysMessage() {
    ioHandler = new IOHandler(new ByteArrayInputStream(new byte[0]), new PrintStream(out));
    menuPage.refresh(ioHandler);

    String output = out.toString();
    assertTrue(output.contains("\033[H\033[2J"));
    assertTrue(output.contains("Choose:\n"));
  }

  @Test
  void updateMSG_changesMessage() {
    ioHandler = new IOHandler(new ByteArrayInputStream(new byte[0]), new PrintStream(out));
    menuPage.updateMSG("New menu");
    menuPage.refresh(ioHandler);

    assertTrue(out.toString().contains("New menu"));
  }
}