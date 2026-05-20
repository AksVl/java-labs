package org.example.model;

import java.awt.Rectangle;

public abstract class Entity {
  protected int x, y, width, height;
  protected boolean active;

  public Entity(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.active = true;
  }

  public Rectangle getBounds() {
    return new Rectangle(x, y, width, height);
  }

  public int getX() { return x; }
  public void setX(int x) { this.x = x; }
  public int getY() { return y; }
  public void setY(int y) { this.y = y; }
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}