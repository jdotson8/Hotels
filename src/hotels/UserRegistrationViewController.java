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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class UserRegistrationViewController implements ScreenController, Initializable {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button submitButton;
    
    private ScreenManager manager;
    
    @FXML
    private void cancelHandler(ActionEvent event) {
        manager.setScreen("LoginView", null);
    }
    
    @FXML
    private void submitHandler(ActionEvent event) {
        // TODO
    }
    
    @Override
    public void onSet(List arguments) {
        //TODO
    }
    
    @Override
    public void cleanUp() {
        //Nothing
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    
    
}
