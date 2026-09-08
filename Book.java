public class Book extends Resource {
    private static final long serialVersionUID = 1L;
    private int pages;
   
    public Book(String title, String author, String publisher, String genre,
                String isbn, int year, int pages) {
        super(title, author, publisher, genre, isbn, year);
        this.pages = pages;
    }
   
    public int getPages() { return pages; }
   
    @Override
    public String getResourceType() { return "Book"; }
   
    @Override
    public String toString() {
        return super.toString() + String.format(" | Pages: %d", pages);
    }
}
