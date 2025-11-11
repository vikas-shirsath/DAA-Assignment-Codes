// FractionalKnapsack.java
import java.util.*; // needed for List, ArrayList, Collections, Comparator, Scanner

// Main class implementing fractional knapsack functionality
public class FractionalKnapsack {

    // Item class: stores weight, value (utility), whether divisible, and ID/name
    static class Item {
        String name;     // human-friendly name or id for the item
        double weight;   // weight in kg (wi)
        double value;    // utility value (vi)
        boolean divisible; // true if fractional amounts allowed

        // constructor to initialize fields
        Item(String name, double weight, double value, boolean divisible) {
            this.name = name;
            this.weight = weight;
            this.value = value;
            this.divisible = divisible;
        }

        // convenience: compute value per unit weight (density)
        double valuePerWeight() {
            // if weight is zero (shouldn't happen), return very large density to prefer it,
            // but practically weight 0 is invalid for physical items so treat carefully.
            if (this.weight == 0) return Double.POSITIVE_INFINITY;
            return this.value / this.weight;
        }
    }

    // Result entry for what we load: item, fraction taken (1.0 means whole), and gained utility
    static class Load {
        Item item;       // reference to the original item
        double fraction; // fraction of the item taken (0..1)
        double takenWeight; // actual weight loaded for this item
        double gainedValue; // actual utility gained

        Load(Item item, double fraction, double takenWeight, double gainedValue) {
            this.item = item;
            this.fraction = fraction;
            this.takenWeight = takenWeight;
            this.gainedValue = gainedValue;
        }
    }

    // The core greedy algorithm that returns list of loads and total value
    public static Map<String, Object> fractionalKnapsack(List<Item> items, double capacity) {
        // Sort items by value-per-weight in descending order (greedy priority)
        items.sort(new Comparator<Item>() {
            public int compare(Item a, Item b) {
                // compare b to a for descending order
                return Double.compare(b.valuePerWeight(), a.valuePerWeight());
            }
        });

        double remaining = capacity;            // remaining capacity of the boat
        double totalValue = 0.0;                // accumulated utility value
        List<Load> loads = new ArrayList<>();   // record of what we put on boat

        // iterate items in sorted order of importance per kg
        for (Item it : items) {
            if (remaining <= 0) break; // boat is full, stop

            // if item weight is zero (degenerate), skip or take full value without weight effect
            if (it.weight == 0) {
                // take it fully if divisible or not; it consumes no capacity
                double frac = 1.0;
                double takenW = 0.0;
                double gain = it.value;
                totalValue += gain;
                loads.add(new Load(it, frac, takenW, gain));
                continue;
            }

            // if item fits fully and we can take whole item
            if (it.weight <= remaining) {
                // take entire item only if it's allowed to be taken whole
                double frac = 1.0; // whole item
                double takenW = it.weight;
                double gain = it.value;
                // update trackers
                remaining -= takenW;
                totalValue += gain;
                loads.add(new Load(it, frac, takenW, gain));
            } else {
                // item does not fit fully in remaining capacity
                if (it.divisible) {
                    // take fraction that fits
                    double frac = remaining / it.weight;         // fraction of the item
                    double takenW = remaining;                   // we use all remaining capacity
                    double gain = it.value * frac;               // proportional utility
                    totalValue += gain;
                    loads.add(new Load(it, frac, takenW, gain));
                    remaining = 0; // boat is now full
                    break; // no more capacity
                } else {
                    // item is indivisible and cannot fit, skip it
                    // continue to next item (maybe a smaller indivisible or divisible one fits)
                    continue;
                }
            }
        }

        // prepare output data structure
        Map<String, Object> result = new HashMap<>();
        result.put("totalValue", totalValue);
        result.put("remainingCapacity", remaining);
        result.put("loads", loads);
        return result;
    }

    // Pretty-print helper to display what was loaded
    public static void printResult(Map<String, Object> res, double capacity) {
        double totalValue = (double) res.get("totalValue");
        double rem = (double) res.get("remainingCapacity");
        @SuppressWarnings("unchecked")
        List<Load> loads = (List<Load>) res.get("loads");

        System.out.printf("Boat capacity: %.2f kg\n", capacity);
        System.out.printf("Total utility loaded: %.4f\n", totalValue);
        System.out.printf("Unused capacity: %.4f kg\n\n", rem);

        System.out.println("Loaded items (in order of selection):");
        for (Load l : loads) {
            System.out.printf("- %s : fraction=%.4f, weight=%.4f kg, utility=%.4f, divisible=%b, v/w=%.4f\n",
                    l.item.name, l.fraction, l.takenWeight, l.gainedValue, l.item.divisible, l.item.valuePerWeight());
        }
    }

    // Example main to demonstrate usage with the flood-relief scenario
    public static void main(String[] args) {
        // Example: define a set of relief items present at the relief center
        List<Item> items = new ArrayList<>();
        // name, weight(kg), utility value (higher = more critical), divisible?
        items.add(new Item("MedicineKit",     5.0, 100.0, false)); // indivisible, very high utility
        items.add(new Item("WaterContainer",  20.0, 60.0, true));  // divisible (water can be fractioned)
        items.add(new Item("FoodPack",        15.0, 45.0, true));  // divisible
        items.add(new Item("Blankets",        10.0, 10.0, false)); // indivisible
        items.add(new Item("FirstAidBox",     3.0, 40.0, false));  // indivisible
        items.add(new Item("PurificationTabs",1.0, 20.0, true));   // divisible or small items
        items.add(new Item("ExtraMedicine",   8.0, 80.0, true));   // divisible bag of medicine organizers

        // define boat capacity (W)
        double capacity = 30.0; // 30 kg capacity

        // run the fractional knapsack optimizer
        Map<String, Object> result = fractionalKnapsack(items, capacity);

        // print results
        printResult(result, capacity);
    }
}
