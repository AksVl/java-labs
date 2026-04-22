package org.example.commands;

import org.example.Context;

@Command(name = "print")
public class Print implements Function{
  @Override
  public void execute(Context context) {
    context.print(context.getStack().peek().toString());
  }
}
