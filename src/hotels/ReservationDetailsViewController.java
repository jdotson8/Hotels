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
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
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
public class ReservationDetailsViewController implements ScreenController, Initializable {
    private static final String maxResIDQuery = "SELECT MAX(Res_ID) FROM Reservation;";
    
    private static final String reservationUpdate =
            "INSERT INTO Reservation "
            + "VALUES ('%d', '%d', '%s', '%s', '%s', '%f', '%d', '0', '%s', '%s')";
    
    @FXML
    private Pane parent;
    
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
    private ComboBox cardSelect;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button confirmationButton;
    
    private ScreenManager manager;
    private ObservableList<Room> selectedRooms;
    private ObservableList<Card> availableCards;
    private long lengthOfStay;
    private DoubleProperty totalCost;
    
    private Service<List<Card>> cardService = new Service<List<Card>>() {
        @Override
        protected Task<List<Card>> createTask() {
            return new Task<List<Card>>() {
                @Override
                protected List<Card> call() throws Exception {
                    List<Card> numbers = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                    Statement s = con.createStatement();) {
                        ResultSet rs = s.executeQuery(manager.getCardQuery());
                        while (rs.next()) {
                            numbers.add(new Card(rs.getString(1), rs.getDate(2).toLocalDate()));
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                    return numbers;
                }
            };
        }  
    };
    
    private class ReservationService extends Service<Void> {
        private Reservation res;
        
        public void setReservationInfo(Reservation res) {
            this.res = res;
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        ResultSet rs = s.executeQuery(maxResIDQuery);
                        rs.next();
                        int lastRes = rs.getInt(1);
                        if (lastRes >= 1000) {
                            res.setResID(lastRes + 1);
                        } else {
                            res.setResID(1000);
                        }
                        String update;
                        for (Room room : res.getRooms()) {
                            update = String.format(reservationUpdate,
                                    res.getResID(),
                                    room.getRoomNumber(),
                                    room.getLocation(),
                                    res.getStartDate(),
                                    res.getEndDate(),
                                    res.getTotalCost(),
                                    (room.isSelected()) ? 1 : 0,
                                    manager.getUserUsername(),
                                    res.getCardNum());
                            s.executeUpdate(update);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                    return null;
                }
            };
        }         
    }
    private ReservationService reservationService = new ReservationService();
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("MakeReservationView", null);
    }

    @FXML
    private void addCardHandler(ActionEvent event) {
        manager.setScreen("PaymentView", null);
    }
    
    @FXML
    private void confirmationHandler(ActionEvent event) {
        manager.setReservationCost(totalCost.getValue());
        manager.setReservationCardNum(((Card)cardSelect.getValue()).getCardNum());
        reservationService.setReservationInfo(manager.getPartialReservation());
        reservationService.restart();
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
        availableCards = FXCollections.observableArrayList();
        cardSelect.setItems(availableCards);
        totalCost = new SimpleDoubleProperty();
        
        totalCostLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            return String.format("Total Cost: %s", currencyFormat.format(totalCost.getValue()));   
        }, totalCost));
        
        cardService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            availableCards.setAll((List<Card>)event.getSource().getValue());
        });
        
        reservationService.setOnSucceeded((final WorkerStateEvent event) -> {
            manager.setScreen("ConfirmationView", null);
        });
        
        errorLabel.setText("Card expires before end date.");
    }    

    @Override
    public void onSet(List arguments) {
        List<Room> rooms = manager.getReservationRooms();
        LocalDate startDate = manager.getReservationStartDate();
        LocalDate endDate = manager.getReservationEndDate();
        
        selectedRooms.setAll(rooms);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        startDateLabel.setText(String.format("Start Date: %s", startDate.format(dateFormatter)));
        endDateLabel.setText(String.format("Date Date: %s", endDate.format(dateFormatter)));
        lengthOfStay = startDate.until(endDate, ChronoUnit.DAYS);
        lengthOfStayLabel.setText(String.format("Length of Stay: %d", lengthOfStay));
        
        Observable[] dependencies = new BooleanProperty[selectedRooms.size()];
        for (int i = 0; i < dependencies.length; i++) {
            dependencies[i] = selectedRooms.get(i).selectedProperty();
        }
        DoubleBinding costBinding = Bindings.createDoubleBinding(() -> {
            double cost = 0.0;
            for (Room room : selectedRooms) {
                cost += room.getDailyCost() * lengthOfStay;
                if (room.isSelected()) {
                    cost += room.getExtraBedCost() * lengthOfStay;
                }
            }
            return cost;
        }, dependencies);
        totalCost.bind(costBinding);
        
        BooleanBinding invalidCard = Bindings.createBooleanBinding(() -> {
            if (cardSelect.getValue() != null) {
                LocalDate expDate = ((Card)cardSelect.getValue()).getExpDate();
                return expDate.compareTo(endDate) <= 0;
            } else {
                return false;
            }      
        }, cardSelect.valueProperty());
        
        errorLabel.visibleProperty().bind(invalidCard);
        
        confirmationButton.disableProperty().bind((Bindings.createBooleanBinding(() ->
            cardSelect.getValue() == null,
        cardSelect.valueProperty())).or(invalidCard));
        
        cardService.restart();
    }

    @Override
    public void cleanUp() {
        selectedRooms.clear();
        availableCards.clear();
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
    
}
