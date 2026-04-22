package org.example.commands;

import org.example.Context;
import org.example.FunctionException;

@Command(name = "push")
public class Push implements Function {
  public static final String PARSING_ERROR_MSG = "Unable to parse value to double";

  @Override
  public void execute(Context context) {
    double value;
    try {
      value = Double.parseDouble(context.getInput().get(1));
    } catch (NumberFormatException e) {
      throw new FunctionException(PARSING_ERROR_MSG);
    }
    context.getStack().push(value);
  }
}
