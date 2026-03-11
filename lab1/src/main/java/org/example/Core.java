package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Core {
  private final IOHandler ioHandler;
  private GameSettings settings;

  public Core(InputStream inputStream, PrintStream output) throws IOException {
    this.ioHandler = new IOHandler(inputStream, output);
    this.settings = new GameSettings(GameSettings.defaultFilePath);
    PagesImpl.initPages(ioHandler,settings);
  }

  public void start() {
    Page current = PagesImpl.main;
    current.run(ioHandler, null);
  }
}