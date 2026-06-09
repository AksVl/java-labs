package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "sqrt")
public class Sqrt implements Function {
  public final String EMPTY_STACK_MSG = "stack is empty";
  public static final String NEGATIVE_SQRT_MSG = "unable to take square root of a negative number";

  @Override
  public void execute(Context context) {
    if (context.getStack().isEmpty()) {
      throw new FunctionException(EMPTY_STACK_MSG);
    }
    double value = context.getStack().pop();
    if (value < 0) {
      throw new FunctionException(NEGATIVE_SQRT_MSG);
    }
    double result = Math.sqrt(value);
    context.getStack().push(result);
  }
}