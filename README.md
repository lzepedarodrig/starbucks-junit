# Starbucks Sales Tracker

A command-line application for tracking Starbucks drink sales with menu management, order processing, promotions, and sales analytics.

## Run & Test Instructions

### Building and Running

1. **Prerequisites**: Java JDK 8 or higher

2. **Compile the program**:
   ```bash
   javac *.java
   ```

3. **Run the program**:
   ```bash
   java StarbucksSalesTracker
   ```
   Or with a custom CSV file:
   ```bash
   java StarbucksSalesTracker menu.csv
   ```

4. **Using the CLI**:
   - Select option 1 to view all available drinks
   - Select option 2 to search drinks by type (Coffee, Tea, Refresher, Frappuccino, Seasonal)
   - Select option 3 to place an order (supports quantity, add-ons, and automatic promotions)
   - Select option 4 to view today's sales summary
   - Select option 5 to quit

### Running Tests

1. **Compile test files**:
   ```bash
   javac *.java
   ```

2. **Run the test class**:
   ```bash
   java TestStarbucks
   ```
   
   Note: For JUnit tests, ensure JUnit is in your classpath and use:
   ```bash
   javac -cp ".:junit-4.13.2.jar:hamcrest-core-1.3.jar" *.java
   java -cp ".:junit-4.13.2.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore StarbucksTest
   ```

## Project Map

All source files are in the root directory:

- **Core Domain Classes**:
  - `Drink.java` - Abstract base class for all drink types
  - `Coffee.java`, `Tea.java`, `Refresher.java`, `Frappuccino.java`, `Seasonal.java` - Concrete drink category classes
  - `CartItem.java` - Represents an item in the shopping cart with quantity and add-ons
  - `Order.java` - Represents a completed order with totals, discounts, and timestamp

- **Factory & Creation**:
  - `DrinkFactory.java` - Factory pattern for creating drink instances from CSV data

- **Promotions (Strategy Pattern)**:
  - `Promotion.java` - Interface for promotion strategies
  - `BulkOrderPromotion.java` - 10% off for 4+ items
  - `HappyHourPromotion.java` - 20% off Tea drinks during 2-4 PM
  - `BuyNGetMPromotion.java` - Buy 3 get 1 free promotion
  - `PromotionManager.java` - Manages and selects the best applicable promotion

- **Statistics & Tracking**:
  - `SalesStatistics.java` - Tracks all sales metrics, add-ons, categories, and promotions

- **Main Application**:
  - `StarbucksSalesTracker.java` - Main CLI application with menu, ordering, and reporting
  - `TestStarbucks.java` - Test suite for bulk discount functionality

- **Interfaces**:
  - `Pricable.java` - Interface for objects with price and display label

- **Data**:
  - `menu.csv` - CSV file containing drink menu data

## Design Pattern & Interface(s)

### Design Patterns

1. **Strategy Pattern** (Promotions):
   - **Location**: `Promotion.java` interface and `BulkOrderPromotion.java`, `HappyHourPromotion.java`, `BuyNGetMPromotion.java`
   - **Purpose**: Encapsulates promotion algorithms as interchangeable strategies. The `PromotionManager` selects the best promotion without hard-coded conditionals, making it easy to add new promotions without modifying existing code.

2. **Factory Pattern** (Drink Creation):
   - **Location**: `DrinkFactory.java`
   - **Purpose**: Centralizes drink object creation from CSV data. Returns appropriate category-specific classes (Coffee, Tea, etc.) based on type name, encapsulating the creation logic and ensuring consistent object initialization.

### Interfaces

1. **Pricable Interface**:
   - **Methods**: `calculatePrice()`, `getDisplayLabel()`
   - **Responsibilities**: Defines contract for objects that have a price and can be displayed. Implemented by `Drink` class, enabling polymorphic pricing and display behavior across all drink types.

2. **Promotion Interface**:
   - **Methods**: `calculateDiscount()`, `getPromotionName()`, `isApplicable()`
   - **Responsibilities**: Defines contract for promotion strategies. Allows different promotion types to be treated uniformly, enabling the Strategy pattern implementation for flexible discount calculation.

## Known Limitations / Future Improvements

- **CSV Validation**: Currently handles missing fields gracefully but could benefit from more robust validation (e.g., price ranges, valid size values, duplicate detection with warnings).

- **Promotion Configuration**: Promotions are hard-coded in the application. Future enhancement could load promotions from a configuration file or database, allowing dynamic promotion management without code changes.

- **Persistence**: Sales statistics are stored only in memory and reset when the program exits. Adding database persistence would allow historical sales tracking across multiple sessions and days.

- **Add-on Extensibility**: Current add-ons (vanilla syrup, extra shot) are hard-coded. A more flexible system could allow configurable add-ons with different prices per drink category.

- **Receipt Customization**: Receipt format is fixed. Future improvement could allow customizable receipt templates or export to different formats (PDF, email).

