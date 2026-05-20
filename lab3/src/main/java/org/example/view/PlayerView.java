package org.example.view;

import org.example.model.Player;
import java.awt.Graphics2D;
import java.awt.Color;

public class PlayerView {
  public void draw(Graphics2D g, Player player) {
    if (player != null && player.isActive()) {
      g.setColor(Color.CYAN);
      g.fill(player.getBounds());
    }
  }
}