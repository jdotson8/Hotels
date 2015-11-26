/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.text.NumberFormat;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ReservationDetailsViewController implements ScreenController, Initializable {
    @FXML
    private Label startDateLabel;
    
    @FXML
    private Label endDateLabel;
    
    @FXML
    private Label lengthOfStayLabel;
    
    @FXML
    private Label totalCostLabel;
    
    @FXML
    private TableView selectedRoomsTable;
    
    @FXML
    private TableColumn roomNumberColumn;
    
    @FXML
    private TableColumn typeColumn;
    
    @FXML
    private TableColumn capacityColumn;
    
    @FXML
    private TableColumn dailyCostColumn;
    
    @FXML
    private TableColumn extraBedColumn;
    
    @FXML
    private TableColumn selectColumn;
    
    @FXML
    private Button confirmationButton;
    
    private ScreenManager manager;
    private ObservableList<Room> selectedRooms;
    private Period lengthOfStay;
    private DoubleProperty totalCost;

    @FXML
    private void addCardHandler(ActionEvent event) {
        manager.setScreen("UserRegistrationView", null);
    }
    
    @FXML
    private void confirmationHandler(ActionEvent event) {
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        dailyCostColumn.setCellValueFactory(new PropertyValueFactory<>("dailyCost"));
        extraBedColumn.setCellValueFactory(new PropertyValueFactory<>("extraBedCost"));
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        StringConverter<Double> currencyConverter = new StringConverter<Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            @Override
            public String toString(Double object) {
                return currencyFormat.format(object);
            }
            @Override
            public Double fromString(String string) {
                //Nothing
                return null;
            }
        };
        
        dailyCostColumn.setCellFactory(TextFieldTableCell.<Room, Double>forTableColumn(currencyConverter));
        extraBedColumn.setCellFactory(TextFieldTableCell.<Room, Double>forTableColumn(currencyConverter));
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        
        selectedRooms = FXCollections.observableArrayList();
        totalCost = new SimpleDoubleProperty(0.0);
        
        totalCostLabel.textProperty().bind(new StringBinding() {
            @Override
            protected String computeValue() {
                return String.format("Total Cost: %s", totalCost.getValue());
            }
        });
        
        selectedRoomsTable.setItems(selectedRooms);
    }    

    @Override
    public void onSet(List arguments) {
        Reservation reservation = manager.getPartialReservation();
        selectedRooms.setAll(reservation.getRooms());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        startDateLabel.setText(String.format("Start Date: %s", reservation.getStartDate().format(dateFormatter)));
        endDateLabel.setText(String.format("Start Date: %s", reservation.getEndDate().format(dateFormatter)));
        lengthOfStay = reservation.getStartDate().until(reservation.getEndDate());
        lengthOfStayLabel.setText(String.format("Length of Stay: %d", lengthOfStay.getDays()));
        
        Observable[] dependencies = new BooleanProperty[selectedRooms.size()];
        for (int i = 0; i < dependencies.length; i++) {
            dependencies[i] = selectedRooms.get(i).selectedProperty();
        }
        DoubleBinding costBinding = Bindings.createDoubleBinding(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                double cost = 0.0;
                for (Room room : selectedRooms) {
                    cost += room.getDailyCost() * lengthOfStay.getDays();
                    if (room.isSelected()) {
                        cost += room.getExtraBedCost() * lengthOfStay.getDays();
                    }
                }
                return cost;
            }
        }, dependencies);
        totalCost.bind(costBinding);
        
        totalCostLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                return String.format("Total Cost: %s", currencyFormat.format(totalCost.getValue()));
            }   
        }, totalCost));
        
        
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
