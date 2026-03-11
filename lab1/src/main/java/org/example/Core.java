package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.example.Main.logger;

public class Core {
  private final IOHandler ioHandler;

  public Core(InputStream inputStream, PrintStream output) throws IOException {
    this.ioHandler = new IOHandler(inputStream, output);
    GameSettings settings = new GameSettings(GameSettings.defaultFilePath);
    logger.info("Game settings : " + settings);
    PagesImpl.initPages(ioHandler, settings);
  }

  public void start() throws IOException {
    Page current = PagesImpl.mainPage;
    current.run(ioHandler, null);
  }
}