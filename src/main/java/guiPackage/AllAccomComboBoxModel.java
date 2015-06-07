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
import sources.*;

/**
 *
 * @author Richard
 */
public class AllAccomComboBoxModel extends AbstractListModel implements ComboBoxModel {

    List<Accomodation> myValues = getValues();
    
    Object selection = null;

    private List<Accomodation> getValues() {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        AccomManager accomMngr = new AccomManagerImpl(mySource);
        return accomMngr.getAllAccoms();
    }

    @Override
    public int getSize() {
        return myValues.size();
    }

    public void addAccom(Accomodation accom) {
        myValues.add(accom);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    public void deleteAccom(Accomodation accom) {
        myValues.remove(accom);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    public void updateAccom(Accomodation accom) {
        Long id = accom.getAccomId();
        Accomodation toUpdate = null;
        for (Accomodation item : myValues) {
            if (item.getAccomId().equals(id)) {
                toUpdate = item;
            }
        }
        myValues.remove(toUpdate);
        myValues.add(accom);
        this.fireContentsChanged(this, 0, myValues.size());
    }

    @Override
    public Object getElementAt(int index) {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        
        GuestManager guestMngr = new GuestManagerImpl(mySource);
        RoomManager roomMngr = new RoomManagerImpl(mySource);
        return (String) (myValues.get(index).getAccomId() + ": "
                + guestMngr.getGuest(myValues.get(index).getGuestId()).getName()
                + ", " + roomMngr.getRoom(myValues.get(index).getRoomId()).getRoomNumber()
                + " (" + myValues.get(index).getStartDate()
                + " - " + myValues.get(index).getEndDate() + ")");
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
