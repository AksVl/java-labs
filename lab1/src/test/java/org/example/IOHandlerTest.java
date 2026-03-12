package org.example;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class IOHandlerTest {

  @Test
  void display_writesToOutput() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    IOHandler io = new IOHandler(new ByteArrayInputStream(new byte[0]), ps);

    io.display("Hello");
    assertEquals("Hello", out.toString());
  }

  @Test
  void clear_sendsEscapeSequences() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    IOHandler io = new IOHandler(new ByteArrayInputStream(new byte[0]), ps);

    io.clear();
    assertEquals("\033[H\033[2J", out.toString());
  }

  @Test
  void readLine_returnsInput() throws IOException {
    String input = "test line\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    IOHandler io = new IOHandler(in, System.out);

    assertEquals("test line", io.readLine());
  }

  @Test
  void readInt_parsesInteger() throws IOException {
    String input = "42\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    IOHandler io = new IOHandler(in, System.out);

    assertEquals(42, io.getInt());
  }

  @Test
  void getInt_returnsIntOrThrowsRuntimeException() {
    ByteArrayInputStream in = new ByteArrayInputStream("42\n".getBytes());
    IOHandler io = new IOHandler(in, System.out);
    assertEquals(42, io.getInt());

    in = new ByteArrayInputStream("abc\n".getBytes());
    IOHandler io2 = new IOHandler(in, System.out);
    assertThrows(RuntimeException.class, io2::getInt);
  }

  @Test
  void readLineWithTimeout_returnsLineWithinTimeout() throws IOException, InterruptedException {
    PipedInputStream pipedIn = new PipedInputStream();
    PipedOutputStream pipedOut = new PipedOutputStream(pipedIn);
    IOHandler io = new IOHandler(pipedIn, System.out);

    Thread writer = new Thread(() -> {
      try {
        Thread.sleep(100);
        pipedOut.write("hello\n".getBytes());
        pipedOut.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    writer.start();

    String result = io.readLineWithTimeout(1, TimeUnit.SECONDS);
    assertEquals("hello", result);
    writer.join();
  }

  @Test
  void readLineWithTimeout_returnsNullOnTimeout() throws IOException {
    PipedInputStream pipedIn = new PipedInputStream();
    IOHandler io = new IOHandler(pipedIn, System.out);

    String result = io.readLineWithTimeout(100, TimeUnit.MILLISECONDS);
    assertNull(result);
  }
}