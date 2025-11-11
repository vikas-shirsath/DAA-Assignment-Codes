// SmartAmbulanceRouting.java
import java.util.*;

// Dijkstra-based ambulance routing for nearest hospital with dynamic edge updates.
// Note: keep all indices 0..n-1 for nodes.
public class SmartAmbulanceRouting {

    // Edge structure: stores destination, weight, and an edge id for updates.
    static class Edge {
        int to;            // destination node id
        long weight;       // travel time in minutes (use long to be safe)
        final int id;      // unique edge id (for lookup when updating)

        Edge(int to, long weight, int id) {
            this.to = to;
            this.weight = weight;
            this.id = id;
        }
    }

    // Graph: adjacency list and a mapping from edge id -> (u, index in adj[u]) for updates
    private final int n;                                 // number of nodes
    private final List<List<Edge>> adj;                  // adjacency list
    private final Map<Integer, Pair<Integer, Integer>> edgeIndex; // edgeId -> (u, index)

    // Helper pair
    private static class Pair<A, B> {
        A a; B b;
        Pair(A a, B b) { this.a = a; this.b = b; }
    }

    // constructor
    public SmartAmbulanceRouting(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        this.edgeIndex = new HashMap<>();
    }

    // add directed edge u->v with weight and id (for undirected graphs, call twice)
    public void addEdge(int u, int v, long weight, int edgeId) {
        Edge e = new Edge(v, weight, edgeId);
        adj.get(u).add(e);
        edgeIndex.put(edgeId, new Pair<>(u, adj.get(u).size() - 1));
    }

    // update weight for edge with given edgeId
    public boolean updateEdgeWeight(int edgeId, long newWeight) {
        Pair<Integer, Integer> info = edgeIndex.get(edgeId);
        if (info == null) return false;
        int u = info.a;
        int idx = info.b;
        Edge e = adj.get(u).get(idx);
        e.weight = newWeight; // update weight in-place
        return true;
    }

    // Dijkstra that returns path and distance to nearest hospital.
    // hospitals is a boolean[] or set marking hospital nodes.
    // Returns null if no hospital reachable.
    public Result shortestPathToNearestHospital(int src, Set<Integer> hospitals) {
        // early check: if source is hospital
        if (hospitals.contains(src)) {
            List<Integer> path = new ArrayList<>();
            path.add(src);
            return new Result(src, 0L, path);
        }

        // distances and parents
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[n];

        // min-heap
        PriorityQueue<Node> pq = new PriorityQueue<>();
        dist[src] = 0L;
        pq.add(new Node(src, 0L));

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            int u = cur.id;
            long d = cur.dist;
            if (visited[u]) continue;
            visited[u] = true;

            // if this node is a hospital, we found the nearest one (early exit)
            if (hospitals.contains(u)) {
                // reconstruct path
                List<Integer> path = buildPath(parent, src, u);
                return new Result(u, dist[u], path);
            }

            // relax edges
            for (Edge e : adj.get(u)) {
                int v = e.to;
                long w = e.weight;
                if (w < 0) continue; // ignore negative; routing should not have negative times
                if (!visited[v] && dist[u] != Long.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    pq.add(new Node(v, dist[v]));
                }
            }
        }

        // no reachable hospital
        return null;
    }

    // build path from src to target using parent array
    private List<Integer> buildPath(int[] parent, int src, int target) {
        LinkedList<Integer> path = new LinkedList<>();
        int cur = target;
        while (cur != -1) {
            path.addFirst(cur);
            if (cur == src) break;
            cur = parent[cur];
        }
        return path;
    }

    // Node for PQ
    private static class Node implements Comparable<Node> {
        int id;
        long dist;
        Node(int id, long dist) { this.id = id; this.dist = dist; }
        public int compareTo(Node o) { return Long.compare(this.dist, o.dist); }
    }

    // Result type
    public static class Result {
        public final int hospitalNode;
        public final long totalTime;
        public final List<Integer> path;
        public Result(int hospitalNode, long totalTime, List<Integer> path) {
            this.hospitalNode = hospitalNode;
            this.totalTime = totalTime;
            this.path = path;
        }
    }

    // Simple demo / test harness
    public static void main(String[] args) {
        // build sample graph
        int n = 8;
        SmartAmbulanceRouting router = new SmartAmbulanceRouting(n);

        // Note: edge IDs must be unique. For undirected road, add two directed edges with unique ids.
        router.addEdge(0,1,5, 101); router.addEdge(1,0,5,102);
        router.addEdge(1,2,10,103); router.addEdge(2,1,10,104);
        router.addEdge(0,3,2,105); router.addEdge(3,0,2,106);
        router.addEdge(3,4,2,107); router.addEdge(4,3,2,108);
        router.addEdge(4,2,2,109); router.addEdge(2,4,2,110);
        router.addEdge(2,5,5,111); router.addEdge(5,2,5,112);
        router.addEdge(5,6,3,113); router.addEdge(6,5,3,114);
        router.addEdge(6,7,2,115); router.addEdge(7,6,2,116);

        // hospitals at nodes 5 and 7
        Set<Integer> hospitals = new HashSet<>(Arrays.asList(5, 7));

        // ambulance at node 0
        Result res = router.shortestPathToNearestHospital(0, hospitals);
        if (res != null) {
            System.out.println("Nearest hospital: " + res.hospitalNode);
            System.out.println("ETA (minutes): " + res.totalTime);
            System.out.println("Path: " + res.path);
        } else {
            System.out.println("No reachable hospital.");
        }

        // simulate dynamic traffic: make edge 3->4 slower (increase weight)
        System.out.println("Simulating traffic increase on edge 3->4 (edgeId 107)...");
        router.updateEdgeWeight(107, 50); // spike to 50 min
        // recompute
        Result res2 = router.shortestPathToNearestHospital(0, hospitals);
        if (res2 != null) {
            System.out.println("After update - Nearest hospital: " + res2.hospitalNode);
            System.out.println("After update - ETA: " + res2.totalTime);
            System.out.println("After update - Path: " + res2.path);
        } else {
            System.out.println("After update - No reachable hospital.");
        }
    }
}
