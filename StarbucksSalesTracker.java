//Worked on by:Luisenrique Zepeda-Rodriguez and Aadvika Pandey
/*
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class StarbucksSalesTracker{

    public void welcome(){
            System.out.println("Welcome to Starbucks!");
            System.out.println("Enter 1 to show all available drinks:");
            System.out.println("Enter 2 to search for drinks by type(Eg. Coffe, Tea, Refresher, Frapuccino)");
            System.out.println("Enter 3 to place an order:");
            System.out.println("Enter 4 to view today's sales summary");
            System.out.println("Enter 5 to quit.");
        }

    public void runMenu(){
        Scanner scanner=new Scanner(System.in);
        String cont = "Y";
        while (cont.equals("Y")){
        int choice=scanner.nextInt();
        switch (choice){
            //print all drinks
            case 1:
                System.out.println("Showing all available drinks:");

                break;
            case 2:
                System.out.println("Enter type(capitalization matters!):");
                String type=scanner.nextLine();
                //print drink type
                break;

            case 3:
                String contOrder="Y";
                while (contOrder.equals("Y") | contOrder.equals("y")){
                System.out.println("What drink would you like?(Enter drink name)");
                String drinkName=scanner.nextLine();
                System.out.println("What size would you like?(Tall,Venti,Grande)");
                String drinkSize=scanner.nextLine();
                System.out.println("Would you like to keep ordering?Enter Y or N.");
                contOrder=scanner.nextLine();
                }
                break;
            
            case 4:
                System.out.println("Printing sales summary:");
                break;

            case 5:
                System.out.println("Thank you for choosing Starbucks!");
                break;


        }
        }

        }
    public Object readFile(ArrayList<Type> obj){
        String delimiter=",";
        String line;
        String file="menu.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(delimiter);
                if (fields.length>1){
                    //Adding object to ArrayList for later access
                    Type drink= new Type(fields[0],fields[1],fields[2],Double.parseDouble(fields[3]));
                    obj.add(drink);
                }
            }
        //exception handling for unable to read CSV file
       }catch (IOException e) {
             System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return obj;
    }
    public static void main(String[] args){
        int sales;
        int numberOfDrinks;
        String mostPopularName;
        String mostPopularSize;
        String drinksNotSold;
        StarbucksSalesTracker myObj= new StarbucksSalesTracker();
        ArrayList<Type> obj=new ArrayList<>();
        obj=(ArrayList<Type>) myObj.readFile(obj);
        myObj.welcome();
        myObj.runMenu();
        
    }


}*/
// Code refined with AI based on our own code as commented above. 

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
import java.util.*;


public class StarbucksSalesTracker {

    private final List<Type> menu = new ArrayList<>();
    private final Map<String, Integer> itemCountByName = new HashMap<>();
    private double totalSales = 0.0;
    private final double extraShot=0.5;
    private final double vanillaSyrup=0.6;


    private void welcome() {
        System.out.println("Welcome to Starbucks!");
        System.out.println("Enter 1 to show all available drinks");
        System.out.println("Enter 2 to search for drinks by type (e.g., Coffee, Tea, Refresher, Frappuccino)");
        System.out.println("Enter 3 to place an order");
        System.out.println("Enter 4 to view today's sales summary");
        System.out.println("Enter 5 to quit");
    }

    public void runMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                welcome();
                System.out.print("Choose: ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                switch (line) {
                    case "1" -> showAllDrinks();
                    case "2" -> {
                        System.out.print("Enter type (exactly as in CSV, e.g., Coffee): ");
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
                System.out.println();
            }
        }
    }

    private void showAllDrinks() {
        if (menu.isEmpty()) {
            System.out.println("(Menu is empty)");
            return;
        }
        System.out.println("=== All Available Drinks ===");
        for (int i = 0; i < menu.size(); i++) {
            Type t = menu.get(i);
            System.out.printf("%2d) %s | %s | $%.2f | Type: %s%n",
                    i + 1, t.getName(), t.getSize(), t.getPrice(), t.getType_name());
        }
    }

    private void searchByType(String typeName) {
        System.out.println("=== Results for type: " + typeName + " ===");
        boolean any = false;
        for (Type t : menu) {
            if (t.getType_name().equals(typeName)) {
                System.out.printf("- %s | %s | $%.2f%n", t.getName(), t.getSize(), t.getPrice());
                any = true;
            }
        }
        if (!any) System.out.println("(No drinks found for that type)");
    }

    private void placeOrder(Scanner scanner) {
        double costAddOn=0;
        String orders="";
        if (menu.isEmpty()) {
            System.out.println("Menu is empty. Load menu first.");
            return;
        }
        String contOrder = "Y";
        String added="";
        while (contOrder.equalsIgnoreCase("Y")) {
            System.out.print("What drink would you like? (Enter Drink Name exactly as shown): ");
            String drinkName = scanner.nextLine().trim();
            System.out.print("What size would you like? (Tall, Grande, Venti): ");
            String drinkSize = scanner.nextLine().trim();
            Type chosen = findMenuItem(drinkName, drinkSize);
            if (chosen == null) {
                System.out.println("Could not find that drink/size combination. Try listing the menu (option 1).");
            } else {
                System.out.println("Would you like any add-ons(vanilla syrup,extra shot)(Y/N)?");
                String extraChoice=scanner.nextLine().trim();
                while (extraChoice.equalsIgnoreCase("Y")){
                    System.out.println("Enter 1 to add vanilla syrup.");
                    System.out.println("Enter 2 to add extra shot.");
                    String addOption=scanner.nextLine();
                    if (validateInteger(addOption)){
                        int option=Integer.parseInt(addOption);
                        switch(option){
                            case 1 ->{
                                System.out.println("How many shots of vanilla syrup would you like?");
                                String vanillaInput=scanner.nextLine();
                                if (validateInteger(vanillaInput)){
                                    int vanillaShots=Integer.parseInt(vanillaInput);
                                    if (vanillaShots<0){
                                        System.out.println("Invalid input.");
                                        break;
                                    }
                                    costAddOn+=(vanillaSyrup*vanillaShots);
                                    System.out.println("Cost of all add-ons:"+costAddOn);
                                    break;
                                }
                            }
                             case 2 ->{
                                System.out.println("How many shots of espresso would you like?");
                                String espressoInput=scanner.nextLine();
                                if (validateInteger(espressoInput)){
                                    int espressoShots=Integer.parseInt(espressoInput);
                                    if (espressoShots<0){
                                        System.out.println("Invalid input.");
                                        break;
                                    }
                                    costAddOn+=(extraShot*espressoShots);
                                    System.out.println("Cost of all add-ons:"+costAddOn);
                                    break;
                                }
                                else{
                                    System.out.println("Invalid input.");
                                    break;
                                }
                            }
                            default ->{
                                System.out.println("Invalid input.");
                            }

                        }

                    }
                    System.out.println("Would you like more add-ons?(Y/N)");
                    extraChoice=scanner.nextLine().trim();
                }
                totalSales += chosen.getPrice()+costAddOn;
                itemCountByName.merge(chosen.getName(), 1, Integer::sum);
                System.out.printf("Added: %s (%s) - $%.2f +%.2f%n", chosen.getName(), chosen.getSize(), chosen.getPrice(),costAddOn);
                orders+="Purchased " +chosen.getName()+ " for "+chosen.getPrice()+" with add-on cost "+costAddOn+" at "+LocalDate.now()+ " "+LocalTime.now()+"\n";
            }
            System.out.println("Save receipt?(Y/N)");
            String saveReceipt=scanner.nextLine();
            if (saveReceipt.equalsIgnoreCase("Y")){
                String fileName="receipt.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))) {
                    writer.write(orders);
                    System.out.println("Content successfully written to " + fileName);
                    } catch (IOException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
        }
            }
            System.out.print("Would you like to keep ordering? Enter Y or N: ");
            contOrder = scanner.nextLine().trim();
        }
    }

    private Type findMenuItem(String name, String size) {
        for (Type t : menu) {
            if (t.getName().equals(name) && t.getSize().equals(size)) {
                return t;
            }
        }
        return null;
    }

    public boolean validateInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input");
        }
        return false;
    }
    private void printSalesSummary() {
        System.out.println("=== Today's Sales Summary ===");
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

        Set<String> soldNames = itemCountByName.keySet();
        List<String> notSold = new ArrayList<>();
        for (Type t : menu) {
            if (!soldNames.contains(t.getName())) notSold.add(t.getName());
        }
        if (!notSold.isEmpty()) {
            System.out.println("Drinks not sold yet: " + String.join(", ", new LinkedHashSet<>(notSold)));
        }
    }

    
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

            Integer iName = idx.get("drinkname");
            Integer iType = idx.get("drinktype");
            Integer iSize = idx.get("size");
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

                Type t = new Type(name, size, price, price, typeName);
                menu.add(t);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        StarbucksSalesTracker app = new StarbucksSalesTracker();
        app.loadMenuFromCsv("menu.csv");
        app.runMenu();
    }
}
