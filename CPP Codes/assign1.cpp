#include <iostream>
#include <vector>
#include <ctime>
#include <cstdlib>
#include <iomanip>
using namespace std;

size_t max_auxiliary_space = 0;

struct Order {
    int order_id;
    time_t timestamp;
};

void merge(vector<Order>& orders, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;
    vector<Order> L(orders.begin() + left, orders.begin() + mid + 1);
    vector<Order> R(orders.begin() + mid + 1, orders.begin() + right + 1);
    size_t current_aux_space = (L.size() + R.size()) * sizeof(Order);
    if (current_aux_space > max_auxiliary_space) max_auxiliary_space = current_aux_space;
    int i = 0, j = 0, k = left;
    while (i < n1 && j < n2) {
        if (L[i].timestamp <= R[j].timestamp) orders[k++] = L[i++];
        else orders[k++] = R[j++];
    }
    while (i < n1) orders[k++] = L[i++];
    while (j < n2) orders[k++] = R[j++];
}

void merge_sort(vector<Order>& orders, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        merge_sort(orders, left, mid);
        merge_sort(orders, mid + 1, right);
        merge(orders, left, mid, right);
    }
}

void generate_sample_orders(vector<Order>& orders, int n) {
    struct tm base_time_tm = {};
    base_time_tm.tm_year = 2025 - 1900;
    base_time_tm.tm_mon = 5;
    base_time_tm.tm_mday = 24;
    base_time_tm.tm_hour = 12;
    time_t base_time = mktime(&base_time_tm);
    orders.resize(n);
    for (int i = 0; i < n; i++) {
        int random_minutes = rand() % 100000;
        orders[i].timestamp = base_time + (random_minutes * 60);
        orders[i].order_id = i + 1;
    }
}

void print_first_n_orders(const vector<Order>& orders, int n) {
    char time_str[30];
    cout << "\n--- First 5 Sorted Orders ---\n";
    int limit = min(n, static_cast<int>(orders.size()));
    for (int i = 0; i < limit; i++) {
        strftime(time_str, sizeof(time_str), "%Y-%m-%dT%H:%M:%SZ", gmtime(&orders[i].timestamp));
        cout << "Order ID: " << left << setw(10) << orders[i].order_id
             << "| Timestamp: " << time_str << endl;
    }
}

int main() {
    srand(time(nullptr));
    int num_orders;
    cout << "Enter the number of orders to generate and sort: ";
    cin >> num_orders;
    if (cin.fail() || num_orders <= 0) {
        cout << "Error: Invalid input. Please enter a positive number." << endl;
        return 1;
    }
    vector<Order> orders;
    cout << "\nGenerating " << num_orders << " orders...\n";
    generate_sample_orders(orders, num_orders);
    cout << "Sorting orders using Merge Sort...\n";
    clock_t start = clock();
    if (!orders.empty()) merge_sort(orders, 0, orders.size() - 1);
    clock_t end = clock();
    cout << "Done!\n";
    double time_taken = static_cast<double>(end - start) / CLOCKS_PER_SEC;
    cout << "\nTime taken to sort " << num_orders << " orders: "
         << fixed << setprecision(2) << time_taken << " seconds.\n";
    print_first_n_orders(orders, 5);
    cout << "\n--- Space Complexity Report ---\n";
    cout << "Size of one Order object: " << sizeof(Order) << " bytes\n";
    cout << "Total input size: " << orders.size() * sizeof(Order) << " bytes\n";
    cout << "Peak auxiliary space used: " << max_auxiliary_space << " bytes\n";
    return 0;
}
