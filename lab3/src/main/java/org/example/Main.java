package org.example;

import org.example.controller.GameController;
import org.example.model.GameModel;
import org.example.view.GameView;

public class Main {
  public static void main(String[] args) {
    GameModel model = new GameModel();
    GameView guiView = new GameView(model);
    GameController controller = new GameController(model, guiView);
    guiView.setController(controller);
    controller.start();
  }
}