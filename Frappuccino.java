/**
 * Frappuccino category drink class.
 * Extends Drink and implements category-specific behavior.
 */
public class Frappuccino extends Drink {
    
    public Frappuccino(String name, String size, double price) {
        super(name, size, price);
    }
    
    @Override
    public String getCategoryName() {
        return "Frappuccino";
    }
    
    @Override
    public String getDisplayLabel() {
        return String.format("%s (%s) - $%.2f", getName(), getSize(), getPrice());
    }
}

