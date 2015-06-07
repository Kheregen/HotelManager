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
import sources.*;

/**
 *
 * @author Jan
 */
public class AllGuestsTableModel extends AbstractTableModel {

    private final List<Guest> guests = getValues();
    ResourceBundle resB;
    public AllGuestsTableModel(ResourceBundle res){
        resB = res;
    }
    private List<Guest> getValues() {
        BasicDataSource mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        GuestManager mngr = new GuestManagerImpl(mySource);
        return mngr.getAllGuests();
    }
    public void addGuest(Guest guest) {
        guests.add(guest);
        this.fireTableDataChanged();
    }
    public void deleteGuest(Guest guest){
        guests.remove(guest);
        this.fireTableDataChanged();
    }
    public void updateGuest(Guest guest){
        Long id = guest.getGuestID();
        Guest toUpdate = null;
        for(Guest item: guests){
            if(item.getGuestID().equals(id)){
                toUpdate = item;
            }
        }
        guests.remove(toUpdate);
        guests.add(guest);
        this.fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return guests.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Guest guest = guests.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return guest.getGuestID();
            case 1:
                return guest.getName();
            case 2:
                return guest.getCreditCard();
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
                return resB.getString("common5");
            case 2:
                return resB.getString("guestOp5");
            default:
                throw new IllegalArgumentException("wrong columnIndex");
        }
    }
}
