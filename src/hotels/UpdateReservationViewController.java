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
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.BooleanProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class UpdateReservationViewController implements ScreenController, Initializable {
    private static final String conflictingResQuery =
        "SELECT COUNT(*) "
        + "FROM Reservation "
        + "WHERE NOT Res_Cancelled AND Res_ID != '%1$s' AND ( "
            + "Start_Date <= '%2$s' AND End_Date >= '%2$s' OR "
            + "Start_Date <= '%3$s' AND End_Date >= '%3$s' OR "
            + "Start_Date >= '%2$s' AND End_Date <= '%3$s');";
    
    private static final String modifyReservationUpdate = 
        "UPDATE Reservation AS newRes "
        + "SET Start_Date = '%s', End_Date = '%s' "
        + "WHERE Res_ID = '%s';";
    
    @FXML
    private Pane parent;
    
    @FXML
    private Label startDateLabel;
    
    @FXML
    private Label endDateLabel;
    
    @FXML
    private DatePicker startDateSelect;
    
    @FXML
    private DatePicker endDateSelect;
    
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
    private ComboBox cardSelect;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button confirmationButton;
    
    private ScreenManager manager;
    private ObservableList<Room> selectedRooms;
    private ObservableList<Card> availableCards;
    private LongBinding lengthOfStay;
    private DoubleProperty totalCost;
    
    private class ReservationService extends Service<Void> {
        private LocalDate startDate;
        private LocalDate endDate;
        
        public void setReservationInfo(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String start = startDate.format(DateTimeFormatter.ISO_DATE);
                    String end = endDate.format(DateTimeFormatter.ISO_DATE);
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = String.format(conflictingResQuery, manager.getReservationID(), startDate, endDate);
                        ResultSet rs = s.executeQuery(query);
                        rs.next();
                        if (rs.getInt(1) != 0) {
                            String update = String.format(modifyReservationUpdate, startDate, endDate, manager.getReservationID());
                            s.executeUpdate(update);
                        } else {
                            throw new Exception("Rooms are not available for new date range.");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return null;
                }
            };
        }         
    }
    private ReservationService reservationService = new ReservationService();
    
    @FXML
    private void confirmationHandler(ActionEvent event) {
        parent.setDisable(true);
        errorLabel.setVisible(false);
        reservationService.setReservationInfo(startDateSelect.getValue(), endDateSelect.getValue());
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
        totalCost = new SimpleDoubleProperty();
        
        totalCostLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            return String.format("Total Cost: %s", currencyFormat.format(totalCost.getValue()));   
        }, totalCost));
        
        lengthOfStay = Bindings.createLongBinding(() -> {
            if (startDateSelect.getValue() == null || endDateSelect.getValue() == null) {
                return 0L;
            }
            return startDateSelect.getValue().until(endDateSelect.getValue(), ChronoUnit.DAYS);
        }, startDateSelect.valueProperty(), endDateSelect.valueProperty());
        lengthOfStayLabel.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("Updated Length of Stay: %d", lengthOfStay.getValue()), lengthOfStay));
        
        reservationService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            manager.setScreen("ConfirmationView", null);
        });
        
        reservationService.setOnFailed((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            errorLabel.setText(reservationService.getException().getMessage());
            errorLabel.setVisible(true);
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
        endDateLabel.setText(String.format("Date Date: %s", endDate.format(dateFormatter)));
        startDateSelect.setValue(startDate);
        endDateSelect.setValue(endDate);
        
        startDateSelect.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (endDateSelect.getValue() != null &&
                                item.isAfter(endDateSelect.getValue().minusDays(1)) ||
                                item.isBefore(LocalDate.now())) {
                            setDisable(true);
                        }   
                    }
                };
            }
        });
        
        endDateSelect.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (startDateSelect.getValue() != null &&
                                item.isBefore(startDateSelect.getValue().plusDays(1)) ||
                                item.isBefore(LocalDate.now())) {
                            setDisable(true);
                        }   
                    }
                };
            }
        });

        DoubleBinding costBinding = Bindings.createDoubleBinding(() -> {
            double cost = 0.0;
            for (Room room : selectedRooms) {
                cost += room.getDailyCost() * lengthOfStay.getValue();
                if (room.isSelected()) {
                    cost += room.getExtraBedCost() * lengthOfStay.getValue();
                }
            }
            return cost;
        }, lengthOfStay);
        totalCost.bind(costBinding);
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
