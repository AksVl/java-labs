package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "plus")
public class Plus implements Function {
  public final String SMALL_STACK_MSG = "stack is too small";

  @Override
  public void execute(Context context) {
    if (context.getStack().size() < 2) {
      throw new FunctionException(SMALL_STACK_MSG);
    }
    double a = context.getStack().pop();
    double b = context.getStack().pop();
    double result = b + a;
    context.getStack().push(result);
  }
}