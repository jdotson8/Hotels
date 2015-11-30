/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class CustomerMenuViewController implements ScreenController, Initializable {
    @FXML
    private Pane parent;
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button makeResButton;
    
    @FXML
    private Button updateResButton;
    
    @FXML
    private Button cancelResButton;
    
    @FXML
    private Button createReviewButton;
    
    @FXML
    private Button readReviewsButton;
    
    private ScreenManager manager;
    
    @FXML
    private void makeResHandler(ActionEvent event) {
        manager.setState(ScreenManager.ReservationState.UPDATE);
        manager.setScreen("MakeReservationView", null);
    }
    
    @FXML
    private void updateResHandler(ActionEvent event) {
        manager.setState(ScreenManager.ReservationState.UPDATE);
        manager.setScreen("SelectReservationView", null);
    }
    @FXML
    private void cancelResHandler(ActionEvent event) {
        manager.setState(ScreenManager.ReservationState.CANCEL);
        manager.setScreen("SelectReservationView", null);
    }
    
    @FXML
    private void createReviewHandler(ActionEvent event) {
        // TODO
    }
    
    @FXML
    private void readReviewsHandler(ActionEvent event) {
        // TODO
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void onSet(List arguments) {
        parent.setDisable(false);
        welcomeLabel.setText(String.format("Welcome %s,", manager.getUserUsername()));
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }

    @Override
    public void cleanUp() {
        //Nothing
    }
    
}
