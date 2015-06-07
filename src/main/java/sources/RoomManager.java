package sources;


import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jan
 */
public interface RoomManager {
    public void createRoom(Room room) throws ServiceFailureException;
    public void deleteRoom(Room room) throws ServiceFailureException;
    public void updateRoom(Room room)throws ServiceFailureException;
    public Room getRoom(Long id) throws ServiceFailureException;
    public List<Room> getAllRooms() throws ServiceFailureException;
}
