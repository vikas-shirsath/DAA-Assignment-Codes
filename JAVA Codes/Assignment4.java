import java.util.*;

class Assignment4 {

    // Edge class representing a connection to a node with a weight
    static class Edge {
        int to;
        int weight;

        Edge(int t, int w) {
            to = t;
            weight = w;
        }
    }

    // Dijkstra's algorithm to calculate shortest distances from source
    static void dijkstra(int source, List<List<Edge>> graph, int[] dist, int V) {
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0];
            int u = curr[1];

            if (d > dist[u]) continue;

            for (Edge e : graph.get(u)) {
                int v = e.to;
                int w = e.weight;

                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }
    }

    // Update the weight (travel time) of a road between two nodes
    static void updateEdgeWeight(List<List<Edge>> graph, int u, int v, int newWeight) {
        boolean updated = false;
        
        // Update u → v
        for (Edge e : graph.get(u)) {
            if (e.to == v) {
                e.weight = newWeight;
                updated = true;
                break;
            }
        }

        // Update v → u (for undirected graph)
        for (Edge e : graph.get(v)) {
            if (e.to == u) {
                e.weight = newWeight;
                updated = true;
                break;
            }
        }

        if (updated) {
            System.out.println("\nRoad between " + u + " and " + v + " updated to " + newWeight + ".");
        } else {
            System.out.println("\n Error: Road between " + u + " and " + v + " does not exist.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Graph Input
        System.out.print("Enter number of intersections (vertices): ");
        int V = sc.nextInt();

        System.out.print("Enter number of roads (edges): ");
        int E = sc.nextInt();

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }

        System.out.println("Enter edges in format (u v w):");
        for (int i = 0; i < E; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();

            graph.get(u).add(new Edge(v, w));
            graph.get(v).add(new Edge(u, w)); // Because it's undirected
        }

        // Ambulance Source and Hospitals
        System.out.print("Enter ambulance start location (source node): ");
        int source = sc.nextInt();

        System.out.print("Enter number of hospitals: ");
        int H = sc.nextInt();

        int[] hospitals = new int[H];
        System.out.print("Enter hospital nodes: ");
        for (int i = 0; i < H; i++) {
            hospitals[i] = sc.nextInt();
        }

        // Menu Loop
        int choice;
        do {
            System.out.println("\n-------------------------------------");
            System.out.println("MENU:");
            System.out.println("1: Calculate Shortest Path to Nearest Hospital");
            System.out.println("2: Update Distance (Weight) of a Road");
            System.out.println("3: Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    // Compute shortest paths
                    int[] dist = new int[V];
                    dijkstra(source, graph, dist, V);

                    int minTime = Integer.MAX_VALUE;
                    int nearestHospital = -1;

                    for (int h : hospitals) {
                        if (dist[h] < minTime) {
                            minTime = dist[h];
                            nearestHospital = h;
                        }
                    }

                    if (nearestHospital == -1 || minTime == Integer.MAX_VALUE) {
                        System.out.println("🚨 No hospital reachable from source " + source + ".");
                    } else {
                        System.out.println("\n⭐ RESULT: Nearest hospital is at node " + nearestHospital +
                                " with minimum travel time " + minTime + " minutes.");
                    }
                    break;

                case 2:
                    // Update edge weights
                    System.out.print("Enter the first node (u) of the road: ");
                    int u = sc.nextInt();
                    System.out.print("Enter the second node (v): ");
                    int v = sc.nextInt();
                    System.out.print("Enter the new travel time (weight): ");
                    int newWeight = sc.nextInt();

                    updateEdgeWeight(graph, u, v, newWeight);
                    System.out.println("Note: Recalculate shortest path (Option 1) to apply changes.");
                    break;

                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        } while (choice != 3);

        sc.close();
    }
}

