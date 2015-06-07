/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiPackage;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Jan
 */
public class DateComboBox extends AbstractListModel implements ComboBoxModel {

    private int variable;
    private final List<Integer> year = new ArrayList<>();
    private final List<Integer> month= new ArrayList<>();
    private Integer selection;
    public DateComboBox(int i) {
        variable = i;
        for (int j = 2014; j < 2034; j++) {
            year.add(j);
        }
        for (int k = 1; k <= 12; k++) {
            month.add(k);
        }        
        switch(variable){
            case 1:
                selection = year.get(0);
            case 2:
                selection = month.get(0);
            case 3:
                selection = 1;
        }
    }

    @Override
    public int getSize() {
        switch (variable) {
            case 1:
                return year.size();
            case 2:
                return month.size();
            case 3:
                return 31;
            default:
                throw new IllegalArgumentException("bad constructor");
        }
    }

    @Override
    public Object getElementAt(int index) {
        switch(variable){
            case 1:
                return year.get(index);
            case 2:
                return month.get(index);
            case 3:
                return index + 1;
            default:
                throw new IllegalArgumentException("bad constructor");
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (int) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection.toString();
    }

}
