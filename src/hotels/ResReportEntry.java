/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Administrator
 */
public class ResReportEntry {
    private StringProperty location;
    private IntegerProperty resCount;

    public ResReportEntry(String location, int resCount) {
        this.location = new SimpleStringProperty(location);
        this.resCount = new SimpleIntegerProperty(resCount);
    }
        
    public StringProperty locationProperty() {
        return location;
    }
    
    public IntegerProperty reservationsProperty() {
        return resCount;
    }
    
    public String getLocation() {
        return location.getValue();
    }
    
    public int getReservations() {
        return resCount.getValue();
    }
}
