package sources;


import java.sql.Date;
import java.util.Collection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan
 */
public interface HotelManager {

    public Collection<Room> findRoomWithGuest(Guest guest,Date date);

    public Collection<Guest> findGuestsfromRoom(Room room,Date date);

    public boolean addGuestToRoom(Room room, Guest guest,Date startDate, Date endDate);

    public boolean removeGuestFromRoom(Room room, Guest guest);

}
