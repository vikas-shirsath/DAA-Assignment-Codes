import java.util.*;

class Edge {
    int to;
    double baseCost, curCost;

    Edge(int t, double c) {
        to = t;
        baseCost = curCost = c;
    }
}

public class Assi5 {
    static final double INF = 1e30;

    static double recomputeNode(int node, List<List<Edge>> adj, double[] best, int[] next) {
        double bestVal = INF;
        int bestV = -1;

        for (Edge e : adj.get(node)) {
            if (best[e.to] >= INF / 2) continue;
            double cand = e.curCost + best[e.to];
            if (cand < bestVal) {
                bestVal = cand;
                bestV = e.to;
            }
        }

        next[node] = bestV;
        return bestVal;
    }

    static void printPath(int src, List<List<Edge>> adj, double[] best, int[] next) {
        if (best[src] >= INF / 2) {
            System.out.println("No route from " + src);
            return;
        }

        System.out.print("Path from " + src + " : ");
        double total = 0;
        int cur = src;

        while (cur != -1) {
            System.out.print(cur);
            int nxt = next[cur];

            if (nxt != -1) {
                for (Edge e : adj.get(cur))
                    if (e.to == nxt) {
                        total += e.curCost;
                        break;
                    }
                System.out.print(" -> ");
            }

            cur = nxt;
        }

        System.out.printf("%nTotal route cost: %.6f%n", total);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of stages: ");
        int S = sc.nextInt();

        int[] stageCount = new int[S], start = new int[S];
        long N = 0;

        System.out.print("Enter nodes per stage: ");
        for (int i = 0; i < S; i++) {
            stageCount[i] = sc.nextInt();
            N += stageCount[i];
        }

        for (int i = 1; i < S; i++)
            start[i] = start[i - 1] + stageCount[i - 1];

        System.out.print("Enter number of edges: ");
        int M = sc.nextInt();

        List<List<Edge>> adj = new ArrayList<>();
        List<List<Integer>> rev = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            adj.add(new ArrayList<>());
            rev.add(new ArrayList<>());
        }

        System.out.println("Enter each edge as: u v cost");
        for (int i = 0; i < M; i++) {
            int u = sc.nextInt(), v = sc.nextInt();
            double c = sc.nextDouble();

            if (u < 0 || v < 0 || u >= N || v >= N) {
                System.err.println("Invalid edge!");
                return;
            }

            adj.get(u).add(new Edge(v, c));
            rev.get(v).add(u);
        }

        double[] best = new double[(int) N];
        int[] next = new int[(int) N];
        Arrays.fill(best, INF);
        Arrays.fill(next, -1);

        // Initialize last stage with cost 0
        for (int k = 0; k < stageCount[S - 1]; k++)
            best[start[S - 1] + k] = 0;

        // Compute best costs for earlier stages
        for (int st = S - 2; st >= 0; st--)
            for (int k = 0; k < stageCount[st]; k++)
                best[start[st] + k] = recomputeNode(start[st] + k, adj, best, next);

        System.out.println("\nBest costs from Stage-0 nodes:");
        for (int k = 0; k < stageCount[0]; k++) {
            int u = start[0] + k;
            System.out.printf("Node %d: %s%n",
                    u,
                    best[u] >= INF / 2
                            ? "unreachable"
                            : String.format("cost = %.6f", best[u]));
        }

        System.out.print("\nEnter source node id (in stage 0) or -1 to skip: ");
        int src = sc.nextInt();
        if (src >= 0 && src < N)
            printPath(src, adj, best, next);

        System.out.print("\nEnter number of live updates: ");
        int Q = sc.nextInt();

        while (Q-- > 0) {
            System.out.print("Enter edge update (u v multiplier): ");
            int u = sc.nextInt(), v = sc.nextInt();
            double mul = sc.nextDouble();

            for (Edge e : adj.get(u))
                if (e.to == v)
                    e.curCost = e.baseCost * mul;

            Queue<Integer> q = new LinkedList<>();
            double newCost = recomputeNode(u, adj, best, next);

            if (Math.abs(newCost - best[u]) > 1e-9) {
                best[u] = newCost;
                q.add(u);
            }

            while (!q.isEmpty()) {
                int node = q.poll();

                for (int pred : rev.get(node)) {
                    double nc = recomputeNode(pred, adj, best, next);

                    if (Math.abs(nc - best[pred]) > 1e-9) {
                        best[pred] = nc;
                        q.add(pred);
                    }
                }
            }
        }

        System.out.println("\nAfter updates, best costs:");
        for (int k = 0; k < stageCount[0]; k++) {
            int u = start[0] + k;
            System.out.printf("Node %d: %s%n",
                    u,
                    best[u] >= INF / 2
                            ? "unreachable"
                            : String.format("cost = %.6f", best[u]));
        }

        System.out.print("\nEnter source node id (in stage 0) or -1 to skip: ");
        src = sc.nextInt();
        if (src >= 0 && src < N)
            printPath(src, adj, best, next);

        sc.close();
    }
}
