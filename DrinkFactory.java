/**
 * Factory class for creating drink instances based on type name.
 * Uses Factory Pattern to create appropriate category-specific drink classes.
 */
public class DrinkFactory {
    
    /**
     * Creates a Drink instance of the appropriate category based on type name.
     * @param name the drink name
     * @param size the drink size
     * @param price the drink price
     * @param typeName the category type name (Coffee, Tea, Refresher, Frappuccino, Seasonal)
     * @return a Drink instance of the appropriate category, or null if type is unknown
     */
    public static Drink createDrink(String name, String size, double price, String typeName) {
        if (typeName == null) {
            return null;
        }
        
        String normalizedType = typeName.trim();
        
        // Case-insensitive matching
        if (normalizedType.equalsIgnoreCase("Coffee")) {
            return new Coffee(name, size, price);
        } else if (normalizedType.equalsIgnoreCase("Tea")) {
            return new Tea(name, size, price);
        } else if (normalizedType.equalsIgnoreCase("Refresher")) {
            return new Refresher(name, size, price);
        } else if (normalizedType.equalsIgnoreCase("Frappuccino")) {
            return new Frappuccino(name, size, price);
        } else if (normalizedType.equalsIgnoreCase("Seasonal")) {
            return new Seasonal(name, size, price);
        }
        
        // Unknown type - return null (will be handled by caller)
        return null;
    }
}

