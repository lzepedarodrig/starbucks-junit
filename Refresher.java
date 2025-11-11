/**
 * Refresher category drink class.
 * Extends Drink and implements category-specific behavior.
 */
public class Refresher extends Drink {
    
    public Refresher(String name, String size, double price) {
        super(name, size, price);
    }
    
    @Override
    public String getCategoryName() {
        return "Refresher";
    }
    
    @Override
    public String getDisplayLabel() {
        return String.format("%s (%s) - $%.2f", getName(), getSize(), getPrice());
    }
}

