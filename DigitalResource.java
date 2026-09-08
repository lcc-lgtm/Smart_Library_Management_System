public class DigitalResource extends Resource { 
    private static final long serialVersionUID = 1L;
    private String fileFormat;
    private double fileSize;
   
    public DigitalResource(String title, String author, String publisher, String genre,
                           String isbn, int year, String fileFormat, double fileSize) {
        super(title, author, publisher, genre, isbn, year);
        this.fileFormat = fileFormat;
        this.fileSize = fileSize;
    }
   
    public String getFileFormat() { return fileFormat; }
    public double getFileSize() { return fileSize; }
   
    @Override
    public String getResourceType() { return "Digital Resource"; }
   
    @Override
    public String toString() {
        return super.toString() + String.format(" | Format: %s, Size: %.1f MB", fileFormat, fileSize);
    }
}
