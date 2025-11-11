import java.util.*;

/**
 * Tracks and maintains sales statistics for the day.
 */
public class SalesStatistics {
    private Map<String, Integer> drinkCountByNameAndSize; // key: "Name (Size)"
    private Map<String, Integer> addonCount; // key: "vanilla syrup", "extra shot"
    private Map<String, Double> addonRevenue; // key: add-on name
    private Map<String, Integer> categoryItemCount; // key: category name
    private Map<String, Double> categoryRevenue; // key: category name
    private double totalDiscountGiven;
    private int ordersWithPromotions;
    private Set<String> uniqueDrinkTypesSold; // category names
    private int totalDrinksSold;
    private double totalRevenue;
    
    public SalesStatistics() {
        drinkCountByNameAndSize = new HashMap<>();
        addonCount = new HashMap<>();
        addonRevenue = new HashMap<>();
        categoryItemCount = new HashMap<>();
        categoryRevenue = new HashMap<>();
        uniqueDrinkTypesSold = new HashSet<>();
        totalDiscountGiven = 0.0;
        ordersWithPromotions = 0;
        totalDrinksSold = 0;
        totalRevenue = 0.0;
    }
    
    /**
     * Records an order and updates all statistics.
     * @param order the completed order
     */
    public void recordOrder(Order order) {
        if (order == null) {
            return;
        }
        
        // Update revenue and discount
        totalRevenue += order.getFinalTotal();
        totalDiscountGiven += order.getDiscount();
        if (order.getDiscount() > 0.0) {
            ordersWithPromotions++;
        }
        
        // Process each item in the order
        for (CartItem item : order.getItems()) {
            Drink drink = item.getDrink();
            int quantity = item.getQuantity();
            
            // Update drink count by name and size
            String drinkKey = drink.getName() + " (" + drink.getSize() + ")";
            drinkCountByNameAndSize.put(drinkKey, 
                drinkCountByNameAndSize.getOrDefault(drinkKey, 0) + quantity);
            
            // Update category statistics
            String category = drink.getCategoryName();
            uniqueDrinkTypesSold.add(category);
            categoryItemCount.put(category, 
                categoryItemCount.getOrDefault(category, 0) + quantity);
            categoryRevenue.put(category, 
                categoryRevenue.getOrDefault(category, 0.0) + (item.basePrice() * quantity));
            
            // Update add-on statistics
            if (item.getVanillaShots() > 0) {
                int vanillaCount = item.getVanillaShots() * quantity;
                addonCount.put("vanilla syrup", 
                    addonCount.getOrDefault("vanilla syrup", 0) + vanillaCount);
                addonRevenue.put("vanilla syrup", 
                    addonRevenue.getOrDefault("vanilla syrup", 0.0) + 
                    (vanillaCount * 0.60)); // $0.60 per shot
            }
            if (item.getEspressoShots() > 0) {
                int espressoCount = item.getEspressoShots() * quantity;
                addonCount.put("extra shot", 
                    addonCount.getOrDefault("extra shot", 0) + espressoCount);
                addonRevenue.put("extra shot", 
                    addonRevenue.getOrDefault("extra shot", 0.0) + 
                    (espressoCount * 0.50)); // $0.50 per shot
            }
            
            // Update total drinks sold
            totalDrinksSold += quantity;
        }
    }
    
    /**
     * Returns the most popular drink (by name + size).
     * @return "Name (Size)" or null if no drinks sold
     */
    public String getMostPopularDrink() {
        if (drinkCountByNameAndSize.isEmpty()) {
            return null;
        }
        
        String mostPopular = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : drinkCountByNameAndSize.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopular = entry.getKey();
            }
        }
        return mostPopular;
    }
    
    /**
     * Returns the count for the most popular drink.
     * @return the count, or 0 if no drinks sold
     */
    public int getMostPopularDrinkCount() {
        String mostPopular = getMostPopularDrink();
        if (mostPopular == null) {
            return 0;
        }
        return drinkCountByNameAndSize.getOrDefault(mostPopular, 0);
    }
    
    /**
     * Returns the top 3 add-ons by count.
     * @return list of add-on names (up to 3)
     */
    public List<String> getTop3Addons() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(addonCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        List<String> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            top3.add(sorted.get(i).getKey());
        }
        return top3;
    }
    
    /**
     * Returns the total revenue from add-ons.
     * @return total add-on revenue
     */
    public double getTotalAddonRevenue() {
        double total = 0.0;
        for (Double revenue : addonRevenue.values()) {
            total += revenue;
        }
        return total;
    }
    
    /**
     * Returns the set of unsold drinks (by name + size).
     * @param menu the full menu
     * @return set of "Name (Size)" strings for unsold drinks
     */
    public Set<String> getUnsoldDrinks(List<Drink> menu) {
        Set<String> unsold = new LinkedHashSet<>();
        Set<String> sold = new HashSet<>(drinkCountByNameAndSize.keySet());
        
        for (Drink drink : menu) {
            String key = drink.getName() + " (" + drink.getSize() + ")";
            if (!sold.contains(key)) {
                unsold.add(key);
            }
        }
        
        return unsold;
    }
    
    // Getters
    public int getTotalDrinksSold() {
        return totalDrinksSold;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public Set<String> getUniqueDrinkTypesSold() {
        return new HashSet<>(uniqueDrinkTypesSold);
    }
    
    public double getTotalDiscountGiven() {
        return totalDiscountGiven;
    }
    
    public int getOrdersWithPromotions() {
        return ordersWithPromotions;
    }
    
    public Map<String, Integer> getCategoryItemCount() {
        return new HashMap<>(categoryItemCount);
    }
    
    public Map<String, Double> getCategoryRevenue() {
        return new HashMap<>(categoryRevenue);
    }
    
    public Map<String, Integer> getAddonCount() {
        return new HashMap<>(addonCount);
    }
    
    public Map<String, Double> getAddonRevenue() {
        return new HashMap<>(addonRevenue);
    }
}

