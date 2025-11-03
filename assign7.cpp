#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int C;
    cout << "Enter number of courses: ";
    cin >> C;
    vector<string> courseNames(C);
    unordered_map<string,int> courseIndex;
    cout << "Enter course names:\n";
    for (int i = 0; i < C; ++i) {
        cin >> ws;
        getline(cin, courseNames[i]);
        courseIndex[courseNames[i]] = i;
    }

    int S;
    cout << "Enter number of students: ";
    cin >> S;
    vector<vector<int>> studentCourses(S);
    vector<int> studentsInCourse(C, 0);
    cout << "For each student, enter number of enrolled courses followed by course names:\n";
    for (int i = 0; i < S; ++i) {
        int k;
        cin >> k;
        studentCourses[i].resize(k);
        for (int j = 0; j < k; ++j) {
            string cname;
            cin >> ws;
            getline(cin, cname);
            if (courseIndex.find(cname) != courseIndex.end()) {
                int idx = courseIndex[cname];
                studentCourses[i][j] = idx;
                studentsInCourse[idx] += 1;
            } else {
                studentCourses[i][j] = -1;
            }
        }
        studentCourses[i].erase(remove(studentCourses[i].begin(), studentCourses[i].end(), -1),
                                studentCourses[i].end());
    }

    int R;
    cout << "Enter number of rooms: ";
    cin >> R;
    vector<string> roomNames(R);
    vector<int> roomCap(R);
    cout << "Enter room name and capacity for each room:\n";
    for (int i = 0; i < R; ++i) {
        cin >> ws;
        getline(cin, roomNames[i]);
        cin >> roomCap[i];
    }

    vector<unordered_set<int>> graph(C);
    for (int i = 0; i < S; ++i) {
        auto &cl = studentCourses[i];
        for (size_t a = 0; a < cl.size(); ++a) {
            for (size_t b = a + 1; b < cl.size(); ++b) {
                int u = cl[a], v = cl[b];
                if (u >= 0 && v >= 0 && u != v) {
                    graph[u].insert(v);
                    graph[v].insert(u);
                }
            }
        }
    }

    vector<int> degree(C);
    for (int i = 0; i < C; ++i) degree[i] = graph[i].size();
    vector<int> order(C);
    iota(order.begin(), order.end(), 0);
    sort(order.begin(), order.end(), [&](int a, int b){
        if (degree[a] != degree[b]) return degree[a] > degree[b];
        return a < b;
    });

    vector<int> color(C, -1);
    if (C > 0) color[order[0]] = 0;
    for (int idx = 1; idx < C; ++idx) {
        int u = order[idx];
        vector<bool> available(C + 1, true);
        for (int v : graph[u]) {
            if (color[v] != -1) available[color[v]] = false;
        }
        int c = 0;
        while (!available[c]) ++c;
        color[u] = c;
    }

    int maxColor = 0;
    for (int i = 0; i < C; ++i) if (color[i] > maxColor) maxColor = color[i];
    int numSlots = maxColor + 1;

    vector<int> roomOrder(R);
    iota(roomOrder.begin(), roomOrder.end(), 0);
    sort(roomOrder.begin(), roomOrder.end(), [&](int a, int b){
        if (roomCap[a] != roomCap[b]) return roomCap[a] > roomCap[b];
        return a < b;
    });

    vector<vector<bool>> roomOccupied(R, vector<bool>(numSlots, false));
    vector<string> assignedRoom(C, "Unassigned");

    for (int t = 0; t < numSlots; ++t) {
        vector<int> examsInSlot;
        for (int c = 0; c < C; ++c) if (color[c] == t) examsInSlot.push_back(c);
        sort(examsInSlot.begin(), examsInSlot.end(), [&](int a, int b){
            if (studentsInCourse[a] != studentsInCourse[b]) return studentsInCourse[a] > studentsInCourse[b];
            return a < b;
        });
        for (int e : examsInSlot) {
            bool placed = false;
            for (int ridx : roomOrder) {
                if (!roomOccupied[ridx][t] && roomCap[ridx] >= studentsInCourse[e]) {
                    assignedRoom[e] = roomNames[ridx];
                    roomOccupied[ridx][t] = true;
                    placed = true;
                    break;
                }
            }
            if (!placed) assignedRoom[e] = "Unassigned";
        }
    }

    cout << "\nFinal Timetable:\n";
    cout << left << setw(30) << "Course" << setw(12) << "TimeSlot" << "Room\n";
    for (int i = 0; i < C; ++i) {
        cout << left << setw(30) << courseNames[i]
             << setw(12) << color[i]
             << assignedRoom[i] << '\n';
    }

    return 0;
}
