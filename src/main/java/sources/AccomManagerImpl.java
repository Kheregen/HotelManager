package sources;


import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan, work mostly Richard
 */
public class AccomManagerImpl implements AccomManager {

    public AccomManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private static final Logger logger = Logger.getLogger(
            RoomManagerImpl.class.getName());

    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }    
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createAccom(Accomodation accom) throws ServiceFailureException {
        checkDataSource();
        validate(accom);    
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO ACCOMODATION (GUESTID,ROOMID,STARTDATE,ENDDATE) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, accom.getGuestId());
            st.setLong(2, accom.getRoomId());
            st.setDate(3, (Date) accom.getStartDate());
            st.setDate(4, (Date) accom.getEndDate());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, accom, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            accom.setAccomId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting accomadation into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateAccom(Accomodation accom) throws ServiceFailureException {
        checkDataSource();
        validate(accom);
        
        if (accom.getAccomId() == null) {
            throw new IllegalArgumentException("accom id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);            
            st = conn.prepareStatement(
                    "UPDATE ACCOMODATION SET GUESTID = ?, ROOMID = ?, STARTDATE = ?, ENDDATE = ? WHERE ID = ?");
            st.setLong(1, accom.getGuestId());
            st.setLong(2, accom.getRoomId());
            st.setDate(3, (Date) accom.getStartDate());
            st.setDate(4, (Date) accom.getEndDate());
            st.setLong(5, accom.getAccomId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, accom, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating accom in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }        
    }

    @Override
    public void deleteAccom(Accomodation accom) throws ServiceFailureException {
        checkDataSource();
        if (accom == null) {
            throw new IllegalArgumentException("accom is null");
        }        
        if (accom.getAccomId() == null) {
            throw new IllegalArgumentException("accom id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM ACCOMODATION WHERE ID = ?");
            st.setLong(1, accom.getAccomId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, accom, false);
            conn.commit();
            accom.setAccomId(null);
        } catch (SQLException ex) {
            String msg = "Error when deleting accom from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Accomodation getAccom(Long accomId) {
        checkDataSource();
        
        if (accomId == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT ID,GUESTID,ROOMID,STARTDATE,ENDDATE FROM ACCOMODATION WHERE ID = ?");
            st.setLong(1, accomId);
            return executeQueryForSingleAccom(st);
        } catch (SQLException ex) {
            String msg = "Error when getting accom with id = " + accomId + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Accomodation> getAllAccoms() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT ID, GUESTID, ROOMID, STARTDATE, ENDDATE FROM ACCOMODATION");
            return executeQueryForMultipleAccoms(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all accom from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }         
    }
    
    static Accomodation executeQueryForSingleAccom(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Accomodation result = rowToAccom(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more accomodations with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }
    
    static List<Accomodation> executeQueryForMultipleAccoms(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Accomodation> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToAccom(rs));
        }
        return result;
    }
    
    static private Accomodation rowToAccom(ResultSet rs) throws SQLException {
        Accomodation result = new Accomodation();
        result.setAccomId(rs.getLong("ID"));
        result.setGuestId(rs.getLong("GUESTID"));
        result.setRoomId(rs.getLong("ROOMID"));
        result.setStartDate(rs.getDate("STARTDATE"));
        result.setEndDate(rs.getDate("ENDDATE"));
        return result;
    }
    
    static public void validate(Accomodation accom) {        
        if (accom == null) {
            throw new IllegalArgumentException("accom is null");
        }
        if(accom.getGuestId() == null){
            throw new IllegalArgumentException("guestID is null");
        }
        if(accom.getRoomId() == null){
            throw new IllegalArgumentException("roomID is null");
        }        
        if(accom.getStartDate() == null){
            throw new IllegalArgumentException("startDate is null");
        }
        if(accom.getEndDate() == null){
            throw new IllegalArgumentException("endDate is null");
        }
        if (accom.getStartDate().after(accom.getEndDate()) ) {
            throw new InvalidParameterException("Accomdoation ends before starts");
        }
        
    }
}
