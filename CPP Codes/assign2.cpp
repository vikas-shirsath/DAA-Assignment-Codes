#include <iostream>
#include <vector>
#include <string>
#include <cstdlib>
#include <ctime>
#include <iomanip>
#include <cmath>
using namespace std;

size_t max_recursion_depth = 0;

struct Movie {
    string title;
    float rating;
    int release_year;
    int popularity;
};

bool compareByRating(const Movie& a, const Movie& b) {
    return a.rating < b.rating;
}

bool compareByYear(const Movie& a, const Movie& b) {
    return a.release_year < b.release_year;
}

bool compareByPopularity(const Movie& a, const Movie& b) {
    return a.popularity < b.popularity;
}

void quickSort(vector<Movie>& movies, int low, int high, bool (*compare)(const Movie&, const Movie&), int current_depth) {
    if (current_depth > max_recursion_depth) max_recursion_depth = current_depth;
    if (low < high) {
        int pivotIndex = low;
        Movie pivot = movies[high];
        for (int i = low; i < high; i++) {
            if (compare(movies[i], pivot)) {
                swap(movies[i], movies[pivotIndex]);
                pivotIndex++;
            }
        }
        swap(movies[pivotIndex], movies[high]);
        quickSort(movies, low, pivotIndex - 1, compare, current_depth + 1);
        quickSort(movies, pivotIndex + 1, high, compare, current_depth + 1);
    }
}

void generateMovies(vector<Movie>& movies, int n) {
    movies.resize(n);
    for (int i = 0; i < n; i++) {
        movies[i].title = "Movie " + to_string(i + 1);
        movies[i].rating = static_cast<float>(rand() % 91 + 10) / 10.0f;
        movies[i].release_year = rand() % 45 + 1980;
        movies[i].popularity = rand() % 1000000 + 1000;
    }
}

void print_top_n_movies(const vector<Movie>& movies, int n, const string& sort_by) {
    cout << "\n--- Top 5 Movies by " << sort_by << " ---\n";
    int count = 0;
    for (int i = movies.size() - 1; i >= 0 && count < n; --i, ++count) {
        cout << left << setw(15) << movies[i].title
             << "| Rating: " << fixed << setprecision(1) << setw(4) << movies[i].rating
             << "| Year: " << setw(5) << movies[i].release_year
             << "| Popularity: " << movies[i].popularity << endl;
    }
}

int main() {
    srand(time(nullptr));
    int num_movies;
    cout << "Enter the number of movies to generate and sort: ";
    cin >> num_movies;
    if (cin.fail() || num_movies <= 0) {
        cout << "Error: Invalid input. Please enter a positive number." << endl;
        return 1;
    }
    vector<Movie> movies;
    cout << "\nGenerating " << num_movies << " movies...\n";
    generateMovies(movies, num_movies);
    cout << "Sort movies by (rating/year/popularity): ";
    string sort_by;
    cin >> sort_by;
    bool (*compare)(const Movie&, const Movie&);
    if (sort_by == "rating") compare = compareByRating;
    else if (sort_by == "year") compare = compareByYear;
    else if (sort_by == "popularity") compare = compareByPopularity;
    else {
        cout << "Invalid choice. Defaulting to rating.\n";
        sort_by = "rating";
        compare = compareByRating;
    }
    cout << "Sorting movies using QuickSort...\n";
    clock_t start = clock();
    if (!movies.empty()) quickSort(movies, 0, movies.size() - 1, compare, 1);
    clock_t end = clock();
    cout << "Done!\n";
    double time_taken = static_cast<double>(end - start) / CLOCKS_PER_SEC;
    cout << "\nTime taken to sort " << num_movies << " movies: "
         << fixed << setprecision(2) << time_taken << " seconds.\n";
    print_top_n_movies(movies, 5, sort_by);
    cout << "\n--- Space Complexity Report ---\n";
    cout << "Total input size: " << movies.size() * sizeof(Movie) << " bytes\n";
    size_t frame_estimate = sizeof(Movie) + (sizeof(int) * 3) + sizeof(void*);
    size_t stack_space_estimate = max_recursion_depth * frame_estimate;
    cout << "\nQuickSort Auxiliary Space (Call Stack Estimate):\n";
    cout << " - Max recursion depth reached: " << max_recursion_depth 
         << " (Theoretical O(log n) is ~" << ceil(log2(num_movies)) << ")\n";
    cout << " - Estimated size per stack frame: ~" << frame_estimate << " bytes\n";
    cout << " - Estimated peak stack space used: ~" << stack_space_estimate << " bytes\n";
    return 0;
}
