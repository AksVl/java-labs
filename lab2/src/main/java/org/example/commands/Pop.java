package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;

@Command(name = "pop")
public class Pop implements Function {
  @Override
  public void execute(Context context) {
    context.output(context.getStack().pop().toString());
  }
}
