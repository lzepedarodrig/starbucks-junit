# UML Class Diagram

## Starbucks Sales Tracker - Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            UML Class Diagram                                 │
│                    Starbucks Sales Tracker System                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                Interfaces                                    │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │    <<interface>>     │
                    │      Pricable        │
                    ├──────────────────────┤
                    │ + calculatePrice()   │
                    │ + getDisplayLabel()  │
                    └──────────────────────┘
                             ▲
                             │ implements
                             │
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Core Classes                                    │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │   <<abstract>>       │
                    │       Drink          │
                    ├──────────────────────┤
                    │ - name: String       │
                    │ - size: String       │
                    │ - price: double      │
                    ├──────────────────────┤
                    │ + getName(): String  │
                    │ + getSize(): String  │
                    │ + getPrice(): double │
                    │ + getCategoryName()  │
                    │   : String {abstract}│
                    │ + getDisplayLabel()  │
                    │   : String {abstract}│
                    │ + calculatePrice()   │
                    │   : double           │
                    └──────────────────────┘
                             ▲
                             │ extends
        ┌────────────────────┼────────────────────┬────────────────────┐
        │                    │                    │                    │
┌───────┴──────┐  ┌──────────┴─────────┐  ┌──────┴──────┐  ┌────────┴────────┐
│    Coffee    │  │        Tea         │  │  Refresher  │  │  Frappuccino   │
├──────────────┤  ├────────────────────┤  ├─────────────┤  ├────────────────┤
│ + Coffee(...)│  │ + Tea(...)         │  │ + Refresher │  │ + Frappuccino  │
│ + getCategory│  │ + getCategoryName()│  │   (...)     │  │   (...)        │
│   Name()     │  │ + getDisplayLabel()│  │ + getCategory│  │ + getCategory  │
│ + getDisplay │  └────────────────────┘  │   Name()     │  │   Name()       │
│   Label()    │                          │ + getDisplay │  │ + getDisplay   │
└──────────────┘                          │   Label()    │  │   Label()      │
                                          └─────────────┘  └────────────────┘
                                                                    │
                                                          ┌─────────┴─────────┐
                                                          │     Seasonal      │
                                                          ├───────────────────┤
                                                          │ + Seasonal(...)   │
                                                          │ + getCategoryName │
                                                          │   ()              │
                                                          │ + getDisplayLabel │
                                                          │   ()              │
                                                          └───────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          Factory Pattern                                     │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │   DrinkFactory       │
                    ├──────────────────────┤
                    │ + createDrink(name:  │
                    │   String, size:      │
                    │   String, price:     │
                    │   double, typeName:  │
                    │   String): Drink     │
                    └──────────────────────┘
                             │ creates
                             │
                             ▼
                    ┌──────────────────────┐
                    │   Drink instances    │
                    │   (Coffee, Tea, etc.)│
                    └──────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                        Strategy Pattern (Promotions)                         │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │  <<interface>>       │
                    │     Promotion        │
                    ├──────────────────────┤
                    │ + calculateDiscount  │
                    │   (items, baseTotal, │
                    │   addonsTotal):      │
                    │   double             │
                    │ + getPromotionName() │
                    │   : String           │
                    │ + isApplicable(items)│
                    │   : boolean          │
                    └──────────────────────┘
                             ▲
                             │ implements
        ┌────────────────────┼────────────────────┬────────────────────┐
        │                    │                    │                    │
┌───────┴──────────┐  ┌──────┴──────────┐  ┌──────┴──────────────┐
│ BulkOrder        │  │ HappyHour       │  │ BuyNGetM           │
│ Promotion        │  │ Promotion       │  │ Promotion          │
├──────────────────┤  ├─────────────────┤  ├────────────────────┤
│ - MIN_ITEMS: int │  │ - HAPPY_HOUR_   │  │ - REQUIRED_        │
│ - DISCOUNT_%:    │  │   START:         │  │   QUANTITY: int    │
│   double         │  │   LocalTime     │  │ - menu: List<Drink>│
├──────────────────┤  │ - HAPPY_HOUR_   │  ├────────────────────┤
│ + calculateDisc  │  │   END: LocalTime│  │ + calculateDiscount│
│   ount(): double │  │ - DISCOUNT_%:   │  │   (): double       │
│ + getPromotion   │  │   double        │  │ + getPromotionName │
│   Name(): String │  ├─────────────────┤  │   (): String       │
│ + isApplicable() │  │ + calculateDisc │  │ + isApplicable():  │
│   : boolean      │  │   ount(): double│  │   boolean          │
└──────────────────┘  │ + getPromotion  │  └────────────────────┘
                      │   Name(): String│
                      │ + isApplicable()│
                      │   : boolean     │
                      └─────────────────┘
                             ▲
                             │ uses
                    ┌────────┴────────┐
                    │ PromotionManager│
                    ├─────────────────┤
                    │ - promotions:   │
                    │   List<Promotion│
                    ├─────────────────┤
                    │ + selectBest    │
                    │   Promotion(...)│
                    │   : Promotion   │
                    └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                        Order & Cart Management                               │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │     CartItem         │
                    ├──────────────────────┤
                    │ - drink: Drink       │
                    │ - quantity: int      │
                    │ - vanillaShots: int  │
                    │ - espressoShots: int │
                    ├──────────────────────┤
                    │ + getDrink(): Drink  │
                    │ + getQuantity(): int │
                    │ + addonsCost():      │
                    │   double             │
                    │ + basePrice(): double│
                    │ + getDisplayString() │
                    │   : String           │
                    └──────────────────────┘
                             ▲
                             │ contains
                             │
                    ┌────────┴────────┐
                    │      Order      │
                    ├─────────────────┤
                    │ - items: List<  │
                    │   CartItem>     │
                    │ - baseTotal:    │
                    │   double        │
                    │ - addonsTotal:  │
                    │   double        │
                    │ - discount:     │
                    │   double        │
                    │ - promotionName:│
                    │   String        │
                    │ - subtotalBefore│
                    │   Tax: double   │
                    │ - tax: double   │
                    │ - finalTotal:   │
                    │   double        │
                    │ - timestamp:    │
                    │   LocalDateTime │
                    ├─────────────────┤
                    │ + getItems():   │
                    │   List<CartItem>│
                    │ + getBaseTotal()│
                    │   : double      │
                    │ + getFinalTotal │
                    │   (): double    │
                    │ + ...           │
                    └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         Statistics & Tracking                                │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │  SalesStatistics     │
                    ├──────────────────────┤
                    │ - drinkCountByName   │
                    │   AndSize: Map<String│
                    │   , Integer>         │
                    │ - addonCount: Map<   │
                    │   String, Integer>   │
                    │ - addonRevenue: Map< │
                    │   String, Double>    │
                    │ - categoryItemCount: │
                    │   Map<String,        │
                    │   Integer>           │
                    │ - categoryRevenue:   │
                    │   Map<String,        │
                    │   Double>            │
                    │ - totalDiscountGiven:│
                    │   double             │
                    │ - ordersWithPromo:   │
                    │   int                │
                    │ - uniqueDrinkTypes:  │
                    │   Set<String>        │
                    │ - totalDrinksSold:   │
                    │   int                │
                    │ - totalRevenue:      │
                    │   double             │
                    ├──────────────────────┤
                    │ + recordOrder(order: │
                    │   Order): void       │
                    │ + getMostPopular     │
                    │   Drink(): String    │
                    │ + getTop3Addons():   │
                    │   List<String>       │
                    │ + getTotalAddon      │
                    │   Revenue(): double  │
                    │ + getUnsoldDrinks(   │
                    │   menu): Set<String> │
                    │ + ...                │
                    └──────────────────────┘
                             ▲
                             │ uses
                             │
                    ┌────────┴────────┐
                    │      Order      │
                    └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          Main Application                                    │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────────┐
                    │ StarbucksSalesTracker│
                    ├──────────────────────┤
                    │ - menu: List<Drink>  │
                    │ - cart: List<        │
                    │   CartItem>          │
                    │ - statistics:        │
                    │   SalesStatistics    │
                    │ - completedOrders:   │
                    │   List<Order>        │
                    │ - promotionManager:  │
                    │   PromotionManager   │
                    │ - TAX_RATE: double   │
                    ├──────────────────────┤
                    │ + loadMenuFromCsv(   │
                    │   filePath): void    │
                    │ + runMenu(): void    │
                    │ - showAllDrinks():   │
                    │   void               │
                    │ - searchByType(...): │
                    │   void               │
                    │ - placeOrder(...):   │
                    │   void               │
                    │ - checkoutAndSave    │
                    │   Receipt(...): void │
                    │ - printSalesSummary()│
                    │   : void             │
                    │ + main(args): void   │
                    └──────────────────────┘
                             │ uses
        ┌────────────────────┼────────────────────┬────────────────────┐
        │                    │                    │                    │
        ▼                    ▼                    ▼                    ▼
┌──────────────┐  ┌──────────────────┐  ┌──────────────┐  ┌──────────────┐
│   Drink      │  │ PromotionManager │  │ SalesStats   │  │    Order     │
│   (via menu) │  │                  │  │              │  │              │
└──────────────┘  └──────────────────┘  └──────────────┘  └──────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            Key Relationships                                 │
└─────────────────────────────────────────────────────────────────────────────┘

1. Inheritance:
   - Coffee, Tea, Refresher, Frappuccino, Seasonal extend Drink
   - All drink categories implement Pricable interface (via Drink)

2. Composition:
   - StarbucksSalesTracker contains List<Drink> (menu)
   - StarbucksSalesTracker contains List<CartItem> (cart)
   - StarbucksSalesTracker contains SalesStatistics
   - StarbucksSalesTracker contains PromotionManager
   - Order contains List<CartItem>
   - CartItem contains Drink

3. Strategy Pattern:
   - Promotion interface defines strategy contract
   - BulkOrderPromotion, HappyHourPromotion, BuyNGetMPromotion implement Promotion
   - PromotionManager uses Promotion strategies

4. Factory Pattern:
   - DrinkFactory creates Drink instances based on type name
   - Returns appropriate concrete class (Coffee, Tea, etc.)

5. Dependency:
   - SalesStatistics uses Order to record statistics
   - BuyNGetMPromotion uses List<Drink> (menu) to find cheapest size

```

## Diagram Legend

- **Solid line with arrow (─→)**: Inheritance/extends relationship
- **Dashed line with arrow (- -→)**: Implements/interface relationship
- **Solid line with diamond (─◆)**: Composition/contains relationship
- **Solid line (─)**: Uses/dependency relationship
- **<<abstract>>**: Abstract class
- **<<interface>>**: Interface

## Design Patterns Highlighted

1. **Strategy Pattern**: Promotion interface and implementations (BulkOrderPromotion, HappyHourPromotion, BuyNGetMPromotion) with PromotionManager
2. **Factory Pattern**: DrinkFactory for creating Drink instances
3. **Inheritance**: Drink hierarchy with category-specific classes
4. **Composition**: Order contains CartItems, CartItem contains Drink, StarbucksSalesTracker contains various components

## Key Interfaces

1. **Pricable**: Implemented by Drink, defines price calculation and display contract
2. **Promotion**: Defines promotion strategy contract, implemented by promotion classes

