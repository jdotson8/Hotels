/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Administrator
 */
public class RoomReportEntry {
    private StringProperty location;
    private StringProperty type;
    private IntegerProperty resCount;

    public RoomReportEntry(String location, String type, int resCount) {
        this.location = new SimpleStringProperty(location);
        this.type = new SimpleStringProperty(type);
        this.resCount = new SimpleIntegerProperty(resCount);
    }
        
    public StringProperty locationProperty() {
        return location;
    }
    
    public StringProperty typeProperty() {
        return type;
    }
    
    public IntegerProperty resCountProperty() {
        return resCount;
    }
    
    public String getLocation() {
        return location.getValue();
    }
    
    public String getType() {
        return type.getValue();
    }
    
    public int getResCount() {
        return resCount.getValue();
    }
}
