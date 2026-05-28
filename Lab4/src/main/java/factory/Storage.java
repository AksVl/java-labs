package factory;

import factory.products.Car;
import factory.products.Product;

import java.util.LinkedList;
import java.util.Queue;

public class Storage<T extends Product> implements Storable<T> {
  private final int capacity;
  private final Queue<T> items = new LinkedList<>();
  private final Object lock = new Object();
  private final StorageListener listener;

  public Storage(int capacity, StorageListener listener) {
    this.capacity = capacity;
    this.listener = listener;
  }

  @Override
  public void put(T item) throws InterruptedException {
    synchronized (lock) {
      while (items.size() >= capacity) {
        lock.wait();
      }
      items.add(item);
      lock.notifyAll();
    }
    listener.onProductAdded(item.getClass());
  }

  public T get() throws InterruptedException {
    synchronized (lock) {
      while (items.isEmpty()) {
        lock.wait();
      }
      T item = items.poll();
      lock.notifyAll();
      if (item instanceof Car) {
        listener.onCarRemoved();
      }
      return item;
    }
  }

  public int size() {
    synchronized (lock) {
      return items.size();
    }
  }

  public int getCapacity() { return capacity; }
  public boolean isFull() {
    synchronized (lock) {
      return items.size() >= capacity;
    }
  }
}