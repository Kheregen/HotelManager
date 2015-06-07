/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourceBundles;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan
 */
public class ResourceBundle_cz implements ResourceBundle {

    private String[] main_window1 = {"Ubytovat hosta", "Vystěhovat hosta", "Najít hosty z pokoje", "Najít pokoj hosta"};
    private String[] main_window2 = {"Vypsat všechny hosty", "Vypsat všechny pokoje", "Vypsat všechna ubytování"};
    private String[] menu = {"Možnosti"};
    private String[] menu_moznosti = {"Ukončit", "Operace s pokoji", "Operace s hosty", "Operace s ubytováním"};

    private String[] accom_panel = {"Host:", "Ubytovat na pokoj:", "Ubytovat od:", "Ubytovat do:", "Odeslat"};
    private String[] common = {"Host", "Pokoj", "Ubytování", "Číslo", "Jméno", "Odeslat","Datum","Ubytován od","Ubytován do"};
    private String[] tables = {"Seznam všech hostů", "Seznam všech pokojů", "Seznam všech ubytování"};
    private String[] roomOp = {"Vytvořit pokoj", "Aktualizovat pokoj", "Odstranit pokoj", "Číslo pokoje", "Kapacita", "Patro", "Nové číslo", "Nová kapacita", "Nové patro",};
    private String[] guestOp ={"Vytvořit hosta", "Aktualizovat hosta", "Odstranit hosta","Jméno","Kreditní karta","Nové jméno","Nová kr. karta"};
    private String[] accomOp = {"Aktualizovat ubytování",""};
    private String[] months = {"Leden","Únor","Březen","Duben","Květen","Červen","Červenec","Srpen","Září","Říjen","Listopad","Prosinec"};
    private List<String> monthtsList = getMonthsList();
    

    private List getMonthsList(){
        List<String> result = new ArrayList();
        for(String item: getMonths()){
            result.add(item);
        }
        return result;
    }
    public String[] getMain_window1() {
        return main_window1;
    }

    public String[] getAccomOp() {
        return accomOp;
    }

    public List<String> getMonthtsList() {
        return monthtsList;
    }

    public String[] getMonths() {
        return months;
    }

    public String[] getGuestOp() {
        return guestOp;
    }

    public String[] getMain_window2() {
        return main_window2;
    }

    public String[] getMenu() {
        return menu;
    }

    public String[] getRoomOp() {
        return roomOp;
    }

    public String[] getMenu_moznosti() {
        return menu_moznosti;
    }

    public String[] getAccom_panel() {
        return accom_panel;
    }

    public String[] getCommon() {
        return common;
    }

    public String[] getTables() {
        return tables;
    }

}
