/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.text.NumberFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
        IntegerProperty roomNumber;
        StringProperty type;
        IntegerProperty capacity;
        StringProperty dailyCost;
        StringProperty extraBedCost;
        BooleanProperty selected;
        
        public Room(int roomNumber, String type, int capacity, double dailyCost, double extraBedCost) {
            this.roomNumber = new SimpleIntegerProperty(roomNumber);
            this.type = new SimpleStringProperty(type);
            this.capacity = new SimpleIntegerProperty(capacity);
            NumberFormat cost = NumberFormat.getCurrencyInstance();
            this.dailyCost = new SimpleStringProperty(cost.format(dailyCost));
            this.extraBedCost = new SimpleStringProperty(cost.format(extraBedCost));
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
        
        public StringProperty dailyCostProperty() {
            return dailyCost;
        }
        
        public StringProperty extraBedCostProperty() {
            return extraBedCost;
        }
        
        public BooleanProperty selectedProperty() {
            return selected;
        }
    }
