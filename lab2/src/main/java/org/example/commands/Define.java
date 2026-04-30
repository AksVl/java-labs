package org.example.commands;

import org.example.Command;
import org.example.Context;
import org.example.Function;
import org.example.FunctionException;

@Command(name = "define")
public class Define implements Function {
  public static final String PARSING_ERROR_MSG = "Unable to parse value to double";

  @Override
  public void execute(Context context) {
    String name;
    double value;
    name = context.getInput().get(1);
    try {
      value = Double.parseDouble(context.getInput().get(2));
    } catch (NumberFormatException e) {
      throw new FunctionException(PARSING_ERROR_MSG);
    }
    context.addDefine(name, value);
  }
}
