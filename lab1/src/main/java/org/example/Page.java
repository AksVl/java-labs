package org.example;

import static org.example.Main.logger;

/**
 * base class for all pages in the application
 * page represents a screen or state that the user can interact with
 */
public class Page {
  protected String name;
  protected boolean exit;
  protected Page previous;

  /**
   * constructs a page with given name
   *
   * @param name page name
   */
  public Page(String name) {
    this.name = name;
    this.exit = false;
  }

  /**
   * runs page logic Subclasses
   * base class implementation only logs entry
   *
   * @param ioHandler the handler for input output
   * @param previous  the page that was active before this one
   */
  public void run(IOHandler ioHandler, Page previous) {
    logger.info("entered " + this.toString());
  }

  /**
   * logic that needs to be done after returning to a page
   * base class implementation only logs the refresh
   *
   * @param ioHandler the handler for input output
   */
  public void refresh(IOHandler ioHandler) {
    logger.info("refreshed " + this.toString());
  }

  /**
   * exits current page and returns to previous page
   *
   * @param ioHandler the handler for input output
   */
  public void exit(IOHandler ioHandler) {
    logger.info("exited " + this.toString());
    if (previous != null) {
      previous.refresh(ioHandler);
    }
    exit = true;
  }

  /**
   * returns the name of the page.
   *
   * @return the page name
   */
  @Override
  public String toString() {
    return name;
  }
}
