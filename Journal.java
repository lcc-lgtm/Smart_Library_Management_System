public class Journal extends Resource {
    private static final long serialVersionUID = 1L;
    private String volume;
    private String issue;
   
    public Journal(String title, String author, String publisher, String genre,
                   String isbn, int year, String volume, String issue) {
        super(title, author, publisher, genre, isbn, year);
        this.volume = volume;
        this.issue = issue;
    }
   
    public String getVolume() { return volume; }
    public String getIssue() { return issue; }
   
    @Override
    public String getResourceType() { return "Journal"; }
   
    @Override
    public String toString() {
        return super.toString() + String.format(" | Vol: %s, Issue: %s", volume, issue);
    }
}
