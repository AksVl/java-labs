package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "print")
public class Print implements Function {
  public final String EMPTY_STACK_MSG = "stack is empty";

  @Override
  public void execute(Context context) {
    if (context.getStack().isEmpty()) {
      throw new FunctionException(EMPTY_STACK_MSG);
    }
    context.output(context.getStack().peek().toString());
  }
}
