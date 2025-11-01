import org.junit.Test;
import static org.junit.Assert.*;

public class TestStarbucks {
    @Test
    public void addSum() {
        assertEquals(3, 1 + 1);
    }
}
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;
import org.junit.Before;

public class TestStarbucks {

    private StarbucksSalesTracker sales;

    @Before
    public void setUp(){
         sales = new StarbucksSalesTracker();
    }

    @Test
    public void testBulkDiscountAppliedCorrectly() throws Exception {
        // Create a temporary menu CSV with one drink
        File temp = File.createTempFile("menu", ".csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write("Drink Name,Drink Type,Size,Price\n");
            bw.write("Latte,Coffee,Tall,3.50\n");
        }

        // Load that menu
        sales.loadMenuFromCsv(temp.getAbsolutePath());

        // Simulate user input for ordering 4 drinks to qualify for bulk discount (10% off base drinks)
        // Base: 4 × $3.50 = $14.00, Discount: $14.00 × 0.10 = $1.40, Final: $14.00 - $1.40 = $12.60
        String simulatedInput = String.join("\n", List.of(
                "Latte",          // Drink 1: Drink name
                "Tall",           // Drink 1: Size
                "0",              // Drink 1: Zero vanilla syrup shots
                "0",              // Drink 1: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 2: Drink name
                "Tall",           // Drink 2: Size
                "0",              // Drink 2: Zero vanilla syrup shots
                "0",              // Drink 2: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 3: Drink name
                "Tall",           // Drink 3: Size
                "0",              // Drink 3: Zero vanilla syrup shots
                "0",              // Drink 3: Zero espresso shots
                "Y",              // Add another item
                "Latte",          // Drink 4: Drink name
                "Tall",           // Drink 4: Size
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

        // Check that the total sales equals $12.60 after bulk discount
        var field = StarbucksSalesTracker.class.getDeclaredField("totalSales");
        field.setAccessible(true);
        double totalSales = (double) field.get(sales);

        assertEquals(12.60, totalSales, 0.001);

        temp.deleteOnExit();
    }

}
