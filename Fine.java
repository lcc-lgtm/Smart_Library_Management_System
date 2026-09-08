import java.io.Serializable;  // convert obj into byte stream
import java.time.LocalDate;
import java.util.UUID;

public class Fine implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private String fineId;
    private String transactionId;
    private String userId;
    private double amount;
    private String reason;
    private LocalDate issueDate;
    private boolean paid;
   
    public Fine(String transactionId, String userId, double amount, String reason) {
        this.fineId = "F" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.issueDate = LocalDate.now();
        this.paid = false;
    }
   
    public String getFineId() { return fineId; }
    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public LocalDate getIssueDate() { return issueDate; }
    public boolean isPaid() { return paid; }
   
    public void setPaid(boolean paid) { this.paid = paid; }
   
    @Override
    public String toString() {
        return String.format("Fine[ID=%s, User=%s, Amount=RM%.2f, Reason=%s, Paid=%s]",
                             fineId, userId, amount, reason, paid);
    }
}
