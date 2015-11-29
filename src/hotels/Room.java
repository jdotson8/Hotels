/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.text.NumberFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
        IntegerProperty roomNumber;
        String location;
        StringProperty type;
        IntegerProperty capacity;
        DoubleProperty dailyCost;
        DoubleProperty extraBedCost;
        BooleanProperty selected;
        
        public Room(int roomNumber, String location, String type, int capacity, double dailyCost, double extraBedCost) {
            this.roomNumber = new SimpleIntegerProperty(roomNumber);
            this.location = location;
            this.type = new SimpleStringProperty(type);
            this.capacity = new SimpleIntegerProperty(capacity);
            this.dailyCost = new SimpleDoubleProperty(dailyCost);
            this.extraBedCost = new SimpleDoubleProperty(extraBedCost);
            selected = new SimpleBooleanProperty(false);
        }
        
        public IntegerProperty roomNumberProperty() {
            return roomNumber;
        }
        
        public StringProperty typeProperty() {
            return type;
        }
        
        public IntegerProperty capacityProperty() {
            return capacity;
        }
        
        public DoubleProperty dailyCostProperty() {
            return dailyCost;
        }
        
        public DoubleProperty extraBedCostProperty() {
            return extraBedCost;
        }
        
        public BooleanProperty selectedProperty() {
            return selected;
        }
        
        public boolean isSelected() {
            return selected.getValue();
        }
        
        public int getRoomNumber() {
            return roomNumber.getValue();
        }
        
        public String getLocation() {
            return location;
        }
        
        public double getDailyCost() {
            return dailyCost.getValue();
        }
        
        public double getExtraBedCost() {
            return extraBedCost.getValue();
        }
        
        public Room copy() {
            return new Room(roomNumber.getValue(), location, type.getValue(), capacity.getValue(), dailyCost.getValue(), extraBedCost.getValue());
        }
    }
