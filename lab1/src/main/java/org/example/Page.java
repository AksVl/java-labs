package org.example;

import java.io.IOException;

import static org.example.Main.logger;

public class Page {
  protected String name;
  protected boolean exit;
  protected Page previous;

  public Page(String name) {
    this.name = name;
    this.exit = false;
  }

  public void run(IOHandler ioHandler, Page previous){
    logger.info("entered " + this.toString());
  }

  public void refresh(IOHandler ioHandler) {
    logger.info("refreshed " + this.toString());
  }

  public void exit(IOHandler ioHandler) {
    logger.info("exited " + this.toString());
    if (previous != null) {
      previous.refresh(ioHandler);
    }
    exit = true;
  }

  @Override
  public String toString(){
    return name;
  }
}
