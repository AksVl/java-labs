package org.example;

public class FunctionException extends CalculatorException{
  public static final String FUNCTION_EXCEPTION_MSG = "Unable to execute the function: ";
  public FunctionException(String message){
    super(FUNCTION_EXCEPTION_MSG + message);
  }
}