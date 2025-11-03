#include <bits/stdc++.h>
using namespace std;

struct Node {
    vector<int> path;
    vector<vector<int>> reducedMatrix;
    int cost, vertex, level;
    bool operator>(const Node &o) const { return cost > o.cost; }
};

int reduceMatrix(vector<vector<int>> &mat) {
    int n = mat.size(), cost = 0;
    for (int i=0;i<n;i++) {
        int rowMin = INT_MAX;
        for (int j=0;j<n;j++) if (mat[i][j] < rowMin) rowMin = mat[i][j];
        if (rowMin!=INT_MAX && rowMin>0){ cost += rowMin; for(int j=0;j<n;j++)
            if(mat[i][j]!=INT_MAX) mat[i][j]-=rowMin; }
    }
    for (int j=0;j<n;j++) {
        int colMin = INT_MAX;
        for (int i=0;i<n;i++) if (mat[i][j] < colMin) colMin = mat[i][j];
        if (colMin!=INT_MAX && colMin>0){ cost += colMin; for(int i=0;i<n;i++)
            if(mat[i][j]!=INT_MAX) mat[i][j]-=colMin; }
    }
    return cost;
}

Node newNode(vector<vector<int>> parentMatrix, vector<int> const &path, int level, int i, int j) {
    int n = parentMatrix.size();
    vector<vector<int>> mat = parentMatrix;
    if (level != 0)
        for (int k=0;k<n;k++) { mat[i][k] = mat[k][j] = INT_MAX; }
    mat[j][0] = INT_MAX;
    Node node;
    node.path = path; node.path.push_back(j);
    node.level = level;
    node.vertex = j;
    node.reducedMatrix = mat;
    return node;
}

int solveTSP(vector<vector<int>> costMatrix) {
    int n = costMatrix.size();
    vector<vector<int>> reducedMatrix = costMatrix;
    int cost = reduceMatrix(reducedMatrix);
    priority_queue<Node, vector<Node>, greater<Node>> pq;
    Node root; root.path = {0}; root.reducedMatrix = reducedMatrix; root.cost = cost; root.vertex = 0; root.level = 0;
    pq.push(root);
    int best = INT_MAX;
    vector<int> bestPath;
    while (!pq.empty()) {
        Node minNode = pq.top(); pq.pop();
        if (minNode.level == n-1) {
            int last = minNode.vertex;
            int finalCost = minNode.cost + costMatrix[last][0];
            if (finalCost < best) { best = finalCost; bestPath = minNode.path;
                bestPath.push_back(0); }
            continue;
        }
        for (int j=0;j<n;j++) {
            if (minNode.reducedMatrix[minNode.vertex][j] != INT_MAX) {
                Node child = newNode(minNode.reducedMatrix, minNode.path, minNode.level+1,
                                     minNode.vertex, j);
                child.cost = minNode.cost + minNode.reducedMatrix[minNode.vertex][j] +
                             reduceMatrix(child.reducedMatrix);
                pq.push(child);
            }
        }
    }
    cout << "Optimal Route: ";
    for (int c : bestPath) cout << c << " ";
    cout << "\nMinimum Cost: " << best << "\n";
    return best;
}

int main() {
    int n; cin >> n;
    vector<vector<int>> costMatrix(n, vector<int>(n));
    for(int i=0;i<n;i++) for(int j=0;j<n;j++) cin>>costMatrix[i][j];
    solveTSP(costMatrix);
    return 0;
}
