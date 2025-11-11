import java.util.List;

/**
 * Interface for promotion strategies.
 * Part 6.1 Requirement - Strategy Pattern for promotions.
 */
public interface Promotion {
    /**
     * Calculates the discount amount for this promotion.
     * @param items the cart items
     * @param baseTotal the total base price of drinks (excluding add-ons)
     * @param addonsTotal the total cost of add-ons
     * @return the discount amount
     */
    double calculateDiscount(List<CartItem> items, double baseTotal, double addonsTotal);
    
    /**
     * Returns the name of this promotion.
     * @return promotion name
     */
    String getPromotionName();
    
    /**
     * Checks if this promotion is applicable to the given items.
     * @param items the cart items
     * @return true if promotion is applicable, false otherwise
     */
    boolean isApplicable(List<CartItem> items);
}

