package org.example;

public class Page {
  protected boolean exit;
  protected Page previous;

  public Page(){
    this.exit = false;
  }

  public void run(IOHandler ioHandler, Page previous){
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
