package sources;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
public class HotelManagerImpl implements HotelManager {

    final static Logger log = LoggerFactory.getLogger(RoomManagerImpl.class);
    private final DataSource dataSource;

    public HotelManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void checkDataSource() throws ServiceFailureException {
        if (this.dataSource == null) {
            throw new ServiceFailureException("dataSource is not set");
        }
    }

    @Override
    public Collection<Room> findRoomWithGuest(Guest guest, Date date) {
        checkDataSource();
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (guest.getGuestID() == null) {
            throw new IllegalArgumentException("guestID is null");
        }
        RoomManager roomManager = new RoomManagerImpl(dataSource);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ROOMID FROM ACCOMODATION WHERE GUESTID = ? AND STARTDATE<=? AND ENDDATE >=?")) {
                st.setLong(1, guest.getGuestID());
                st.setDate(2, date);
                st.setDate(3, date);
                ResultSet rs = st.executeQuery();
                List<Room> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(roomManager.getRoom(rs.getLong("ROOMID")));
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("database problem", ex);
        }
    }

    @Override
    public Collection<Guest> findGuestsfromRoom(Room room, Date date) {
        checkDataSource();
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (room.getRoomID() == null) {
            throw new IllegalArgumentException("roomID is null");
        }
        GuestManager guestManager = new GuestManagerImpl(dataSource);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT GUESTID FROM ACCOMODATION WHERE ROOMID = ? AND STARTDATE<=? AND ENDDATE >=?")) {
                st.setLong(1, room.getRoomID());
                st.setDate(2, date);
                st.setDate(3, date);
                ResultSet rs = st.executeQuery();
                List<Guest> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(guestManager.getGuest(rs.getLong("GUESTID")));
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("database problem", ex);
        }

    }

    @Override
    public boolean addGuestToRoom(Room room, Guest guest, Date startDate, Date endDate) {
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate is null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate is null");
        }
        if (this.findGuestsfromRoom(room, startDate).size() >= room.getCapacity()) {
            return false;//capacity is full
        }

        AccomManager accomManager = new AccomManagerImpl(dataSource);

        Accomodation accom = new Accomodation();
        accom.setGuestId(guest.getGuestID());
        accom.setRoomId(room.getRoomID());
        accom.setAccomId(null);
        accom.setStartDate(startDate);
        accom.setEndDate(endDate);
        AccomManagerImpl.validate(accom);
        try {
            accomManager.createAccom(accom);
        } catch (ServiceFailureException ex) {
            throw new ServiceFailureException("database problem", ex);
        }
        return true;
    }

    @Override
    public boolean removeGuestFromRoom(Room room, Guest guest) {
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (room.getRoomID() == null) {
            throw new IllegalArgumentException("roomID is null");
        }
        if (guest.getGuestID() == null) {
            throw new IllegalArgumentException("guestID is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ID FROM ACCOMODATION WHERE GUESTID = ? AND ROOMID = ?")) {
                st.setLong(1, guest.getGuestID());
                st.setLong(2, room.getRoomID());
                ResultSet rs = st.executeQuery();
                AccomManager accomManager = new AccomManagerImpl(dataSource);
                if (rs.next()) {
                    accomManager.deleteAccom(accomManager.getAccom(rs.getLong(1)));
                    return true;
                }
                return false;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("database problem", ex);
        }
    }

    private Date date(String date) {
        return Date.valueOf(date);
    }
}
