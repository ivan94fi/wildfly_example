package dtos;

public class BookingDTO {

    private Long id;
    private String creationDate;
    private String bookingStart;
    private String bookingEnd;
    private Long structure;
    private Long user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getBookingStart() {
        return bookingStart;
    }

    public void setBookingStart(String bookingStart) {
        this.bookingStart = bookingStart;
    }

    public String getBookingEnd() {
        return bookingEnd;
    }

    public void setBookingEnd(String bookingEnd) {
        this.bookingEnd = bookingEnd;
    }

    public Long getStructure() {
        return structure;
    }

    public void setStructure(Long structure) {
        this.structure = structure;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

}
