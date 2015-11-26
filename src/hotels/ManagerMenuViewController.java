/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ManagerMenuViewController implements ScreenController, Initializable {
    @FXML 
    private Label welcomeLabel;
    
    
    @FXML
    private Button viewResReportButton;
    
    @FXML
    private Button viewPopRoomButton;
    
    @FXML
    private Button viewRevenueButton;
    
    private ScreenManager manager;
    
    @FXML
    private void viewResHandler(ActionEvent event) {
        // TODO
    }
    
    @FXML
    private void viewPopRoomHandler(ActionEvent event) {
        // TODO
    }
    
    @FXML
    private void viewRevenueHandler(ActionEvent event) {
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
    public void onSet() {
        welcomeLabel.setText(String.format("Welcome %s,", manager.getUser().getUsername()));
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
