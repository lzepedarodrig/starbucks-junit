// Merged & simplified StarbucksSalesTracker
// Combines both teammates' features: menu loading, browse/search, cart-based ordering,
// add-ons (vanilla syrup, extra shot), receipt saving, sales summary, and promotions.
// Notes:
//  - Type extends Drink (provided). We rely on getters like getName(), getSize(), getPrice(), getType_name().
//  - CSV expected columns: Drink Name, Drink Type, Size, Price (header required).
//  - Matching on drink/type is case-insensitive.
//  - Promotions (applied to base drink prices only, not add-ons):
//      * Bulk: 10% off if you buy 4+ drinks in one checkout
//      * Happy Hour: 20% off TEA drinks between 2:00 PM and 4:00 PM (local system time)
//  - Receipts are appended to "receipt.txt" in the working directory.

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StarbucksSalesTracker {

    // ======= Config =======
    private static final double ADDON_VANILLA_SYRUP = 0.60;  // per shot
    private static final double ADDON_EXTRA_SHOT     = 0.50;  // per shot
    private static final LocalTime HAPPY_HOUR_START  = LocalTime.of(14, 0); // 2:00 PM
    private static final LocalTime HAPPY_HOUR_END    = LocalTime.of(16, 0); // 4:00 PM

    // ======= State =======
    private final List<Type> menu = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final Map<String, Integer> itemCountByName = new HashMap<>();
    private double totalSales = 0.0;

    // Inner helper to track per-item add-ons
    private static class CartItem {
        Type drink;
        int vanillaShots; // count
        int espressoShots; // count

        CartItem(Type drink, int vanillaShots, int espressoShots) {
            this.drink = drink;
            this.vanillaShots = Math.max(0, vanillaShots);
            this.espressoShots = Math.max(0, espressoShots);
        }

        double addonsCost() {
            return (vanillaShots * ADDON_VANILLA_SYRUP) + (espressoShots * ADDON_EXTRA_SHOT);
        }

        double basePrice() {
            return drink.getPrice();
        }

        double lineSubtotalBeforeDiscounts() {
            return basePrice() + addonsCost();
        }

        String addonsLabel() {
            List<String> parts = new ArrayList<>();
            if (vanillaShots > 0) parts.add(vanillaShots + "x vanilla");
            if (espressoShots > 0) parts.add(espressoShots + "x extra shot");
            return parts.isEmpty() ? "no add-ons" : String.join(", ", parts);
        }
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
                    case "1" -> showAllDrinks();
                    case "2" -> {
                        System.out.print("Enter a drink type (e.g., Coffee, Tea, Refresher, Frappuccino): ");
                        String type = scanner.nextLine().trim();
                        searchByType(type);
                    }
                    case "3" -> placeOrder(scanner);
                    case "4" -> printSalesSummary();
                    case "5" -> {
                        System.out.println("Thank you for choosing Starbucks!");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            }
        }
    }

    // ======= Menu browsing =======
    private void showAllDrinks() {
        if (menu.isEmpty()) {
            System.out.println("(Menu is empty)");
            return;
        }
        System.out.println("\n=== All Available Drinks ===");
        for (int i = 0; i < menu.size(); i++) {
            Type t = menu.get(i);
            System.out.printf("%2d) %-28s | %-6s | $%5.2f | Type: %s%n",
                    i + 1, t.getName(), t.getSize(), t.getPrice(), t.getType_name());
        }
    }

    private void searchByType(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            System.out.println("(No type entered)");
            return;
        }
        System.out.println("\n=== Results for type: " + typeName + " ===");
        boolean any = false;
        for (Type t : menu) {
            if (t.getType_name() != null && t.getType_name().equalsIgnoreCase(typeName)) {
                System.out.printf("- %-28s | %-6s | $%5.2f%n", t.getName(), t.getSize(), t.getPrice());
                any = true;
            }
        }
        if (!any) {
            System.out.println("(No drinks found for that type)");
            // Small hint: show distinct types to guide user
            Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Type t : menu) if (t.getType_name() != null) types.add(t.getType_name());
            if (!types.isEmpty()) System.out.println("Try one of: " + String.join(", ", types));
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

            Type chosen = findMenuItem(drinkName, size);
            if (chosen == null) {
                System.out.println("Not found. Tip: use option 1 to list the exact names and sizes.");
            } else {
                int vanilla = promptForNonNegativeInt(scanner, "How many shots of vanilla syrup? (each $%.2f): ".formatted(ADDON_VANILLA_SYRUP));
                int espresso = promptForNonNegativeInt(scanner, "How many extra espresso shots? (each $%.2f): ".formatted(ADDON_EXTRA_SHOT));
                cart.add(new CartItem(chosen, vanilla, espresso));
                System.out.printf("Added: %s (%s) — $%.2f [%s]%n",
                        chosen.getName(), chosen.getSize(), chosen.getPrice(), cart.get(cart.size()-1).addonsLabel());
            }
            System.out.print("Add another item? (Y/N): ");
            cont = scanner.nextLine().trim();
        }

        if (!cart.isEmpty()) {
            checkoutAndSaveReceipt(scanner);
        } else {
            System.out.println("(Cart was empty; nothing to checkout.)");
        }
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

    private Type findMenuItem(String name, String size) {
        for (Type t : menu) {
            if (t.getName() != null && t.getSize() != null) {
                if (t.getName().equalsIgnoreCase(name) && t.getSize().equalsIgnoreCase(size)) {
                    return t;
                }
            }
        }
        return null;
    }

    // ======= Promotions & Checkout =======
    private double calculateBulkDiscount(int itemCount, double baseTotal) {
        // 10% off if 4 or more drinks (excludes add-ons)
        return itemCount >= 4 ? baseTotal * 0.10 : 0.0;
    }

    private double calculateHappyHourTeaDiscount(double teaBaseTotal) {
        // 20% off TEA drinks during 2-4 PM local time (excludes add-ons)
        LocalTime now = LocalTime.now();
        if (!now.isBefore(HAPPY_HOUR_START) && now.isBefore(HAPPY_HOUR_END)) {
            return teaBaseTotal * 0.20;
        }
        return 0.0;
    }

    private void checkoutAndSaveReceipt(Scanner scanner) {
        // Compute totals
        double baseTotal = 0.0;   // drinks only (for promos)
        double addonsTotal = 0.0; // add-ons only
        double teaBaseTotal = 0.0;

        for (CartItem ci : cart) {
            baseTotal += ci.basePrice();
            addonsTotal += ci.addonsCost();
            if (ci.drink.getType_name() != null && ci.drink.getType_name().equalsIgnoreCase("Tea")) {
                teaBaseTotal += ci.basePrice();
            }
        }

        double bulkDisc = calculateBulkDiscount(cart.size(), baseTotal);
        double hhDisc   = calculateHappyHourTeaDiscount(teaBaseTotal);

        // Choose best promo
        double appliedDisc = Math.max(bulkDisc, hhDisc);
        String promoLabel  = appliedDisc == 0.0 ? "None"
                : (appliedDisc == bulkDisc ? "Bulk Order 10% (drinks only)"
                                           : "Happy Hour: Tea 20% (drinks only, 2–4 PM)");

        double subtotal = baseTotal + addonsTotal;
        double finalTotal = subtotal - appliedDisc;

        // Update global sales + counts
        totalSales += finalTotal;
        for (CartItem ci : cart) {
            itemCountByName.merge(ci.drink.getName(), 1, Integer::sum);
        }

        // Print checkout summary
        System.out.println("\n===== CHECKOUT =====");
        for (CartItem ci : cart) {
            System.out.printf("- %s (%s)  base $%.2f  | add-ons [%s] $%.2f%n",
                    ci.drink.getName(), ci.drink.getSize(),
                    ci.basePrice(), ci.addonsLabel(), ci.addonsCost());
        }
        System.out.printf("Drinks total:     $%.2f%n", baseTotal);
        System.out.printf("Add-ons total:    $%.2f%n", addonsTotal);
        System.out.printf("Promotion:  %s  (-$%.2f)%n", promoLabel, appliedDisc);
        System.out.printf("Amount due:       $%.2f%n", finalTotal);
        System.out.println("====================\n");

        // Offer to save receipt
        System.out.print("Save receipt? (Y/N): ");
        String save = scanner.nextLine().trim();
        if (save.equalsIgnoreCase("Y")) {
            saveReceipt(cart, baseTotal, addonsTotal, appliedDisc, promoLabel, finalTotal);
        }

        cart.clear();
    }

    private void saveReceipt(List<CartItem> items, double baseTotal, double addonsTotal,
                             double appliedDisc, String promoLabel, double finalTotal) {
        String fileName = "receipt.txt";
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("==== Starbucks Receipt ====\n");
        sb.append("Date: ").append(LocalDate.now()).append("  Time: ").append(LocalTime.now().format(tf)).append("\n\n");
        for (CartItem ci : items) {
            sb.append(String.format("%-28s (%-6s)  base $%5.2f  add-ons [%s] $%4.2f%n",
                    ci.drink.getName(), ci.drink.getSize(), ci.basePrice(), ci.addonsLabel(), ci.addonsCost()));
        }
        sb.append(String.format("\nDrinks total:     $%.2f%n", baseTotal));
        sb.append(String.format("Add-ons total:    $%.2f%n", addonsTotal));
        sb.append(String.format("Promotion:  %s  (-$%.2f)%n", promoLabel, appliedDisc));
        sb.append(String.format("TOTAL DUE:        $%.2f%n", finalTotal));
        sb.append("===========================\n\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(sb.toString());
            System.out.println("Receipt appended to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing receipt: " + e.getMessage());
        }
    }

    // ======= Reporting =======
    private void printSalesSummary() {
        System.out.println("\n=== Today's Sales Summary ===");
        System.out.printf("Total Sales: $%.2f%n", totalSales);

        if (itemCountByName.isEmpty()) {
            System.out.println("No drinks sold yet.");
            return;
        }

        String mostPopularName = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> e : itemCountByName.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                mostPopularName = e.getKey();
            }
        }
        System.out.printf("Most Popular Drink: %s (%d sold)%n", mostPopularName, maxCount);

        // Drinks not sold yet (by name, ignoring size)
        Set<String> soldNames = new HashSet<>(itemCountByName.keySet());
        Set<String> notSold = new LinkedHashSet<>();
        for (Type t : menu) {
            if (t.getName() != null && !soldNames.contains(t.getName())) {
                notSold.add(t.getName());
            }
        }
        if (!notSold.isEmpty()) {
            System.out.println("Drinks not sold yet: " + String.join(", ", notSold));
        }
    }

    // ======= CSV =======
    public void loadMenuFromCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return;

            // Map header → index (normalized to lower-case w/o spaces)
            String[] h = header.split(",", -1);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < h.length; i++) {
                String key = h[i].trim().toLowerCase().replace(" ", "");
                idx.put(key, i);
            }

            Integer iName  = idx.get("drinkname");
            Integer iType  = idx.get("drinktype");
            Integer iSize  = idx.get("size");
            Integer iPrice = idx.get("price");

            if (iName == null || iType == null || iSize == null || iPrice == null) {
                System.err.println("CSV missing required columns. Expected: Drink Name, Drink Type, Size, Price");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = line.split(",", -1);
                if (f.length <= Math.max(Math.max(iName, iType), Math.max(iSize, iPrice))) continue;

                String name = f[iName].trim();
                String typeName = f[iType].trim();
                String size = f[iSize].trim();

                double price;
                try {
                    price = Double.parseDouble(f[iPrice].trim());
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping row with bad price: " + line);
                    continue;
                }

                // Type(name, size, price, compute_price, type_name)
                Type t = new Type(name, size, price, price, typeName);
                menu.add(t);
            }
            System.out.println("Loaded " + menu.size() + " menu items from " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // ======= Main =======
    public static void main(String[] args) {
        StarbucksSalesTracker app = new StarbucksSalesTracker();
        // Load menu.csv from working dir by default
        String path = "menu.csv";
        if (args != null && args.length > 0) path = args[0];
        app.loadMenuFromCsv(path);
        app.runMenu();
    }
}
