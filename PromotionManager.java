import java.util.ArrayList;
import java.util.List;

/**
 * Manages promotions and selects the best applicable promotion.
 * Part 6.1 Requirement - Strategy Pattern implementation.
 */
public class PromotionManager {
    private List<Promotion> promotions;
    
    public PromotionManager(List<Promotion> promotions) {
        this.promotions = promotions != null ? new ArrayList<>(promotions) : new ArrayList<>();
    }
    
    /**
     * Selects the best promotion from the list of applicable promotions.
     * The best promotion is the one that provides the highest discount.
     * @param items the cart items
     * @param baseTotal the total base price of drinks
     * @param addonsTotal the total cost of add-ons
     * @return the best applicable promotion, or null if no promotion is applicable
     */
    public Promotion selectBestPromotion(List<CartItem> items, double baseTotal, double addonsTotal) {
        Promotion bestPromotion = null;
        double bestDiscount = 0.0;
        
        for (Promotion promotion : promotions) {
            if (promotion.isApplicable(items)) {
                double discount = promotion.calculateDiscount(items, baseTotal, addonsTotal);
                if (discount > bestDiscount) {
                    bestDiscount = discount;
                    bestPromotion = promotion;
                }
            }
        }
        
        return bestPromotion;
    }
    
    /**
     * Gets all promotions.
     * @return list of promotions
     */
    public List<Promotion> getPromotions() {
        return new ArrayList<>(promotions);
    }
}

