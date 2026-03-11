package org.example;

import java.util.Map;
import java.util.function.Consumer;

public class MenuPage extends Page {

  public static final String unknownOptionMSG = "No such option\n";

  private final String msg;
  private final Map<Integer, Runnable> options;

  public MenuPage(String msg, Map<Integer, Runnable> options) {
    this.msg = msg;
    this.options = options;
    this.exit = false;
  }

  @Override
  public void run(IOHandler ioHandler, Page previous) {
    this.previous = previous;
    this.exit = false;
    ioHandler.clear();
    ioHandler.display(msg);
    while (!exit) {
      int choice;
      try {
        choice = ioHandler.getInt();
      } catch (Exception e) {
        ioHandler.display(MenuPage.unknownOptionMSG);
        ioHandler.getString();
        continue;
      }
      Runnable action = options.get(choice);
      if (action == null) {
        ioHandler.display(MenuPage.unknownOptionMSG);
        ioHandler.getString();
        continue;
      }
      action.run();
    }
  }

  @Override
  public void refresh(IOHandler ioHandler){
    ioHandler.clear();
    ioHandler.display(msg);
  }
}