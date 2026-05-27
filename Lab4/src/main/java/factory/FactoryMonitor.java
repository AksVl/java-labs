package factory;

import factory.products.Car;
import factory.products.Product;
import threadpool.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FactoryMonitor implements StorageListener {
    private final Storage<Car> carStorage;
    private final ThreadPool workers;
    private final Map<Class<? extends Product>, Storage<? extends Product>> productsStorages;
    private Logger logger = LoggerFactory.getLogger(FactoryMonitor.class);

    public FactoryMonitor(Storage<Car> carStorage, ThreadPool workers, Map<Class<? extends Product>, Storage<? extends Product>> productsStorages) {
        this.carStorage = carStorage;
        this.workers = workers;
        this.productsStorages = productsStorages;
    }

    @Override
    public void onProductAdded(Class<?> productClass) {
        logger.info("Product added: " + productClass.getSimpleName());
    }

    @Override
    public void onCarRemoved() {
        logger.info("Car removed from storage");
    }
}