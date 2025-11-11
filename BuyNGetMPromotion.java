import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Buy-N-Get-M Promotion: Buy 3 of the same drink (any size) get 1 free.
 * The free item is the cheapest size of that drink.
 */
public class BuyNGetMPromotion implements Promotion {
    private static final int REQUIRED_QUANTITY = 3;
    private List<Drink> menu;
    
    public BuyNGetMPromotion(List<Drink> menu) {
        this.menu = menu != null ? new ArrayList<>(menu) : new ArrayList<>();
    }
    
    @Override
    public double calculateDiscount(List<CartItem> items, double baseTotal, double addonsTotal) {
        if (!isApplicable(items)) {
            return 0.0;
        }
        
        // Find the drink name that appears 3+ times (by quantity)
        Map<String, Integer> drinkNameCounts = new HashMap<>();
        for (CartItem item : items) {
            String drinkName = item.getDrink().getName();
            drinkNameCounts.put(drinkName, 
                drinkNameCounts.getOrDefault(drinkName, 0) + item.getQuantity());
        }
        
        // Find drink name with 3+ items
        String eligibleDrinkName = null;
        for (Map.Entry<String, Integer> entry : drinkNameCounts.entrySet()) {
            if (entry.getValue() >= REQUIRED_QUANTITY) {
                eligibleDrinkName = entry.getKey();
                break;
            }
        }
        
        if (eligibleDrinkName == null) {
            return 0.0;
        }
        
        // Find cheapest size of this drink from menu
        double cheapestPrice = Double.MAX_VALUE;
        for (Drink drink : menu) {
            if (drink.getName().equalsIgnoreCase(eligibleDrinkName)) {
                if (drink.getPrice() < cheapestPrice) {
                    cheapestPrice = drink.getPrice();
                }
            }
        }
        
        // If we found a price, return it as discount
        if (cheapestPrice != Double.MAX_VALUE) {
            return cheapestPrice;
        }
        
        return 0.0;
    }
    
    @Override
    public String getPromotionName() {
        return "Buy 3 Get 1 Free (cheapest size)";
    }
    
    @Override
    public boolean isApplicable(List<CartItem> items) {
        if (items == null || items.isEmpty() || menu == null || menu.isEmpty()) {
            return false;
        }
        
        // Count items by drink name (sum quantities)
        Map<String, Integer> drinkNameCounts = new HashMap<>();
        for (CartItem item : items) {
            String drinkName = item.getDrink().getName();
            drinkNameCounts.put(drinkName, 
                drinkNameCounts.getOrDefault(drinkName, 0) + item.getQuantity());
        }
        
        // Check if any drink name has 3+ items
        for (int count : drinkNameCounts.values()) {
            if (count >= REQUIRED_QUANTITY) {
                return true;
            }
        }
        
        return false;
    }
}

