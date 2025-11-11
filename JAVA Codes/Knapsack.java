import java.util.*;

// Simple Pair class to return both max utility and list of chosen items
class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K key, V value) { this.key = key; this.value = value; }
    public K getKey() { return key; }
    public V getValue() { return value; }
}

public class Knapsack {
    // DP solution returning max utility and list of 1-based indices of included items
    public static Pair<Integer, List<Integer>> knapsack(int[] weight, int[] value, int N, int W) {
        // dp[i][w] = maximum value using first i items with capacity w
        int[][] dp = new int[N + 1][W + 1];

        // build table row by row (items 1..N)
        for (int i = 1; i <= N; i++) {
            for (int w = 1; w <= W; w++) {
                // if current item i (index i-1) fits into current capacity w
                if (weight[i - 1] <= w) {
                    // option1: include item i => value[i-1] + best with remaining capacity
                    int includeVal = value[i - 1] + dp[i - 1][w - weight[i - 1]];
                    // option2: exclude item i
                    int excludeVal = dp[i - 1][w];
                    // choose better of include or exclude
                    dp[i][w] = Math.max(includeVal, excludeVal);
                } else {
                    // cannot include item i because it exceeds w
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // maximum utility at full capacity using all N items
        int maxUtility = dp[N][W];

        // backtrack to find which items were included
        int w = W;
        List<Integer> includedItems = new ArrayList<>();
        for (int i = N; i > 0; i--) {
            // if dp value differs from previous row, item i was included
            if (dp[i][w] != dp[i - 1][w]) {
                includedItems.add(i);           // store 1-based index for clarity
                w -= weight[i - 1];             // reduce remaining capacity
            }
        }
        Collections.reverse(includedItems);     // optional: present indices in ascending order
        return new Pair<>(maxUtility, includedItems);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of items (N) and truck capacity (W): ");
        int N = sc.nextInt();
        int W = sc.nextInt();

        // safety: non-negative checks
        if (N <= 0 || W <= 0) {
            System.out.println("N and W should be positive integers.");
            return;
        }

        int[] weight = new int[N];
        int[] value  = new int[N];

        System.out.println("Enter weights for " + N + " items (space or newline separated):");
        for (int i = 0; i < N; i++) {
            weight[i] = sc.nextInt();
            if (weight[i] < 0) weight[i] = 0;  // sanitize negative weights if input is bad
        }

        System.out.println("Enter utility values for " + N + " items:");
        for (int i = 0; i < N; i++) {
            value[i] = sc.nextInt();
            if (value[i] < 0) value[i] = 0;    // sanitize negative values
        }

        Pair<Integer, List<Integer>> result = knapsack(weight, value, N, W);

        System.out.println("\nMaximum Utility: " + result.getKey());
        System.out.print("Items Included (1-based indices): ");
        for (int item : result.getValue()) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
    

