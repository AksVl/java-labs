package threadpool;

import factory.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

class PooledThread extends Thread {
  private final ArrayDeque<Task> taskQueue;
  private volatile boolean isRunning;
  private Logger logger = LoggerFactory.getLogger(Factory.class.getName());

  public PooledThread(String name, ArrayDeque<Task> taskQueue, boolean isRunning) {
    super(name);
    this.isRunning = isRunning;
    this.taskQueue = taskQueue;
  }

  void finish() {
    isRunning = false;
    taskQueue.clear();
  }

  public Task getTask() {
    return taskQueue.peek();
  }

  @Override
  public void run() {
    Task task = null;
    while (isRunning || !taskQueue.isEmpty()) {
      synchronized (taskQueue) {
        if (taskQueue.isEmpty() && isRunning) {
          try {
            taskQueue.wait();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted: " + getName());
          }
          continue;
        } else {
          //if (!taskQueue.isEmpty()) {
            task = taskQueue.remove();
         // }
        }
      }
      try {
        task.execute();
        synchronized (taskQueue) {
          taskQueue.notifyAll();
        }
      } catch (InterruptedException e) {
        logger.info("Thread has been interrupted " + task.getTaskName());
        break;
      }
      logger.info(getName() + " got the task " + task.getTaskName());
    }
    logger.info(getName() + " is shutting down");
  }
}