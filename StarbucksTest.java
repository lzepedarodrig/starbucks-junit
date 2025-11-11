
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

public class StarbucksTest {

    private StarbucksSalesTracker tracker;
    private Drink latte;
    private Drink cappuccino;
    private Drink greenTea;

    @Before
    public void setUp() {
        tracker = new StarbucksSalesTracker();

        // Create some sample drinks manually (instead of loading CSV)
        latte = DrinkFactory.createDrink("Latte", "Grande", 4.50, "Coffee");
        cappuccino = DrinkFactory.createDrink("Cappuccino", "Grande", 4.00, "Coffee");
        greenTea = DrinkFactory.createDrink("Green Tea", "Tall", 3.50, "Tea");

        // Add drinks to tracker menu
        tracker.addDrinkForTest(latte);
        tracker.addDrinkForTest(cappuccino);
        tracker.addDrinkForTest(greenTea);
    }

    // ======= Test 1: Show all drinks =======
    @Test
    public void testShowAllDrinks() {
        List<Drink> coffees = tracker.searchByTypeForTest("Coffee");
        assertTrue(coffees.contains(latte));
        assertTrue(coffees.contains(cappuccino));

        List<Drink> teas = tracker.searchByTypeForTest("Tea");
        assertTrue(teas.contains(greenTea));
    }

    // ======= Test 2: Search drinks by type =======
    @Test
    public void testSearchByType() {
        List<Drink> coffees = tracker.searchByTypeForTest("Coffee");
        assertEquals(2, coffees.size());

        List<Drink> teas = tracker.searchByTypeForTest("Tea");
        assertEquals(1, teas.size());
    }

    // ======= Test 3: Place order with add-ons =======
    @Test
    public void testPlaceOrderWithAddOns() {
        tracker.placeOrderTest(latte, 1, 2, 1); // 2 vanilla + 1 espresso

        List<Order> orders = tracker.getCompletedOrders();
        assertEquals(1, orders.size());

        Order order = orders.get(0);
        assertEquals(1, order.getItems().get(0).getQuantity());
        assertEquals(2, order.getItems().get(0).getVanillaShots());
        assertEquals(1, order.getItems().get(0).getEspressoShots());
    }

    // ======= Test 4: Bulk discount =======
    @Test
    public void testBulkDiscount() {
        tracker.placeOrderTest(latte, 4, 0, 0); // 4 lattes to trigger bulk discount

        double discount = tracker.getTotalDiscountGiven();
        assertTrue(discount > 0);
    }

    // ======= Test 5: Drink not sold =======
    @Test
     public void testUnsoldDrinks() {
        // Initially, all drinks are unsold
        Set<String> unsold = tracker.getUnsoldDrinkNames();
        assertTrue(unsold.contains("Latte (Grande)"));
        assertTrue(unsold.contains("Green Tea (Tall)"));

        // Simulate ordering Latte
        tracker.placeOrderForTest(latte, 1, 0, 0); // 1 latte, no add-ons

        // Re-check unsold drinks
        Set<String> unsoldAfterOrder = tracker.getUnsoldDrinkNames();
        assertFalse(unsoldAfterOrder.contains("Latte (Grande)"));
        assertTrue(unsoldAfterOrder.contains("Green Tea (Tall)"));
    }
    // ======= Test 6: Total revenue calculation =======
    @Test
    public void testTotalRevenue() {
        tracker.placeOrderTest(latte, 1, 0, 0);
        tracker.placeOrderTest(greenTea, 1, 0, 0);

        double revenue = tracker.getTotalRevenue();
        assertTrue(revenue >= latte.getPrice() + greenTea.getPrice());
    }
}
