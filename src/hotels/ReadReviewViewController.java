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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ReadReviewViewController implements ScreenController, Initializable {
    public static final String reviewsQuery =
            "SELECT Rating, Comment "
            + "FROM Hotel_Review "
            + "WHERE Location = '%s'";
    
    @FXML
    private Pane parent;
    
    @FXML
    private ComboBox<String> locationSelect;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private TableView reviewsTable;
    
    @FXML
    private TableColumn ratingColumn;
    
    @FXML
    private TableColumn commentColumn;
    
    private ScreenManager manager;
    private ObservableList<String> availableLocations;
    private ObservableList<Review> reviews;

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
    
    private class ReviewsService extends Service<List<Review>> {
        private String location;
        
        public void setReviewsInfo(String location) {
            this.location = location;
        }
        
        @Override
        protected Task<List<Review>> createTask() {
            return new Task<List<Review>>() {
                @Override
                protected List<Review> call() throws Exception {
                    List<Review> foundReviews = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = String.format(reviewsQuery, location);
                        ResultSet rs = s.executeQuery(query);
                        while(rs.next()) {
                            foundReviews.add(new Review(rs.getString(1), rs.getString(2)));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                    return foundReviews;
                }
            };
        }         
    }
    private ReviewsService reviewsService = new ReviewsService();
    
    @FXML
    private void searchHandler(ActionEvent event) {
        parent.setDisable(true);
        reviewsService.setReviewsInfo(locationSelect.getValue());
        reviewsService.restart();
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
        reviews = FXCollections.observableArrayList();
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        locationSelect.setItems(availableLocations);
        reviewsTable.setItems(reviews);
        
        locationsService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            availableLocations.setAll((List<String>)event.getSource().getValue());
        });
        
        reviewsService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            reviews.setAll((List<Review>)event.getSource().getValue());
        });
    }    

    @Override
    public void onSet(List arguments) {
        locationsService.restart();
    }

    @Override
    public void cleanUp() {
        locationSelect.setValue(null);
        reviews.clear();
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
}
