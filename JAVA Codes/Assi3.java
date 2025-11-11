import java.util.*; 
import java.text.DecimalFormat; 
 
public class Assi3 { 
 
    static class Item { 
        String name; 
        double weight; 
        double value; 
        boolean divisible; 
        int priority; 
 
        Item(String n, double w, double v, boolean d, int p) { 
            name = n; 
            weight = w; 
            value = v; 
            divisible = d; 
            priority = p; 
        } 
 
        double valuePerWeight() { 
            return value / weight; 
        } 
    } 
 
    // Sort by priority, then value/weight 
    static boolean compare(Item a, Item b) { 
        if (a.priority == b.priority) 
            return a.valuePerWeight() > b.valuePerWeight(); 
        return a.priority < b.priority; 
    } 
 
    static double fractionalKnapsack(List<Item> items, double capacity, double[] 
totalWeightCarried) { 
        // Sort items by custom comparator 
        items.sort((a, b) -> { 
            if (a.priority == b.priority) 
                return Double.compare(b.valuePerWeight(), a.valuePerWeight()); 
            return Integer.compare(a.priority, b.priority); 
        }); 
 
        System.out.println("\nSorted Items (by Priority, then Value/Weight):"); 
        System.out.printf("%-20s%-10s%-10s%-12s%-15s%-15s%n", 
                "Item", "Weight", "Value", "Priority", "Value/Weight", "Type"); 
 
        DecimalFormat df = new DecimalFormat("0.00"); 
        for (Item item : items) { 
            System.out.printf("%-20s%-10.2f%-10.2f%-12d%-15s%-15s%n", 
                    item.name, item.weight, item.value, item.priority, 
                    df.format(item.valuePerWeight()), 
                    item.divisible ? "Divisible" : "Indivisible"); 
        } 
 
        double totalValue = 0.0; 
        totalWeightCarried[0] = 0.0; 
 
        System.out.println("\nItems selected for transport:"); 
 
        for (Item item : items) { 
            if (capacity <= 0) 
                break; 
 
            if (item.divisible) { 
                double takenWeight = Math.min(item.weight, capacity); 
                double takenValue = item.valuePerWeight() * takenWeight; 
                totalValue += takenValue; 
                capacity -= takenWeight; 
                totalWeightCarried[0] += takenWeight; 
 
                System.out.println(" - " + item.name + ": " + df.format(takenWeight) 
                        + " kg, Utility = " + df.format(takenValue) 
                        + ", Priority = " + item.priority + ", Type = Divisible"); 
            } else { 
                if (item.weight <= capacity) { 
                    totalValue += item.value; 
                    capacity -= item.weight; 
                    totalWeightCarried[0] += item.weight; 
 
                    System.out.println(" - " + item.name + ": " + df.format(item.weight) 
                            + " kg, Utility = " + df.format(item.value) 
                            + ", Priority = " + item.priority + ", Type = Indivisible"); 
                } 
            } 
        } 
 
        return totalValue; 
    } 
 
    public static void main(String[] args) { 
        Scanner sc = new Scanner(System.in); 
 
        System.out.print("Enter number of relief items: "); 
        int n = sc.nextInt(); 
        sc.nextLine(); // clear newline 
 
        List<Item> items = new ArrayList<>(); 
 
        for (int i = 0; i < n; ++i) { 
            System.out.println("\nItem #" + (i + 1) + ":"); 
 
            System.out.print("Name: "); 
            String name = sc.nextLine(); 
 
            System.out.print("Weight (kg): "); 
            double weight = sc.nextDouble(); 
 
            System.out.print("Utility Value: "); 
            double value = sc.nextDouble(); 
 
            System.out.print("Is it divisible? (1 = Yes, 0 = No): "); 
            int divisibleInt = sc.nextInt(); 
 
            System.out.print("Priority (1 = High, 2 = Medium, 3 = Low): "); 
            int priority = sc.nextInt(); 
            sc.nextLine(); // consume newline 
 
            items.add(new Item(name, weight, value, divisibleInt == 1, priority)); 
        } 
 
        System.out.print("\nEnter maximum weight capacity of the boat (in kg): "); 
        double capacity = sc.nextDouble(); 
 
        double[] totalWeightCarried = new double[1]; 
        double maxValue = fractionalKnapsack(items, capacity, totalWeightCarried); 
 
        DecimalFormat df = new DecimalFormat("0.00"); 
        System.out.println("\n===== Final Report ====="); 
        System.out.println("Total weight carried: " + df.format(totalWeightCarried[0]) + " kg"); 
        System.out.println("Total utility value carried: " + df.format(maxValue) + " units"); 
 
        sc.close(); 
    } 
}