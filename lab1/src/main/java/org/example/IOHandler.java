package org.example;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * handles all input/output operations for the application
 * wraps a {@link BufferedReader} for input and a {@link PrintStream} for output
 */
public class IOHandler {
  private final BufferedReader reader;
  private final PrintStream output;

  /**
   * creates an IOHandler with the given input stream and output stream
   *
   * @param stream the input stream
   * @param output the output stream
   */
  public IOHandler(InputStream stream, PrintStream output) {
    this.reader = new BufferedReader(new InputStreamReader(stream));
    this.output = output;
  }

  /**
   * displays given message
   *
   * @param msg the message to display
   */
  public void display(String msg) {
    output.print(msg);
  }

  /**
   * clears the console screen
   */
  public void clear() {
    output.print("\033[H\033[2J");
    output.flush();
  }

  /**
   * reads a line of input with a timeout
   * the method polls the reader every 50 ms until the timeout expires or input is available
   *
   * @param timeout the amount of time to wait
   * @param unit    the time unit of the timeout
   * @return the line read, or {@code null} if the timeout expires or the thread is interrupted
   * @throws IOException if readLine() fails
   */
  public String readLineWithTimeout(long timeout, TimeUnit unit) throws IOException {
    long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
    while (System.currentTimeMillis() < deadline) {
      if (reader.ready()) {
        return reader.readLine();
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }
    }
    return null;
  }

  /**
   * reads a line of input via reader
   *
   * @return the line read
   * @throws IOException if reader.readline() fails
   */
  public String readLine() throws IOException {
    return reader.readLine();
  }

  /**
   * reads an integer from input via reader
   *
   * @return the integer read
   * @throws IOException if reader.readline() fails
   */
  public int getInt() {
    try {
      String line = readLine();
      return Integer.parseInt(line.trim());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}