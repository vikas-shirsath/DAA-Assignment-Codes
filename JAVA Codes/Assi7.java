import java.util.*;

class Exam {
    String name;
    int students;

    Exam(String n, int s) {
        name = n;
        students = s;
    }
}

class Room {
    String name;
    int capacity;

    Room(String n, int c) {
        name = n;
        capacity = c;
    }
}

public class Assi7 {

    static List<String> splitCourses(String s) {
        List<String> result = new ArrayList<>();
        for (String c : s.split(",")) {
            if (!(c = c.trim()).isEmpty()) result.add(c);
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Map<String, Integer> studentCounts = new LinkedHashMap<>();
        Map<String, String> studentEnrollments = new LinkedHashMap<>();
        List<Room> rooms = new ArrayList<>();
        Set<String> courses = new LinkedHashSet<>();

        System.out.print("Enter total number of courses: ");
        int nCourses = sc.nextInt();
        System.out.println("Enter each course (Name Students):");
        for (int i = 0; i < nCourses; i++) {
            String name = sc.next();
            int count = sc.nextInt();
            studentCounts.put(name, count);
            courses.add(name);
        }

        System.out.print("\nEnter total number of students: ");
        int nStudents = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter each student (ID EnrolledCourses):");
        for (int i = 0; i < nStudents; i++) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                i--;
                continue;
            }
            String[] parts = line.split(" ", 2);
            if (parts.length == 2) studentEnrollments.put(parts[0], parts[1].trim());
        }

        System.out.print("\nEnter total number of rooms: ");
        int nRooms = sc.nextInt();
        System.out.println("Enter each room (Name Capacity):");
        for (int i = 0; i < nRooms; i++) {
            rooms.add(new Room(sc.next(), sc.nextInt()));
        }

        // --- Build conflict graph ---
        Map<String, Set<String>> graph = new HashMap<>();
        for (var e : studentEnrollments.values()) {
            List<String> list = splitCourses(e);
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    graph.computeIfAbsent(list.get(i), k -> new HashSet<>()).add(list.get(j));
                    graph.computeIfAbsent(list.get(j), k -> new HashSet<>()).add(list.get(i));
                }
            }
        }

        List<String> allCourses = new ArrayList<>(courses);
        Map<String, Integer> color = new HashMap<>();
        allCourses.sort((a, b) ->
                Integer.compare(
                        graph.getOrDefault(b, Set.of()).size(),
                        graph.getOrDefault(a, Set.of()).size()
                )
        );

        int slots = 0;
        for (String u : allCourses) {
            Set<Integer> used = new HashSet<>();
            for (String v : graph.getOrDefault(u, Set.of())) {
                if (color.containsKey(v)) used.add(color.get(v));
            }
            int c = 0;
            while (used.contains(c)) c++;
            color.put(u, c);
            slots = Math.max(slots, c + 1);
        }

        // --- Room allocation ---
        rooms.sort(Comparator.comparingInt(r -> r.capacity));
        Map<String, String> roomAssigned = new HashMap<>();
        Map<Integer, Set<String>> occupied = new HashMap<>();

        for (int t = 0; t < slots; t++) {
            List<Exam> exams = new ArrayList<>();
            for (String c : allCourses) {
                if (color.get(c) == t) {
                    exams.add(new Exam(c, studentCounts.getOrDefault(c, 0)));
                }
            }
            exams.sort((a, b) -> b.students - a.students);

            for (Exam e : exams) {
                occupied.putIfAbsent(t, new HashSet<>());
                boolean done = false;
                for (Room r : rooms) {
                    if (r.capacity >= e.students && occupied.get(t).add(r.name)) {
                        roomAssigned.put(e.name, r.name);
                        done = true;
                        break;
                    }
                }
                if (!done) roomAssigned.put(e.name, "UNASSIGNED");
            }
        }

        // --- Output final schedule ---
        System.out.println("\nFinal Timetable:");
        System.out.printf("%-10s%-12s%-10s%n", "Course", "Slot", "Room");
        System.out.println("-".repeat(32));
        Collections.sort(allCourses);
        for (String c : allCourses) {
            System.out.printf("%-10s%-12s%-10s%n", c, "Slot " + (color.get(c) + 1), roomAssigned.get(c));
        }

        sc.close();
    }
}
