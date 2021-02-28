package model;

public class Room {

    private int roomID;
    private boolean isAvailable;
    private boolean isBooked;

    public Room(int roomID, boolean isAvailable, boolean isBooked) {
        this.roomID = roomID;
        this.isAvailable = isAvailable;
        this.isBooked = isBooked;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @Override
    public String toString() {
        return "Room #"+ roomID +
                (isAvailable ? " is available " : " is not available") +
                (isBooked ? " but is booked " : " and is not booked");
    }
}
