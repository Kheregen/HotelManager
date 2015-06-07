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
 * TODO
 * @author Jan
 */
public class AllGuestComboBoxModel extends AbstractListModel implements ComboBoxModel {

    List<Guest> myValues = getValues();
    Object selection = null;   

    private List<Guest> getValues() {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        GuestManager mngr = new GuestManagerImpl(mySource);
        return mngr.getAllGuests();
    }

    public void addGuest(Guest guest) {
        myValues.add(guest);
       this.fireContentsChanged(this, 0, myValues.size());
    }
    public void deleteGuest(Guest guest){
        myValues.remove(guest);
        this.fireContentsChanged(this, 0, myValues.size());
    }
    public void updateGuest(Guest guest){
        Long id = guest.getGuestID();
        Guest toUpdate = null;
        for(Guest item: myValues){
            if(item.getGuestID().equals(id)){
                toUpdate = item;
            }
        }
        myValues.remove(toUpdate);
        myValues.add(guest);
        this.fireContentsChanged(this, 0, myValues.size());
    }
    
    @Override
    public int getSize() {
        return myValues.size();
    }

    @Override
    public Object getElementAt(int index) {
        return (String)(myValues.get(index).getGuestID() + " "+myValues.get(index).getName());
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
