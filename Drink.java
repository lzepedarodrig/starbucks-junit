/**
 * Abstract base class for all drink types.
 * Implements Pricable interface and provides common drink attributes.
 * Part 6.2 Requirement - Interface Implementation
 */
public abstract class Drink implements Pricable {
    // Private Attributes - Encapsulation
    private String name;
    private String size;
    private double price;

    // Default Constructor
    public Drink() {
        
    }

    // Concrete Constructor
    public Drink(String name, String size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }

    // Abstract methods - must be implemented by subclasses
    /**
     * Returns the category name of this drink (e.g., "Coffee", "Tea", "Refresher").
     * @return the category name
     */
    public abstract String getCategoryName();
    
    /**
     * Returns a formatted display label for this drink.
     * @return formatted string like "Caffé Latte (Grande) - $4.25"
     */
    public abstract String getDisplayLabel();

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

    /**
     * Implements Pricable interface - returns the price of this drink.
     * @return the price
     */
    @Override
    public double calculatePrice() {
        return price;
    }
}
