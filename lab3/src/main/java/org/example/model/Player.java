package org.example.model;

public class Player extends Entity {
  private int lives;

  public Player(int x, int y, int width, int height, int lives) {
    super(x, y, width, height);
    this.lives = lives;
  }

  public int getLives() { return lives; }
  public void setLives(int lives) { this.lives = lives; }
}