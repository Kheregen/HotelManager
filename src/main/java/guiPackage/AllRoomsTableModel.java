/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiPackage;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.dbcp2.BasicDataSource;
import sources.Room;
import sources.RoomManager;
import sources.RoomManagerImpl;

/**
 *
 * @author Jan
 */
public class AllRoomsTableModel extends AbstractTableModel {

    private List<Room> rooms = getValues();
    private ResourceBundle resB;
    
    public AllRoomsTableModel(ResourceBundle res){
        resB = res;
    }
            
    private List<Room> getValues() {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        RoomManager mngr = new RoomManagerImpl(mySource);
        return mngr.getAllRooms();
    }
    
    public void addRoom(Room room) {
        rooms.add(room);
        this.fireTableDataChanged();
    }
    public void deleteRoom(Room room){
        rooms.remove(room);
        this.fireTableDataChanged();
    }
    public void updateRoom(Room room){
        Long id = room.getRoomID();
        Room toUpdate = null;
        for(Room item: rooms){
            if(item.getRoomID().equals(id)){
                toUpdate = item;
            }
        }
        rooms.remove(toUpdate);
        rooms.add(room);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rooms.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room room = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return room.getRoomID();
            case 1:
                return room.getRoomNumber();
            case 2:
                return room.getCapacity();
            case 3:
                return room.getFloor();
            default:
                throw new IllegalArgumentException("wrong columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "ID";
            case 1:
                return resB.getString("roomOp4");
            case 2:
                return resB.getString("roomOp5");
            case 3:
                return resB.getString("roomOp6");
            default:
                throw new IllegalArgumentException("wrong columnIndex");
        }
    }
}
