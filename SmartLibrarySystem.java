import java.util.*;  // Scanner , List , Set , Map
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.io.*;  // input output stream -> File , Serializable , FileInputStream , ObjectOutputStream

public class SmartLibrarySystem {
    private static final String DEFAULT_ADMIN_ID = "UID001";
    private static final String DEFAULT_FACULTY_ID = "UID002";
    private static final String DEFAULT_STUDENT_ID = "UID003";
    private static final String DEFAULT_PUBLIC_ID = "UID004";
    private static final String DEFAULT_PUBLIC_FINE_ID = "UID005";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_FACULTY_PASSWORD = "faculty123";
    private static final String DEFAULT_STUDENT_PASSWORD = "student123";
    private static final String DEFAULT_PUBLIC_PASSWORD = "abc123";
    private static final String DEFAULT_PUBLIC_FINE_PASSWORD = "sudo123";

    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static void main(String[] args) {
        loadData();
       
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static String getResourceIdWithQuit(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Operation cancelled. Returning to menu...");
                return null;
            }
            
            if (input.isEmpty()) {
                System.out.println("Invalid input! Resource ID cannot be empty.");
                continue;
            }
            
            return input; //Return back the entered Resource ID from the user
        }
    }

    private static String getStringInputWithQuit(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Registration cancelled. Returning to menu...");
                    return null; 
                }
                
                if (input.isEmpty()) {
                    System.out.println("Invalid input! This field cannot be empty. Enter 'q' to cancel.");
                    continue;
                }
                
                return input;
            } catch (Exception e) {
                System.out.println("Error reading input: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static String getPhoneInputWithQuit(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Registration cancelled. Returning to menu...");
                    return null; 
                }
                
                if (input.isEmpty()) {
                    System.out.println("Invalid input! Phone number cannot be empty. Enter 'q' to cancel.");
                    continue;
                }
                
                // to validate input contains digits only
                if (!input.matches("\\d+")) {
                    System.out.println("Invalid phone number!!! Only numeric digits.");
                    continue;
                }
                
                // to validate input has = 10 digits
                if (input.length() != 10) {
                    System.out.println("Invalid phone number!!! Must be 10-digit number.");
                    continue;
                }
                
                return input;  // Return the entered phone number
            } catch (Exception e) {
                System.out.println("Error reading input: " + e.getMessage() + " Please try again.");
            }
        }
    }

    private static void showInputError(String message) {
        System.out.println(message);
    }

    private static void showMainMenu() {
        printDefaultUserHint();
        System.out.println("\n========================================");
        System.out.println(" SMART LIBRARY MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        while (true) {
            System.out.print("Choose option: ");
           
            int choice = getIntInput();
           
            switch (choice) {
                case 1:
                    login();
                    return;
                case 2:
                    register();
                    return;
                case 3:
                    System.out.println("Thank you for using the system!");
                    saveData();
                    System.exit(0);
                default:
                    showInputError("Invalid option! Please try again.");
            }
        }
    }
    private static void showUserMenu() {
        System.out.println("\n========================================");
        System.out.println(" Welcome, " + currentUser.getName());
        System.out.println(" User Type: " + currentUser.getUserType());
        System.out.println("========================================");
        System.out.println("1. Search Resources");
        System.out.println("2. Borrow Resource");
        System.out.println("3. Return Resource");
        System.out.println("4. View My Borrowed Items");
        System.out.println("5. Reserve Resource");
        System.out.println("6. Pay Fines");
        System.out.println("7. View My Fines");
        System.out.println("8. Logout");
       
        if (currentUser instanceof Librarian) {
            System.out.println("\n--- LIBRARIAN MENU ---");
            System.out.println("9. Add New Resource");
            System.out.println("10. Update Resource");
            System.out.println("11. Remove Resource");
            System.out.println("12. View All Resources");
            System.out.println("13. View All Users");
            System.out.println("14. Update User");
            System.out.println("15. Delete User");
            System.out.println("16. Generate Reports");
            System.out.println("17. Process Fine Payments");
        }
       
        while (true) {
            System.out.print("Choose option: ");
           
            int choice = getIntInput();
           
            switch (choice) {
                case 1: searchResources(); return;
                case 2: borrowResource(); return;
                case 3: returnResource(); return;
                case 4: viewBorrowedItems(); return;
                case 5: reserveResource(); return;
                case 6: payFines(); return;
                case 7: viewMyFines(); return;
                case 8:
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    if (currentUser instanceof Librarian) {
                        switch (choice) {
                            case 9: addResource(); return;
                            case 10: updateResource(); return;
                            case 11: removeResource(); return;
                            case 12: viewAllResources(); return;
                            case 13: viewAllUsers(); return;
                            case 14: updateUser(); return;
                            case 15: deleteUser(); return;
                            case 16: generateReports(); return;
                            case 17: processFinePayments(); return;
                            default: showInputError("Invalid option! Please try again.");
                        }
                    } else {
                        showInputError("Invalid option! Please try again.");
                    }
            }
        }
    }
    private static void login() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
       
        User user = library.authenticateUser(userId, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }
    }
    private static void register() {
        System.out.println("\n--- User Registration ---");
        System.out.println("(Enter 'q' to cancel registration and exit.)");
        
        String name = getStringInputWithQuit("Enter Name: ");
        if (name == null) return;
        String email = getStringInputWithQuit("Enter Email: ");
        if (email == null) return;
        String phone = getPhoneInputWithQuit("Enter Phone: ");
        if (phone == null) return;
        String password = getStringInputWithQuit("Create Password: ");
        if (password == null) return;
       
        System.out.println("User Type:");
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Public Member");
        System.out.println("4. Librarian");
        System.out.print("Choose: ");
        int type = getIntInput();
       
        int borrowLimit = 3;
        int loanDuration = 7;
        String userType = "Student";
       
        switch (type) {
            case 1:
                userType = "Student";
                borrowLimit = 3;
                loanDuration = 7;
                break;
            case 2:
                userType = "Faculty";
                borrowLimit = 10;
                loanDuration = 7;
                break;
            case 3:
                userType = "Public Member";
                borrowLimit = 2;
                loanDuration = 7;
                break;
            case 4:
                Librarian librarian = new Librarian(name, email, phone, "", password);
                library.addUser(librarian);
                System.out.println("Librarian registration successful! Your User ID is: " + librarian.getUserId());
                return;
            default:
                System.out.println("Invalid type, setting as Student");
        }
       
        User user = new User(name, email, phone, "", password, userType, borrowLimit, loanDuration);
        library.addUser(user);
        System.out.println("Registration successful! Your User ID is: " + user.getUserId());
    }
    private static void searchResources() {
        System.out.println("\n");
        System.out.println("=========================================");
        System.out.println("    ---      Search Resources     ---    ");
        System.out.println("=========================================");
        System.out.print("Enter search term: ");
        String term = scanner.nextLine();
       
        List<Resource> results = library.searchResources(term);
       
        if (results.isEmpty()) {
            System.out.println("No resources found.");
        } else {
            System.out.println("\n--- Results ---");
            displayResourceTable(results);
        }
    }
    private static void borrowResource() {
        System.out.println("\n--- Borrow Resource ---");
        System.out.println("Available Resources:");
        displayResourceTable(library.getAllResources());
        
        while (true) {
            String resourceId = getResourceIdWithQuit("Enter Resource ID to borrow (or q to cancel): ");
            if (resourceId == null) return; 
            
            try {
                Transaction transaction = library.borrowResource(currentUser, resourceId);
                if (transaction != null) {
                    System.out.println("========================================");
                    System.out.println("    Resource borrowed successfully!");
                    System.out.println("    Due Date: " + transaction.getDueDate().format(dateFormatter));
                    System.out.println("========================================");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());  
            }
        }
    }
    
    private static void displayResourceTable(List<Resource> resources) {
        if (resources.isEmpty()) {
            System.out.println("No resources to display.");
            return;
        }
        
        System.out.println("=".repeat(140));
        System.out.printf("%-10s %-10s %-22s %-11s %-5s %-6s %-15s %-18s %-17s %-10s\n",
                         "Type", "Res ID", "Title", "Author", "Year", "Pages", "ISBN", "Genre", "Publisher", "Status");
        System.out.println("=".repeat(140));
        for (Resource r : resources) {
            String type = r.getResourceType();
            if (type.length() > 9) type = type.substring(0, 7) + "...";
            String resourceId = r.getResourceId();
            String title = r.getTitle();
            if (title.length() > 20) title = title.substring(0, 17) + "...";
            String author = r.getAuthor();
            if (author.length() > 10) author = author.substring(0, 7) + "...";
            String year = String.valueOf(r.getYear());
            String pages = "";
            if (r instanceof Book) {
                pages = String.valueOf(((Book) r).getPages());}
            if (pages.length() > 7) pages = pages.substring(0, 4) + "...";
            String isbn = r.getIsbn();
            if (isbn.length() > 14) isbn = isbn.substring(0, 11) + "...";
            String genre = r.getGenre();
            if (genre.length() > 16) genre = genre.substring(0, 13) + "...";
            String publisher = r.getPublisher();
            if (publisher.length() > 16) publisher = publisher.substring(0, 13) + "...";
            String status = r.isAvailable() ? "Available" : "On Loan";
            if (status.length() > 10) status = status.substring(0, 7) + "...";

            System.out.printf("%-10s %-10s %-22s %-11s %-5s %-6s %-15s %-18s %-17s %-10s\n",
                             type, resourceId, title, author, year, pages, isbn, genre, publisher, status);
        }
        System.out.println("_".repeat(140));
        System.out.println();
    }
    private static void returnResource() {
        System.out.println("\n--- Return Resource ---");
        
        List<Transaction> borrowedItems = library.getUserBorrowedItems(currentUser.getUserId());
        List<Transaction> unreturned = new ArrayList<>();
        for (Transaction t : borrowedItems) {
            if (!t.isReturned()) {
                unreturned.add(t);
            }
        }

        if (unreturned.isEmpty()) {
            System.out.println("You have no borrowed items to return!");
            return;
        }

        System.out.println("Your Borrowed Items:");
        System.out.println("=".repeat(140));
        System.out.printf("%-10s %-10s %-22s %-11s %-5s %-6s %-15s %-18s %-17s %-10s\n",
                         "Type", "Res ID", "Title", "Author", "Year", "Pages", "ISBN", "Genre", "Publisher", "Status");
        System.out.println("=".repeat(140));

        for (Transaction t : unreturned) {
            Resource r = library.getResource(t.getResourceId());
            if (r != null) {
                String type = r.getResourceType();
                if (type.length() > 9) type = type.substring(0, 7) + "...";
                String resourceId = r.getResourceId();
                String title = r.getTitle();
                if (title.length() > 20) title = title.substring(0, 17) + "...";
                String author = r.getAuthor();
                if (author.length() > 10) author = author.substring(0, 7) + "...";
                String year = String.valueOf(r.getYear());
                String pages = "";
                if (r instanceof Book) {
                    pages = String.valueOf(((Book) r).getPages());
                }
                if (pages.length() > 7) pages = pages.substring(0, 4) + "...";

                String isbn = r.getIsbn();
                if (isbn.length() > 14) isbn = isbn.substring(0, 11) + "...";

                String genre = r.getGenre();
                if (genre.length() > 16) genre = genre.substring(0, 13) + "...";

                String publisher = r.getPublisher();
                if (publisher.length() > 16) publisher = publisher.substring(0, 13) + "...";

                String status = "On Loan";

                System.out.printf("%-10s %-10s %-22s %-11s %-5s %-6s %-15s %-18s %-17s %-10s\n",
                                type, resourceId, title, author, year, pages, isbn, genre, publisher, status);
            }
        }
        System.out.println("_".repeat(140));
        System.out.println();

        while (true) {
            String resourceId = getResourceIdWithQuit("Enter Resource ID to return (or q to cancel): ");
            if (resourceId == null) return;

            try {
                Transaction transaction = library.returnResource(currentUser, resourceId);
                if (transaction != null) {
                    System.out.println("Resource returned successfully!");
                    if (transaction.getFineAmount() > 0) {
                        System.out.println("Late return! Fine amount: RM " + transaction.getFineAmount());
                    }
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void viewBorrowedItems() {
        List<Transaction> borrowed = library.getUserBorrowedItems(currentUser.getUserId());
       
        List<Transaction> unreturned = new ArrayList<>();
        for (Transaction t : borrowed) {
            if (!t.isReturned()) {
                unreturned.add(t);
            }
        }
       
        if (unreturned.isEmpty()) {
            System.out.println("=".repeat(50));
            System.out.println("No borrowed items.");
            System.out.println("=".repeat(50));
        } else {
            System.out.println("\n");
            System.out.println("=".repeat(100));
            System.out.println("--- Borrowed Items ---");
            System.out.println("=".repeat(100));
            System.out.printf("%-15s %-30s %-12s %-12s %-12s %-15s\n",
                              "Resource ID", "Title", "Borrow Date", "Due Date", "Days Left", "Status");
            System.out.println("=".repeat(100));

            for (Transaction t : unreturned) {
                Resource r = library.getResource(t.getResourceId());
                if (r != null) {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), t.getDueDate());
                    String status = daysLeft < 0 ? "OVERDUE" : "ON LOAN";
                    String title = r.getTitle();
                    if (title.length() > 28) title = title.substring(0, 25) + "...";
                   
                    System.out.printf("%-15s %-30s %-12s %-12s %-12d %-15s\n",
                                      t.getResourceId(), title, t.getBorrowDate().toString(),
                                      t.getDueDate().toString(), daysLeft, status);
                }
            }
            System.out.println("_".repeat(100));
        }
    }
    private static void reserveResource() {
        System.out.println("\n" + "=".repeat(70) 
                        + "\n--- Reserve Resource ---" + "\n" + "=".repeat(70));
        
        List<Resource> onLoan = new ArrayList<>();
        for (Resource r : library.getAllResources()) {
            if (!r.isAvailable()) {
                onLoan.add(r);
            }
        }

        System.out.println("Resources Available for Reservation (On Loan):" + "\n" + "-".repeat(70));
        System.out.printf("%-16s %-30s\n",
                              "Resource ID", "Title");
        System.out.println("=".repeat(70));
        if (onLoan.isEmpty()) {
            System.out.println("No resources currently on loan.");
        } else {
            for (Resource r : onLoan) {
                System.out.printf("%-15s %-40s\n", r.getResourceId(), r.getTitle());
            }
        }
        
        while (true) {
            String resourceId = getResourceIdWithQuit("_".repeat(70) 
                        + "\n" + "\nEnter Resource ID to reserve (or q to cancel): ");
            if (resourceId == null) return;

            boolean success = library.reserveResource(currentUser, resourceId);
            if (success) {
                System.out.println("\n" + "=".repeat(70) 
                        + "\n" + "Resource reserved successfully!" 
                        + "\n" + "=".repeat(70));
                return;
            } else {
                System.out.println("\n" + "=".repeat(70) 
                        + "\n" + "Error: Resource not found, already available, or cannot be reserved."
                        + "\n" + "=".repeat(70));
            }
        }
    }
    
    private static void payFines() {
        List<Fine> fines = library.getUserFines(currentUser.getUserId());
        double total = 0;
       
        if (fines.isEmpty()) {
            System.out.println("=".repeat(100));
            System.out.println("You have no fines.");
            System.out.println("=".repeat(100));
            return;
        }
        System.out.println("\n" + "=".repeat(100));
        System.out.println("--- Your Fines ---");
        System.out.println("=".repeat(100));
        for (Fine f : fines) {
            if (!f.isPaid()) {
                System.out.println(String.format("Fine ID: %s | Amount: RM %.2f", f.getFineId(), f.getAmount()));
                total += f.getAmount();
            }
        }
        System.out.println("_".repeat(100));
        System.out.println(String.format("Total unpaid: RM %.2f", total));
        System.out.println("_".repeat(100) + "\n");
        while (true) {
            System.out.print("Pay all fines? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                library.payFines(currentUser.getUserId());
                System.out.println("\n" + "=".repeat(100));
                System.out.println("Your fine payment was successful.");
                System.out.println("=".repeat(100));
                break;
            } else if (confirm.equalsIgnoreCase("n")) {
                System.out.println("\n" + "=".repeat(100));
                System.out.println("You have successfully cancelled all payments.");
                System.out.println("=".repeat(100));
                break;
            } else {
                System.out.println("=".repeat(100));
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
                System.out.println("=".repeat(100));
            }
        }
    }
    private static void viewMyFines() {
        List<Fine> fines = library.getUserFines(currentUser.getUserId());
       
        if (fines.isEmpty()) {
            System.out.println("You have no fines.");
        } else {
            System.out.println("\n--- Fine History ---");
            for (Fine f : fines) {
                System.out.println(f);
            }
        }
    }

    // Managing User Operations
    private static void updateUser() {
        List<User> users = library.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users available to update.");
            return;
        }
        
        System.out.println("\n--- Available User ---");
        System.out.printf("%-4s %-20s %-13s %-12s %-15s %-28s %-15s\n",
                          "No.", "Name", "User Type", "ID", "Password", "Email", "Phone");
        System.out.println("---- -------------------- ------------- ------------ --------------- ---------------------------- ---------------");
        int count = 1;
        for (User user : users) {
            String displayType = user.getUserType().equals("Librarian") ? "Admin" : 
                                (user.getUserType().equals("Public Member") ? "Public" : user.getUserType());
            String maskedPassword = maskPassword(user.getPassword());
            String name = user.getName();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }
            String email = user.getEmail();
            if (email.length() > 28) {
                email = email.substring(0, 25) + "...";
            }
            System.out.printf("%-4d %-20s %-13s %-12s %-15s %-28s %-15s\n",
                              count, name, displayType, user.getUserId(), maskedPassword, email, user.getPhone());
            count++;
        }
        
        String userId;
        while (true) {
            System.out.print("\nEnter User ID to update (or q to cancel): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Operation cancelled. Returning to menu...");
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("Invalid input! User ID cannot be empty.");
                continue;
            }
            
            userId = input;
            break;
        }
        
        User user = library.getUser(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
       
        System.out.println("\n--- Updating User: " + user.getName() + " ---");
        System.out.println("1. Update Name");
        System.out.println("2. Update Email");
        System.out.println("3. Update Phone");
        System.out.println("4. Update Password");
        System.out.println("5. Cancel");
        System.out.print("Choose: ");
       
        int choice = getIntInput();
       
        switch (choice) {
            case 1:
                System.out.print("Enter new name: ");
                user.setName(scanner.nextLine());
                System.out.println("Name updated successfully!");
                break;
            case 2:
                System.out.print("Enter new email: ");
                user.setEmail(scanner.nextLine());
                System.out.println("Email updated successfully!");
                break;
            case 3:
                System.out.print("Enter new phone: ");
                user.setPhone(scanner.nextLine());
                System.out.println("Phone updated successfully!");
                break;
            case 4:
                System.out.print("Enter new password: ");
                user.setPassword(scanner.nextLine());
                System.out.println("Password updated successfully!");
                break;
            default:
                System.out.println("Update cancelled.");
        }
    }
   
    private static void deleteUser() {
        List<User> users = library.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        
        System.out.println("\n--- All Users ---");
        System.out.printf("%-4s %-20s %-13s %-12s %-15s %-28s %-15s\n",
                          "No.", "Name", "User Type", "ID", "Password", "Email", "Phone");
        System.out.println("---- -------------------- ------------- ------------ --------------- ---------------------------- ---------------");
        int count = 1;
        for (User u : users) {
            String displayType = u.getUserType().equals("Librarian") ? "Admin" : 
                                (u.getUserType().equals("Public Member") ? "Public" : u.getUserType());
            String maskedPassword = maskPassword(u.getPassword());
            String name = u.getName();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }
            String email = u.getEmail();
            if (email.length() > 28) {
                email = email.substring(0, 25) + "...";
            }
            System.out.printf("%-4d %-20s %-13s %-12s %-15s %-28s %-15s\n",
                              count, name, displayType, u.getUserId(), maskedPassword, email, u.getPhone());
            count++;
        }
        
        String userId;
        while (true) {
            System.out.print("\nEnter User ID to delete (or q to cancel): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Operation cancelled. Returning to menu...");
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("Invalid input! User ID cannot be empty.");
                continue;
            }
            
            userId = input;
            break;
        }
        
        User user = library.getUser(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
       
        System.out.println("Are you sure you want to delete user: " + user.getName() + "? (y/n)");
        String confirm = scanner.nextLine();
       
        if (confirm.equalsIgnoreCase("y")) {
            List<Transaction> borrowed = library.getUserBorrowedItems(userId);
            boolean hasUnreturned = false;
            for (Transaction t : borrowed) {
                if (!t.isReturned()) {
                    hasUnreturned = true;
                    break;
                }
            }
           
            if (hasUnreturned) {
                System.out.println("Cannot delete user with unreturned items!");
                return;
            }
           
            boolean success = library.deleteUser(userId);
            if (success) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // Resource User Operations
    private static void addResource() {
        System.out.println("\n--- Add New Resource ---");
        System.out.println("Resource Type:");
        System.out.println("1. Book");
        System.out.println("2. Journal");
        System.out.println("3. Digital Resource");
        System.out.print("Choose: ");
       
        int type = getIntInput();
       
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Publisher: ");
        String publisher = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
       
        int year = 0;
        while (year == 0) {
            System.out.print("Year: ");
            try {
                year = Integer.parseInt(scanner.nextLine());
                if (year < 1000 || year > 2026) {
                    System.out.println("Please enter a valid year (1000-2026)");
                    year = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid year number!");
            }
        }
       
        Resource resource = null;
       
        switch (type) {
            case 1:
                int pages = 0;
                while (pages == 0) {
                    System.out.print("Number of Pages: ");
                    try {
                        pages = Integer.parseInt(scanner.nextLine());
                        if (pages <= 0) {
                            System.out.println("Pages must be positive!");
                            pages = 0;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number!");
                    }
                }
                resource = new Book(title, author, publisher, genre, isbn, year, pages);
                break;
            case 2:
                System.out.print("Volume: ");
                String volume = scanner.nextLine();
                System.out.print("Issue: ");
                String issue = scanner.nextLine();
                resource = new Journal(title, author, publisher, genre, isbn, year, volume, issue);
                break;
            case 3:
                System.out.print("File Format: ");
                String format = scanner.nextLine();
                double size = 0;
                while (size == 0) {
                    System.out.print("File Size (MB): ");
                    try {
                        size = Double.parseDouble(scanner.nextLine());
                        if (size <= 0) {
                            System.out.println("Size must be positive!");
                            size = 0;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number!");
                    }
                }
                resource = new DigitalResource(title, author, publisher, genre, isbn, year, format, size);
                break;
            default:
                System.out.println("Invalid type");
                return;
        }
       
        library.addResource(resource);
        System.out.println("Resource added successfully! ID: " + resource.getResourceId());
    }
   
    private static void updateResource() {
        List<Resource> resources = library.getAllResources();
        if (resources.isEmpty()) {
            System.out.println("No resources available to update.");
            return;
        }
        
        System.out.println("\n--- All Resources ---");
        System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                         "Type", "Title", "Resource ID", "Author", "Year", "Pages", "Status");
        System.out.println("=".repeat(84));
        
        for (Resource r : resources) {
            String type = r.getResourceType();
            if (type.length() > 10) type = type.substring(0, 7) + "...";
            
            String title = r.getTitle();
            if (title.length() > 20) title = title.substring(0, 17) + "...";
            
            String resourceId = r.getResourceId();
            if (resourceId.length() > 10) resourceId = resourceId.substring(0, 7) + "...";
            
            String author = r.getAuthor();
            if (author.length() > 13) author = author.substring(0, 10) + "...";
            
            String year = String.valueOf(r.getYear());
            
            String pages = "";
            if (r instanceof Book) {
                pages = String.valueOf(((Book) r).getPages());
            }
            if (pages.length() > 7) pages = pages.substring(0, 4) + "...";
            
            String status = r.isAvailable() ? "Available" : "On Loan";
            if (status.length() > 10) status = status.substring(0, 7) + "...";
            
            System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                             type, title, resourceId, author, year, pages, status);
        }
        System.out.println();
        
        String resourceId;
        while (true) {
            System.out.print("Enter Resource ID to update (or q to cancel): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Operation cancelled. Returning to menu...");
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("Invalid input! Resource ID cannot be empty.");
                continue;
            }
            
            resourceId = input;
            break;
        }
        
        Resource resource = library.getResource(resourceId);
        if (resource == null) {
            System.out.println("Resource not found!");
            return;
        }
       
        System.out.println("\n--- Updating Resource: " + resource.getTitle() + " ---");
        System.out.println("1. Update Title");
        System.out.println("2. Update Author");
        System.out.println("3. Update Publisher");
        System.out.println("4. Update Genre");
        System.out.println("5. Update Year");
        System.out.println("6. Cancel");
        System.out.print("Choose: ");
       
        int choice = getIntInput();
       
        switch (choice) {
            case 1:
                System.out.print("Enter new title: ");
                resource.setTitle(scanner.nextLine());
                System.out.println("Title updated successfully!");
                break;
            case 2:
                System.out.print("Enter new author: ");
                resource.setAuthor(scanner.nextLine());
                System.out.println("Author updated successfully!");
                break;
            case 3:
                System.out.print("Enter new publisher: ");
                resource.setPublisher(scanner.nextLine());
                System.out.println("Publisher updated successfully!");
                break;
            case 4:
                System.out.print("Enter new genre: ");
                resource.setGenre(scanner.nextLine());
                System.out.println("Genre updated successfully!");
                break;
            case 5:
                System.out.print("Enter new year: ");
                int year = getIntInput();
                resource.setYear(year);
                System.out.println("Year updated successfully!");
                break;
            default:
                System.out.println("Update cancelled.");
        }
    }
   
    private static void removeResource() {
        List<Resource> resources = library.getAllResources();
        if (resources.isEmpty()) {
            System.out.println("No resources available to remove.");
            return;
        }
        
        System.out.println("\n--- All Resources ---");
        System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                         "Type", "Title", "Resource ID", "Author", "Year", "Pages", "Status");
        System.out.println("=".repeat(84));
        
        for (Resource r : resources) {
            String type = r.getResourceType();
            if (type.length() > 10) type = type.substring(0, 7) + "...";
            
            String title = r.getTitle();
            if (title.length() > 20) title = title.substring(0, 17) + "...";
            
            String resourceId = r.getResourceId();
            if (resourceId.length() > 10) resourceId = resourceId.substring(0, 7) + "...";
            
            String author = r.getAuthor();
            if (author.length() > 13) author = author.substring(0, 10) + "...";
            
            String year = String.valueOf(r.getYear());
            
            String pages = "";
            if (r instanceof Book) {
                pages = String.valueOf(((Book) r).getPages());
            }
            if (pages.length() > 7) pages = pages.substring(0, 4) + "...";
            
            String status = r.isAvailable() ? "Available" : "On Loan";
            if (status.length() > 10) status = status.substring(0, 7) + "...";
            
            System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                             type, title, resourceId, author, year, pages, status);
        }
        System.out.println();
        
        String resourceId;
        while (true) {
            System.out.print("Enter Resource ID to remove (or q to cancel): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Operation cancelled. Returning to menu...");
                return;
            }
            
            if (input.isEmpty()) {
                System.out.println("Invalid input! Resource ID cannot be empty.");
                continue;
            }
            
            resourceId = input;
            break;
        }
        
        Resource resource = library.getResource(resourceId);
        if (resource == null) {
            System.out.println("Resource not found!");
            return;
        }
       
        System.out.println("Are you sure you want to delete: " + resource.getTitle() + "? (y/n)");
        String confirm = scanner.nextLine();
       
        if (confirm.equalsIgnoreCase("y")) {
            boolean success = library.removeResource(resourceId);
            if (success) {
                System.out.println("Resource removed successfully!");
            } else {
                System.out.println("Resource is currently borrowed and cannot be removed.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
   
    private static void viewAllResources() {
        List<Resource> resources = library.getAllResources();
       
        if (resources.isEmpty()) {
            System.out.println("No resources available.");
        } else {
            System.out.println("\n--- All Resources ---");
            System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                             "Type", "Title", "Resource ID", "Author", "Year", "Pages", "Status");
            System.out.println("=".repeat(84));
            
            for (Resource r : resources) {
                String type = r.getResourceType();
                if (type.length() > 10) type = type.substring(0, 7) + "...";
                
                String title = r.getTitle();
                if (title.length() > 20) title = title.substring(0, 17) + "...";
                
                String resourceId = r.getResourceId();
                if (resourceId.length() > 10) resourceId = resourceId.substring(0, 7) + "...";
                
                String author = r.getAuthor();
                if (author.length() > 13) author = author.substring(0, 10) + "...";
                
                String year = String.valueOf(r.getYear());
                
                String pages = "";
                if (r instanceof Book) {
                    pages = String.valueOf(((Book) r).getPages());
                }
                if (pages.length() > 7) pages = pages.substring(0, 4) + "...";
                
                String status = r.isAvailable() ? "Available" : "On Loan";
                if (status.length() > 10) status = status.substring(0, 7) + "...";
                
                System.out.printf("%-12s %-22s %-12s %-15s %-6s %-7s %-10s\n",
                                 type, title, resourceId, author, year, pages, status);
            }
            System.out.println();
        }
    }
   
    private static void viewAllUsers() {
        List<User> users = library.getAllUsers();
       
        if (users.isEmpty()) {
            System.out.println("No users registered.");
        } else {
            System.out.println("\n--- All Users ---");
            System.out.printf("%-4s %-20s %-13s %-12s %-15s %-28s %-15s\n",
                              "No.", "Name", "User Type", "ID", "Password", "Email", "Phone");
            System.out.println("---- -------------------- ------------- ------------ --------------- ---------------------------- ---------------");
            int count = 1;
            for (User u : users) {
                String displayType = u.getUserType().equals("Librarian") ? "Admin" : 
                                    (u.getUserType().equals("Public Member") ? "Public" : u.getUserType());
                String maskedPassword = maskPassword(u.getPassword());
                String name = u.getName();
                if (name.length() > 20) {
                    name = name.substring(0, 17) + "...";
                }
                String email = u.getEmail();
                if (email.length() > 28) {
                    email = email.substring(0, 25) + "...";
                }
                System.out.printf("%-4d %-20s %-13s %-12s %-15s %-28s %-15s\n",
                                  count, name, displayType, u.getUserId(), maskedPassword, email, u.getPhone());
                count++;
            }
        }
    }
   
    private static void processFinePayments() {
        List<Fine> unpaidFines = library.getAllUnpaidFines();
       
        if (unpaidFines.isEmpty()) {
            System.out.println("No unpaid fines.");
            return;
        }
       
        System.out.println("\n--- Unpaid Fines ---");
        for (Fine f : unpaidFines) {
            System.out.println(f);
        }
       
        System.out.print("Enter Fine ID to process payment: ");
        String fineId = scanner.nextLine();
       
        boolean success = library.processFinePayment(fineId);
        if (success) {
            System.out.println("Fine payment processed successfully!");
        } else {
            System.out.println("Fine not found or already paid.");
        }
    }
// Generating Report Operations   
    private static void generateReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Most Borrowed Resources Report");
        System.out.println("2. Revenue Report (Fines)");
        System.out.println("3. Overdue History Report");
        System.out.println("4. Active Users Report");
        System.out.print("Choose: ");
        int choice = getIntInput();
       
        switch (choice) {
            case 1:
                library.generateMostBorrowedReport();
                break;
            case 2:
                library.generateRevenueReport();
                break;
            case 3:
                library.generateOverdueReport();
                break;
            case 4:
                library.generateActiveUsersReport();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
   
    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Please enter a number: ");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private static String maskPassword(String password) {
        if (password == null || password.length() <= 4) {
            return password; 
        }
        String firstTwo = password.substring(0, 2);
        String lastTwo = password.substring(password.length() - 2);
        int middleLength = password.length() - 4;
        String middle = "*".repeat(middleLength);
        return firstTwo + middle + lastTwo;
    }
    private static void loadData() {
        try {
            library.loadData();
        } catch (Exception e) {
            System.out.println("No existing data found. Starting with empty system.");
            createDefaultData();
        }
    }
   
    private static void createDefaultData() {
        Librarian admin = new Librarian(DEFAULT_ADMIN_ID, "Admin User", "admin@library.com", "0123456789",
                                        "Library Office", DEFAULT_ADMIN_PASSWORD);
        library.addUser(admin);
       
        User faculty = new User(DEFAULT_FACULTY_ID, "Faculty User", "faculty@test.com", "0123456789",
                                 "Faculty Address", DEFAULT_FACULTY_PASSWORD, "Faculty", 10, 7);
        library.addUser(faculty);
       
        User student = new User(DEFAULT_STUDENT_ID, "Student User", "student@test.com", "0123456789",
                                "Student Address", DEFAULT_STUDENT_PASSWORD, "Student", 3, 7);
        library.addUser(student);
       
        User publicUser = new User(DEFAULT_PUBLIC_ID, "Public User", "public@test.com", "0123456789",
                                   "Public Address", DEFAULT_PUBLIC_PASSWORD, "Public Member", 2, 7);
        library.addUser(publicUser);
       
        User publicFineUser = new User(DEFAULT_PUBLIC_FINE_ID, "Public Fine User", "latepublic@test.com", "0123456789",
                                       "Public Address", DEFAULT_PUBLIC_FINE_PASSWORD, "Public Member", 2, 7);
        library.addUser(publicFineUser);
       
        Book book1 = new Book("Java Programming", "James Gosling", "Oracle Press", "Programming",
                              "978-0134685991", 2020, 1200);
        Book book2 = new Book("Clean Code", "Robert Martin", "Prentice Hall", "Software Engineering",
                              "978-0132350884", 2008, 464);
        Book book3 = new Book("Design Patterns", "Erich Gamma", "Addison-Wesley", "Programming",
                              "978-0201633610", 1994, 395);
        Book book4 = new Book("Effective Java", "Joshua Bloch", "Addison-Wesley", "Programming",
                              "978-0134685991", 2018, 416);
        library.addResource(book1);
        library.addResource(book2);
        library.addResource(book3);
        library.addResource(book4);
       
        Journal journal1 = new Journal("Nature", "Various", "Nature Publishing Group", "Science", "0028-0836", 2023, "620", "7974");
        Journal journal2 = new Journal("The Lancet", "Various", "Elsevier", "Medicine", "0140-6736", 2023, "402", "10396");
        Journal journal3 = new Journal("IEEE Transactions on Computers", "Various", "IEEE", "Computer Science", "0018-9340", 2023, "72", "1");
        DigitalResource digital1 = new DigitalResource("Introduction to Algorithms", "Cormen et al.", "MIT Press", "Computer Science", "978-0262033848", 2009, "PDF", 15.5);
        DigitalResource digital2 = new DigitalResource("Artificial Intelligence: A Modern Approach", "Russell and Norvig", "Pearson", "AI", "978-0136042594", 2020, "EPUB", 25.0);
        library.addResource(journal1);
        library.addResource(journal2);
        library.addResource(journal3);
        library.addResource(digital1);
        library.addResource(digital2);
       
        Transaction lateReturn = new Transaction(book4.getResourceId(), DEFAULT_PUBLIC_FINE_ID,
                                                 LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
        lateReturn.setReturnDate(LocalDate.now().minusDays(2));
        lateReturn.setReturned(true);
        lateReturn.setFineAmount(5.00);
        library.addTransaction(lateReturn);
       
        Fine fine = new Fine(lateReturn.getTransactionId(), DEFAULT_PUBLIC_FINE_ID, 5.00, "Overdue return");
        library.addFine(fine);
       
        System.out.println("Default resources created!");
    }
    private static void saveData() {
        try {
            library.saveData();
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }


    private static void printDefaultUserHint() {
        System.out.println("Available accounts:");
        List<User> users = library.getAllUsers();
        for (User user : users) {
            String displayType = user.getUserType().equals("Librarian") ? "Admin" : 
                                (user.getUserType().equals("Public Member") ? "Public" : user.getUserType());
            System.out.println(displayType + "\t- ID: " + user.getUserId() + ", Password: " + user.getPassword());
        }
    }
}
