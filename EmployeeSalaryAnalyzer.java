import java.io.*;
import java.util.Scanner;

/**
 * Employee Salary Analyzer
 * Console app: add, display, sort by salary, search by name, salary statistics.
 * Data is stored in salaries.txt (loaded at start, saved on exit).
 */
public class EmployeeSalaryAnalyzer {

    static final String FILE_NAME = "salaries.txt";
    static final int MAX = 100;

    // Each record is an object stored in an array
    static class Employee {
        String id;
        String name;
        double salary;

        Employee(String id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        String show() {
            return id + " | " + name + " | " + fmt(salary);
        }
    }

    static Employee[] employees = new Employee[MAX];
    static int count = 0;      // number of records stored
    static int nextId = 101;   // first generated ID is E101

    // Print whole numbers without decimals (45000), others as-is (45000.5)
    static String fmt(double d) {
        if (d == (long) d) return String.valueOf((long) d);
        return String.valueOf(d);
    }

    // ---------- File handling ----------
    static void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null && count < MAX) {
                String[] p = line.split(",");
                if (p.length == 3) {
                    employees[count++] = new Employee(p[0].trim(), p[1].trim(),
                            Double.parseDouble(p[2].trim()));
                    int num = Integer.parseInt(p[0].trim().substring(1));
                    if (num >= nextId) nextId = num + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read file: " + e.getMessage());
        }
    }

    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                pw.println(employees[i].id + "," + employees[i].name + "," + employees[i].salary);
            }
        } catch (IOException e) {
            System.out.println("Could not save file: " + e.getMessage());
        }
    }

    // ---------- Features ----------
    static void addEmployee(Scanner sc) {
        if (count >= MAX) {
            System.out.println("Storage full.");
            return;
        }
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Salary: ");
        String s = sc.nextLine().trim();

        double salary;
        try {
            salary = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary. Salary must be a positive number.");
            return;
        }
        if (salary <= 0 || Double.isNaN(salary) || Double.isInfinite(salary)) {
            System.out.println("Invalid salary. Salary must be a positive number.");
            return;
        }
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        Employee e = new Employee("E" + nextId++, name, salary);
        employees[count++] = e;
        System.out.println("Employee added. ID: " + e.show());
    }

    static void displayAll() {
        if (count == 0) {
            System.out.println("No employees found.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println(employees[i].show());
        }
    }

    // Selection-style sort written by hand: highest salary first
    static void sortBySalary() {
        if (count == 0) {
            System.out.println("No employees found.");
            return;
        }
        Employee[] copy = new Employee[count];
        for (int i = 0; i < count; i++) copy[i] = employees[i];

        for (int i = 0; i < count - 1; i++) {
            int max = i;
            for (int j = i + 1; j < count; j++) {
                if (copy[j].salary > copy[max].salary) max = j;
            }
            Employee t = copy[i];
            copy[i] = copy[max];
            copy[max] = t;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + copy[i].show());
        }
    }

    // Linear search, case-insensitive partial match, written by hand
    static void searchByName(Scanner sc) {
        System.out.print("Enter name to search: ");
        String key = sc.nextLine().trim().toLowerCase();
        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (contains(employees[i].name.toLowerCase(), key)) {
                System.out.println("Found: " + employees[i].show());
                found = true;
            }
        }
        if (!found) System.out.println("Not found.");
    }

    // Manual substring check (no built-in search methods)
    static boolean contains(String text, String key) {
        if (key.isEmpty()) return false;
        for (int i = 0; i + key.length() <= text.length(); i++) {
            int j = 0;
            while (j < key.length() && text.charAt(i + j) == key.charAt(j)) j++;
            if (j == key.length()) return true;
        }
        return false;
    }

    static void statistics() {
        if (count == 0) {
            System.out.println("No employees found.");
            return;
        }
        Employee hi = employees[0], lo = employees[0];
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (employees[i].salary > hi.salary) hi = employees[i];
            if (employees[i].salary < lo.salary) lo = employees[i];
            sum += employees[i].salary;
        }
        System.out.println("Highest: " + hi.name + " (" + fmt(hi.salary) + ") | Lowest: "
                + lo.name + " (" + fmt(lo.salary) + ") | Average: "
                + String.format("%.2f", sum / count));
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        loadFromFile();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== Employee Salary Analyzer =====");
            System.out.println("1. Add Employee");
            System.out.println("2. Display All");
            System.out.println("3. Sort by Salary (high to low)");
            System.out.println("4. Search by Name");
            System.out.println("5. Salary Statistics");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addEmployee(sc); break;
                case "2": displayAll(); break;
                case "3": sortBySalary(); break;
                case "4": searchByName(sc); break;
                case "5": statistics(); break;
                case "6":
                    saveToFile();
                    System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
                    running = false;
                    break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
        sc.close();
    }
}
