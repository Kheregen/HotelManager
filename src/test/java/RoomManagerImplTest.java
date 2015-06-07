
import sources.Room;
import sources.RoomManagerImpl;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Richard
 */
public class RoomManagerImplTest {
    
    private RoomManagerImpl manager;
    private DataSource dataSource;
    
    @Before
    public void setUp() throws SQLException {
        
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:RoomeManagerTest;create=true");
        this.dataSource = bds;
        //create new empty table before every test
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE ROOM ("
                    + "ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "ROOMNUMBER INT,"
                    + "CAPACITY INT,"
                    + "FLOOR INT)").executeUpdate();
        }
        manager = new RoomManagerImpl(bds);
    }
    
    
    @After
    public void tearDown() throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            con.prepareStatement("DROP TABLE ROOM").executeUpdate();
        }
    }
    
   
    /**
     * Test of createRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testCreateRoom()throws SQLException {
        Room room = newRoom(55,1,2,8);
        manager.createRoom(room);

        Long roomID = room.getRoomID();
        assertNotNull(roomID);
        Room result = manager.getRoom(roomID);
        assertEquals(room, result);
        assertNotSame(room, result);
        assertDeepEquals(room, result);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreateRoomWithNull() throws SQLException
    {
        manager.createRoom(null);
    }
    
    /**
     * Test of getRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testGetRoom() throws SQLException{
        Room room = newRoom(5,1,2,4);
        manager.createRoom(room);
        assertNotNull(manager.getRoom(room.getRoomID()));
        assertEquals(room, manager.getRoom(room.getRoomID()));
    }    
       
    /**
     * Test of updateRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testUpdateRoom() throws SQLException{
        Room room = newRoom(1,2,3,4);
        manager.createRoom(room);
        Long roomId = room.getRoomID();
        
        room = manager.getRoom(roomId);
        room.setCapacity(0);
        manager.updateRoom(room);
        assertEquals(0, room.getCapacity());
        assertEquals(4, room.getFloor());
        assertEquals(2, room.getRoomNumber());
        
        room = manager.getRoom(roomId);
        room.setFloor(9);
        manager.updateRoom(room);
        assertEquals(0, room.getCapacity());
        assertEquals(9, room.getFloor());      
        assertEquals(2, room.getRoomNumber());
        
        room = manager.getRoom(roomId);
        room.setRoomNumber(5);
        manager.updateRoom(room);
        assertEquals(0, room.getCapacity());
        assertEquals(5, room.getRoomNumber());
        assertEquals(9, room.getFloor());      
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUpdateRoomWithNull()
    {
        manager.updateRoom(null);
    }

    /**
     * Test of deleteRoom method, of class RoomManagerImpl.
     */
    @Test
    public void testDeleteRoom() throws SQLException{
        Room r1 = newRoom(2,5,2,6);
        Room r2 = newRoom(3,214,9,2);
        manager.createRoom(r1);
        manager.createRoom(r2);
        
        assertNotNull(manager.getRoom(r1.getRoomID()));
        assertNotNull(manager.getRoom(r2.getRoomID()));

        manager.deleteRoom(r1);
        
        assertNotNull(manager.getRoom(r2.getRoomID()));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testDeleteRoomWithNull()
    {
        manager.deleteRoom(null);
    }
    
    private static Room newRoom (long id,int roomNumber, int capacity, int floor) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomID(null);
        room.setCapacity(capacity);
        room.setFloor(floor);
        
        return room;
    }
    
    private void assertDeepEquals(Room expected, Room actual) {
        assertEquals(expected.getRoomID(), actual.getRoomID());
        assertEquals(expected.getRoomNumber(), actual.getRoomNumber());
        assertEquals(expected.getFloor(), actual.getFloor());
        assertEquals(expected.getCapacity(), actual.getCapacity());
    }
}