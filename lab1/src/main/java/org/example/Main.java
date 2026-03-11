package org.example;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
  static final Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) throws IOException {
    FileHandler fh = new FileHandler("app.log");
    fh.setFormatter(new SimpleFormatter());
    Logger.getLogger("org.example").addHandler(fh);
    Logger.getLogger("org.example").setUseParentHandlers(false);
    logger.info("Application started");
    Core core = new Core(System.in, System.out);
    core.start();
  }
}
