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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ConfirmationViewController implements ScreenController, Initializable {
    @FXML
    private Pane parent;
            
    @FXML
    private TextField resIDField;
    
    private ScreenManager manager;
    
    @FXML
    private void menuHandler(ActionEvent event) {
        manager.setScreen("CustomerMenuView", null);
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
        resIDField.setText(Integer.toString(manager.getReservationID()));
        manager.setState(ScreenManager.ReservationState.NONE);
        manager.setPartialReservation(null);
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
