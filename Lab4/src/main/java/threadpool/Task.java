package threadpool;

public interface Task {
    void execute() throws InterruptedException;
    String getTaskName();
    void setParameters(int parameter);
}