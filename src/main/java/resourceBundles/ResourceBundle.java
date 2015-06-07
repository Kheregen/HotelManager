/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourceBundles;

import java.util.List;

/**
 *
 * @author Jan
 */
public interface ResourceBundle {
    public String[] getMain_window1();

    public String[] getMain_window2();

    public String[] getMenu();

    public String[] getMenu_moznosti();

    public String[] getAccom_panel();
    public String[] getCommon();
    public String[] getTables();
    public String[] getRoomOp();
    public String[] getGuestOp();
    public String[] getAccomOp();
    public String[] getMonths() ;
    public List<String> getMonthtsList();
}
