package org.example;

import java.util.Scanner;

public class ConsoleIOHandler implements IOHandler {
  private final Scanner scanner = new Scanner(System.in);

  @Override
  public void output(String message) {
    System.out.println(message);
  }

  @Override
  public String readLine() {
    return scanner.nextLine();
  }
}