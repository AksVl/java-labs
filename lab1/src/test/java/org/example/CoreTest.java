package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreTest {

  @Test
  void start_runsMainPageAndExits() throws IOException {
    String input = "3\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);

    Core core = new Core(in, ps);
    core.start();

    String output = out.toString();
    assertTrue(output.contains("Bulls&Cows") || output.contains("Main page"));
  }
}