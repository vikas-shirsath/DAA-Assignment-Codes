import java.util.*;
import java.time.*;

public class Assi2 {

    static class Movie {
        String title;
        float rating;
        int release_year;
        int popularity;

        void display() {
            System.out.println(title + " | Rating: " + rating
                    + " | Year: " + release_year
                    + " | Popularity: " + popularity);
        }
    }

    // QuickSort with comparator functional interface
    static void quickSort(List<Movie> movies, int low, int high, Comparator<Movie> compare) {
        if (low < high) {
            int pivotIndex = low;
            Movie pivot = movies.get(high);
            for (int i = low; i < high; i++) {
                if (compare.compare(movies.get(i), pivot) < 0) {
                    Collections.swap(movies, i, pivotIndex);
                    pivotIndex++;
                }
            }
            Collections.swap(movies, pivotIndex, high);
            quickSort(movies, low, pivotIndex - 1, compare);
            quickSort(movies, pivotIndex + 1, high, compare);
        }
    }

    // Comparators
    static Comparator<Movie> compareByRating = (a, b) -> Float.compare(a.rating, b.rating);
    static Comparator<Movie> compareByYear = (a, b) -> Integer.compare(a.release_year, b.release_year);
    static Comparator<Movie> compareByPopularity = (a, b) -> Integer.compare(a.popularity, b.popularity);

    // Random movie generator
    static List<Movie> generateMovies(int n) {
        List<Movie> movies = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Movie m = new Movie();
            m.title = "Movie " + (i + 1);
            m.rating = (rand.nextInt(90) + 10) / 10.0f;       // 1.0 to 10.0
            m.release_year = rand.nextInt(45) + 1980;         // 1980 to 2024
            m.popularity = rand.nextInt(1000000) + 1000;      // 1,000 to 1,000,000
            movies.add(m);
        }
        return movies;
    }

    public static void main(String[] args) {
        Random rand = new Random(System.currentTimeMillis());
        int num_movies = 100000;
        List<Movie> movies = generateMovies(num_movies);

        Scanner sc = new Scanner(System.in);
        System.out.print("Sort movies by (rating/year/popularity): ");
        String sort_by = sc.nextLine();

        Comparator<Movie> compare;
        if (sort_by.equals("rating"))
            compare = compareByRating;
        else if (sort_by.equals("year"))
            compare = compareByYear;
        else if (sort_by.equals("popularity"))
            compare = compareByPopularity;
        else {
            System.out.println("Invalid choice. Defaulting to rating.");
            compare = compareByRating;
        }

        Instant start = Instant.now();
        quickSort(movies, 0, movies.size() - 1, compare);
        Instant end = Instant.now();

        double diff = Duration.between(start, end).toMillis() / 1000.0;

        // Show top 10 in descending order
        System.out.println("\nTop 10 movies by " + sort_by + ":");
        for (int i = num_movies - 1; i >= num_movies - 10; --i) {
            movies.get(i).display();
        }

        System.out.println("\nSorted " + num_movies + " movies in " + diff + " seconds.");
    }
}
