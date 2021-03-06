/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class CreateReviewViewController implements ScreenController, Initializable {
    private static final String reviewUpdate =
            "INSERT INTO Hotel_Review(Location, Rating, Comment, Review_User) "
            + "VALUES ('%s', '%s', '%s', '%s')";
            
    
    @FXML
    private Pane parent;
    
    @FXML
    private ComboBox<String> locationSelect;
    
    @FXML
    private ComboBox<String> ratingSelect;
    
    @FXML
    private TextArea commentArea;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Label remainingLabel;
    
    private ScreenManager manager;
    private ObservableList<String> availableLocations;
    
    private Service<List<String>> locationsService = new Service<List<String>>() {
        @Override
        protected Task<List<String>> createTask() {
            return new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    List<String> locations = new ArrayList<String>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = manager.getLocationsQuery();
                        ResultSet rs = s.executeQuery(query);
                        while (rs.next()) {
                            locations.add(rs.getString(1));
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return locations;
                }
            };
        }
    };
    
    private class ReviewService extends Service<Void> {
        private String location;
        private String rating;
        private String comment;
        
        public void setReviewInfo(String location, String rating, String comment) {
            this.location = location;
            this.rating = rating;
            this.comment = comment;
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String update = String.format(reviewUpdate, location, rating, comment, manager.getUserUsername());
                        s.executeUpdate(update);
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                    return null;
                }
            };
        }         
    }
    private ReviewService reviewService = new ReviewService();
    
    @FXML
    private void submitHandler(ActionEvent event) {
        parent.setDisable(true);
        reviewService.setReviewInfo(locationSelect.getValue(),ratingSelect.getValue(), commentArea.getText());
        reviewService.restart();
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("CustomerMenuView", null);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        availableLocations = FXCollections.observableArrayList();
        locationSelect.setItems(availableLocations);
        ratingSelect.setItems(FXCollections.observableArrayList("Excelent", "Good", "Neutral", "Bad", "Very Bad"));
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(() -> 
            locationSelect.getValue() == null ||
            ratingSelect.getValue() == null,
        locationSelect.valueProperty(), ratingSelect.valueProperty()));
        commentArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 64) {
                    ((StringProperty)observable).setValue(oldValue);
                }
            }
        });
        remainingLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            String.format("(%d of 64 remaining.)", 64 - commentArea.getText().length()), commentArea.textProperty()));
        
        locationsService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            availableLocations.setAll((List<String>)event.getSource().getValue());
        });
        
        reviewService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            manager.setScreen("CustomerMenuView", null);
        });
    }    

    @Override
    public void onSet(List arguments) {
        locationsService.restart();
    }

    @Override
    public void cleanUp() {
       locationSelect.setValue(null);
       ratingSelect.setValue(null);
       commentArea.setText("");
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
    
}
