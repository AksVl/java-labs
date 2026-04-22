package org.example.commands;

import org.example.Context;

@Command(name = "pop")
public class Pop implements Function {
  @Override
  public void execute(Context context) {
    context.print(context.getStack().pop().toString());
  }
}
