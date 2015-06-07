/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import sources.Room;

/**
 *
 * @author Jan
 */
public class CustomRoomsTableModel extends AbstractTableModel {

    List<Room> rooms = new ArrayList();
    ResourceBundle resB;

    public CustomRoomsTableModel(ResourceBundle res) {
        resB = res;
    }

    public void addRoom(Room room) {
        rooms.add(room);
        int lastRow = rooms.size() - 1;
        this.fireTableRowsInserted(lastRow, lastRow);
    }

    public void deleteAll() {
        rooms.clear();
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
