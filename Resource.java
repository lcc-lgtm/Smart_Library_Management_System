import java.io.Serializable;  // as a marker interface
import java.util.LinkedList;  // as a queue type collections container
import java.util.UUID;  // as auto generate unique id

public abstract class Resource implements Serializable {
    private static final long serialVersionUID = 1L;
   
    protected String resourceId;
    protected String title;
    protected String author;
    protected String publisher;
    protected String genre;
    protected String isbn;
    protected int year;
    protected boolean available;
    protected LinkedList<String> reservationQueue;
   
    public Resource(String title, String author, String publisher, String genre,
                    String isbn, int year) {
        this.resourceId = "R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.isbn = isbn;
        this.year = year;
        this.available = true;
        this.reservationQueue = new LinkedList<>();
    }
   
    public String getResourceId() { return resourceId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getGenre() { return genre; }
    public String getIsbn() { return isbn; }
    public int getYear() { return year; }
    public boolean isAvailable() { return available; }
   
    public void setAvailable(boolean available) { this.available = available; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setYear(int year) { this.year = year; }
   
    public void addToReservationQueue(String userId) { reservationQueue.add(userId); }
    public String getNextReservation() { return reservationQueue.poll(); }
    public String peekNextReservation() { return reservationQueue.peek(); }
    public boolean hasReservations() { return !reservationQueue.isEmpty(); }
    public boolean hasReservation(String userId) { return reservationQueue.contains(userId); }
   
    public abstract String getResourceType();
   
    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%d) - %s",
                             getResourceType(), title, author, year,
                             available ? "Available" : "On Loan");
    }
}
