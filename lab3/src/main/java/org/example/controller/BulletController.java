package org.example.controller;

import org.example.model.*;

public class BulletController {
  private final GameModel model;
  private final double[] yAccumulators;

  public BulletController(GameModel model) {
    this.model = model;
    this.yAccumulators = new double[0];
  }

  public void update(double delta) {
    var bullets = model.getBullets();
    double[] accs = new double[bullets.size()];

    for (int i = 0; i < bullets.size(); i++) {
      Bullet b = bullets.get(i);
      double move = b.getSpeed() * delta;
      int intMove = (int) move;
      accs[i] = move - intMove;
      int newY = b.getY() + intMove;
      b.setY(newY);
      if (newY < 0 || newY > model.getScreenH()) {
        b.setActive(false);
      }
    }

    model.getBullets().removeIf(b -> !b.isActive());
  }
}