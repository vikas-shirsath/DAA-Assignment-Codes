import java.util.*;

public class Knapsack {

    public static Pair<Integer, List<Integer>> knapsack(int[] weight, int[] value, int N, int W) {
        int[][] dp = new int[N + 1][W + 1];

        // Build DP table
        for (int i = 1; i <= N; i++) {
            for (int w = 1; w <= W; w++) {
                if (weight[i - 1] <= w) {
                    dp[i][w] = Math.max(
                        value[i - 1] + dp[i - 1][w - weight[i - 1]],
                        dp[i - 1][w]
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Maximum utility
        int maxUtility = dp[N][W];

        // Backtrack to find included items
        int w = W;
        List<Integer> includedItems = new ArrayList<>();

        for (int i = N; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                includedItems.add(i); // store 1-based index
                w -= weight[i - 1];
            }
        }

        Collections.reverse(includedItems);
        return new Pair<>(maxUtility, includedItems);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of items and capacity: ");
        int N = sc.nextInt();
        int W = sc.nextInt();

        int[] weight = new int[N];
        int[] value = new int[N];

        System.out.println("Enter weights:");
        for (int i = 0; i < N; i++) {
            weight[i] = sc.nextInt();
        }

        System.out.println("Enter values:");
        for (int i = 0; i < N; i++) {
            value[i] = sc.nextInt();
        }

        Pair<Integer, List<Integer>> result = knapsack(weight, value, N, W);

        System.out.println("\nMaximum Utility: " + result.getKey());
        System.out.print("Items Included: ");
        for (int item : result.getValue()) {
            System.out.print(item + " ");
        }
        System.out.println();

        sc.close();
    }
}

class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
