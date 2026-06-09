package org.example.controller;

import org.example.model.*;

public class PlayerController {
  private static final long SHOOT_DELAY = 500;
  private static final double MOVEMENT_SPEED = 300.0;

  private final GameModel model;
  private long lastShootTime;
  private boolean leftPressed, rightPressed, shootPressed;
  private double xAccumulator;

  public PlayerController(GameModel model) {
    this.model = model;
    this.lastShootTime = 0;
    this.xAccumulator = 0;
  }

  public void handleInput(GameInput input) {
    switch (input.getAction()) {
      case LEFT -> leftPressed = input.isPressed();
      case RIGHT -> rightPressed = input.isPressed();
      case SHOOT -> shootPressed = input.isPressed();
    }
  }

  public void update(long now, double delta) {
    Player player = model.getPlayer();
    if (player == null) return;

    xAccumulator += MOVEMENT_SPEED * delta * ((leftPressed ? -1 : 0) + (rightPressed ? 1 : 0));
    int move = (int) xAccumulator;
    if (move != 0) {
      int newX = player.getX() + move;
      newX = Math.max(0, Math.min(newX, model.getScreenW() - player.getWidth()));
      player.setX(newX);
      xAccumulator -= move;
    }

    if (shootPressed && (now - lastShootTime) >= SHOOT_DELAY) {
      Bullet bullet = new Bullet(
              player.getX() + player.getWidth() / 2 - 2,
              player.getY(),
              4, 10, -437, true
      );
      model.getBullets().add(bullet);
      lastShootTime = now;
    }
  }

  public boolean isShootPressed() { return shootPressed; }
}