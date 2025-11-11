/**
 * Interface for objects that have a price and display label.
 * Part 6.2 Requirement - Interface Implementation
 */
public interface Pricable {
    /**
     * Calculates and returns the price of this item.
     * @return the price as a double
     */
    double calculatePrice();
    
    /**
     * Returns a formatted display label for this item.
     * @return formatted string representation
     */
    String getDisplayLabel();
}

