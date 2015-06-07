/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiPackage;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.apache.commons.dbcp2.BasicDataSource;
import sources.Room;
import sources.RoomManager;
import sources.RoomManagerImpl;

/**
 *
 * @author Jan
 */
public class AllRoomsComboBoxModel extends AbstractListModel implements ComboBoxModel {

    List<Room> myValues = getValues();
    Object selection = null;

    private List<Room> getValues() {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        RoomManager mngr = new RoomManagerImpl(mySource);
        return mngr.getAllRooms();
    }

    public void addRoom(Room room) {
        myValues.add(room);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    public void deleteRoom(Room room) {
        myValues.remove(room);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    public void updateRoom(Room room) {
        Long id = room.getRoomID();
        Room toUpdate = null;
        for (Room item : myValues) {
            if (item.getRoomID().equals(id)) {
                toUpdate = item;
            }
        }
        myValues.remove(toUpdate);
        myValues.add(room);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    @Override
    public int getSize() {
        return myValues.size();
    }

    @Override
    public Object getElementAt(int index) {
        return myValues.get(index).getRoomID();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }

}
