package sources;


import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan
 */
public class Room {

    private Long roomID;
    private int roomNumber;
    private int capacity;
    private int floor;

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.roomID);
        hash = 83 * hash + this.roomNumber;
        hash = 83 * hash + this.capacity;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (!Objects.equals(this.roomID, other.roomID)) {
            return false;
        }
        if (this.roomNumber != other.roomNumber) {
            return false;
        }
        if (this.capacity != other.capacity) {
            return false;
        }
        if (this.floor != other.floor) {
            return false;
        }
        return true;
    }

   
    
    
}
