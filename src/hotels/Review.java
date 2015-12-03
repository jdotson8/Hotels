/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Administrator
 */
public class Review {
    private StringProperty rating;
    private StringProperty comment;
    
    public Review(String rating, String comment) {
        this.rating = new SimpleStringProperty(rating);
        this.comment = new SimpleStringProperty(comment);
    }
    
    public StringProperty ratingProperty() {
        return rating;
    }
    
    public StringProperty commentProperty() {
        return comment;
    }
    
    public String getRating() {
        return rating.getValue();
    }
    
    public String getComment() {
        return comment.getValue();
    }
}
