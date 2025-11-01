// Drink is the abstract class (parent class) for Type
public abstract class Drink {
    // Private Attributes - Encapsulation
    private static String name;
    private String size;
    private double price;
    private double compute_price;

    // Default Constructor
    public Drink(){
        
    }

    // Concrete Constructor
    public Drink(String name, String size, double price){
        this.name = name;
        this.size = size;
        this.price = price;
    }

    // Methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCompute_price() {
        return compute_price;
    }

    public void setCompute_price(double compute_price) {
        this.compute_price = compute_price;
    }

}
