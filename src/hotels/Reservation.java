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
    private int resID;
    private double totalCost;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Room> rooms;
    private String cardNum;
    
    public Reservation(LocalDate startDate, LocalDate endDate, List<Room> rooms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rooms = rooms;
    }
    
    public void setResID(int resID) {
        this.resID = resID;
    }
    
    public int getResID() {
        return resID;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    } 
    
    public double getTotalCost() {
        return totalCost;
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
    
    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
    
    public String getCardNum(){
        return cardNum;
    }
    
    @Override
    public String toString() {
        return Integer.toString(resID);
    }
}
