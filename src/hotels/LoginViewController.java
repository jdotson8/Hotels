/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 *
 * @author Administrator
 */
public class LoginViewController implements ScreenController, Initializable {
    private static String customerQuery = 
            "SELECT Cust_User "
            + "FROM Customer "
            + "WHERE Cust_User = '%s' AND Cust_Password = '%s';";
    
    private static String managerQuery = 
            "SELECT Man_User "
            + "FROM Manager "
            + "WHERE Man_User = '%s' AND Man_Password = '%s';";
    
    @FXML
    private Pane parent;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button newUserButton;
    
    private ScreenManager manager;

    
    
    private class LoginService extends Service<User> {
        private String username;
        private String password;
        
        public void setLoginInfo(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        protected Task<User> createTask() {
            return new Task<User>() {
                @Override
                protected User call() throws Exception {
                    boolean isManager;
                    if (username.matches("Manager[0-9]{1,43}")) {
                        isManager = true;
                    } else if (username.matches("User[0-9]{1,43}")) {
                        isManager = false;
                    } else {
                        throw new Exception("Invalid username.");
                    }
                    User result;
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = String.format((isManager) ? managerQuery : customerQuery, username, password);
                        ResultSet rs = s.executeQuery(query);
                        if (rs.next()) {
                            result = new User(rs.getString(1), isManager);
                        } else {
                            throw new Exception("Incorrect username or password.");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return result;
                }
            };
        }         
    }
    private LoginService loginService = new LoginService();
    
    private String[] locations = {"Atlanta", "Charlotte", "Savannah", "Orlando", "Miami"};
    private String[] types = {"Standard", "Suite", "Family"};
    private int[] capacities = {2, 4, 4};
    private float[] costs = {100, 250, 170};
    private float[] extraBed = {70, 150, 50};
    
    @FXML
    private void loginHandler(ActionEvent event) {
        errorLabel.setVisible(false);
        parent.setDisable(true);
        loginService.setLoginInfo(usernameField.getText(), passwordField.getText());
        loginService.restart();
    }
    
    @FXML
    private void newUserHandler(ActionEvent event) {
        errorLabel.setVisible(false);
        manager.setScreen("UserRegistrationView", null);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginService.setOnSucceeded((final WorkerStateEvent event) -> {
            manager.login((User)event.getSource().getValue());
            manager.setScreen((manager.userIsManager()) ?
                    "ManagerMenuView" :
                    "CustomerMenuView", null);
        });
        
        loginService.setOnFailed((final WorkerStateEvent event) -> {
            errorLabel.setText(loginService.getException().getMessage());
            errorLabel.setVisible(true);
            parent.setDisable(false);
        });
    }
    
    @Override
    public void onSet(List arguments) {
        parent.setDisable(false);
    }
    
    @Override
    public void cleanUp() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setVisible(false);
    }
    
    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
}
