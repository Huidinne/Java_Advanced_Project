package model;

public class BookingDetail {
    private int id;
    private int bookingId;
    private DetailType type;
    private int refId;
    private int quantity;

    public BookingDetail() {}

    public BookingDetail(int id, int bookingId, DetailType type, int refId, int quantity) {
        this.id = id;
        this.bookingId = bookingId;
        this.type = type;
        this.refId = refId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public DetailType getType() {
        return type;
    }

    public void setType(DetailType type) {
        this.type = type;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
