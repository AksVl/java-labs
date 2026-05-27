package tasks;

import factory.products.Product;
import factory.IdGenerator;
import factory.Storable;
import factory.Storage;
import threadpool.Task;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class SupplyDetail<T extends Product> implements Task {
  private final Class<T> productClass;
  private final AtomicInteger delay;
  private final Storable<T> productStorage;

  public SupplyDetail(Class<T> productClass, Storage<T> detailStorage, int delay) {
    this.productClass = productClass;
    this.productStorage = detailStorage;
    this.delay = new AtomicInteger(delay);
  }

  public T createDetail() {
    T detail;
    try {
      detail = productClass.getDeclaredConstructor(String.class).newInstance(IdGenerator.generateId(productClass));
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Failed to create detail ", e);
    }
    return detail;
  }

  @Override
  public void execute() throws InterruptedException {
    try {
      Thread.sleep(delay.get());
      T detail = createDetail();
      productStorage.put(detail);
    } catch (InterruptedException e) {
      throw e;
    }
  }

  @Override
  public String getTaskName() {
    return "Supplier " + productClass + " done his work";
  }

  @Override
  public void setParameters(int parameter) {
    delay.set(parameter);
  }
}