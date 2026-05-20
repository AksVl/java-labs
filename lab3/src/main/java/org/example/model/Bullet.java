package org.example.model;

public class Bullet extends Entity {
  private final double speed;
  private final boolean playerFired;

  public Bullet(int x, int y, int width, int height, double speed, boolean playerFired) {
    super(x, y, width, height);
    this.speed = speed;
    this.playerFired = playerFired;
  }

  public double getSpeed() { return speed; }
  public boolean isPlayerFired() { return playerFired; }
}