package org.example.controller;

import org.example.model.*;
import java.util.*;

public class EnemyController {
  private final GameModel model;
  private long lastEnemyShootTime;
  private final Map<Enemy, Double> xAccumulators;

  public EnemyController(GameModel model) {
    this.model = model;
    this.lastEnemyShootTime = System.currentTimeMillis();
    this.xAccumulators = new LinkedHashMap<>();
  }

  public void update(long now, double delta) {
    List<Enemy> enemies = model.getEnemies();
    int screenW = model.getScreenW();

    xAccumulators.keySet().retainAll(enemies);
    for (Enemy e : enemies) {
      xAccumulators.putIfAbsent(e, 0.0);
    }

    for (Enemy e : enemies) {
      double acc = xAccumulators.get(e) + e.getSpeedX() * delta;
      int move = (int) acc;
      xAccumulators.put(e, acc - move);

      if (move != 0) {
        int newX = e.getX() + move;
        boolean hitWall = false;

        if (newX <= 0) {
          newX = 0;
          hitWall = true;
        } else if (newX + e.getWidth() >= screenW) {
          newX = screenW - e.getWidth();
          hitWall = true;
        }

        e.setX(newX);

        if (hitWall) {
          e.setSpeedX(-e.getSpeedX());
          e.setY(e.getY() + 15);
          xAccumulators.put(e, 0.0);
        }
      }
    }

    if (!enemies.isEmpty() && (now - lastEnemyShootTime) >= 1500) {
      Random rand = new Random();
      Enemy shooter = enemies.get(rand.nextInt(enemies.size()));
      Bullet eb = new Bullet(
              shooter.getX() + shooter.getWidth() / 2,
              shooter.getY() + shooter.getHeight(),
              4, 10, 250, false
      );
      model.getBullets().add(eb);
      lastEnemyShootTime = now;
    }
  }
}