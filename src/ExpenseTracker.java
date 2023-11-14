import java.io.*; // For handling file I/O
import java.text.SimpleDateFormat; // For formatting date and time
import java.util.*; // For handling user input and data storage and manipulation.

public class ExpenseTracker {
    private static final String FILE_NAME = "expenses.txt"; // New constant for expense file
    private static final String BUDGETS_FILE_NAME = "budgets.ser";  // New constant for budget file
    private static final Scanner userInput = new Scanner(System.in);
    private static List<Expense> expenses = new ArrayList<>(); // List of expenses to store
    private static Map<String, Double> categoryBudgets = new HashMap<>(); // Map of category to budget
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

        // Read the input as a string
        String amountInput = userInput.nextLine();

        // Validate if the input is not empty
        if (amountInput.isEmpty()) {
            System.out.println("Amount cannot be empty. Please enter a valid numeric value.");
            return; // Return to the main menu
        }

        // Validate if the input is a valid double
        double amount;
        try {
            amount = Double.parseDouble(amountInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric value for the amount.");
            return; // Return to the main menu
        }

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

        // Check if the category exists in the list of expenses and get the current budget
        if (categoryBudgets.containsKey(category)) {
            System.out.printf("Current budget for category '%s': %.2f\n",
                    category, categoryBudgets.get(category));
        }

        double newBudget = getBudgetInput();

        // Set the new budget for the category in the map.
        categoryBudgets.put(category, newBudget);

        System.out.println("Budget set successfully for the category: " + category);
    }

    private static double getBudgetInput() {
        double budget = 0; // The Initial budget amount is set to 0
        boolean validInput = false; // Flag to check if the input is valid

        // Loop until the input is valid
        while (!validInput) {
            try {
                System.out.print("Enter the new budget amount for the category: ");
                budget = Double.parseDouble(userInput.nextLine());

                // Check if the budget amount is non-negative and non-zero.
                if (budget >= 0) {
                    validInput = true; // Input is valid, exit the loop
                } else {
                    System.out.println("Invalid budget amount. Please enter a non-negative value.");
                }
            } catch (NumberFormatException e) { // Input is not a valid number
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return budget; // Return the valid budget amount for the category
    }

    // View all budgets
    private static void viewAllBudgets() {

        // Check if there are any budgets set yet.
        if (categoryBudgets.isEmpty()) {
            System.out.println("No budgets set yet.");
        } else {
            System.out.println("\n===== Expense Budgets =====");

            // Print the budget for each category in descending order based on the map.
            categoryBudgets.forEach((category, budget) ->
                    System.out.printf("Category: %s - Budget: %.2f\n", category, budget));
        }
    }

    @SuppressWarnings("unchecked") // Load budgets from file and add them to the list of budgets
    private static void loadBudgetsFromFile() {

        // Try to load budgets from file and add them to the list of budgets if they exist.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("budgets.ser"))) {
            categoryBudgets = (Map<String, Double>) ois.readObject(); // Read the serialized object
            System.out.println("Budgets loaded successfully.");
        } catch (IOException | ClassNotFoundException e) { // No existing budgets found
            System.out.println("No existing budgets found. Starting with an empty budget list.");
        }
    }

    // Save budgets to file
    private static void saveBudgetsToFile() {

        // Try to save budgets to file and print a success message if successful.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUDGETS_FILE_NAME))) {

            // Write the serialized object to the file.
            oos.writeObject(categoryBudgets);
            System.out.println("Budgets saved successfully.");
        } catch (IOException e) { // Error saving budgets to file
            System.out.println("Error saving budgets to file.");
        }
    }

    // View expense history
    private static void viewExpenseHistory() {

        // Check if there are any expenses recorded.
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else { // Print the expense history
            System.out.println("\n===== Expense History =====");

            // Create a SimpleDateFormat object to format the timestamp as "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Print the expense history for each expense in the list of expenses
            for (Expense expense : expenses) {
                System.out.printf("Timestamp: %s - Category: %s - Amount: %.2f - Description: %s\n",
                        dateFormat.format(expense.getTimestamp()), expense.getCategory(),
                        expense.getAmount(), expense.getDescription());
            }
        }
    }

    // Convert currency
    private static void convertCurrency() {

        // Check if there are any expenses recorded. If not, return.
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded. Cannot perform currency conversion.");
            return;
        }

        System.out.print("Enter the target currency code (e.g., USD, BDT): ");
        String targetCurrencyCode = userInput.nextLine().toUpperCase();

        try {
            // Check if the target currency code is valid.
            Currency.getInstance(targetCurrencyCode);
        } catch (IllegalArgumentException e) { // If not, throw an exception.
            System.out.println("Invalid currency code. Please enter a valid currency code.");
            return;
        }

        System.out.println("\n===== Currency Conversion =====");

        // Convert the expenses to the target currency and print the results
        // for each expense in the list of expenses.
        for (Expense expense : expenses) {

            // Convert the expense amount to the target currency and print the results
            double convertedAmount = expense.getAmount() * CONVERSION_RATE;
            String convertedCurrency = Currency.getInstance(targetCurrencyCode).getSymbol();

            System.out.printf("%s - Original: %.2f - Converted: %.2f %s\n",
                    expense.getDescription(), expense.getAmount(), convertedAmount, convertedCurrency);
        }
    }

    @SuppressWarnings("unchecked") // Load expenses from file and add them to the list of expenses
    private static void loadExpensesFromFile() {

        // Try to load expenses from file and add them to the list of expenses if they exist.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            expenses = (List<Expense>) ois.readObject(); // Read the serialized object
            System.out.println("Expenses loaded successfully.");
        } catch (IOException | ClassNotFoundException e) { // No existing expenses found
            System.out.println("No existing expenses found. Starting with an empty expense list.");
        }
    }

    // Save expenses to file
    private static void saveExpensesToFile() {

        // Try to save expenses to file and print a success message if successful.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(expenses); // Write the serialized object to the file.
            System.out.println("Expenses saved successfully.");
        } catch (IOException e) { // Error saving expenses to file
            System.out.println("Error saving expenses to file.");
        }
    }
}