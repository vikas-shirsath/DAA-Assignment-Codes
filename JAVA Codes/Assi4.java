import java.util.*; 
 
public class Assi4 { 
 
    static void dijkstra(int source, List<List<int[]>> graph, List<Integer> dist) { 
        int V = graph.size(); 
        for (int i = 0; i < V; i++) dist.add(Integer.MAX_VALUE); 
        dist.set(source, 0); 
 
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); 
        pq.add(new int[]{0, source}); 
 
        while (!pq.isEmpty()) { 
            int[] curr = pq.poll(); 
            int d = curr[0]; 
            int u = curr[1]; 
 
            if (d > dist.get(u)) continue; 
 
            for (int[] edge : graph.get(u)) { 
                int v = edge[0]; 
                int w = edge[1]; 
 
                if (dist.get(v) > dist.get(u) + w) { 
                    dist.set(v, dist.get(u) + w); 
                    pq.add(new int[]{dist.get(v), v}); 
                } 
            } 
        } 
    } 
 
    public static void main(String[] args) { 
        Scanner sc = new Scanner(System.in); 
 
        System.out.print("Enter number of intersections (vertices): "); 
        int V = sc.nextInt(); 
        System.out.print("Enter number of roads (edges): "); 
        int E = sc.nextInt(); 
 
        List<List<int[]>> graph = new ArrayList<>(); 
        for (int i = 0; i < V; i++) graph.add(new ArrayList<>()); 
 
        System.out.println("Enter edges (u v w):"); 
        for (int i = 0; i < E; i++) { 
            int u = sc.nextInt(); 
            int v = sc.nextInt(); 
            int w = sc.nextInt(); 
            graph.get(u).add(new int[]{v, w}); 
            graph.get(v).add(new int[]{u, w}); // undirected road 
        } 
 
        System.out.print("Enter ambulance start location (source): "); 
        int source = sc.nextInt(); 
 
        System.out.print("Enter number of hospitals: "); 
        int H = sc.nextInt(); 
        List<Integer> hospitals = new ArrayList<>(); 
        System.out.print("Enter hospital nodes: "); 
        for (int i = 0; i < H; i++) { 
            hospitals.add(sc.nextInt()); 
        } 
 
        List<Integer> dist = new ArrayList<>(Collections.nCopies(V, Integer.MAX_VALUE)); 
        dijkstra(source, graph, dist); 
 
        int minTime = Integer.MAX_VALUE; 
        int nearestHospital = -1; 
        for (int h : hospitals) { 
            if (dist.get(h) < minTime) { 
                minTime = dist.get(h); 
                nearestHospital = h; 
            } 
        } 
 
        if (nearestHospital == -1) 
            System.out.println("No hospital reachable."); 
        else 
            System.out.println("Nearest hospital is at node " + nearestHospital + 
                    " with travel time " + minTime + " minutes."); 
 
        sc.close(); 
    } 
}