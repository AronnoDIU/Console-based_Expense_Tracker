import java.io.*; // For handling file I/O
import java.text.SimpleDateFormat;
import java.util.*; // For handling user input and data storage and manipulation.

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.txt";
    private static final String BUDGETS_FILE_NAME = "budgets.ser";  // New constant for budget file
    private static final Scanner userInput = new Scanner(System.in);
    private static List<Expense> expenses = new ArrayList<>();
    private static Map<String, Double> categoryBudgets = new HashMap<>();
    private static final double CONVERSION_RATE = 110.70;  // Conversion rate (USD to BDT)

    public static void main(String[] args) {
        loadExpensesFromFile(); // Load expenses from file
        loadBudgetsFromFile(); // Load budgets when the application starts

        // Start the application loop and display menu
        while (true) {
            displayMenu(); // Display menu options and get user choice
            int choice = userInput.nextInt();

            switch (choice) {
                case 1: // For Record an expense
                    recordExpense();
                    break;
                case 2: // For View expense summary
                    viewExpenseSummary();
                    break;
                case 3: // For Set budget
                    setBudget();
                    saveBudgetsToFile(); // // Save budgets after setting or updating
                    System.out.println("Budget saved successfully.");
                    break;
                case 4: // For view budgets
                    viewAllBudgets();
                    break;
                case 5: // For View expense history
                    viewExpenseHistory();
                    break;
                case 6: // For Convert currency
                    convertCurrency();
                    break;
                case 7: // For Save expenses
                    saveExpensesToFile();
                    System.out.println("Expenses saved successfully.");
                    break;
                case 8: // For Load expenses
                    loadExpensesFromFile();
                    System.out.println("Expenses loaded successfully.");
                    break;
                case 9: // For Exit
                    saveExpensesToFile(); // Save expenses before exiting
                    saveBudgetsToFile(); // Save budgets before exiting
                    System.exit(0);
                default: // For Invalid choice
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Display menu options and get user choice from the user
    private static void displayMenu() {
        System.out.println("\n===== Expense Tracker Menu =====");
        System.out.println("1. Record an expense");
        System.out.println("2. View expense summary");
        System.out.println("3. Set budget");
        System.out.println("4. View budget");
        System.out.println("5. View expense history");
        System.out.println("6. Convert currency");
        System.out.println("7. Save expenses");
        System.out.println("8. Load expenses");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    // Record an expense and add it to the list of expenses
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

    // View expense summary including total spending and spending per category
    private static void viewExpenseSummary() {

        // Check if there are any expenses recorded
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {

            // Calculate total spending and spending per category based on the list of expenses
            double totalSpending = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("Total Spending: %.2f\n", totalSpending);

            // Group expenses by category and calculate the total spending for each category
            Map<String, Double> categorySpending = new HashMap<>();

            // Iterate through the list of expenses and group them by category
            for (Expense expense : expenses) {

                // Calculate the total spending for the current category and add it to the map
                categorySpending.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            }

            System.out.println("\nCategory-wise Spending:");

            // Print the total spending for each category in descending order based on the map
            categorySpending.forEach((category, spending) ->
                    System.out.printf("%s: %.2f\n", category, spending));
        }
    }

    // Set monthly budget for an expense category and add it to the list of expenses
    private static void setBudget() {
        System.out.print("Enter the expense category to set a budget: ");
        String category = userInput.nextLine();

        if (categoryBudgets.containsKey(category)) {
            System.out.printf("Current budget for category '%s': %.2f\n",
                    category, categoryBudgets.get(category));
        }

        double newBudget = getBudgetInput();

        categoryBudgets.put(category, newBudget);

        System.out.println("Budget set successfully for the category: " + category);
    }

    private static double getBudgetInput() {
        double budget = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter the new budget amount for the category: ");
                budget = Double.parseDouble(userInput.nextLine());

                if (budget >= 0) {
                    validInput = true;
                } else {
                    System.out.println("Invalid budget amount. Please enter a non-negative value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return budget;
    }

    // View all budgets
    private static void viewAllBudgets() {
        if (categoryBudgets.isEmpty()) {
            System.out.println("No budgets set yet.");
        } else {
            System.out.println("\n===== Expense Budgets =====");
            categoryBudgets.forEach((category, budget) ->
                    System.out.printf("Category: %s - Budget: %.2f\n", category, budget));
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadBudgetsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("budgets.ser"))) {
            categoryBudgets = (Map<String, Double>) ois.readObject();
            System.out.println("Budgets loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing budgets found. Starting with an empty budget list.");
        }
    }

    private static void saveBudgetsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUDGETS_FILE_NAME))) {
            oos.writeObject(categoryBudgets);
            System.out.println("Budgets saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving budgets to file.");
        }
    }

    private static void viewExpenseHistory() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            System.out.println("\n===== Expense History =====");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Expense expense : expenses) {
                System.out.printf("Timestamp: %s - Category: %s - Amount: %.2f - Description: %s\n",
                        dateFormat.format(expense.getTimestamp()), expense.getCategory(),
                        expense.getAmount(), expense.getDescription());
            }
        }
    }

    private static void convertCurrency() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded. Cannot perform currency conversion.");
            return;
        }

        System.out.print("Enter the target currency code (e.g., USD, EUR): ");
        String targetCurrencyCode = userInput.nextLine().toUpperCase();

        try {
            Currency.getInstance(targetCurrencyCode);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid currency code. Please enter a valid currency code.");
            return;
        }

        System.out.println("\n===== Currency Conversion =====");

        for (Expense expense : expenses) {
            double convertedAmount = expense.getAmount() * CONVERSION_RATE;
            String convertedCurrency = Currency.getInstance(targetCurrencyCode).getSymbol();

            System.out.printf("%s - Original: %.2f - Converted: %.2f %s\n",
                    expense.getDescription(), expense.getAmount(), convertedAmount, convertedCurrency);
        }
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