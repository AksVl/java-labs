package org.example.view;

import org.example.model.Enemy;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

public class EnemyView {
  public void draw(Graphics2D g, List<Enemy> enemies) {
    g.setColor(Color.RED);
    for (Enemy e : enemies) {
      if (e.isActive()) {
        g.fill(e.getBounds());
      }
    }
  }
}