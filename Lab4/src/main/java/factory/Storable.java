package factory;

public interface Storable<T> {
    void put(T item) throws InterruptedException;
}
