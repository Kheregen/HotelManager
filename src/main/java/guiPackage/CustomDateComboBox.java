/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Jan
 */
public class CustomDateComboBox extends AbstractListModel implements ComboBoxModel {

    private ResourceBundle res;
    private int chosen;
    private int days;
    private List<Integer> years = new ArrayList();
    private CustomDateComboBox yearModel = null;
    private CustomDateComboBox monthModel = null;
    private CustomDateComboBox dayModel = null;

    Object selection = null;

    public CustomDateComboBox(int chosen, ResourceBundle resB, CustomDateComboBox one, CustomDateComboBox two) {

        this.chosen = chosen;
        res = resB;
        if (chosen == 1) {
            for (int i = 2014; i < 2025; i++) {
                years.add(i);
            }
            selection = years.get(0);
        }
        if (chosen == 2) {
            selection = res.getString("month1");
        }
        if (chosen == 3) {

            days = 0;
            yearModel = one;
            monthModel = two;
            selection = 1;
            yearModel.setDayModel(this);
            monthModel.setDayModel(this);
        }
    }

    public void setDayModel(CustomDateComboBox model) {
        this.dayModel = model;
    }

    @Override
    public int getSize() {
        switch (chosen) {
            case 1:
                return 10;
            case 2:
                return 12;
            case 3:
                if (monthModel.getSelectedItem().equals(res.getString("month2"))) {
                    if ((int) yearModel.getSelectedItem() % 4 == 0) {
                        return 29;
                    } else {
                        return 28;
                    }
                }
                int longM[] = {1, 3, 5, 7, 8, 10, 12};
                for (int item : longM) {
                    if (monthModel.getSelectedItem().equals(res.getString("month" + item))) {
                        return 31;
                    }
                }
                return 30;
            default:
                throw new IllegalArgumentException("bad constructor");
        }
    }

    @Override
    public Object getElementAt(int index) {
        switch (chosen) {
            case 1:
                return years.get(index);
            case 2:
                return res.getString("month" + (index+1));
            case 3:
                return index + 1;
            default:
                throw new IllegalArgumentException("bad constructor");
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = anItem;
        if (chosen != 3) {
            dayModel.fireContentsChanged(this, 0, this.getSize() - 1);
            dayModel.setSelectedItem(1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }

}
