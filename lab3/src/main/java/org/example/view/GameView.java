package org.example.view;

import org.example.controller.GameController;
import org.example.controller.GameInput;
import org.example.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameView extends JFrame {
  private final GamePanel panel;

  public GameView(GameModel model) {
    setTitle("mvc");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    panel = new GamePanel(model);
    add(panel);
    pack();
    setLocationRelativeTo(null);
  }

  public void setController(GameController controller) {
    panel.setController(controller);
    panel.setFocusable(true);
    panel.requestFocusInWindow();
  }

  private class GamePanel extends JPanel {
    private final GameModel model;
    private GameController controller;
    private final PlayerView playerView;
    private final EnemyView enemyView;
    private final BulletView bulletView;

    public GamePanel(GameModel model) {
      this.model = model;
      this.playerView = new PlayerView();
      this.enemyView = new EnemyView();
      this.bulletView = new BulletView();
      setPreferredSize(new Dimension(model.getScreenW(), model.getScreenH()));
      setBackground(Color.BLACK);
      addKeyListener(new InputAdapter());
    }

    public void setController(GameController controller) {
      this.controller = controller;
    }

    private class InputAdapter extends KeyAdapter {
      private void sendInput(int keyCode, boolean pressed) {
        if (controller == null) return;
        GameInput.Action action = null;
        switch (keyCode) {
          case KeyEvent.VK_LEFT -> action = GameInput.Action.LEFT;
          case KeyEvent.VK_RIGHT -> action = GameInput.Action.RIGHT;
          case KeyEvent.VK_SPACE -> action = GameInput.Action.SHOOT;
        }
        if (action != null) {
          controller.handleInput(new GameInput(action, pressed));
        }
      }
      @Override public void keyPressed(KeyEvent e) { sendInput(e.getKeyCode(), true); }
      @Override public void keyReleased(KeyEvent e) { sendInput(e.getKeyCode(), false); }
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      playerView.draw(g2d, model.getPlayer());
      enemyView.draw(g2d, model.getEnemies());
      bulletView.draw(g2d, model.getBullets());

      g2d.setColor(Color.WHITE);
      g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
      g2d.drawString("health: " + (model.getPlayer() != null ? model.getPlayer().getLives() : 0), 20, 55);

      if (model.getState() == GameState.GAME_OVER) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
        g2d.drawString("GAME OVER", model.getScreenW()/2 - 140, model.getScreenH()/2);
      } else if (model.getState() == GameState.WON) {
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
        g2d.drawString("YOU WIN!", model.getScreenW()/2 - 120, model.getScreenH()/2);
      }
    }
  }
}