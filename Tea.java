/**
 * Tea category drink class.
 * Extends Drink and implements category-specific behavior.
 */
public class Tea extends Drink {
    
    public Tea(String name, String size, double price) {
        super(name, size, price);
    }
    
    @Override
    public String getCategoryName() {
        return "Tea";
    }
    
    @Override
    public String getDisplayLabel() {
        return String.format("%s (%s) - $%.2f", getName(), getSize(), getPrice());
    }
}

