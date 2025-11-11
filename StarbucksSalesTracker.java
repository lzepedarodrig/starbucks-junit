// Starbucks Sales Tracker - Complete Implementation
// Implements all requirements: OOP principles, interfaces, design patterns, and all features.

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StarbucksSalesTracker {

    // ======= Config =======
    private static final double TAX_RATE = 0.0825; // 8.25% tax rate

    // ======= State =======
    private final List<Drink> menu = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final SalesStatistics statistics = new SalesStatistics();
    private final List<Order> completedOrders = new ArrayList<>();
    private PromotionManager promotionManager;

    // For JUnit tests
    public List<Drink> getMenu() {
        return Collections.unmodifiableList(menu);
    }

    public List<Order> getCompletedOrders() {
        return Collections.unmodifiableList(completedOrders);
    }

    public SalesStatistics getStatistics() {
        return statistics;
    }

    // ======= Initialization =======
    public StarbucksSalesTracker() {
        // Initialize promotions
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(new BulkOrderPromotion());
        promotions.add(new HappyHourPromotion());
        // BuyNGetMPromotion will be initialized after menu is loaded
        this.promotionManager = new PromotionManager(promotions);
    }

    // ======= UI =======
    private void welcome() {
        System.out.println();
        System.out.println("Welcome to Starbucks!");
        System.out.println("1) Show all available drinks");
        System.out.println("2) Search drinks by type");
        System.out.println("3) Place an order");
        System.out.println("4) View today's sales summary");
        System.out.println("5) Quit");
    }

    public void runMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                welcome();
                System.out.print("Choose: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" ->
                        showAllDrinks();
                    case "2" -> {
                        System.out.print("Enter a drink type (e.g., Coffee, Tea, Refresher, Frappuccino): ");
                        String type = scanner.nextLine().trim();
                        searchByType(type);
                    }
                    case "3" ->
                        placeOrder(scanner);
                    case "4" ->
                        printSalesSummary();
                    case "5" -> {
                        System.out.println("Thank you for choosing Starbucks!");
                        return;
                    }
                    default ->
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    // For tests to inspect unsold drinks
    public Set<String> getUnsoldDrinkNames() {
        return statistics.getUnsoldDrinks(menu);
    }

// Total revenue and discounts
    public double getTotalRevenue() {
        return statistics.getTotalRevenue();
    }

    public double getTotalDiscountGiven() {
        return statistics.getTotalDiscountGiven();
    }

    // ======= Menu browsing =======
    private void showAllDrinks() {
        if (menu.isEmpty()) {
            System.out.println("(Menu is empty)");
            return;
        }
        System.out.println("\n=== All Available Drinks ===");
        // Use Set to avoid duplicates based on name + size
        Set<String> seen = new HashSet<>();
        int index = 1;
        for (Drink drink : menu) {
            String key = drink.getName() + "|" + drink.getSize();
            if (!seen.contains(key)) {
                seen.add(key);
                System.out.printf("%2d) %s | Type: %s%n",
                        index++, drink.getDisplayLabel(), drink.getCategoryName());
            }
        }
    }

    private void searchByType(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            System.out.println("(No type entered)");
            return;
        }
        System.out.println("\n=== Results for type: " + typeName + " ===");
        boolean any = false;
        Set<String> seen = new HashSet<>();
        for (Drink drink : menu) {
            // Use polymorphism - getCategoryName() instead of string comparison
            if (drink.getCategoryName().equalsIgnoreCase(typeName)) {
                String key = drink.getName() + "|" + drink.getSize();
                if (!seen.contains(key)) {
                    seen.add(key);
                    System.out.printf("- %s%n", drink.getDisplayLabel());
                    any = true;
                }
            }
        }
        if (!any) {
            System.out.println("(No drinks found for that type)");
            // Show distinct types to guide user
            Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Drink drink : menu) {
                types.add(drink.getCategoryName());
            }
            if (!types.isEmpty()) {
                System.out.println("Try one of: " + String.join(", ", types));
            }
        }
    }

    // ======= Ordering =======
    private void placeOrder(Scanner scanner) {
        if (menu.isEmpty()) {
            System.out.println("Menu is empty. Load menu first.");
            return;
        }
        cart.clear();
        String cont = "Y";
        while (cont.equalsIgnoreCase("Y")) {
            System.out.print("Drink name (as shown): ");
            String drinkName = scanner.nextLine().trim();
            System.out.print("Size (Tall, Grande, Venti): ");
            String size = scanner.nextLine().trim();

            Drink chosen = findMenuItem(drinkName, size);
            if (chosen == null) {
                System.out.println("Not found. Tip: use option 1 to list the exact names and sizes.");
            } else {
                // Prompt for quantity
                int quantity = promptForPositiveInt(scanner, "Quantity: ");
                int vanilla = promptForNonNegativeInt(scanner,
                        "How many shots of vanilla syrup? (each $0.60): ");
                int espresso = promptForNonNegativeInt(scanner,
                        "How many extra espresso shots? (each $0.50): ");
                CartItem cartItem = new CartItem(chosen, quantity, vanilla, espresso);
                cart.add(cartItem);

                // Show added item with quantity
                if (quantity > 1) {
                    System.out.printf("Added: %dx %s (%s) — $%.2f [%s]%n",
                            quantity, chosen.getName(), chosen.getSize(),
                            cartItem.basePrice(), cartItem.addonsLabel());
                } else {
                    System.out.printf("Added: %s (%s) — $%.2f [%s]%n",
                            chosen.getName(), chosen.getSize(),
                            cartItem.basePrice(), cartItem.addonsLabel());
                }
            }
            System.out.print("Add another item? (Y/N): ");
            cont = scanner.nextLine().trim();
        }

        if (cart.isEmpty()) {
            System.out.println("(Cart was empty; nothing to checkout.)");
            return;
        }

        checkoutAndSaveReceipt(scanner);
    }

    private int promptForNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < 0) {
                    System.out.println("Please enter 0 or a positive integer.");
                } else {
                    return v;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private int promptForPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v <= 0) {
                    System.out.println("Please enter a positive integer (1 or more).");
                } else {
                    return v;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private Drink findMenuItem(String name, String size) {
        for (Drink drink : menu) {
            if (drink.getName() != null && drink.getSize() != null) {
                if (drink.getName().equalsIgnoreCase(name)
                        && drink.getSize().equalsIgnoreCase(size)) {
                    return drink;
                }
            }
        }
        return null;
    }

    // ======= Promotions & Checkout =======
    private void checkoutAndSaveReceipt(Scanner scanner) {
        // Compute totals
        double baseTotal = 0.0;   // drinks only (for promos)
        double addonsTotal = 0.0; // add-ons only

        for (CartItem ci : cart) {
            baseTotal += ci.basePrice();
            addonsTotal += ci.addonsCost();
        }

        // Use PromotionManager to select best promotion
        Promotion bestPromotion = promotionManager.selectBestPromotion(cart, baseTotal, addonsTotal);
        double appliedDisc = 0.0;
        String promoLabel = "None";

        if (bestPromotion != null) {
            appliedDisc = bestPromotion.calculateDiscount(cart, baseTotal, addonsTotal);
            promoLabel = bestPromotion.getPromotionName();
        }

        double subtotal = baseTotal + addonsTotal;
        double finalTotalBeforeTax = subtotal - appliedDisc;
        double tax = finalTotalBeforeTax * TAX_RATE;
        double finalTotal = finalTotalBeforeTax + tax;

        // Create Order object
        Order order = new Order(new ArrayList<>(cart), baseTotal, addonsTotal,
                appliedDisc, promoLabel, finalTotalBeforeTax, tax, finalTotal);
        completedOrders.add(order);

        // Record order in statistics
        statistics.recordOrder(order);

        // Print checkout summary
        System.out.println("\n===== CHECKOUT =====");
        for (CartItem ci : cart) {
            System.out.printf("- %s  base $%.2f  | add-ons [%s] $%.2f%n",
                    ci.getDisplayString(), ci.basePrice(), ci.addonsLabel(), ci.addonsCost());
        }
        System.out.printf("Drinks total:     $%.2f%n", baseTotal);
        System.out.printf("Add-ons total:    $%.2f%n", addonsTotal);
        System.out.printf("Promotion:  %s  (-$%.2f)%n", promoLabel, appliedDisc);
        System.out.printf("Subtotal:         $%.2f%n", finalTotalBeforeTax);
        System.out.printf("Tax (8.25%%):      $%.2f%n", tax);
        System.out.printf("Amount due:       $%.2f%n", finalTotal);
        System.out.println("====================\n");

        // Offer to save receipt
        System.out.print("Save receipt? (Y/N): ");
        String save = scanner.nextLine().trim();
        if (save.equalsIgnoreCase("Y")) {
            saveReceipt(order);
        }

        cart.clear();
    }

    private void saveReceipt(Order order) {
        // Create timestamped filename
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String fileName = "receipt_" + now.format(fileFormatter) + ".txt";

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append("==== Starbucks Receipt ====\n");
        sb.append("Date: ").append(now.format(dateFormatter))
                .append("  Time: ").append(now.format(timeFormatter)).append("\n\n");

        for (CartItem ci : order.getItems()) {
            sb.append(String.format("%-28s  base $%5.2f  add-ons [%s] $%4.2f%n",
                    ci.getDisplayString(), ci.basePrice(), ci.addonsLabel(), ci.addonsCost()));
        }

        sb.append(String.format("\nDrinks total:     $%.2f%n", order.getBaseTotal()));
        sb.append(String.format("Add-ons total:    $%.2f%n", order.getAddonsTotal()));
        sb.append(String.format("Promotion:  %s  (-$%.2f)%n", order.getPromotionName(), order.getDiscount()));
        sb.append(String.format("Subtotal:         $%.2f%n", order.getSubtotalBeforeTax()));
        sb.append(String.format("Tax (8.25%%):      $%.2f%n", order.getTax()));
        sb.append(String.format("TOTAL DUE:        $%.2f%n", order.getFinalTotal()));
        sb.append("===========================\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(sb.toString());
            System.out.println("Receipt saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing receipt: " + e.getMessage());
        }
    }

    // ======= Reporting =======
    private void printSalesSummary() {
        System.out.println("\n=== Today's Sales Summary ===");
        System.out.printf("Total Drinks Sold: %d%n", statistics.getTotalDrinksSold());
        System.out.printf("Total Revenue: $%.2f%n", statistics.getTotalRevenue());

        if (statistics.getTotalDrinksSold() == 0) {
            System.out.println("No drinks sold yet.");
            return;
        }

        // Most popular drink (by name + size)
        String mostPopular = statistics.getMostPopularDrink();
        if (mostPopular != null) {
            int count = statistics.getMostPopularDrinkCount();
            System.out.printf("Most Popular Drink: %s (%d sold)%n", mostPopular, count);
        }

        // Unique drink types sold today
        Set<String> uniqueTypes = statistics.getUniqueDrinkTypesSold();
        if (!uniqueTypes.isEmpty()) {
            System.out.println("Unique Drink Types Sold: " + String.join(", ", uniqueTypes));
        }

        // Drinks not sold today
        Set<String> unsold = statistics.getUnsoldDrinks(menu);
        if (!unsold.isEmpty()) {
            System.out.println("Drinks Not Sold Today: " + String.join(", ", unsold));
        }

        // Top 3 add-ons
        List<String> top3Addons = statistics.getTop3Addons();
        if (!top3Addons.isEmpty()) {
            System.out.println("\nTop 3 Add-ons (by count):");
            Map<String, Integer> addonCounts = statistics.getAddonCount();
            for (String addon : top3Addons) {
                System.out.printf("  - %s: %d%n", addon, addonCounts.get(addon));
            }
        }

        // Add-on revenue
        double addonRevenue = statistics.getTotalAddonRevenue();
        System.out.printf("Total Add-on Revenue: $%.2f%n", addonRevenue);

        // Promotion impact
        System.out.printf("Total Discount Given: $%.2f%n", statistics.getTotalDiscountGiven());
        System.out.printf("Orders with Promotions: %d%n", statistics.getOrdersWithPromotions());

        // Per-category breakdown
        Map<String, Integer> categoryCounts = statistics.getCategoryItemCount();
        Map<String, Double> categoryRevenues = statistics.getCategoryRevenue();
        if (!categoryCounts.isEmpty()) {
            System.out.println("\nPer-Category Breakdown:");
            for (String category : categoryCounts.keySet()) {
                System.out.printf("  - %s: %d items, $%.2f revenue%n",
                        category, categoryCounts.get(category),
                        categoryRevenues.getOrDefault(category, 0.0));
            }
        }
    }

    // ======= CSV =======
    public void loadMenuFromCsv(String filePath) {
        menu.clear();
        Set<String> seen = new HashSet<>(); // To handle duplicates

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) {
                return;
            }

            // Map header → index (normalized to lower-case w/o spaces)
            String[] h = header.split(",", -1);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < h.length; i++) {
                String key = h[i].trim().toLowerCase().replace(" ", "");
                idx.put(key, i);
            }

            Integer iName = idx.get("drinkname");
            Integer iType = idx.get("drinktype");
            Integer iSize = idx.get("size");
            Integer iPrice = idx.get("price");

            if (iName == null || iType == null || iSize == null || iPrice == null) {
                System.err.println("CSV missing required columns. Expected: Drink Name, Drink Type, Size, Price");
                return;
            }

            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] f = line.split(",", -1);
                if (f.length <= Math.max(Math.max(iName, iType), Math.max(iSize, iPrice))) {
                    System.err.println("Skipping row " + lineNumber + " with missing fields: " + line);
                    continue;
                }

                String name = f[iName].trim();
                String typeName = f[iType].trim();
                String size = f[iSize].trim();

                // Skip if missing required fields
                if (name.isEmpty() || typeName.isEmpty() || size.isEmpty()) {
                    System.err.println("Skipping row " + lineNumber + " with empty fields: " + line);
                    continue;
                }

                double price;
                try {
                    price = Double.parseDouble(f[iPrice].trim());
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping row " + lineNumber + " with bad price: " + line);
                    continue;
                }

                // Use DrinkFactory to create appropriate drink
                Drink drink = DrinkFactory.createDrink(name, size, price, typeName);
                if (drink == null) {
                    System.err.println("Skipping row " + lineNumber + " with unknown type: " + typeName);
                    continue;
                }

                // Check for duplicates (same name + size)
                String key = name + "|" + size;
                if (!seen.contains(key)) {
                    seen.add(key);
                    menu.add(drink);
                }
            }

            // Update BuyNGetMPromotion with menu
            List<Promotion> promotions = new ArrayList<>();
            promotions.add(new BulkOrderPromotion());
            promotions.add(new HappyHourPromotion());
            promotions.add(new BuyNGetMPromotion(menu));
            this.promotionManager = new PromotionManager(promotions);

            System.out.println("Loaded " + menu.size() + " menu items from " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
  // StarbucksSalesTracker.java
public List<Drink> searchByTypeForTest(String typeName) {
    List<Drink> result = new ArrayList<>();
    for (Drink drink : menu) {
        if (drink.getCategoryName().equalsIgnoreCase(typeName)) {
            result.add(drink);
        }
    }
    return result;
}


    // ======= Main =======
    public static void main(String[] args) {
        StarbucksSalesTracker app = new StarbucksSalesTracker();
        // Load menu.csv from working dir by default
        String path = "menu.csv";
        if (args != null && args.length > 0) {
            path = args[0];
        }
        app.loadMenuFromCsv(path);
        app.runMenu();
    }

    // ======= Test-friendly ordering method =======
public void placeOrderTest(Drink drink, int quantity, int vanillaShots, int espressoShots) {
    if (drink == null || quantity <= 0) return;

    CartItem cartItem = new CartItem(drink, quantity, vanillaShots, espressoShots);
    cart.clear();
    cart.add(cartItem);

    // Compute totals and apply promotions exactly like checkout
    double baseTotal = 0.0;
    double addonsTotal = 0.0;

    for (CartItem ci : cart) {
        baseTotal += ci.basePrice();
        addonsTotal += ci.addonsCost();
    }

    Promotion bestPromotion = promotionManager.selectBestPromotion(cart, baseTotal, addonsTotal);
    double appliedDisc = 0.0;
    String promoLabel = "None";
    if (bestPromotion != null) {
        appliedDisc = bestPromotion.calculateDiscount(cart, baseTotal, addonsTotal);
        promoLabel = bestPromotion.getPromotionName();
    }

    double subtotal = baseTotal + addonsTotal;
    double finalTotalBeforeTax = subtotal - appliedDisc;
    double tax = finalTotalBeforeTax * TAX_RATE;
    double finalTotal = finalTotalBeforeTax + tax;

    Order order = new Order(new ArrayList<>(cart), baseTotal, addonsTotal,
            appliedDisc, promoLabel, finalTotalBeforeTax, tax, finalTotal);

    completedOrders.add(order);
    statistics.recordOrder(order);
    cart.clear();
}

public void addDrinkForTest(Drink drink) {
    menu.add(drink);
}

public void placeOrderForTest(Drink drink, int quantity, int vanillaShots, int espressoShots) {
    cart.clear();
    CartItem ci = new CartItem(drink, quantity, vanillaShots, espressoShots);
    cart.add(ci);

    // Compute totals and promotions
    double baseTotal = 0.0;
    double addonsTotal = 0.0;

    for (CartItem item : cart) {
        baseTotal += item.basePrice();
        addonsTotal += item.addonsCost();
    }

    Promotion bestPromotion = promotionManager.selectBestPromotion(cart, baseTotal, addonsTotal);
    double appliedDiscount = 0.0;
    String promoLabel = "None";
    if (bestPromotion != null) {
        appliedDiscount = bestPromotion.calculateDiscount(cart, baseTotal, addonsTotal);
        promoLabel = bestPromotion.getPromotionName();
    }

    double subtotal = baseTotal + addonsTotal;
    double totalBeforeTax = subtotal - appliedDiscount;
    double tax = totalBeforeTax * TAX_RATE;
    double finalTotal = totalBeforeTax + tax;

    Order order = new Order(new ArrayList<>(cart), baseTotal, addonsTotal, appliedDiscount,
            promoLabel, totalBeforeTax, tax, finalTotal);

    completedOrders.add(order);
    statistics.recordOrder(order);
    cart.clear();
}
}
