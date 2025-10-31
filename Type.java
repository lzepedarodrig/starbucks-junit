// Type is inheriting from Drink.
public class Type extends Drink{
    // Private Attributes - Encapsulation
    private double compute_price;
    private String type_name;

    //Concrete Constructors - Overloading example polymorphism 
    public Type(double compute_price, String type_name) {
        this.compute_price = compute_price;
        this.type_name = type_name;
    }

    public Type(String name, String size, double price, double compute_price, String type_name) {
        super(name, size, price);
        this.compute_price = compute_price;
        this.type_name = type_name;
    }

    // Methods
    public double getCompute_price() {
        return compute_price;
    }

    public void setCompute_price(double compute_price) {
        this.compute_price = compute_price;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public void printInfo() {
        System.out.println("=== Drinks Info ===");
        System.out.println("Drink Name: " + getName());   // from Drink
        System.out.println("Drink Size: " + getSize());   // from Drink
        System.out.println("Drink Price: " + getPrice()); // from Drink
        System.out.println("Type: " + this.type_name);    // from Type
    }

}
