import java.io.*;
import java.util.*;

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.txt";
    private static final Scanner userInput = new Scanner(System.in);
    private static List<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        loadExpensesFromFile();

        while (true) {
            displayMenu();
            String choice = userInput.nextLine().toLowerCase();

            switch (choice) {
                case "record":
                    recordExpense();
                    break;
                case "view summary":
                    viewExpenseSummary();
                    break;
                case "set budget":
                    setBudget();
                    break;
                case "view history":
                    viewExpenseHistory();
                    break;
                case "convert currency":
                    convertCurrency();
                    break;
                case "save":
                    saveExpensesToFile();
                    System.out.println("Expenses saved successfully.");
                    break;
                case "load":
                    loadExpensesFromFile();
                    System.out.println("Expenses loaded successfully.");
                    break;
                case "exit":
                    saveExpensesToFile();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n===== Expense Tracker Menu =====");
        System.out.println("1. Record an expense");
        System.out.println("2. View expense summary");
        System.out.println("3. Set budget");
        System.out.println("4. View expense history");
        System.out.println("5. Convert currency");
        System.out.println("6. Save expenses");
        System.out.println("7. Load expenses");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void recordExpense() {
        System.out.print("Enter the expense amount: ");
        double amount = Double.parseDouble(userInput.nextLine());

        System.out.print("Enter the expense category: ");
        String category = userInput.nextLine();

        System.out.print("Enter a brief description: ");
        String description = userInput.nextLine();

        Expense expense = new Expense(amount, category, description);
        expenses.add(expense);

        System.out.println("Expense recorded successfully.");
    }

    private static void viewExpenseSummary() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            double totalSpending = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("Total Spending: %.2f\n", totalSpending);

            Map<String, Double> categorySpending = new HashMap<>();
            for (Expense expense : expenses) {
                categorySpending.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            }

            System.out.println("\nCategory-wise Spending:");
            categorySpending.forEach((category, spending) ->
                    System.out.printf("%s: %.2f\n", category, spending));
        }
    }

    private static void setBudget() {
        System.out.print("Enter the expense category to set a budget: ");
        String category = userInput.nextLine();

        System.out.print("Enter the budget amount for the category: ");
        double budget = Double.parseDouble(userInput.nextLine());

        // Add your budget-setting logic here

        System.out.println("Budget set successfully for the category: " + category);
    }

    private static void viewExpenseHistory() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            System.out.println("\n===== Expense History =====");
            expenses.forEach(System.out::println);
        }
    }

    private static void convertCurrency() {
        // Add your currency conversion logic here
        System.out.println("Currency conversion feature not implemented yet.");
    }

    @SuppressWarnings("unchecked")
    private static void loadExpensesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            expenses = (List<Expense>) ois.readObject();
            System.out.println("Expenses loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing expenses found. Starting with an empty expense list.");
        }
    }

    private static void saveExpensesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(expenses);
            System.out.println("Expenses saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving expenses to file.");
        }
    }
}