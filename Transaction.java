import java.io.Serializable;  // convert obj into byte stream
import java.time.LocalDate;
import java.util.UUID;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private String transactionId;
    private String resourceId;
    private String userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private double fineAmount;
   
    public Transaction(String resourceId, String userId, LocalDate borrowDate, LocalDate dueDate) {
        this.transactionId = "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.resourceId = resourceId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.fineAmount = 0;
    }
   
    public String getTransactionId() { return transactionId; }
    public String getResourceId() { return resourceId; }
    public String getUserId() { return userId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }
    public double getFineAmount() { return fineAmount; }
   
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setReturned(boolean returned) { this.returned = returned; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
   
    @Override
    public String toString() {
        return String.format("Transaction[ID=%s, Resource=%s, Due=%s, Returned=%s, Fine=RM%.2f]",
                             transactionId, resourceId, dueDate, returned, fineAmount);
    }
}
