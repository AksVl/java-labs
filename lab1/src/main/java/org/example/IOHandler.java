package org.example;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class IOHandler {
  private final BufferedReader reader;
  private final PrintStream output;

  public IOHandler(InputStream stream, PrintStream output) {
    this.reader = new BufferedReader(new InputStreamReader(stream));
    this.output = output;
  }

  public void display(String msg) {
    output.print(msg);
  }

  public void clear() {
    output.print("\033[H\033[2J");
    output.flush();
  }

  /**
   * Reads a line with a timeout. Returns null if no input within the timeout.
   */
  public String readLineWithTimeout(long timeout, TimeUnit unit) throws IOException {
    long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
    while (System.currentTimeMillis() < deadline) {
      if (reader.ready()) {
        return reader.readLine();
      }
      try {
        Thread.sleep(50); // small pause to avoid busy‑waiting
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }
    }
    return null;
  }

  /**
   * Reads a full line (blocks until Enter is pressed).
   */
  public String readLine() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Reads an integer from a line. Throws NumberFormatException if invalid.
   */
  public int readInt() throws IOException {
    String line = readLine();
    return Integer.parseInt(line.trim());
  }

  // Optional: keep old methods for backward compatibility, but mark them deprecated
  public String getString() {
    return readLine();
  }

  public int getInt() {
    try {
      return readInt();
    } catch (IOException | NumberFormatException e) {
      throw new RuntimeException(e);
    }
  }
}