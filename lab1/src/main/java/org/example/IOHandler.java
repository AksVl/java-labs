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

  public String readLine() throws IOException {
    return reader.readLine();
  }

  public int readInt() throws IOException {
    String line = readLine();
    return Integer.parseInt(line.trim());
  }

  public int getInt() {
    try {
      return readInt();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}