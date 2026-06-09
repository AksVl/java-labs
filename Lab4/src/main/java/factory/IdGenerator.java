package factory;

import factory.products.Accessory;
import factory.products.Body;
import factory.products.Car;
import factory.products.Motor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class IdGenerator {
    private static final Map<Class<?>, AtomicInteger> idCounters = new ConcurrentHashMap<>();

    public static String generateId(Class<?> type) {
        idCounters.putIfAbsent(type, new AtomicInteger(0));
        int idCounter = idCounters.get(type).getAndIncrement();

        return getPrefix(type) + idCounter;
    }

    private static String getPrefix(Class<?> type) {
        if (type == Accessory.class) {
            return "ACS";
        } else if (type == Body.class) {
            return "BDY";
        } else if (type == Motor.class) {
            return "MTR";
        } else if (type == Car.class) {
            return "CAR";
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getSimpleName());
        }
    }
}
