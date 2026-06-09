package org.example.controller;

public class GameInput {
  public enum Action {
    LEFT, RIGHT, SHOOT
  }

  private final Action action;
  private final boolean pressed;

  public GameInput(Action action, boolean pressed) {
    this.action = action;
    this.pressed = pressed;
  }

  public Action getAction() { return action; }
  public boolean isPressed() { return pressed; }
}