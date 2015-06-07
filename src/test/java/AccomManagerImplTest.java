/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import sources.GuestManagerImpl;
import sources.Room;
import sources.Guest;
import sources.GuestManager;
import sources.Accomodation;
import sources.RoomManager;
import sources.AccomManager;
import sources.AccomManagerImpl;
import sources.RoomManagerImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.Date;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jan
 */
public class AccomManagerImplTest {

    private AccomManager accomManager;
    private RoomManager roomManager;
    private GuestManager guestManager;

    private DataSource dataSource;

    public AccomManagerImplTest() {
    }

    @Before
    public void setUp() throws SQLException {

        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:AccomManagerTest;create=true");
        this.dataSource = bds;
        //create new empty table before every test
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE ACCOMODATION("
                    + "ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "GUESTID INTEGER NOT NULL,"
                    + "ROOMID INTEGER NOT NULL,"
                    + "STARTDATE DATE,"
                    + "ENDDATE DATE)").executeUpdate();
        }
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE GUEST("
                    + "ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "NAME VARCHAR(50) NOT NULL,"
                    + "CREDITCARD VARCHAR(50))").executeUpdate();
        }
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE ROOM("
                    + "ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "ROOMNUMBER INT,"
                    + "CAPACITY INT NOT NULL,"
                    + "FLOOR INT NOT NULL)").executeUpdate();
        }
        accomManager = new AccomManagerImpl(bds);
        roomManager = new RoomManagerImpl(bds);
        guestManager = new GuestManagerImpl(bds);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DROP TABLE ACCOMODATION").executeUpdate();
        }
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DROP TABLE GUEST").executeUpdate();
        }
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DROP TABLE ROOM").executeUpdate();
        }
    }

    /**
     * Test of createAccom method, of class AccomManagerImpl.
     */
    @Test
    public void testCreateAccom() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();

        Accomodation accom = newAccom(guestID, roomID, date("2010-1-1"), date("2012-1-1"));

        accomManager.createAccom(accom);
        assertNotNull(accom.getAccomId());
        assertEquals(accom, accomManager.getAccom(accom.getAccomId()));
    }

    @Test
    public void testCreateAccom_WrongArgs() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        long roomID = room.getRoomID();

        try {
            accomManager.createAccom(null);
            fail("argument cant be null");
        } catch (Exception ex) {
            //ok
        }

        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");
        Accomodation accom = newAccom(guestID, roomID, endDate, startDate);
        try {
            accomManager.createAccom(accom);
            fail("endDate is before startDate");
        } catch (Exception ex) {
            //ok
        }

        accom = newAccom(null, roomID, endDate, startDate);
        try {
            accomManager.createAccom(accom);
            fail("guestID cant be null");
        } catch (Exception ex) {
            //ok
        }

        accom = newAccom(guestID, null, endDate, startDate);
        try {
            accomManager.createAccom(accom);
            fail("roomID cant be null");
        } catch (Exception ex) {
            //ok
        }

        accom = newAccom(guestID, roomID, null, endDate);
        try {
            accomManager.createAccom(accom);
            fail("startDate cant be null");
        } catch (Exception ex) {
            //ok
        }

        accom = newAccom(guestID, roomID, startDate, null);
        try {
            accomManager.createAccom(accom);
            fail("endDate cant be null");
        } catch (Exception ex) {
            //ok
        }
    }

    /**
     * Test of updateAccom method, of class AccomManagerImpl.
     */
    @Test
    public void testUpdateAccom() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();

        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");

        Accomodation accom = newAccom(guestID, roomID, startDate, endDate);
        accomManager.createAccom(accom);
        Long accomId = accom.getAccomId();

        Guest g2 = newGuest("Roman", "1456");
        guestManager.createGuest(g2);
        accom.setGuestId(g2.getGuestID());
        accomManager.updateAccom(accom);
        assertEquals(accom, accomManager.getAccom(accomId));

        Room r2 = newRoom(15, 5);
        roomManager.createRoom(r2);
        accom.setRoomId(r2.getRoomID());
        accomManager.updateAccom(accom);
        assertEquals(accom, accomManager.getAccom(accomId));

        startDate = date("2010-5-1");
        accom.setStartDate(startDate);
        accomManager.updateAccom(accom);
        assertEquals(accom, accomManager.getAccom(accomId));

        endDate = date("2012-5-5");
        accom.setEndDate(endDate);
        accomManager.updateAccom(accom);
        assertEquals(accom, accomManager.getAccom(accomId));
    }

    @Test
    public void testUpdateAccom_WrongArgs() {
        try {
            accomManager.updateAccom(null);
            fail("cant update null");
        } catch (IllegalArgumentException ex) {
            //ok
        }

        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();
        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");
        Accomodation accom = newAccom(guestID, roomID, startDate, endDate);
        accomManager.createAccom(accom);
        Long accomId = accom.getAccomId();
        accom.setAccomId(null);
        try {
            accomManager.updateAccom(accom);
            fail("cant update accomm with null id");
        } catch (IllegalArgumentException ex) {
            //ok
        }
        accom.setAccomId(accomId);

        accom.setGuestId(null);
        try {
            accomManager.updateAccom(accom);
            fail("cant update accomm with null guestId");
        } catch (IllegalArgumentException ex) {
            //ok
        }
        accom.setGuestId(guestID);

        accom.setRoomId(null);
        try {
            accomManager.updateAccom(accom);
        } catch (IllegalArgumentException ex) {
            //ok
        }
        accom.setRoomId(roomID);

        accom.setStartDate(endDate);
        accom.setEndDate(startDate);
        try {
            accomManager.updateAccom(accom);
            fail("endDate cant be before startDate");
        } catch (IllegalArgumentException ex) {
            //ok
        }
    }

    /**
     * Test of deleteAccom method, of class AccomManagerImpl.
     */
    @Test
    public void testDeleteAccom() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();
        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");
        Accomodation accom = newAccom(guestID, roomID, startDate, endDate);
        accomManager.createAccom(accom);

        assertNotNull(accom.getAccomId());
        accomManager.deleteAccom(accom);
        assertNull(accom.getAccomId());
    }

    /**
     * Test of getAccom method, of class AccomManagerImpl.
     */
    @Test
    public void testGetAccom() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();
        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");
        Accomodation accom = newAccom(guestID, roomID, startDate, endDate);
        accomManager.createAccom(accom);
        Long accomId = accom.getAccomId();

        assertEquals(accom, accomManager.getAccom(accomId));

        try {
            accomManager.getAccom(null);
            fail("should not get null");
        } catch (IllegalArgumentException ex) {
            //ok
        }
    }

    /**
     * Test of getAllAccoms method, of class AccomManagerImpl.
     */
    @Test
    public void testGetAllAccoms() {
        Guest guest = newGuest("Pavel", "1234");
        Room room = newRoom(10, 3);
        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        Long guestID = guest.getGuestID();
        Long roomID = room.getRoomID();

        Date startDate = date("2010-1-1");
        Date endDate = date("2012-1-1");
        Accomodation accom = newAccom(guestID, roomID, startDate, endDate);
        accomManager.createAccom(accom);
        Long accomId = accom.getAccomId();

        Room room2 = newRoom(5, 5);
        roomManager.createRoom(room2);
        Accomodation accom2 = newAccom(guestID, room2.getRoomID(), startDate, endDate);
        accomManager.createAccom(accom2);
        Collection result = accomManager.getAllAccoms();
        assertTrue(result.contains(accom));
        assertTrue(result.contains(accom2));
        assertEquals(result.size(), 2);
    }

    private Accomodation newAccom(Long guestID, Long roomID, Date startDate, Date endDate) {
        Accomodation accom = new Accomodation();
        accom.setAccomId(null);
        accom.setGuestId(guestID);
        accom.setRoomId(roomID);
        accom.setStartDate(startDate);
        accom.setEndDate(endDate);

        return accom;
    }

    private Guest newGuest(String name, String creditCard) {
        Guest guest = new Guest();
        guest.setGuestID(null);
        guest.setName(name);
        guest.setCreditCard(creditCard);

        return guest;
    }

    private Room newRoom(int capacity, int floor) {
        Room room = new Room();
        room.setRoomID(null);
        room.setCapacity(capacity);
        room.setFloor(floor);

        return room;
    }

    private Date date(String date) {
        return Date.valueOf(date);
    }

}
