package factory;

import factory.products.*;
import threadpool.Task;
import threadpool.ThreadPool;
import gui.FactoryGUI;
import javax.swing.Timer;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tasks.*;

public class Factory {
    private static boolean logSale;
    private final Map<Class<? extends Product>, Storage<? extends Product>> productsStorages;
    private final int suppliersDelay = 3000;
    private FactoryMonitor factoryMonitor;
    private int workersNum;
    private int dealersNum;
    private int accessorySuppliersNum;
    private int bodySuppliersNum;
    private int motorSuppliersNum;
    private Logger logger = LoggerFactory.getLogger(Factory.class.getName());
    private Properties config;

    private ThreadPool workerThreadPool;
    private ThreadPool supplierThreadPool;
    private ThreadPool dealerThreadPool;

    private Task supplyAccessories;
    private Task supplyBodies;
    private Task supplyMotors;
    private Task orderBuild;
    private Task orderSell;

    public Factory() {
        this.config = new Properties();
        config = ConfigHandler.readConfigFile();
        this.logSale = Boolean.parseBoolean(config.getProperty("LogSale"));
        if (logSale) {
            logger.info("Logging initialized");
        }
        this.productsStorages = new HashMap<>();
        initializeProduction();
        initializeStorages();
    }

    private void initializeStorages() {
        productsStorages.put(Body.class, new Storage<>(
                Integer.parseInt(config.getProperty("StorageBodySize")), factoryMonitor));
        productsStorages.put(Motor.class, new Storage<>(
                Integer.parseInt(config.getProperty("StorageMotorSize")), factoryMonitor));
        productsStorages.put(Accessory.class, new Storage<>(
                Integer.parseInt(config.getProperty("StorageAccessorySize")), factoryMonitor));
        productsStorages.put(Car.class, new Storage<>(
                Integer.parseInt(config.getProperty("StorageAutoSize")), factoryMonitor));
    }

    private void initializeProduction() {
        workersNum = Integer.parseInt(config.getProperty("Workers"));
        dealersNum = Integer.parseInt(config.getProperty("Dealers"));
        accessorySuppliersNum = Integer.parseInt(config.getProperty("AccessorySuppliers"));
        bodySuppliersNum = Integer.parseInt(config.getProperty("BodySuppliers"));
        motorSuppliersNum = Integer.parseInt(config.getProperty("MotorSuppliers"));
        int suppliersNum = accessorySuppliersNum + bodySuppliersNum + motorSuppliersNum;
        supplierThreadPool = new ThreadPool("Suppliers", suppliersNum);
        workerThreadPool = new ThreadPool("Workers", workersNum);
        dealerThreadPool = new ThreadPool("Dealers", dealersNum);

        this.factoryMonitor = new FactoryMonitor(
                (Storage<Car>) productsStorages.get(Car.class),
                workerThreadPool, productsStorages);

        logger.info("Production initialized");
    }

    public void start() {
        logger.info("Production started");

        Storage<Car> carStorage = (Storage<Car>) productsStorages.get(Car.class);
        Storage<Motor> motorDetailStorage = (Storage<Motor>) productsStorages.get(Motor.class);
        Storage<Body> bodyDetailStorage = (Storage<Body>) productsStorages.get(Body.class);
        Storage<Accessory> accessoryDetailStorage = (Storage<Accessory>) productsStorages.get(Accessory.class);

        int accessorySuppliersDelay, bodySuppliersDelay, motorSuppliersDelay;
        accessorySuppliersDelay = bodySuppliersDelay = motorSuppliersDelay = suppliersDelay;
        int dealerDelay = 3000;

        supplyAccessories = new SupplyDetail<>(Accessory.class, accessoryDetailStorage, accessorySuppliersDelay);
        supplyBodies = new SupplyDetail<>(Body.class, bodyDetailStorage, bodySuppliersDelay);
        supplyMotors = new SupplyDetail<>(Motor.class, motorDetailStorage, motorSuppliersDelay);

        orderBuild = new BuildCar(productsStorages);
        orderSell = new SellCar(carStorage, dealerDelay);

        Thread production = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (carStorage.size() < carStorage.getCapacity()) {
                    supplierThreadPool.addTask(supplyAccessories);
                    supplierThreadPool.addTask(supplyBodies);
                    supplierThreadPool.addTask(supplyMotors);
                    workerThreadPool.addTask(orderBuild);
                }
                dealerThreadPool.addTask(orderSell);
            }
        });

        production.start();

        FactoryGUI gui = new FactoryGUI(
                supplyBodies, supplyMotors, supplyAccessories, orderSell,
                bodyDetailStorage.getCapacity(), motorDetailStorage.getCapacity(),
                accessoryDetailStorage.getCapacity(), carStorage.getCapacity(),
                bodySuppliersDelay, motorSuppliersDelay, accessorySuppliersDelay, dealerDelay
        );
        gui.setVisible(true);

        Timer timer = new Timer(1000, e -> {
            int total_sold_cars = ((SellCar) orderSell).getSoldCarsNum();
            gui.updateStats(
                    bodyDetailStorage.size(),
                    motorDetailStorage.size(),
                    accessoryDetailStorage.size(),
                    carStorage.size(),
                    total_sold_cars
            );
        });
        timer.start();
    }

    private void shutdownProduction() {
        workerThreadPool.shutdown();
        dealerThreadPool.shutdown();
        supplierThreadPool.shutdown();
    }
}