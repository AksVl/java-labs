package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "push")
public class Push implements Function {
  public static final String PARSING_ERROR_MSG = "Unable to parse value to double";

  @Override
  public void execute(Context context) {
    Double value = context.resolve(context.getInput().get(1));
    if (value != null) {
      context.getStack().push(value);
    } else {
      throw new FunctionException(PARSING_ERROR_MSG);
    }
  }
}
