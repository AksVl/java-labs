package factory;

public interface StorageListener {
    void onProductAdded(Class<?> detailClass);
    void onCarRemoved();
}