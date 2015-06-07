package sources;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jan
 */
public class RoomManagerImpl implements RoomManager {

    final static Logger log = LoggerFactory.getLogger(RoomManagerImpl.class);
    private final DataSource dataSource;

    public RoomManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * method used to create new room in database
     *
     * @param room room to add to database
     * @throws ServiceFailureException
     */
    @Override
    public void createRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (room.getRoomID() != null) {
            throw new IllegalArgumentException("room is is already set.");
        }
        if (room.getCapacity() < 0) {
            throw new IllegalArgumentException("room has negative capacity.");
        }
        if (room.getRoomNumber() < 0) {
            throw new IllegalArgumentException("room has negative number");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO ROOM(ROOMNUMBER,CAPACITY, FLOOR) VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setInt(1, room.getRoomNumber());
                st.setInt(2, room.getCapacity());
                st.setInt(3, room.getFloor());
                int added = st.executeUpdate();
                if (added != 1) {
                    throw new ServiceFailureException("Inserted more rooms than 1");
                }
                ResultSet rs = st.getGeneratedKeys();
                room.setRoomID(getKey(rs, room));
            }
        } catch (SQLException ex) {
            log.error("Database connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }

    /**
     * method used to extract generated id from ResultSet
     *
     * @param rs ResultSet generated when adding room to database
     * @param room added room
     * @return generated ID
     * @throws SQLException
     */
    private Long getKey(ResultSet rs, Room room) throws SQLException {
        if (rs.next()) {
            if (rs.getMetaData().getColumnCount() != 1) {
                throw new SQLException("Internal Error: Generated key"
                        + "retriving failed when trying to insert room " + room
                        + " - wrong key fields count: " + rs.getMetaData().getColumnCount());
            }
            Long result = rs.getLong(1);
            if (rs.next()) {
                throw new SQLException("Internal Error: Generated key"
                        + "retriving failed when trying to insert room " + room
                        + " - more keys found");
            }
            return result;
        } else {
            throw new SQLException("Internal Error: Generated key"
                    + "retriving failed when trying to insert room " + room
                    + " - no key found");
        }
    }

    /**
     * used to delete Room from database
     *
     * @param room Room to delete
     * @throws ServiceFailureException
     */
    @Override
    public void deleteRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (room.getRoomID() == null) {
            throw new IllegalArgumentException("room has null id");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM ROOM WHERE ID=?")) {
                st.setLong(1, room.getRoomID());
                if (st.executeUpdate() != 1) {
                    throw new SQLException("didn't delete 1 entry.");
                }
            }
            room.setRoomID(null);
        } catch (SQLException ex) {
            log.error("Database connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }

    /**
     * used to update existing room in database into another
     *
     * @param room Room to be updated
     *
     */
    @Override
    public void updateRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("Room is null");
        }
        if (room.getRoomID() == null) {
            throw new NullPointerException("Room has null id");
        }
        if (room.getCapacity() < 0) {
            throw new IllegalArgumentException("room has negative capacity");
        }
        if (room.getRoomNumber() < 0) {
            throw new IllegalArgumentException("room has negative roomNumber");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE ROOM SET ROOMNUMBER=?,CAPACITY=?,FLOOR=? WHERE ID=?")) {
                st.setInt(1, room.getRoomNumber());
                st.setInt(2, room.getCapacity());
                st.setInt(3, room.getFloor());
                st.setLong(4, room.getRoomID());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("cannot update room " + room);
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("db connection problem", ex);
        }
    }

    /**
     * returns room from database with given id
     *
     * @param id id of room
     * @return room
     * @throws ServiceFailureException
     */
    @Override
    public Room getRoom(Long id) throws ServiceFailureException {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ID,ROOMNUMBER,CAPACITY,FLOOR FROM ROOM WHERE ID=? ")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Room foundRoom = resultSetToRoom(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException("returned more rooms than 1 with id=" + id);
                    }
                    return foundRoom;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }

    /**
     * Converts ResultSet to Room object
     *
     * @param rs ResultSet to be converted
     * @return converted room
     * @throws SQLException
     */
    public static Room resultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomID(rs.getLong("id"));
        room.setRoomNumber(rs.getInt("roomnumber"));
        room.setCapacity(rs.getInt("capacity"));
        room.setFloor(rs.getInt("floor"));
        return room;
    }

    /**
     * returns all rooms in database
     *
     * @return list of rooms
     */
    @Override
    public List<Room> getAllRooms() {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ID, ROOMNUMBER, CAPACITY, FLOOR FROM ROOM")) {
                ResultSet rs = st.executeQuery();
                List<Room> rooms = new ArrayList<>();
                while (rs.next()) {
                    rooms.add(resultSetToRoom(rs));
                }
                return rooms;
            }
        } catch (SQLException ex) {
            log.error("Database connection problem");
            throw new ServiceFailureException("Database connection problem", ex);
        }
    }

}
