package factory.products;

public abstract class Product {
    private String ID;
    public Product(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }
}
