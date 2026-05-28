package threadpool;

import factory.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class ThreadPool {
  private final int threadsNum;
  private final ArrayDeque<Task> taskQueue;
  private static final int MAX_QUEUE_SIZE = 1000;
  private final Set<PooledThread> availableThreads;
  private volatile boolean isRunning;
  private String poolName;
  private Logger logger = LoggerFactory.getLogger(Factory.class.getName());

  public ThreadPool(String poolName, int threadsNum) {
    this.poolName = poolName;
    this.threadsNum = threadsNum;
    this.taskQueue = new ArrayDeque<>();
    this.availableThreads = new HashSet<>();
    this.isRunning = true;

    for (int i = 0; i < threadsNum; i++) {
      PooledThread thread = new PooledThread("Pool " + poolName + " Thread-" + i, taskQueue, isRunning);
      System.out.println("Pool " + poolName + " Thread-" + i);
      this.availableThreads.add(thread);
    }
    for (PooledThread thread : availableThreads) {
      thread.start();
    }
  }

  public void addTask(Task task) {
    synchronized (taskQueue) {
      while (taskQueue.size() >= MAX_QUEUE_SIZE) {
        try {
          taskQueue.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      taskQueue.add(task);
      taskQueue.notify();
    }
  }
}