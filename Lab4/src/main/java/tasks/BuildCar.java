package tasks;

import factory.*;
import factory.products.*;
import threadpool.Task;

import java.util.Map;

public class BuildCar implements Task {
  private final Map<Class<? extends Product>, Storage<? extends Product>> detailStorages;
  private final Storage<Car> carStorage;

  public BuildCar(Map<Class<? extends Product>, Storage<? extends Product>> detailStorages) {
    this.detailStorages = detailStorages;
    this.carStorage = (Storage<Car>) detailStorages.get(Car.class);
  }

  @Override
  public void execute() throws InterruptedException {
    try {
      Body body = (Body) detailStorages.get(Body.class).get();
      Motor motor = (Motor) detailStorages.get(Motor.class).get();
      Accessory accessory = (Accessory) detailStorages.get(Accessory.class).get();
      Car car = new Car(IdGenerator.generateId(Car.class), body, motor, accessory);
      carStorage.put(car);
    } catch (InterruptedException e) {
      throw e;
    }
  }

  @Override
  public String getTaskName() {
    return "Worker " + this + " assembled car: ";
  }

  @Override
  public void setParameters(int parameter) {
  }
}
