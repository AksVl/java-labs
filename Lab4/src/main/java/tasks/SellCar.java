package tasks;

import factory.products.Car;
import factory.Storage;
import threadpool.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class SellCar implements Task {
  private final Storage<Car> carStorage;
  private final AtomicInteger dealerDelay;
  private int soldCarsNum;

  public SellCar(Storage<Car> carStorage, int dealerDelay) {
    this.carStorage = carStorage;
    this.dealerDelay = new AtomicInteger(dealerDelay);
  }

  @Override
  public void execute() throws InterruptedException {
    try {
      Car car = carStorage.get();
      Thread.sleep(dealerDelay.get());
      ++soldCarsNum;
    } catch (InterruptedException e) {
      throw e;
    }
  }

  public int getSoldCarsNum() {
    return soldCarsNum;
  }

  @Override
  public String getTaskName() {
    return "Dealer " + this + " sold car: ";
  }

  @Override
  public void setParameters(int parameter) {
    dealerDelay.set(parameter); // Атомарное обновление задержки
  }
}