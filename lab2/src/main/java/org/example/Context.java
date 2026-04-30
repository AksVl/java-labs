package org.example;

import java.util.*;

public class Context {
  private Stack<Double> stack;
  private Map<String, Double> defines = new HashMap<>();
  private List<String> input;
  private IOHandler ioHandler;
  private CommandFactory factory;

  public Context(IOHandler ioHandler, CommandFactory factory) {
    this.stack = new Stack<>();
    this.ioHandler = ioHandler;
    this.factory = factory;
  }

  public Double resolve(String token) {
    Double val = defines.get(token);
    if (val != null) return val;
    try {
      return Double.parseDouble(token);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public List<String> getInput() {
    return input;
  }

  public void setInput(List<String> input) {
    this.input = input;
  }

  public Stack<Double> getStack() {
    return stack;
  }

  public CommandFactory getFactory() {
    return factory;
  }

  public void output(String string) {
    ioHandler.output(string);
  }

  public void addDefine(String name, Double value) {
    defines.put(name, value);
  }

  public void execute() {
    if (input == null || input.isEmpty()) {
      throw new CalculatorException("No input provided");
    }
    String commandName = input.get(0);   // use get(0) for compatibility
    Class<? extends Function> cmdClass = factory.getCommand(commandName);
    try {
      Function command = cmdClass.getDeclaredConstructor().newInstance();
      command.execute(this);
    } catch (Exception e) {
      throw new FunctionException("Failed to instantiate or execute command '" + commandName + "': " + e.getMessage());
    }
  }
}