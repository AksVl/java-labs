package org.example;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    IOHandler io = new ConsoleIOHandler();
    CommandFactory factory = new CommandFactory();
    Context context = new Context(io, factory);

    io.output("Calculator started.");

    while (true) {
      String line = io.readLine().trim();
      if (line.equalsIgnoreCase("exit")) {
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
        io.output("Error: " + e.getMessage());
      } catch (Exception e) {
        io.output("Unexpected error: " + e.getMessage());
      }
    }
    io.output("Calculator terminated.");
  }
}