import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

class Expense implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String category;
    private final String description;
    private final Date timestamp;

    public Expense(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.timestamp = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%tF %tT - %s - %.2f - %s", timestamp, timestamp, category, amount, description);
    }
}