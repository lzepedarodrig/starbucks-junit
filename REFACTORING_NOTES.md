# Refactoring Notes

This document summarizes key refactoring improvements made to the Starbucks Sales Tracker codebase.

## Refactoring 1: Eliminated String-Based Type Checking with Polymorphism

### Smell / Issue Detected
The original implementation used string comparisons to determine drink types throughout the codebase:
```java
if (ci.drink.getType_name().equalsIgnoreCase("Tea")) {
    teaBaseTotal += ci.basePrice();
}
```
This approach violated the Open/Closed Principle and created maintenance issues:
- Adding new drink categories required modifying conditionals throughout the code
- Type checking logic was scattered across multiple methods
- Risk of typos in string comparisons
- No compile-time type safety

### Refactoring Applied
1. Created abstract `Drink` class with abstract method `getCategoryName()`
2. Implemented category-specific classes (`Coffee`, `Tea`, `Refresher`, `Frappuccino`, `Seasonal`) extending `Drink`
3. Replaced all string comparisons with polymorphic method calls:
   ```java
   if (item.getDrink() instanceof Tea) {
       teaBaseTotal += item.basePrice();
   }
   ```
4. Used `getCategoryName()` for display and filtering operations

### Why It Improved the Design
- **Clarity**: Code intent is clearer - `instanceof Tea` is more readable than string comparison
- **Maintainability**: Adding new drink categories only requires creating a new class, no conditional modifications
- **Type Safety**: Compiler enforces type correctness, reducing runtime errors
- **Testability**: Category-specific behavior can be tested in isolation through subclass implementations
- **Extensibility**: New categories can override behavior (e.g., custom pricing) without affecting existing code

## Refactoring 2: Extracted Promotion Logic with Strategy Pattern

### Smell / Issue Detected
Promotion calculation was implemented as hard-coded methods in the main `StarbucksSalesTracker` class:
```java
private double calculateBulkDiscount(int itemCount, double baseTotal) { ... }
private double calculateHappyHourTeaDiscount(double teaBaseTotal) { ... }
// Promotion selection logic with if-else chains
double appliedDisc = Math.max(bulkDisc, hhDisc);
```
Issues identified:
- Long method with multiple responsibilities
- Hard to add new promotions (requires modifying existing code)
- Promotion logic mixed with business logic
- Difficult to test promotions in isolation
- Violation of Single Responsibility Principle

### Refactoring Applied
1. Created `Promotion` interface with `calculateDiscount()`, `getPromotionName()`, and `isApplicable()` methods
2. Extracted promotion logic into separate strategy classes:
   - `BulkOrderPromotion.java`
   - `HappyHourPromotion.java`
   - `BuyNGetMPromotion.java`
3. Created `PromotionManager` to select the best applicable promotion
4. Removed promotion calculation methods from `StarbucksSalesTracker`

### Why It Improved the Design
- **Maintainability**: Each promotion is self-contained in its own class, making changes isolated
- **Testability**: Promotions can be unit tested independently without instantiating the entire application
- **Extensibility**: New promotions can be added by implementing the `Promotion` interface, following Open/Closed Principle
- **Clarity**: Promotion selection logic is centralized in `PromotionManager`, making the flow easier to understand
- **Separation of Concerns**: Business logic (ordering) is separated from promotion logic

## Refactoring 3: Centralized Sales Tracking with SalesStatistics Class

### Smell / Issue Detected
Sales tracking was implemented using scattered data structures in `StarbucksSalesTracker`:
```java
private final Map<String, Integer> itemCountByName = new HashMap<>();
private double totalSales = 0.0;
// Statistics calculation logic mixed throughout the class
```
Problems identified:
- Statistics logic scattered across multiple methods
- Difficult to add new metrics (requires modifying multiple places)
- No single source of truth for sales data
- Statistics calculation mixed with display logic
- Hard to test statistics independently

### Refactoring Applied
1. Created `SalesStatistics` class to encapsulate all sales tracking:
   - `Map<String, Integer> drinkCountByNameAndSize`
   - `Map<String, Integer> addonCount`
   - `Map<String, Double> addonRevenue`
   - `Map<String, Integer> categoryItemCount`
   - `Map<String, Double> categoryRevenue`
   - Promotion impact tracking
2. Implemented `recordOrder(Order order)` method to update all statistics in one place
3. Added query methods: `getMostPopularDrink()`, `getTop3Addons()`, `getUnsoldDrinks()`, etc.
4. Created `Order` class to represent completed orders with all relevant data
5. Moved statistics display logic to use `SalesStatistics` methods

### Why It Improved the Design
- **Maintainability**: All statistics logic is in one place, making it easy to modify or extend
- **Testability**: `SalesStatistics` can be tested independently with mock `Order` objects
- **Clarity**: Statistics calculation is separated from display, making both easier to understand
- **Extensibility**: Adding new metrics only requires updating `SalesStatistics` class
- **Data Integrity**: Single `recordOrder()` method ensures all statistics are updated consistently
- **Reusability**: `SalesStatistics` could be reused in other contexts (e.g., reporting module, API)

