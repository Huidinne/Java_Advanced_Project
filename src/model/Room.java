package model;

public class Room {
    private int id;
    private String name;
    private int capacity;
    private String location;
    private RoomStatus status;

    public Room() {
    }

    public Room(int id, String name, int capacity, String location, RoomStatus status) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
