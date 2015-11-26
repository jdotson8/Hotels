/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Reservation {
    LocalDate startDate;
    LocalDate endDate;
    List<Room> rooms;
    
    public Reservation(LocalDate startDate, LocalDate endDate, List<Room> rooms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rooms = rooms;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public List<Room> getRooms() {
        return rooms;
    }
}
