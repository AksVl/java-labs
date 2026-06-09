package factory.products;

public class Car extends Product {
    private Body body;
    private Accessory accessory;
    private Motor motor;
    public Car(String ID, Body body, Motor motor, Accessory accessory){
        super(ID);
        this.accessory = accessory;
        this.body = body;
        this.motor = motor;
    }
}
