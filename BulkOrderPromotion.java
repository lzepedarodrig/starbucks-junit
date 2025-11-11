import java.util.List;

/**
 * Bulk Order Promotion: 10% off if 4 or more items are purchased.
 * Applies discount to base drink prices only, not add-ons.
 */
public class BulkOrderPromotion implements Promotion {
    private static final int MIN_ITEMS = 4;
    private static final double DISCOUNT_PERCENTAGE = 0.10;
    
    @Override
    public double calculateDiscount(List<CartItem> items, double baseTotal, double addonsTotal) {
        if (!isApplicable(items)) {
            return 0.0;
        }
        // 10% off base drinks only (not add-ons)
        return baseTotal * DISCOUNT_PERCENTAGE;
    }
    
    @Override
    public String getPromotionName() {
        return "Bulk Order 10% (drinks only)";
    }
    
    @Override
    public boolean isApplicable(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        // Count total items (sum of quantities)
        int totalItems = 0;
        for (CartItem item : items) {
            totalItems += item.getQuantity();
        }
        return totalItems >= MIN_ITEMS;
    }
}

