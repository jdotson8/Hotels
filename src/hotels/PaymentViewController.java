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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class PaymentViewController implements ScreenController, Initializable {
    private static final String saveCardUpdate =
            "INSERT INTO Payment_Information "
            + "VALUES ('%s', '%s', '%s', '%s', '%s');";
    
    private static final String deleteCardUpdate =
            "DELETE FROM Payment_Information "
            + "WHERE Card_Num = '%s';";
    
    @FXML
    private Pane parent;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField numberField;
    
    @FXML
    private TextField dateField;
    
    @FXML
    private TextField cvvField;
    
    @FXML
    private ComboBox cardSelect;
    
    @FXML 
    private Button saveButton;
    
    @FXML
    private Label savedLabel;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Label deletedLabel;
    
    @FXML
    private Label errorLabel;
    
    private ScreenManager manager;
    private ObservableList<String> availableCards;
    
    private Service<List<String>> cardService = new Service<List<String>>() {
        @Override
        protected Task<List<String>> createTask() {
            return new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    List<String> numbers = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        ResultSet rs = s.executeQuery(manager.getCardQuery());
                        while (rs.next()) {
                            numbers.add(rs.getString(1));
                        }
                    } catch(Exception ex) {
                        throw ex;
                    }
                    return numbers;
                }
            };
        }  
    };
    
    private class SaveCardService extends Service<String> {
        private String name;
        private String number;
        private LocalDate date;
        private String cvv;
        
        public void setCardInfo(String name, String number, LocalDate date, String cvv) {
            this.name = name;
            this.number = number;
            this.date = date;
            this.cvv = cvv;
        }
        
        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    String expirationDate = date.format(DateTimeFormatter.ISO_DATE);
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String update = String.format(saveCardUpdate, number, cvv, expirationDate, name, manager.getUserUsername());
                        int result = s.executeUpdate(update);
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return number;
                }
            };
        }         
    }
    private SaveCardService saveCardService = new SaveCardService();
    
    private class DeleteCardService extends Service<String> {
        private String number;
        
        public void setCardInfo(String number) {
            this.number = number;
        }
        
        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String update = String.format(deleteCardUpdate, number);
                        int result = s.executeUpdate(update);
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return number;
                }
            };
        }         
    }
    private DeleteCardService deleteCardService = new DeleteCardService();
    
    
    @FXML 
    private void saveHandler(ActionEvent event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate expirationDate = LocalDate.parse(String.format("01/%s",dateField.getText()), dateFormatter);
        if (LocalDate.now().until(expirationDate).getDays() < 1) {
            errorLabel.setText("Card is expired.");
            errorLabel.setVisible(true);
        } else {
            saveCardService.setCardInfo(nameField.getText(), numberField.getText(), expirationDate, cvvField.getText());
            saveCardService.restart();
        }
    }
    
    @FXML
    private void deleteHandler(ActionEvent event) {
        deleteCardService.setCardInfo((String)cardSelect.getValue());
        deleteCardService.restart();
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("ReservationDetailsView", null);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        availableCards = FXCollections.observableArrayList();
        cardSelect.setItems(availableCards);
        StringConverter<String> cardNumberConverter = new StringConverter<String>() {
            @Override
            public String toString(String object) {
                char[] str = object.toCharArray();
                for (int i = str.length - 5; i >= 0; i--) {
                    str[i] = '•';
                }
                return String.valueOf(str);
            }
            @Override
            public String fromString(String string) {
                //Not used.
                return null;
            }
        };
        cardSelect.setConverter(cardNumberConverter);
        
        numberField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]{0,16}")) {
                    ((StringProperty)observable).setValue(oldValue);
                }
            }
        });
        
        cvvField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]{0,4}")) {
                    ((StringProperty)observable).setValue(oldValue);
                }
            }
        });
        
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(() -> 
            nameField.getText().length() == 0 ||
            numberField.getText().length() < 13 ||
            dateField.getText().length() == 0 ||
            cvvField.getText().length() < 3,
        nameField.textProperty(), numberField.textProperty(), dateField.textProperty(), cvvField.textProperty()));
        
        cardService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            availableCards.setAll((List<String>)event.getSource().getValue());
        });
        
        saveCardService.setOnSucceeded((final WorkerStateEvent event) -> {
            savedLabel.setVisible(true);
            availableCards.add((String)event.getSource().getValue());
            nameField.setText("");
            numberField.setText("");
            dateField.setText("");
            cvvField.setText("");
        });
        
        saveCardService.setOnFailed((final WorkerStateEvent event) -> {
            Throwable ex = saveCardService.getException();
            if (ex instanceof SQLException) {
                SQLException sqlEx = (SQLException)ex;
                if (sqlEx.getErrorCode() == 1062) {
                    errorLabel.setText("Innvalid card number.");
                } else {
                    errorLabel.setText(sqlEx.getMessage());
                }
            } else {
                errorLabel.setText(ex.getMessage());
            }
            errorLabel.setVisible(true);
        });
        
        deleteCardService.setOnSucceeded((final WorkerStateEvent event) -> {
            deletedLabel.setVisible(true);
            availableCards.remove((String)event.getSource().getValue());
            cardSelect.setValue(null);
        });
        
        deleteCardService.setOnFailed((final WorkerStateEvent event) -> {
            Throwable ex = saveCardService.getException();
            if (ex instanceof SQLException) {
                SQLException sqlEx = (SQLException)ex;
                if (sqlEx.getErrorCode() == 1062) {
                    errorLabel.setText("Innvalid card number.");
                } else {
                    errorLabel.setText(Integer.toString(sqlEx.getErrorCode()));
                }
            } else {
                errorLabel.setText(ex.getMessage());
            }
            errorLabel.setVisible(true);
        });
        
        deleteButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
            cardSelect.getValue() == null,
        cardSelect.valueProperty()));
    }    

    @Override
    public void onSet(List arguments) {
        cardService.restart();
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
