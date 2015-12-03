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
public class ManagerMenuViewController implements ScreenController, Initializable {
    @FXML
    private Pane parent;
    
    @FXML 
    private Label welcomeLabel;
    
    private ScreenManager manager;
    
    @FXML
    private void viewResHandler(ActionEvent event) {
        manager.setScreen("ReservationReportView", null);
    }
    
    @FXML
    private void viewPopRoomHandler(ActionEvent event) {
        manager.setScreen("RoomReportView", null);
    }
    
    @FXML
    private void viewRevenueHandler(ActionEvent event) {
        manager.setScreen("RevenueReportView", null);
    }
    
    @FXML
    private void logoutHandler(ActionEvent event) {
        manager.logout();
        manager.setScreen("LoginView", null);
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
        welcomeLabel.setText(String.format("Welcome %s,", manager.getUserUsername()));
        parent.setDisable(false);
    }
    
    @Override
    public void cleanUp() {
        //Nothing
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
}
