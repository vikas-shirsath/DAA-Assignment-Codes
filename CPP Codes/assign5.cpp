#include <bits/stdc++.h>
using namespace std;
const long long INF = 1e18;

struct Edge {
    string to;
    long long weight;
};

struct State {
    string node;
    int stage;
    long long cost;
    bool operator>(const State &other) const {
        return cost > other.cost;
    }
};

long long stagedShortestPath(
    unordered_map<string, vector<Edge>> &adj,
    unordered_map<string, int> &nodeStage,
    vector<string> sources,
    unordered_set<string> targets,
    int numStages)
{
    map<pair<string,int>, long long> dist;
    map<pair<string,int>, pair<string,int>> parent;
    priority_queue<State, vector<State>, greater<State>> pq;

    for (auto &src : sources) {
        int s = (nodeStage[src] == 0) ? 1 : 0;
        dist[{src, s}] = 0;
        parent[{src, s}] = {"", -1};
        pq.push({src, s, 0});
    }

    long long bestCost = INF;
    pair<string,int> bestEnd;

    while (!pq.empty()) {
        auto top = pq.top(); pq.pop();
        string u = top.node;
        int s = top.stage;
        long long d = top.cost;
        if (d != dist[{u, s}]) continue;
        if (s == numStages && targets.count(u)) {
            bestCost = d;
            bestEnd = {u, s};
            break;
        }
        for (auto &edge : adj[u]) {
            string v = edge.to;
            long long w = edge.weight;
            int ns = s;
            if (nodeStage[v] == s) ns = s + 1;
            if (dist.find({v, ns}) == dist.end() || d + w < dist[{v, ns}]) {
                dist[{v, ns}] = d + w;
                parent[{v, ns}] = {u, s};
                pq.push({v, ns, d + w});
            }
        }
    }

    if (bestCost == INF) {
        cout << "No valid path found.\n";
        return INF;
    }

    vector<string> path;
    pair<string,int> cur = bestEnd;
    while (cur.second != -1) {
        path.push_back(cur.first);
        cur = parent[cur];
    }
    reverse(path.begin(), path.end());

    cout << "Minimum Cost: " << bestCost << "\n";
    cout << "Optimal Path: ";
    for (size_t i = 0; i < path.size(); ++i) {
        cout << path[i];
        if (i + 1 < path.size()) cout << " → ";
    }
    cout << "\n";
    return bestCost;
}

int main() {
    unordered_map<string, int> nodeStage = {
        {"A", 0}, {"B", 1}, {"C", 1},
        {"D", 2}, {"E", 1}, {"F", 2}
    };

    unordered_map<string, vector<Edge>> adj = {
        {"A", {{"B", 5}, {"C", 10}}},
        {"B", {{"D", 10}, {"E", 2}}},
        {"C", {{"E", 1}}},
        {"E", {{"F", 3}}},
        {"D", {{"F", 2}}},
        {"F", {}}
    };

    stagedShortestPath(adj, nodeStage, {"A"}, {"F","D"}, 3);
    return 0;
}
