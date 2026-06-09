package org.example.view;

import org.example.model.Bullet;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

public class BulletView {
  public void draw(Graphics2D g, List<Bullet> bullets) {
    for (Bullet b : bullets) {
      if (b.isActive()) {
        g.setColor(b.isPlayerFired() ? Color.GREEN : Color.YELLOW);
        g.fill(b.getBounds());
      }
    }
  }
}