package sources;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Richard
 * Jan edit 1.4 pridal jsem name podminku u createguest + detaily
 * all finished
 */
public class GuestManagerImpl implements GuestManager{

    final static Logger LOG = LoggerFactory.getLogger(GuestManagerImpl.class);
    private final DataSource dataSource;

    
    public GuestManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public void createGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (guest.getGuestID() != null) {
            throw new IllegalArgumentException("guest id is already set");
        }
        if(guest.getName() == null){
            throw new IllegalArgumentException("guestName is null");
        }
        if(guest.getName().length() == 0){
            throw new IllegalArgumentException("guestName is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO GUEST(NAME,CREDITCARD) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, guest.getName());
                st.setString(2, guest.getCreditCard());
                int added = st.executeUpdate();
                if (added != 1) {
                    throw new ServiceFailureException("Inserted more rooms than 1");
                }
                ResultSet keyRS = st.getGeneratedKeys();
                guest.setGuestID(getKey(keyRS, guest));
            }
        } catch (SQLException ex) {
            LOG.error("Database connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }
    
    private Long getKey(ResultSet rs, Guest guest) throws SQLException {
        if (rs.next()) {
            if (rs.getMetaData().getColumnCount() != 1) {
                throw new SQLException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + guest
                        + " - wrong key fields count: " + rs.getMetaData().getColumnCount());
            }
            Long result = rs.getLong(1);
            if (rs.next()) {
                throw new SQLException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + guest
                        + " - more keys found");
            }
            return result;
        } else {
            throw new SQLException("Internal Error: Generated key"
                    + "retriving failed when trying to insert guest " + guest
                    + " - no key found");
        }
    }

    @Override
    public void deleteGuest(Guest guest) throws ServiceFailureException{
        if(guest == null){
            throw new IllegalArgumentException("guest is null");
        }
        if(guest.getGuestID() == null){
            throw new IllegalArgumentException("guest has null id");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM GUEST WHERE ID=?")) {
                st.setLong(1, guest.getGuestID());
                if (st.executeUpdate() != 1) {
                    throw new SQLException("didn't delete 1 entry.");
                }
            }
            guest.setGuestID(null);
        } catch (SQLException ex) {
            LOG.error("Database connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }

    @Override
    public void updateGuest(Guest guest) {
        if(guest == null){
            throw new IllegalArgumentException("Guest is null");
        }
        if(guest.getGuestID() == null){
            throw new NullPointerException("Guest has null id");
        }
        
        
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("UPDATE GUEST SET NAME=?,CREDITCARD=? WHERE ID=?")) {
                st.setString(1, guest.getName());
                st.setString(2, guest.getCreditCard());
                st.setLong(3,guest.getGuestID());
                if(st.executeUpdate()!=1) {
                    throw new IllegalArgumentException("cannot update guest " + guest);
                }
            }
        }catch(SQLException ex){
            throw new ServiceFailureException("db connection problem",ex);
        }        
    }

    @Override
    public Guest getGuest(Long id) throws ServiceFailureException {
        if(id == null){
            throw new IllegalArgumentException("id is null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ID,NAME,CREDITCARD FROM GUEST WHERE ID= ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Guest foundGuest = resultSetToGuest(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException("returned more guests than 1 with id=" + id);
                    }
                    return foundGuest;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOG.error("db connection problem");
            throw new ServiceFailureException("db connection problem", ex);
        }
    }
    
    private Guest resultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestID(rs.getLong("id"));
        guest.setName(rs.getString("name"));
        guest.setCreditCard(rs.getString("creditCard"));
        return guest;
    }

    @Override
    public List<Guest> getAllGuests() throws ServiceFailureException {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT ID,NAME,CREDITCARD FROM GUEST")) {
                ResultSet rs = st.executeQuery();
                List<Guest> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToGuest(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            LOG.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all guests", ex);
        }
    }    
}