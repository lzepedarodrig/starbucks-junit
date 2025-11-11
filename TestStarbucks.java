// Test class for StarbucksSalesTracker
// Note: This test requires JUnit to be in the classpath
// If JUnit is not available, these tests will not compile

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;

public class TestStarbucks {

    private StarbucksSalesTracker sales;

    public void setUp(){
         sales = new StarbucksSalesTracker();
    }

    public void testBulkDiscountAppliedCorrectly() throws Exception {
        setUp();
        
        // Create a temporary menu CSV with one drink
        File temp = File.createTempFile("menu", ".csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write("Drink Name,Drink Type,Size,Price\n");
            bw.write("Latte,Coffee,Tall,3.50\n");
        }

        // Load that menu
        sales.loadMenuFromCsv(temp.getAbsolutePath());

        // Simulate user input for ordering 4 drinks to qualify for bulk discount (10% off base drinks)
        // Base: 4 × $3.50 = $14.00, Discount: $14.00 × 0.10 = $1.40
        // Subtotal: $14.00 - $1.40 = $12.60
        // Tax (8.25%): $12.60 × 0.0825 = $1.0395
        // Final: $12.60 + $1.0395 = $13.6395 ≈ $13.64
        String simulatedInput = String.join("\n", List.of(
                "Latte",          // Drink 1: Drink name
                "Tall",           // Drink 1: Size
                "1",              // Drink 1: Quantity
                "0",              // Drink 1: Zero vanilla syrup shots
                "0",              // Drink 1: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 2: Drink name
                "Tall",           // Drink 2: Size
                "1",              // Drink 2: Quantity
                "0",              // Drink 2: Zero vanilla syrup shots
                "0",              // Drink 2: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 3: Drink name
                "Tall",           // Drink 3: Size
                "1",              // Drink 3: Quantity
                "0",              // Drink 3: Zero vanilla syrup shots
                "0",              // Drink 3: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 4: Drink name
                "Tall",           // Drink 4: Size
                "1",              // Drink 4: Quantity
                "0",              // Drink 4: Zero vanilla syrup shots
                "0",              // Drink 4: Zero espresso shots
                "N",              // Don't add another item
                "N"               // Don't save receipt
        )) + "\n";

        ByteArrayInputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(input);

        // Access the private placeOrder() using reflection
        var method = StarbucksSalesTracker.class.getDeclaredMethod("placeOrder", Scanner.class);
        method.setAccessible(true);
        method.invoke(sales, scanner);

        // Check that the total sales equals expected value after bulk discount and tax
        // Access statistics field using reflection
        var statsField = StarbucksSalesTracker.class.getDeclaredField("statistics");
        statsField.setAccessible(true);
        SalesStatistics stats = (SalesStatistics) statsField.get(sales);
        
        double totalRevenue = stats.getTotalRevenue();
        // Expected: $13.64 (with tax)
        double expected = 13.64;
        double tolerance = 0.01;
        
        if (Math.abs(totalRevenue - expected) > tolerance) {
            throw new AssertionError("Expected total revenue to be approximately " + expected + 
                " but got " + totalRevenue);
        }

        temp.deleteOnExit();
    }

    // Main method to run tests if JUnit is not available
    public static void main(String[] args) {
        TestStarbucks test = new TestStarbucks();
        try {
            test.testBulkDiscountAppliedCorrectly();
            System.out.println("Test passed!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
