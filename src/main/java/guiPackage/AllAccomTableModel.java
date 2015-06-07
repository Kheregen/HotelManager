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
import sources.AccomManager;
import sources.AccomManagerImpl;
import sources.Accomodation;
import sources.GuestManager;
import sources.GuestManagerImpl;
import sources.RoomManager;
import sources.RoomManagerImpl;

/**
 *
 * @author Jan
 */
public class AllAccomTableModel extends AbstractTableModel {

    private List<Accomodation> accoms = getValues();
    private BasicDataSource mySource;
    ResourceBundle res;

    public AllAccomTableModel(ResourceBundle resB) {
        res = resB;
    }

    private List<Accomodation> getValues() {
        mySource = new BasicDataSource();
        mySource.setUrl(ResourceBundle.getBundle("databaseConnection").getString("connection"));
        mySource.setUsername(ResourceBundle.getBundle("databaseConnection").getString("username"));
        mySource.setPassword(ResourceBundle.getBundle("databaseConnection").getString("password"));
        AccomManager mngr = new AccomManagerImpl(mySource);
        return mngr.getAllAccoms();
    }

    public void addAccom(Accomodation accom) {
        accoms.add(accom);
        int lastRow = accoms.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void deleteAccom(Accomodation accom) {
        accoms.remove(accom);
        this.fireTableDataChanged();
    }

    public void updateAccom(Accomodation accom) {
        Long id = accom.getAccomId();
        Accomodation toUpdate = null;
        for (Accomodation item : accoms) {
            if (item.getAccomId().equals(id)) {
                toUpdate = item;
            }
        }
        accoms.remove(toUpdate);
        accoms.add(accom);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return accoms.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GuestManager gMngr = new GuestManagerImpl(mySource);
        RoomManager rMngr = new RoomManagerImpl(mySource);
        Accomodation accom = accoms.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return accom.getAccomId();
            case 1:
                return gMngr.getGuest(accom.getGuestId()).getName();
            case 2:
                return rMngr.getRoom(accom.getRoomId()).getRoomNumber();
            case 3:
                return accom.getStartDate().toString();
            case 4:
                return accom.getEndDate().toString();
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
                return res.getString("common1");
            case 2:
                return res.getString("common2");
            case 3:
                return res.getString("common7");
            case 4:
                return res.getString("common8");
            default:
                throw new IllegalArgumentException("wrong columnIndex");
        }
    }

}
