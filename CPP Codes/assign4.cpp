#include <bits/stdc++.h>
using namespace std;

class Graph {
    int V;
    vector<vector<pair<int,int>>> adj;
public:
    Graph(int V) {
        this->V = V;
        adj.resize(V + 1);
    }

    void addEdge(int u, int v, int w) {
        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }

    pair<vector<long long>, vector<int>> dijkstra(int src) {
        vector<long long> dist(V + 1, LLONG_MAX);
        vector<int> parent(V + 1, -1);
        priority_queue<pair<long long,int>, vector<pair<long long,int>>, greater<pair<long long,int>>> pq;
        dist[src] = 0;
        pq.push({0, src});
        while (!pq.empty()) {
            auto top = pq.top(); pq.pop();
            long long d = top.first;
            int u = top.second;
            if (d != dist[u]) continue;
            for (auto &nb : adj[u]) {
                int v = nb.first;
                int w = nb.second;
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    pq.push({dist[v], v});
                }
            }
        }
        return {dist, parent};
    }

    void printPath(vector<int> &parent, int dest) {
        if (parent[dest] == -1) {
            cout << dest;
            return;
        }
        printPath(parent, parent[dest]);
        cout << " -> " << dest;
    }
};

int main() {
    int V, E;
    cout << "Enter number of intersections (nodes): ";
    cin >> V;
    Graph g(V);
    cout << "Enter number of roads (edges): ";
    cin >> E;
    cout << "Enter edges (u v time_in_minutes):" << endl;
    for (int i = 0; i < E; i++) {
        int u, v, w;
        cin >> u >> v >> w;
        g.addEdge(u, v, w);
    }
    int src, dest;
    cout << "Enter ambulance source intersection: ";
    cin >> src;
    cout << "Enter hospital destination intersection: ";
    cin >> dest;
    auto result = g.dijkstra(src);
    vector<long long> dist = result.first;
    vector<int> parent = result.second;
    cout << "\nShortest time from " << src << " to " << dest << " = " << dist[dest] << " minutes" << endl;
    cout << "Optimal Path: ";
    g.printPath(parent, dest);
    cout << endl;
    return 0;
}
