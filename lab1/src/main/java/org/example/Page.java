package org.example;

import java.io.IOException;

public class Page {
  protected boolean exit;
  protected Page previous;

  public Page(){
    this.exit = false;
  }

  public void run(IOHandler ioHandler, Page previous) throws IOException {
    //blank
  }

  public void refresh(IOHandler ioHandler) {
    //blank
  }

  public void exit(IOHandler ioHandler) {
    if (previous != null) {
      previous.refresh(ioHandler);
    }
    exit = true;
  }
}
