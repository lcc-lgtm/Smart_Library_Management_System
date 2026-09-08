import java.util.UUID;

public class Librarian extends User {
    private static final long serialVersionUID = 1L;
    private String employeeId;
   
    public Librarian(String userId, String name, String email, String phone, String address, String password) {
        super(userId, name, email, phone, address, password, "Librarian", 999, 14);
        this.employeeId = "EMP" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }


    public Librarian(String name, String email, String phone, String address, String password) {
        super(name, email, phone, address, password, "Librarian", 999, 14);
        this.employeeId = "EMP" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
   
    public String getEmployeeId() { return employeeId; }
   
    @Override
    public String toString() {
        return String.format("Librarian[ID=%s, Name=%s, EmployeeID=%s, Email=%s]",
                             getUserId(), getName(), employeeId, getEmail());
    }
}
