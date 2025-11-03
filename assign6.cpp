#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

int knapsack(int W, const vector<int>& wt, const vector<int>& val, int n) {
    vector<vector<int>> dp(n + 1, vector<int>(W + 1, 0));
    for (int i = 1; i <= n; ++i) {
        for (int w = 1; w <= W; ++w) {
            if (wt[i - 1] <= w)
                dp[i][w] = max(val[i - 1] + dp[i - 1][w - wt[i - 1]], dp[i - 1][w]);
            else
                dp[i][w] = dp[i - 1][w];
        }
    }
    return dp[n][W];
}

int main() {
    int n, W;
    cout << "Enter number of items: ";
    cin >> n;
    vector<int> val(n), wt(n);
    cout << "Enter utility values of items:\n";
    for (int i = 0; i < n; ++i) cin >> val[i];
    cout << "Enter weights of items:\n";
    for (int i = 0; i < n; ++i) cin >> wt[i];
    cout << "Enter maximum truck capacity: ";
    cin >> W;
    int maxUtility = knapsack(W, wt, val, n);
    cout << "\nMaximum total utility value: " << maxUtility << endl;
    return 0;
}
