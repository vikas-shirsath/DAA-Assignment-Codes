import java.util.*;

public class Assi8 {
    static final int INF = Integer.MAX_VALUE / 2;

    static class Node implements Comparable<Node> {
        int level, costSoFar, lowerBound;
        List<Integer> path;
        int[][] reducedMatrix;

        Node(int l, int c, int lb, List<Integer> p, int[][] m) {
            level = l;
            costSoFar = c;
            lowerBound = lb;
            path = p;
            reducedMatrix = m;
        }

        public int compareTo(Node o) {
            return Integer.compare(lowerBound, o.lowerBound);
        }
    }

    // Reduce matrix and return total reduction cost
    static int reduceMatrix(int[][] mat) {
        int N = mat.length;
        int reduction = 0;

        // Row reduction
        for (int i = 0; i < N; i++) {
            int rowMin = Arrays.stream(mat[i]).min().orElse(INF);
            if (rowMin != INF && rowMin > 0) {
                reduction += rowMin;
                for (int j = 0; j < N; j++) {
                    if (mat[i][j] != INF) mat[i][j] -= rowMin;
                }
            }
        }

        // Column reduction
        for (int j = 0; j < N; j++) {
            int colMin = INF;
            for (int i = 0; i < N; i++) {
                colMin = Math.min(colMin, mat[i][j]);
            }
            if (colMin != INF && colMin > 0) {
                reduction += colMin;
                for (int i = 0; i < N; i++) {
                    if (mat[i][j] != INF) mat[i][j] -= colMin;
                }
            }
        }

        return reduction;
    }

    static int[][] copyMatrix(int[][] src) {
        int[][] copy = new int[src.length][src.length];
        for (int i = 0; i < src.length; i++)
            copy[i] = Arrays.copyOf(src[i], src[i].length);
        return copy;
    }

    static Map.Entry<Integer, List<Integer>> tspBranchAndBound(int[][] cost, int start) {
        int N = cost.length;
        int[][] rootMat = copyMatrix(cost);

        // Replace diagonals with INF
        for (int i = 0; i < N; i++) rootMat[i][i] = INF;

        int initialReduction = reduceMatrix(rootMat);
        Node root = new Node(0, 0, initialReduction, new ArrayList<>(List.of(start)), rootMat);
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(root);

        int bestCost = INF;
        List<Integer> bestPath = new ArrayList<>();

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (node.lowerBound >= bestCost) continue;

            // Reached last level (complete tour)
            if (node.level == N - 1) {
                int last = node.path.get(node.path.size() - 1);
                if (cost[last][start] != INF) {
                    int total = node.costSoFar + cost[last][start];
                    if (total < bestCost) {
                        bestCost = total;
                        bestPath = new ArrayList<>(node.path);
                        bestPath.add(start);
                    }
                }
                continue;
            }

            int u = node.path.get(node.path.size() - 1);
            for (int v = 0; v < N; v++) {
                if (node.path.contains(v) || cost[u][v] == INF) continue;

                int[][] childMat = copyMatrix(node.reducedMatrix);

                // Invalidate outgoing edges from u and incoming to v
                for (int i = 0; i < N; i++) {
                    childMat[u][i] = INF;
                    childMat[i][v] = INF;
                }
                childMat[v][start] = INF;

                int reduction = reduceMatrix(childMat);
                int costSoFar = node.costSoFar + cost[u][v];
                int lb = costSoFar + reduction;

                if (lb < bestCost) {
                    List<Integer> newPath = new ArrayList<>(node.path);
                    newPath.add(v);
                    pq.add(new Node(node.level + 1, costSoFar, lb, newPath, childMat));
                }
            }
        }

        return Map.entry(bestCost, bestPath);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of cities: ");
        int N = sc.nextInt();
        if (N < 2) {
            System.out.println("TSP requires at least 2 cities.");
            return;
        }

        int[][] cost = new int[N][N];
        System.out.println("Enter cost matrix (" + N + "x" + N + "), use 0 for self and 9999 for no route:");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int val = sc.nextInt();
                if (i == j) cost[i][j] = INF;
                else cost[i][j] = (val == 9999) ? INF : val;
            }
        }

        System.out.println("\nStarting TSP from City 0...");
        var result = tspBranchAndBound(cost, 0);

        int bestCost = result.getKey();
        List<Integer> bestPath = result.getValue();

        System.out.println("\nOptimal TSP Solution (Branch & Bound):");
        if (bestPath.isEmpty()) {
            System.out.println("No feasible tour found (best cost = INF).");
        } else {
            System.out.println("Total Cost: " + bestCost);
            System.out.print("Path: ");
            for (int i = 0; i < bestPath.size(); i++) {
                System.out.print(bestPath.get(i));
                if (i < bestPath.size() - 1) System.out.print(" -> ");
            }
            System.out.println();
        }

        sc.close();
    }
}
