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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class SearchRoomsViewController implements Initializable {
    @FXML
    private ChoiceBox locationSelect;
    
    @FXML
    private DatePicker startDateField;
    
    @FXML
    private DatePicker endDateField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private void searchHandler(ActionEvent event) {
        // TODO
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
