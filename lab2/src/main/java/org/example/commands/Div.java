package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "div")
public class Div implements Function {
  public final String SMALL_STACK_MSG = "stack is too small";
  public static final String DIVISION_BY_ZERO_MSG = "division by zero";

  @Override
  public void execute(Context context) {
    if (context.getStack().size() < 2) {
      throw new FunctionException(SMALL_STACK_MSG);
    }
    double a = context.getStack().pop();
    double b = context.getStack().pop();
    if (a == 0.0) {
      throw new FunctionException(DIVISION_BY_ZERO_MSG);
    }
    double result = b / a;
    context.getStack().push(result);
  }
}