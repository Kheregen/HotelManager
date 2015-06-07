package sources;


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
public interface AccomManager {

    public void createAccom(Accomodation accom)throws ServiceFailureException;

    public void updateAccom(Accomodation accom);

    public void deleteAccom(Accomodation accom);

    public Accomodation getAccom(Long accomId);

    public List<Accomodation> getAllAccoms();

}
