/**
 * Coffee category drink class.
 * Extends Drink and implements category-specific behavior.
 */
public class Coffee extends Drink {
    
    public Coffee(String name, String size, double price) {
        super(name, size, price);
    }
    
    @Override
    public String getCategoryName() {
        return "Coffee";
    }
    
    @Override
    public String getDisplayLabel() {
        return String.format("%s (%s) - $%.2f", getName(), getSize(), getPrice());
    }
}

