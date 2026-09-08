import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Library Core Class
public class Library implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private HashMap<String, User> users;
    private HashMap<String, Resource> resources;
    private HashMap<String, Transaction> transactions;
    private HashMap<String, Fine> fines;
   
    private static final String DATA_DIR = "library_data/";
    private static final double DAILY_FINE_RATE = 1.0;
   
    public Library() {
        users = new HashMap<>();
        resources = new HashMap<>();
        transactions = new HashMap<>();
        fines = new HashMap<>();
    }
   
    // User Management
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }
   
    public User getUser(String userId) {
        return users.get(userId);
    }
   
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
   
    public boolean deleteUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            return false;
        }
       
        for (Transaction t : transactions.values()) {
            if (t.getUserId().equals(userId) && !t.isReturned()) {
                return false;
            }
        }
       
        users.remove(userId);
        return true;
    }
   
    public User authenticateUser(String userId, String password) {
        User user = users.get(userId);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
   
    // Resource Management
    public void addResource(Resource resource) {
        resources.put(resource.getResourceId(), resource);
    }
   
    public boolean removeResource(String resourceId) {
        Resource resource = resources.get(resourceId);
        if (resource != null && resource.isAvailable()) {
            resources.remove(resourceId);
            return true;
        }
        return false;
    }
   
    public Resource getResource(String resourceId) {
        return resources.get(resourceId);
    }
   
    public List<Resource> getAllResources() {
        return new ArrayList<>(resources.values());
    }
   
    public List<Resource> searchByTitle(String title) {
        List<Resource> results = new ArrayList<>();
        for (Resource r : resources.values()) {
            if (r.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(r);
            }
        }
        return results;
    }
   
    public List<Resource> searchByAuthor(String author) {
        List<Resource> results = new ArrayList<>();
        for (Resource r : resources.values()) {
            if (r.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                results.add(r);
            }
        }
        return results;
    }
   
    public List<Resource> searchByGenre(String genre) {
        List<Resource> results = new ArrayList<>();
        for (Resource r : resources.values()) {
            if (r.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                results.add(r);
            }
        }
        return results;
    }
   
    public List<Resource> searchByISBN(String isbn) {
        List<Resource> results = new ArrayList<>();
        for (Resource r : resources.values()) {
            if (r.getIsbn().equals(isbn)) {
                results.add(r);
            }
        }
        return results;
    }
   
    public List<Resource> searchResources(String searchTerm) {
        String term = searchTerm.toLowerCase();
        List<Resource> results = new ArrayList<>();
        for (Resource r : resources.values()) {
            if (matchesSearchTerm(r, term)) {
                results.add(r);
            }
        }
        return results;
    }

    private boolean matchesSearchTerm(Resource r, String term) {
        return r.getResourceId().toLowerCase().contains(term)
            || r.getTitle().toLowerCase().contains(term)
            || r.getAuthor().toLowerCase().contains(term)
            || String.valueOf(r.getYear()).contains(term);
    }

    // Borrowing & Returning the Resources methods
    public Transaction borrowResource(User user, String resourceId) throws Exception {
        Resource resource = resources.get(resourceId);
       
        if (resource == null) {
            throw new Exception("Resource not found");
        }
       
        if (!resource.isAvailable()) {
            throw new Exception("Resource is not available." + " Since there is someone borrowing it" + "\nYou can reserve it if you want.");
        }
       
        if (resource.hasReservations() && !user.getUserId().equals(resource.peekNextReservation())) {
            throw new Exception("Resource is reserved for another user");
        }
       
        int borrowedCount = 0;
        for (Transaction t : transactions.values()) {
            if (t.getUserId().equals(user.getUserId()) && !t.isReturned()) {
                borrowedCount++;
            }
        }
       
        if (borrowedCount >= user.getBorrowLimit()) {
            throw new Exception("Borrowing limit reached");
        }
       
        for (Fine f : fines.values()) {
            if (f.getUserId().equals(user.getUserId()) && !f.isPaid()) {
                throw new Exception("You have unpaid fines. Please clear them first.");
            }
        }
       
        LocalDate dueDate = LocalDate.now().plusDays(user.getLoanDuration());
        Transaction transaction = new Transaction(resourceId, user.getUserId(), LocalDate.now(), dueDate);
       
        if (resource.hasReservations() && user.getUserId().equals(resource.peekNextReservation())) {
            resource.getNextReservation();
        }
        resource.setAvailable(false);
        transactions.put(transaction.getTransactionId(), transaction);
       
        return transaction;
    }
   
    public Transaction returnResource(User user, String resourceId) throws Exception {
        Transaction activeTransaction = null;
       
        for (Transaction t : transactions.values()) {
            if (t.getResourceId().equals(resourceId) &&
                t.getUserId().equals(user.getUserId()) &&
                !t.isReturned()) {
                activeTransaction = t;
                break;
            }
        }
       
        if (activeTransaction == null) {
            throw new Exception("No active borrowing record found for this resource");
        }
       
        Resource resource = resources.get(resourceId);
        resource.setAvailable(true);
        activeTransaction.setReturnDate(LocalDate.now());
        activeTransaction.setReturned(true);
       
        if (activeTransaction.getDueDate().isBefore(LocalDate.now())) {
            long daysOverdue = ChronoUnit.DAYS.between(activeTransaction.getDueDate(), LocalDate.now());
            double fineAmount = daysOverdue * DAILY_FINE_RATE;
            activeTransaction.setFineAmount(fineAmount);
           
            if (fineAmount > 0) {
                Fine fine = new Fine(activeTransaction.getTransactionId(), user.getUserId(),
                                     fineAmount, "Overdue return");
                fines.put(fine.getFineId(), fine);
            }
        }
       
        return activeTransaction;
    }
   
    public List<Transaction> getUserBorrowedItems(String userId) {
        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction t : transactions.values()) {
            if (t.getUserId().equals(userId)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }
   
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }
   
    public void addFine(Fine fine) {
        fines.put(fine.getFineId(), fine);
    }
   
    public boolean reserveResource(User user, String resourceId) {
        Resource resource = resources.get(resourceId);
        if (resource != null && !resource.isAvailable()) {
            if (resource.hasReservation(user.getUserId())) {
                return false;
            }
            resource.addToReservationQueue(user.getUserId());
            return true;
        }
        return false;
    }
   
    // Fine Management
    public List<Fine> getUserFines(String userId) {
        List<Fine> userFines = new ArrayList<>();
        for (Fine f : fines.values()) {
            if (f.getUserId().equals(userId)) {
                userFines.add(f);
            }
        }
        return userFines;
    }
   
    public void payFines(String userId) {
        for (Fine f : fines.values()) {
            if (f.getUserId().equals(userId) && !f.isPaid()) {
                f.setPaid(true);
            }
        }
    }
   
    public boolean processFinePayment(String fineId) {
        Fine fine = fines.get(fineId);
        if (fine != null && !fine.isPaid()) {
            fine.setPaid(true);
            return true;
        }
        return false;
    }
   
    public List<Fine> getAllUnpaidFines() {
        List<Fine> unpaidFines = new ArrayList<>();
        for (Fine f : fines.values()) {
            if (!f.isPaid()) {
                unpaidFines.add(f);
            }
        }
        return unpaidFines;
    }
   
    // Reports Generating Methods
    public void generateMostBorrowedReport() {
        Map<String, Integer> borrowCount = new HashMap<>();
       
        for (Transaction t : transactions.values()) {
            String resourceId = t.getResourceId();
            borrowCount.put(resourceId, borrowCount.getOrDefault(resourceId, 0) + 1);
        }
       
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(borrowCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
       
        System.out.println("\n=== MOST BORROWED RESOURCES ===");
        System.out.printf("%-15s %-30s %-10s\n", "Resource ID", "Title", "Borrow Count");
        System.out.println("------------------------------------------------");
       
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            Resource r = resources.get(entry.getKey());
            if (r != null) {
                String title = r.getTitle();
                if (title.length() > 30) title = title.substring(0, 27) + "...";
                System.out.printf("%-15s %-30s %-10d\n",
                                  entry.getKey(), title, entry.getValue());
            }
        }
    }
   
    public void generateRevenueReport() {
        double totalRevenue = 0;
        int paidFines = 0;
       
        for (Fine f : fines.values()) {
            if (f.isPaid()) {
                totalRevenue += f.getAmount();
                paidFines++;
            }
        }
       
        double outstanding = 0;
        for (Fine f : fines.values()) {
            if (!f.isPaid()) {
                outstanding += f.getAmount();
            }
        }
       
        System.out.println("\n=== REVENUE REPORT ===");
        System.out.printf("Total fines paid: RM %.2f\n", totalRevenue);
        System.out.println("Number of fines paid: " + paidFines);
        System.out.printf("Outstanding fines: RM %.2f\n", outstanding);
    }
   
    public void generateOverdueReport() {
        System.out.println("\n=== OVERDUE ITEMS REPORT ===");
        System.out.printf("%-15s %-15s %-15s %-20s %-20s %-15s\n", "Transaction ID", "User ID", "Resource ID", "Due Date", "Return Date", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------");
       
        boolean hasOverdue = false;
        for (Transaction t : transactions.values()) {
            LocalDate actualReturnDate = t.isReturned() ? t.getReturnDate() : LocalDate.now();
            if (actualReturnDate.isAfter(t.getDueDate())) {
                String status = t.isReturned() ? "Returned Late" : "Currently Overdue";
                System.out.printf("%-15s %-15s %-15s %-20s %-20s %-15s\n",
                                  t.getTransactionId(), t.getUserId(), t.getResourceId(),
                                  t.getDueDate().toString(), 
                                  t.isReturned() ? t.getReturnDate().toString() : "Not Returned",
                                  status);
                hasOverdue = true;
            }
        }
       
        if (!hasOverdue) {
            System.out.println("No overdue history found.");
        }
    }
   
    public void generateActiveUsersReport() {
        Map<String, Integer> userActivity = new HashMap<>();
       
        for (Transaction t : transactions.values()) {
            userActivity.put(t.getUserId(), userActivity.getOrDefault(t.getUserId(), 0) + 1);
        }
       
        System.out.println("\n=== ACTIVE USERS REPORT ===");
        System.out.printf("%-15s %-20s %-10s\n", "User ID", "Name", "Borrow Count");
        System.out.println("----------------------------------------");
       
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(userActivity.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
       
        for (Map.Entry<String, Integer> entry : sorted) {
            User u = users.get(entry.getKey());
            if (u != null) {
                String name = u.getName();
                if (name.length() > 20) name = name.substring(0, 17) + "...";
                System.out.printf("%-15s %-20s %-10d\n",
                                  entry.getKey(), name, entry.getValue());
            }
        }
    }
   
    // Data Persistence Methods
    public void saveData() throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
       
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "users.dat"))) {
            oos.writeObject(new ArrayList<>(users.values()));
        }
       
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "resources.dat"))) {
            oos.writeObject(new ArrayList<>(resources.values()));
        }
       
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "transactions.dat"))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
        }
       
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + "fines.dat"))) {
            oos.writeObject(new ArrayList<>(fines.values()));
        }
    }
   
    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            throw new FileNotFoundException("Data directory not found");
        }
       
        File usersFile = new File(DATA_DIR + "users.dat");
        if (usersFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usersFile))) {
                List<User> userList = (List<User>) ois.readObject();
                for (User u : userList) {
                    users.put(u.getUserId(), u);
                }
            }
        }
       
        File resourcesFile = new File(DATA_DIR + "resources.dat");
        if (resourcesFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resourcesFile))) {
                List<Resource> resourceList = (List<Resource>) ois.readObject();
                for (Resource r : resourceList) {
                    resources.put(r.getResourceId(), r);
                }
            }
        }

        // create a file object
        File transactionsFile = new File(DATA_DIR + "transactions.dat");
        if (transactionsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(transactionsFile))) {
                List<Transaction> transactionList = (List<Transaction>) ois.readObject();
                for (Transaction t : transactionList) {
                    transactions.put(t.getTransactionId(), t);
                }
            }
        }
       
        File finesFile = new File(DATA_DIR + "fines.dat");
        if (finesFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(finesFile))) {
                List<Fine> fineList = (List<Fine>) ois.readObject();
                for (Fine f : fineList) {
                    fines.put(f.getFineId(), f);
                }
            }
        }
    }
}
