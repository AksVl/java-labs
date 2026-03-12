package org.example;

import java.util.Map;

import static org.example.Main.logger;

/**
 * a page that presents a menu of numbered options
 */
public class MenuPage extends Page {

  public static final String unknownOptionMSG = "No such option\n";

  private String msg;

  private final Map<Integer, Runnable> options;

  /**
   * creates a menu page
   *
   * @param name    the page name
   * @param msg     the menu text
   * @param options a map of given options
   */
  public MenuPage(String name, String msg, Map<Integer, Runnable> options) {
    super(name);
    this.msg = msg;
    this.options = options;
    this.exit = false;
  }

  /**
   * updates the menu text
   *
   * @param newMSG new menu text
   */
  public void updateMSG(String newMSG) {
    logger.info("msg updated in " + this.toString());
    this.msg = newMSG;
  }

  /**
   * runs the menu page: displays the menu, reads user choices, and executes chosen options
   *
   * @param ioHandler the handler for input output
   * @param previous  the previous page
   */
  @Override
  public void run(IOHandler ioHandler, Page previous) {
    logger.info("entered " + this.toString());
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
        continue;
      }
      Runnable action = options.get(choice);
      if (action == null) {
        ioHandler.display(MenuPage.unknownOptionMSG);
        continue;
      }
      action.run();
    }
  }

  /**
   * clears output and re-shows menu text
   *
   * @param ioHandler the handler for input output
   */
  @Override
  public void refresh(IOHandler ioHandler) {
    logger.info("refreshed " + this.toString());
    ioHandler.clear();
    ioHandler.display(msg);
  }
}