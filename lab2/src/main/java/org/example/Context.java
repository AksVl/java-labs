package org.example;

import org.example.commands.Function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Context {
  private Stack<Double> stack;
  private Map<String, Double> defines = new HashMap<>();
  private List<String> input;
  private IOHandler ioHandler;
  private CommandFactory factory;



  public List<String> getInput(){
    return input;
  }

  public Stack<Double> getStack(){
    return stack;
  }

  public CommandFactory getFactory(){
    return factory;
  }

  public void print(String string) {
    ioHandler.print(string);
  }

  public void addDefine(String name, Double value){
    defines.put(name,value);
  }

  public void execute(){
    Function command = null;
    try {
      command = factory.getCommand(input.getFirst()).newInstance();
    }catch(IllegalAccessException e){
      //excp
    } catch (InstantiationException e) {
      //excp
    }
    command.execute(this);
  }
}
