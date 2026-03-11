package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {

  private Page page;
  private Page previous;
  private ByteArrayOutputStream out;
  private IOHandler ioHandler;

  @BeforeEach
  void setUp() {
    out = new ByteArrayOutputStream();
    ioHandler = new IOHandler(new ByteArrayInputStream(new byte[0]), new PrintStream(out));
    page = new Page("TestPage");
    previous = new Page("Previous");
  }

  @Test
  void run_doesNotThrow() {
    page.run(ioHandler, previous);
  }

  @Test
  void refresh_doesNotThrow() {
    page.refresh(ioHandler);
  }

  @Test
  void exit_setsExitFlagAndCallsPreviousRefresh() {
    page.previous = previous;
    page.exit(ioHandler);

    assertTrue(page.exit);
  }

  @Test
  void exit_withoutPrevious_doesNotThrow() {
    page.exit(ioHandler);
    assertTrue(page.exit);
  }

  @Test
  void toString_returnsName() {
    assertEquals("TestPage", page.toString());
  }
}