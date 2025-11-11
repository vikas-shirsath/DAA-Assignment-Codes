import java.util.*;
import java.text.*;

public class Main {

    static final int NUM_ORDERS = 1000000; // Use 10000 for testing

    static class Order {
        String order_id;
        long timestamp;
    }

    // Generate random orders
    static void generate_sample_orders(Order[] orders, int n) {
        Calendar base_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        base_time.clear();
        base_time.set(Calendar.YEAR, 2025);
        base_time.set(Calendar.MONTH, 5);      // June (0-based)
        base_time.set(Calendar.DAY_OF_MONTH, 24);
        base_time.set(Calendar.HOUR_OF_DAY, 12);
        base_time.set(Calendar.MINUTE, 0);
        base_time.set(Calendar.SECOND, 0);

        long base = base_time.getTimeInMillis() / 1000;

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int random_minutes = rand.nextInt(100000); // up to ~70 days
            orders[i] = new Order();
            orders[i].timestamp = base + (random_minutes * 60L);
            orders[i].order_id = String.format("ORD%d", i + 1);
        }
    }

    // Merge Sort functions
    static void merge(Order[] orders, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Order[] L = new Order[n1];
        Order[] R = new Order[n2];

        for (int i = 0; i < n1; i++) L[i] = orders[left + i];
        for (int j = 0; j < n2; j++) R[j] = orders[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i].timestamp <= R[j].timestamp)
                orders[k++] = L[i++];
            else
                orders[k++] = R[j++];
        }

        while (i < n1) orders[k++] = L[i++];
        while (j < n2) orders[k++] = R[j++];
    }

    static void merge_sort(Order[] orders, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            merge_sort(orders, left, mid);
            merge_sort(orders, mid + 1, right);
            merge(orders, left, mid, right);
        }
    }

    // Print first N orders
    static void print_first_n_orders(Order[] orders, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (int i = 0; i < n; i++) {
            String time_str = sdf.format(new Date(orders[i].timestamp * 1000));
            System.out.printf("Order ID: %s, Timestamp: %s%n", orders[i].order_id, time_str);
        }
    }

    public static void main(String[] args) {
        Random rand = new Random(System.currentTimeMillis());

        System.out.println("Generating orders...");
        Order[] orders = new Order[NUM_ORDERS];
        generate_sample_orders(orders, NUM_ORDERS);

        System.out.println("Sorting orders by timestamp...");
        long start = System.currentTimeMillis();
        merge_sort(orders, 0, NUM_ORDERS - 1);
        long end = System.currentTimeMillis();

        double time_taken = (end - start) / 1000.0;
        System.out.printf("Done! Sorted %d orders in %.2f seconds.%n", NUM_ORDERS, time_taken);

        System.out.println("\nFirst 5 Sorted Orders:");
        print_first_n_orders(orders, 5);
    }
}
