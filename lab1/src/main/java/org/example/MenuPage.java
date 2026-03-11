package org.example;

import java.util.Map;

import static org.example.Main.logger;

public class MenuPage extends Page {

  public static final String unknownOptionMSG = "No such option\n";

  private String msg;
  private final Map<Integer, Runnable> options;

  public MenuPage(String name, String msg, Map<Integer, Runnable> options) {
    super(name);
    this.msg = msg;
    this.options = options;
    this.exit = false;
  }

  public void updateMSG(String newMSG){
    logger.info("msg updated in "+this.toString());
    this.msg = newMSG;
  }

  @Override
  public void run(IOHandler ioHandler, Page previous){
    logger.info("entered "+this.toString());
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
        ioHandler.consumeBuffered();
        continue;
      }
      Runnable action = options.get(choice);
      if (action == null) {
        ioHandler.display(MenuPage.unknownOptionMSG);
        ioHandler.consumeBuffered();
        continue;
      }
      action.run();
    }
  }

  @Override
  public void refresh(IOHandler ioHandler){
    logger.info("refreshed "+this.toString());
    ioHandler.clear();
    ioHandler.display(msg);
  }
}