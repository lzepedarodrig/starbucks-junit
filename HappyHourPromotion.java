import java.time.LocalTime;
import java.util.List;

/**
 * Happy Hour Promotion: 20% off Tea drinks between 2:00 PM and 4:00 PM.
 * Applies discount to base drink prices only, not add-ons.
 */
public class HappyHourPromotion implements Promotion {
    private static final LocalTime HAPPY_HOUR_START = LocalTime.of(14, 0); // 2:00 PM
    private static final LocalTime HAPPY_HOUR_END = LocalTime.of(16, 0); // 4:00 PM
    private static final double DISCOUNT_PERCENTAGE = 0.20;
    
    @Override
    public double calculateDiscount(List<CartItem> items, double baseTotal, double addonsTotal) {
        if (!isApplicable(items)) {
            return 0.0;
        }
        // Calculate total for Tea drinks only
        double teaBaseTotal = 0.0;
        for (CartItem item : items) {
            if (item.getDrink() instanceof Tea) {
                teaBaseTotal += item.basePrice();
            }
        }
        // 20% off Tea drinks only (not add-ons)
        return teaBaseTotal * DISCOUNT_PERCENTAGE;
    }
    
    @Override
    public String getPromotionName() {
        return "Happy Hour: Tea 20% (drinks only, 2–4 PM)";
    }
    
    @Override
    public boolean isApplicable(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        // Check if current time is within happy hour
        LocalTime now = LocalTime.now();
        if (now.isBefore(HAPPY_HOUR_START) || !now.isBefore(HAPPY_HOUR_END)) {
            return false;
        }
        // Check if there are any Tea drinks in the cart
        for (CartItem item : items) {
            if (item.getDrink() instanceof Tea) {
                return true;
            }
        }
        return false;
    }
}

