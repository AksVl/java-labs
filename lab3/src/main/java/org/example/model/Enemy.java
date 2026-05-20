package org.example.model;

public class Enemy extends Entity {
  private double speedX;

  public Enemy(int x, int y, int width, int height, double speedX) {
    super(x, y, width, height);
    this.speedX = speedX;
  }

  public double getSpeedX() { return speedX; }
  public void setSpeedX(double speedX) { this.speedX = speedX; }
}