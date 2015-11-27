/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
 * FXML Controller class
 *
 * @author Administrator
 */
public class UserRegistrationViewController implements ScreenController, Initializable {
    private static final String newCustomerUpdate =
            "INSERT INTO Customer "
            + "VALUES ('%s', '%s', '%s');";
    
    private static final String newManagerUpdate = 
            "INSERT INTO Manager "
            + "VALUES ('%s', '%s');";
    
    @FXML
    private Pane parent;
    
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
    private Button submitButton;
    
    private ScreenManager manager;
    
    private class NewUserService extends Service<User> {
        private String username;
        private String password;
        private String email;
        
        public void setUserInfo(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
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
                        if (email.equals("")) {
                            throw new Exception("Customers must include an email address.");
                        }
                    } else {
                        throw new Exception("Username must match Manager# or User#.");
                    }
                    User result;
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String update = (isManager) ?
                                String.format(newManagerUpdate, username, password) :
                                String.format(newCustomerUpdate, username, email, password);
                        s.executeUpdate(update);
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return new User(username, isManager);
                }
            };
        }         
    }
    private NewUserService newUserService = new NewUserService();
    
    @FXML
    private void cancelHandler(ActionEvent event) {
        errorLabel.setVisible(false);
        manager.setScreen("LoginView", null);
    }
    
    @FXML
    private void submitHandler(ActionEvent event) {
        errorLabel.setVisible(false);
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Password does not match confirmation.");
            errorLabel.setVisible(true);
        } else if (!emailField.getText().matches(".+@.+\\..+")) {
            errorLabel.setText("Innvalid email address.");
            errorLabel.setVisible(true);
        } else {
            parent.setDisable(true);
            newUserService.setUserInfo(usernameField.getText(), passwordField.getText(), emailField.getText());
            newUserService.restart();
        }
    }
    
    @Override
    public void onSet(List arguments) {
        parent.setDisable(false);
    }
    
    @Override
    public void cleanUp() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        errorLabel.setVisible(false);
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
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
            usernameField.getText().equals("") ||
            passwordField.getText().equals("") ||
            confirmPasswordField.getText().equals(""),
        usernameField.textProperty(), passwordField.textProperty(), confirmPasswordField.textProperty()));
        
        newUserService.setOnSucceeded((final WorkerStateEvent event) -> {
            manager.login((User)event.getSource().getValue());
            manager.setScreen((manager.userIsManager()) ?
                    "ManagerMenuView" :
                    "CustomerMenuView", null);
        });
        
        newUserService.setOnFailed((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            Throwable ex = newUserService.getException();
            if (ex instanceof SQLException) {
                SQLException sqlEx = (SQLException) ex;
                if (sqlEx.getErrorCode() == 1062) {
                    errorLabel.setText("Username already exists.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setText(sqlEx.getMessage());
                    errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });
    }    

    
    
}
