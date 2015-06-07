package sources;


import java.util.Collection;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan
 */
public interface GuestManager {

    public void createGuest(Guest guest);

    public void deleteGuest(Guest guest);

    public void updateGuest(Guest guest);

    public Guest getGuest(Long id);

    public List<Guest> getAllGuests();
}
