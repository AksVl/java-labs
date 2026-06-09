package org.example;

import java.util.Arrays;

public class Main {
  public static final String START_MSG = "Calculator started";
  public static final String END_MSG = "Calculator terminated";
  public static final String ERROR_MSG = "Error: ";
  public static final String UNEXPECTED_ERROR_MSG = "Unexpected error: ";
  public static final String EXIT = "exit";

  public static void main(String[] args) {
    IOHandler io = new ConsoleIOHandler();
    CommandFactory factory = new CommandFactory();
    Context context = new Context(io, factory);

    io.output(START_MSG);

    while (true) {
      String line = io.readLine().trim();
      if (line.equalsIgnoreCase(EXIT)) {
        break;
      }
      if (line.isEmpty()) {
        continue;
      }

      String[] parts = line.split("\\s+");
      context.setInput(Arrays.asList(parts));

      try {
        context.execute();
      } catch (CalculatorException e) {
        io.output(ERROR_MSG + e.getMessage());
      } catch (Exception e) {
        io.output(UNEXPECTED_ERROR_MSG + e.getMessage());
      }
    }
    io.output(END_MSG);
  }
}