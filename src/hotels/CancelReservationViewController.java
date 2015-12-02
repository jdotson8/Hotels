/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class CancelReservationViewController implements ScreenController, Initializable {
    private static final String reservationUpdate =
            "UPDATE Reservation "
            + "SET Res_Cancelled = 1 "
            + "WHERE Res_ID = '%s'";
    
    @FXML
    private Pane parent;
    
    @FXML
    private Label startDateLabel;
    
    @FXML
    private Label endDateLabel;
    
    @FXML
    private Label totalCostLabel;
    
    @FXML
    private Label cancellationLabel;
    
    @FXML
    private Label refundLabel;
    
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
    
    private Service<List<String>> reservationService = new Service<List<String>>() {
        @Override
        protected Task<List<String>> createTask() {
            return new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    List<String> locations = new ArrayList<String>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String update = String.format(reservationUpdate, manager.getReservationID());
                        s.executeUpdate(update);
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return locations;
                }
            };
        }
    };
    
    @FXML
    private void confirmationHandler(ActionEvent event) {
        parent.setDisable(true);
        reservationService.restart();
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("SelectReservationView", null);
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
        selectedRoomsTable.setItems(selectedRooms);
        
        reservationService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            manager.setScreen("ConfirmationView", null);
        });
    }    

    @Override
    public void onSet(List arguments) {
        parent.setDisable(false);
        
        List<Room> rooms = manager.getReservationRooms();
        LocalDate startDate = manager.getReservationStartDate();
        LocalDate endDate = manager.getReservationEndDate();
        
        selectedRooms.setAll(rooms);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        startDateLabel.setText(String.format("Start Date: %s", startDate.format(dateFormatter)));
        endDateLabel.setText(String.format("End Date: %s", endDate.format(dateFormatter)));
        cancellationLabel.setText(String.format("Cancellation Date: %s", LocalDate.now().format(dateFormatter)));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        totalCostLabel.setText(String.format("Total Cost: %s", currencyFormat.format(manager.getReservationCost())));
        long daysPrior = LocalDate.now().until(startDate, ChronoUnit.DAYS);
        double refundMultiplier = (daysPrior > 3) ? 1.0 : (daysPrior > 1) ? 0.8 : 0.0;
        System.err.println(refundMultiplier);
        refundLabel.setText(String.format("Refund Amount: %s", currencyFormat.format(refundMultiplier * manager.getReservationCost())));
    }

    @Override
    public void cleanUp() {
        //
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
    
}
