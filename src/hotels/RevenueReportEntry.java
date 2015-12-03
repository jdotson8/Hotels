/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Administrator
 */
public class RevenueReportEntry {
    private StringProperty location;
    private DoubleProperty revenue;

    public RevenueReportEntry(String location, double revenue) {
        this.location = new SimpleStringProperty(location);
        this.revenue = new SimpleDoubleProperty(revenue);
    }
        
    public StringProperty locationProperty() {
        return location;
    }
    
    public DoubleProperty revenueProperty() {
        return revenue;
    }
    
    public String getLocation() {
        return location.getValue();
    }
    
    public double getRevenue() {
        return revenue.getValue();
    }
}
