package sources;


import java.sql.Date;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan
 */
public class Accomodation {

    private Long accomId;
    private Long guestId;
    private Long roomId;
    private Date startDate;
    private Date endDate;

    public Long getAccomId() {
        return accomId;
    }

    public void setAccomId(Long accomId) {
        this.accomId = accomId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.accomId);
        hash = 37 * hash + Objects.hashCode(this.guestId);
        hash = 37 * hash + Objects.hashCode(this.roomId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Accomodation other = (Accomodation) obj;
        if (!Objects.equals(this.accomId, other.accomId)) {
            return false;
        }
        if (!Objects.equals(this.guestId, other.guestId)) {
            return false;
        }
        if (!Objects.equals(this.roomId, other.roomId)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.endDate, other.endDate)) {
            return false;
        }
        return true;
    }
    
    

}
