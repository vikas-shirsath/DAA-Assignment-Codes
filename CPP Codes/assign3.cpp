#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;
// Structure to represent a relief item
struct Item {
string name;
double weight; // kg
double value; // utility value
bool divisible; // true if divisible (food/water), false if indivisible (medicine)
};
// Comparator function to sort items by value-to-weight ratio in descending order
bool cmp(const Item &a, const Item &b) {
double r1 = a.value / a.weight;
double r2 = b.value / b.weight;#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

struct Item {
    string name;
    double weight;
    double value;
    bool divisible;
};

bool cmp(const Item &a, const Item &b) {
    double r1 = a.value / a.weight;
    double r2 = b.value / b.weight;
    return r1 > r2;
}

double fractionalKnapsack(vector<Item> &items, double W) {
    sort(items.begin(), items.end(), cmp);
    double totalUtility = 0.0;
    cout << "Items selected:\n";
    for (auto &item : items) {
        if (W <= 0) break;
        if (item.divisible) {
            double takeWeight = min(item.weight, W);
            totalUtility += takeWeight * (item.value / item.weight);
            W -= takeWeight;
            cout << item.name << " -> " << takeWeight << " kg (partial)\n";
        } else {
            if (item.weight <= W) {
                totalUtility += item.value;
                W -= item.weight;
                cout << item.name << " -> " << item.weight << " kg (whole)\n";
            }
        }
    }
    return totalUtility;
}

int main() {
    int n;
    double W;
    cout << "Enter number of relief items: ";
    cin >> n;
    vector<Item> items(n);
    cout << "Enter name, weight(kg), value, divisible(1=yes/0=no) for each item:\n";
    for (int i = 0; i < n; i++) {
        cin >> items[i].name >> items[i].weight >> items[i].value >> items[i].divisible;
    }
    cout << "Enter boat weight capacity (kg): ";
    cin >> W;
    double maxUtility = fractionalKnapsack(items, W);
    cout << "\nMaximum total utility value: " << maxUtility << endl;
    return 0;
}

return r1 > r2;
}
// Fractional Knapsack algorithm
double fractionalKnapsack(vector<Item> &items, double W) {
// Sort items by value-to-weight ratio
sort(items.begin(), items.end(), cmp);
double totalUtility = 0.0;
cout << "Items selected:\n";
for (auto &item : items) {
if (W <= 0) break;
if (item.divisible) {
// Take as much as possible (partial allowed)
double takeWeight = min(item.weight, W);
totalUtility += takeWeight * (item.value / item.weight);
W -= takeWeight;
cout << item.name << " -> " << takeWeight << " kg (partial)\n";
} else {
// Take whole item if possible
if (item.weight <= W) {
totalUtility += item.value;
W -= item.weight;

8

cout << item.name << " -> " << item.weight << " kg (whole)\n";
}
}
}
return totalUtility;
}
int main() {
int n;
double W;
cout << "Enter number of relief items: ";
cin >> n;
vector<Item> items(n);
cout << "Enter name, weight(kg), value, divisible(1=yes/0=no) for each item:\n";
for (int i = 0; i < n; i++) {
cin >> items[i].name >> items[i].weight >> items[i].value >> items[i].divisible;
}
cout << "Enter boat weight capacity (kg): ";
cin >> W;
double maxUtility = fractionalKnapsack(items, W);
cout << "\nMaximum total utility value: " << maxUtility << endl;
return 0;
}