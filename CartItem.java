import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in the shopping cart.
 * Tracks drink, quantity, and add-ons.
 */
public class CartItem {
    private static final double ADDON_VANILLA_SYRUP = 0.60;  // per shot
    private static final double ADDON_EXTRA_SHOT = 0.50;  // per shot
    
    private Drink drink;
    private int quantity;
    private int vanillaShots; // count
    private int espressoShots; // count

    public CartItem(Drink drink, int quantity, int vanillaShots, int espressoShots) {
        this.drink = drink;
        this.quantity = Math.max(1, quantity); // Ensure at least 1
        this.vanillaShots = Math.max(0, vanillaShots);
        this.espressoShots = Math.max(0, espressoShots);
    }

    public Drink getDrink() {
        return drink;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getVanillaShots() {
        return vanillaShots;
    }
    
    public int getEspressoShots() {
        return espressoShots;
    }

    public double addonsCost() {
        return quantity * ((vanillaShots * ADDON_VANILLA_SYRUP) + (espressoShots * ADDON_EXTRA_SHOT));
    }

    public double basePrice() {
        return quantity * drink.getPrice();
    }

    public double lineSubtotalBeforeDiscounts() {
        return basePrice() + addonsCost();
    }

    public String addonsLabel() {
        List<String> parts = new ArrayList<>();
        if (vanillaShots > 0) parts.add(vanillaShots + "x vanilla");
        if (espressoShots > 0) parts.add(espressoShots + "x extra shot");
        return parts.isEmpty() ? "no add-ons" : String.join(", ", parts);
    }
    
    /**
     * Returns a string representation of this cart item for display.
     */
    public String getDisplayString() {
        if (quantity > 1) {
            return String.format("%dx %s (%s)", quantity, drink.getName(), drink.getSize());
        }
        return String.format("%s (%s)", drink.getName(), drink.getSize());
    }
}

