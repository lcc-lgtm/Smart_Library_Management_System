import java.io.Serializable;  // convert to String for data storage

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 100;
   
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String userType;
    private int borrowLimit;
    private int loanDuration;
   
    public User(String userId, String name, String email, String phone, String address,
                String password, String userType, int borrowLimit, int loanDuration) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.userType = userType;
        this.borrowLimit = borrowLimit;
        this.loanDuration = loanDuration;
    }
   
    public User(String name, String email, String phone, String address,
                String password, String userType, int borrowLimit, int loanDuration) {
        this("UID" + nextId++,
             name, email, phone, address, password, userType, borrowLimit, loanDuration);
    }
   
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }
    public int getBorrowLimit() { return borrowLimit; }
    public int getLoanDuration() { return loanDuration; }
   
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setPassword(String password) { this.password = password; }
   
    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Type=%s, Email=%s, Phone=%s]",
                             userId, name, userType, email, phone);
    }
}
