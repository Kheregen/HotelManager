/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import sources.GuestManagerImpl;
import sources.Room;
import sources.HotelManagerImpl;
import sources.Guest;
import sources.GuestManager;
import sources.Accomodation;
import sources.RoomManager;
import sources.AccomManager;
import sources.HotelManager;
import sources.AccomManagerImpl;
import sources.RoomManagerImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 *
 */
public class HotelManagerImplTest {

    private HotelManager hotelManager;
    private GuestManager guestManager;
    private RoomManager roomManager;
    private AccomManager accomManager;

    private DataSource dataSource;

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
        hotelManager = new HotelManagerImpl(bds);

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
     * Test of findRoomWithGuest method, of class HotelManagerImpl.
     */
    @Test
    public void testFindRoomWithGuest() {
        Guest guest = newGuest("Adam", "4587");
        guestManager.createGuest(guest);
        Room room = newRoom(1, 10, 2);
        roomManager.createRoom(room);
        Accomodation accom = newAccom(guest.getGuestID(), room.getRoomID(), date("2010-1-1"), date("2012-1-1"));
        accomManager.createAccom(accom);
        Collection<Room> result = (ArrayList)hotelManager.findRoomWithGuest(guest,date("2010-1-1"));
        Collection<Room> result2 = new ArrayList();
        result2.add(room);
        assertEquals(result, result2);

        Room room2 = newRoom(3, 15, 3);
        roomManager.createRoom(room2);
        Accomodation accom2 = newAccom(guest.getGuestID(), room2.getRoomID(), date("2010-2-3"), date("2013-3-3"));
        accomManager.createAccom(accom2);
        Collection<Room> result3 = (ArrayList)hotelManager.findRoomWithGuest(guest,date("2011-1-1"));
        result2.add(room2);
        assertEquals(result3, result2);
    }

    @Test
    public void testFindRoomWithGuest_WrongArg() {
        try {
            hotelManager.findRoomWithGuest(null,date("2010-2-3"));
            fail();

        } catch (IllegalArgumentException ex) {
            //OK
        }

        //Guest with null ID
        Guest guest = newGuest("Lojzo", "666");
        try {
            hotelManager.findRoomWithGuest(guest,date("2010-2-3"));
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        try {
            hotelManager.findRoomWithGuest(guest,null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void testAddGuestToRoom() {
        Guest guest1 = newGuest("Martin", "23044");
        Guest guest2 = newGuest("Andrej", "269424");
        Guest guest3 = newGuest("Peter", "276525");
        guestManager.createGuest(guest1);
        guestManager.createGuest(guest2);
        guestManager.createGuest(guest3);
        Room room = newRoom(2, 2, 2);
        roomManager.createRoom(room);
        Date startDate = date("2014-01-01");
        Date endDate = date("2016-01-01");

        boolean result = hotelManager.addGuestToRoom(room, guest1, startDate, endDate);

        assertEquals(true, result);
        result = hotelManager.addGuestToRoom(room, guest2, startDate, endDate);
        assertEquals(true, result);
        result = hotelManager.addGuestToRoom(room, guest3, startDate, endDate);
        assertEquals(false, result); // full capacity
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGuestToRoom_WrongArg() {
        Guest guest = newGuest("Andrea", "5808888");
        Room room = newRoom(3, 2, 4);
        Date startDate = date("2001-01-01");
        Date endDate = date("2002-01-01");

        hotelManager.addGuestToRoom(null, guest, startDate, endDate);
        hotelManager.addGuestToRoom(room, null, startDate, endDate);
        hotelManager.addGuestToRoom(room, guest, null, endDate);
        hotelManager.addGuestToRoom(room, guest, startDate, null);
    }

    @Test
    public void testRemoveGuestFromRoom() {
        Guest guest = newGuest("Lenka", "64851");
        guestManager.createGuest(guest);
        Room room = newRoom(5, 12, 1);
        roomManager.createRoom(room);
        Accomodation accom = newAccom(guest.getGuestID(), room.getRoomID(), date("2010-2-2"), date("2013-3-3"));

        hotelManager.addGuestToRoom(room, guest, date("2010-2-2"), date("2013-3-3"));
        assertEquals(true, hotelManager.removeGuestFromRoom(room, guest));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGuestFromRoom_WrongArg() {
        Guest guest = newGuest("Silvia", "451");
        guestManager.createGuest(guest);
        Room room = newRoom(5, 10, 3);
        roomManager.createRoom(room);
        hotelManager.addGuestToRoom(room, guest, date("2010-2-2"), date("2013-3-3"));

        hotelManager.removeGuestFromRoom(null, guest);
        hotelManager.removeGuestFromRoom(room, null);
        hotelManager.removeGuestFromRoom(null, null);
    }

    @Test
    public void testFindGuestsFromRoom() {
        Room room = newRoom(2, 4, 3);
        roomManager.createRoom(room);
        Guest guest1 = newGuest("Anna", "1113552");
        Guest guest2 = newGuest("Jana", "222454532");
        Guest guest3 = newGuest("Hanka", "33345254");
        guestManager.createGuest(guest1);
        guestManager.createGuest(guest2);
        guestManager.createGuest(guest3);
        Date startDate = date("2014-2-2");
        hotelManager.addGuestToRoom(room, guest1, startDate, date("2017-3-3"));
        hotelManager.addGuestToRoom(room, guest2, startDate, date("2018-3-3"));
        hotelManager.addGuestToRoom(room, guest3, startDate, date("2019-3-3"));

        Collection<Guest> result = new ArrayList<>();
        result = hotelManager.findGuestsfromRoom(room,startDate);

        assertTrue(result.contains(guest1));
        assertTrue(result.contains(guest2));
        assertTrue(result.contains(guest3));
        assertTrue(result.remove(guest1));
        assertTrue(result.remove(guest2));
        assertTrue(result.remove(guest3));
        assertTrue(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindGuestsFromRoom_WrongArg() {
        Guest guest = newGuest("Martina", "548");
        guestManager.createGuest(guest);
        Room room = newRoom(5, 10, 4);
        roomManager.createRoom(room);
        hotelManager.addGuestToRoom(room, guest, date("2010-2-2"), date("2013-3-3"));

        hotelManager.findGuestsfromRoom(null,null);
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

    private Room newRoom(int roomNumber, int capacity, int floor) {
        Room room = new Room();
        room.setRoomID(null);
        room.setRoomNumber(roomNumber);
        room.setCapacity(capacity);
        room.setFloor(floor);

        return room;
    }

    private Date date(String date) {
        return Date.valueOf(date);
    }
}
