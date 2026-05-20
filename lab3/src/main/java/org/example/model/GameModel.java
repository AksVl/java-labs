package org.example.model;

import java.util.*;

public class GameModel {
  private Player player;
  private List<Enemy> enemies;
  private List<Bullet> bullets;
  private int score;
  private GameState state;
  private final int screenW = 800, screenH = 600;

  public GameModel() {
    this.enemies = new ArrayList<>();
    this.bullets = new ArrayList<>();
    this.score = 0;
    this.state = GameState.RUNNING;
    this.player = null;
  }

  public Player getPlayer() { return player; }
  public void setPlayer(Player player) { this.player = player; }
  public List<Enemy> getEnemies() { return enemies; }
  public void setEnemies(List<Enemy> enemies) { this.enemies = enemies; }
  public List<Bullet> getBullets() { return bullets; }
  public void setBullets(List<Bullet> bullets) { this.bullets = bullets; }
  public int getScore() { return score; }
  public void setScore(int score) { this.score = score; }
  public GameState getState() { return state; }
  public void setState(GameState state) { this.state = state; }
  public int getScreenW() { return screenW; }
  public int getScreenH() { return screenH; }
}