/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import sources.GuestManagerImpl;
import sources.Guest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jan
 * all finished
 */
public class GuestManagerImplTest {

    private GuestManagerImpl manager;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:GuestManagerTest;create=true");
        this.dataSource = bds;
        //create new empty table before every test
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE GUEST ("
                    + "ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "NAME VARCHAR(50),"
                    + "CREDITCARD VARCHAR(50))").executeUpdate();
        }
        manager = new GuestManagerImpl(bds);
    }

    @After
    public void tearUp() throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            con.prepareStatement("DROP TABLE GUEST").executeUpdate();
        }
    }

    public static Guest newGuest(String name, String creditCard) {
        Guest guest = new Guest();
        guest.setGuestID(null);
        guest.setName(name);
        guest.setCreditCard(creditCard);
        return guest;
    }

    /**
     * Test of createGuest method, of class GuestManagerImpl.
     */
    @Test
    public void testCreateGuest() {
        Guest guest = newGuest("Karel", "1234");
        manager.createGuest(guest);
        Long id1 = guest.getGuestID();
        assertNotNull(id1);
        Guest result = manager.getGuest(id1);
        assertEquals(guest, result);
        assertEquals(manager.getAllGuests().size(), 1);
        Guest g2 = newGuest("Lojza", "5678");
        manager.createGuest(g2);
        Long id2 = g2.getGuestID();
        assertEquals(g2, manager.getGuest(id2));
        assertEquals(manager.getAllGuests().size(), 2);

        Guest g3 = newGuest("Alfonz", null);
        manager.createGuest(g3);
        Long id3 = g3.getGuestID();
        assertEquals(g3, manager.getGuest(id3));
        assertEquals(manager.getAllGuests().size(), 3);
    }

    @Test
    public void testCreateWrongAtt() {
        //create null
        try {
            manager.createGuest(null);
            fail("guest is null");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        //guest has set id
        Guest guest = newGuest("Karel", "1234");
        guest.setGuestID(1L);
        try {
            manager.createGuest(guest);
            fail("guest has set id");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        //null name
        guest = newGuest(null, "1234");
        try {
            manager.createGuest(guest);
            fail("guest has null name");
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    /**
     * Test of deleteGuest method, of class GuestManagerImpl.
     */
    @Test
    public void testDeleteGuest() {
        Guest g1 = newGuest("Lojza", "1234");
        Guest g2 = newGuest("Fred", "5678");
        manager.createGuest(g1);
        manager.createGuest(g2);
        assertNotNull(g1.getGuestID());
        assertNotNull(g2.getGuestID());
        assertEquals(manager.getAllGuests().size(), 2);
        manager.deleteGuest(g2);
        assertEquals(manager.getAllGuests().size(), 1);
        Collection<Guest> all = manager.getAllGuests();
        assertTrue(all.contains(g1));
    }

    @Test
    public void testDeleteWrongAtt() {
        //delete null
        try {
            manager.deleteGuest(null);
            fail("guest is null");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        //guest with null id
        Guest guest = newGuest("Fido", "1478");
        try {
            manager.deleteGuest(guest);
            fail("guest has null id");
        } catch (IllegalArgumentException ex) {
            //OK
        }


    }

    /**
     * Test of updateGuest method, of class GuestManagerImpl.
     */
    @Test
    public void testUpdateGuest() {
        Guest guest = newGuest("Gustav", "4567");
        manager.createGuest(guest);
        Long id = guest.getGuestID();

        manager.updateGuest(guest);
        assertEquals("Gustav", guest.getName());
        assertEquals("4567", guest.getCreditCard());
        assertEquals(id, guest.getGuestID());

        guest.setName("Carlos");
        manager.updateGuest(guest);
        assertEquals("Carlos", manager.getGuest(id).getName());
        assertEquals("4567", manager.getGuest(id).getCreditCard());

        guest.setCreditCard("1111");
        manager.updateGuest(guest);
        assertEquals("Carlos", manager.getGuest(id).getName());
        assertEquals("1111", manager.getGuest(id).getCreditCard());

        guest.setCreditCard(null);
        manager.updateGuest(guest);
        assertEquals("Carlos", manager.getGuest(id).getName());
        assertNull(manager.getGuest(id).getCreditCard());

    }

    /**
     * Test of getGuest method, of class GuestManagerImpl.
     */
    @Test
    public void testGetGuest() {
        Guest guest = newGuest("Anton", "2222");
        assertNull(manager.getGuest(0L));
        manager.createGuest(guest);
        Long id = guest.getGuestID();
        assertEquals(guest, manager.getGuest(id));
        guest.setName("Pablo");
        assertTrue(guest != manager.getGuest(id));
        manager.updateGuest(guest);
        assertEquals(guest, manager.getGuest(id));
    }

    /**
     * Test of getAllGuests method, of class GuestManagerImpl.
     */
    @Test
    public void testGetAllGuests() {
        Guest g1 = newGuest("Petr", "7895");
        Guest g2 = newGuest("Pavel", "7844");
        manager.createGuest(g1);
        assertEquals(manager.getAllGuests().size(), 1);
        assertTrue(manager.getAllGuests().contains(g1));
        manager.createGuest(g2);
        assertEquals(manager.getAllGuests().size(), 2);
        assertTrue(manager.getAllGuests().contains(g1));
        assertTrue(manager.getAllGuests().contains(g2));

        manager.deleteGuest(g2);
        assertEquals(manager.getAllGuests().size(), 1);
        assertTrue(manager.getAllGuests().contains(g1));
    }

}
