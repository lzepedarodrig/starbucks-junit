import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a completed order with all order details.
 */
public class Order {
    private List<CartItem> items;
    private double baseTotal;
    private double addonsTotal;
    private double discount;
    private String promotionName;
    private double subtotalBeforeTax;
    private double tax;
    private double finalTotal;
    private LocalDateTime timestamp;
    
    public Order(List<CartItem> items, double baseTotal, double addonsTotal, 
                 double discount, String promotionName, double subtotalBeforeTax, 
                 double tax, double finalTotal) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.baseTotal = baseTotal;
        this.addonsTotal = addonsTotal;
        this.discount = discount;
        this.promotionName = promotionName;
        this.subtotalBeforeTax = subtotalBeforeTax;
        this.tax = tax;
        this.finalTotal = finalTotal;
        this.timestamp = LocalDateTime.now();
    }
    
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public double getBaseTotal() {
        return baseTotal;
    }
    
    public double getAddonsTotal() {
        return addonsTotal;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public String getPromotionName() {
        return promotionName;
    }
    
    public double getSubtotalBeforeTax() {
        return subtotalBeforeTax;
    }
    
    public double getTax() {
        return tax;
    }
    
    public double getFinalTotal() {
        return finalTotal;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

