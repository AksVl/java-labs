package org.example.controller;

import org.example.model.*;
import org.example.view.GameView;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.util.*;

public class GameController {
  private final GameModel model;
  private final GameView view;
  private final Timer gameTimer;

  private final PlayerController playerController;
  private final EnemyController enemyController;
  private final BulletController bulletController;

  private long lastTickTime;

  public GameController(GameModel model, GameView view) {
    this.model = model;
    this.view = view;
    this.playerController = new PlayerController(model);
    this.enemyController = new EnemyController(model);
    this.bulletController = new BulletController(model);
    this.gameTimer = new Timer(16, this::onTick);

    initGame();
  }

  private void initGame() {
    int screenW = model.getScreenW();
    int screenH = model.getScreenH();
    Player player = new Player(screenW / 2 - 20, screenH - 40, 40, 30, 3);
    model.setPlayer(player);

    List<Enemy> enemies = new ArrayList<>();
    int startX = 50, startY = 50, spacingX = 60, spacingY = 40;
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 6; c++) {
        enemies.add(new Enemy(startX + c * spacingX, startY + r * spacingY, 30, 20, 125));
      }
    }
    model.setEnemies(enemies);
    model.setBullets(new ArrayList<>());
    model.setState(GameState.RUNNING);
    lastTickTime = System.currentTimeMillis();
  }

  public void handleInput(GameInput input) {
    playerController.handleInput(input);
  }

  public void start() {
    view.setVisible(true);
    lastTickTime = System.currentTimeMillis();
    gameTimer.start();
  }

  private void onTick(ActionEvent e) {
    long now = System.currentTimeMillis();
    double delta = (now - lastTickTime) / 1000.0;
    lastTickTime = now;

    if (model.getState() == GameState.RUNNING) {
      playerController.update(now, delta);
      enemyController.update(now, delta);
      bulletController.update(delta);
      handleCollisions();
      checkWinLose();
    }

    view.repaint();

    if (playerController.isShootPressed() && model.getState() != GameState.RUNNING) {
      initGame();
    }
  }

  private void handleCollisions() {
    List<Bullet> bullets = model.getBullets();
    List<Enemy> enemies = model.getEnemies();
    Player player = model.getPlayer();

    Iterator<Bullet> bulletIt = bullets.iterator();
    while (bulletIt.hasNext()) {
      Bullet b = bulletIt.next();
      if (b.isPlayerFired()) {
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
          Enemy e = enemyIt.next();
          if (e.isActive() && e.getBounds().intersects(b.getBounds())) {
            e.setActive(false);
            b.setActive(false);
            break;
          }
        }
      }
    }
    bullets.removeIf(b -> !b.isActive());
    enemies.removeIf(e -> !e.isActive());

    for (Bullet b : bullets) {
      if (!b.isPlayerFired() && b.isActive() && player.getBounds().intersects(b.getBounds())) {
        b.setActive(false);
        player.setLives(player.getLives() - 1);
      }
    }

    for (Enemy e : enemies) {
      if (e.isActive() && (e.getBounds().intersects(player.getBounds()) ||
              e.getY() + e.getHeight() >= model.getScreenH())) {
        player.setLives(player.getLives() - 1);
        e.setActive(false);
      }
    }
  }

  private void checkWinLose() {
    Player player = model.getPlayer();
    if (player.getLives() <= 0)
      model.setState(GameState.GAME_OVER);
    else if (model.getEnemies().isEmpty())
      model.setState(GameState.WON);
  }
}