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
import sources.Guest;

/**
 *
 * @author Jan
 */
public class CustomGuestsTableModel extends AbstractTableModel {

    private List<Guest> guests = new ArrayList();
    private ResourceBundle resB;
    
    public CustomGuestsTableModel(ResourceBundle res){
        resB = res;
    }
    public void addGuest(Guest guest){
        guests.add(guest);
        int lastRow = guests.size() - 1;
        this.fireTableRowsInserted(lastRow, lastRow);
    }
    public void deleteAll(){
        guests.clear();
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
                return resB.getString("guestOp4");
            case 2:
                return resB.getString("guestOp5");
            default:
                throw new IllegalArgumentException("wrong columnIndex");
        }
    }

}
