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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
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
public class MakeReservationViewController implements ScreenController, Initializable {
    private static String roomSearchQuery = 
            "SELECT Room_Num, Room_Categ, Num_People, Cost_Per_Day, Extra_Bed_Cost "
            + "FROM Room "
            + "WHERE Room_Location = '%1$s' AND NOT EXISTS( "
                + "SELECT * "
                + "FROM Reservation "
                + "WHERE NOT Res_Cancelled AND Room_Location = Res_Location AND Room_Num = Res_RoomNum AND ( "
                    + "Start_Date <= '%2$s' AND End_Date >= '%2$s' OR "
                    + "Start_Date <= '%3$s' AND End_Date >= '%3$s' OR "
                    + "Start_Date >= '%2$s' AND End_Date <= '%3$s'));";
    
    @FXML
    private Pane parent;
    
    @FXML
    private ComboBox<String> locationSelect;
    
    @FXML
    private DatePicker startDateSelect;
    
    @FXML
    private DatePicker endDateSelect;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private TableView availableRoomsTable;
    
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
    private Button nextButton;
    
    private ScreenManager manager;
    private ObservableList<String> availableLocations;
    private ObservableList<Room> availableRooms;
    
    private Service<List<String>> locationsService = new Service<List<String>>() {
        @Override
        protected Task<List<String>> createTask() {
            return new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    List<String> locations = new ArrayList<String>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = manager.getLocationsQuery();
                        ResultSet rs = s.executeQuery(query);
                        while (rs.next()) {
                            locations.add(rs.getString(1));
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return locations;
                }
            };
        }
    };
    
    private class SearchService extends Service<List<Room>> {
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        
        public void setSearchInfo(String location, LocalDate startDate, LocalDate endDate) {
            this.location = location;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        @Override
        protected Task<List<Room>> createTask() {
            return new Task<List<Room>>() {
                @Override
                protected List<Room> call() throws Exception {
                    String start = startDate.format(DateTimeFormatter.ISO_DATE);
                    String end = endDate.format(DateTimeFormatter.ISO_DATE);
                    List<Room> rooms = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = String.format(roomSearchQuery, location, startDate, endDate);
                        ResultSet rs = s.executeQuery(query);
                        while (rs.next()) {
                            rooms.add(new Room(rs.getInt(1), location, rs.getString(2), rs.getInt(3), rs.getDouble(4), rs.getDouble(5)));
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        throw ex;
                    }
                    return rooms;
                }
            };
        }         
    }
    private SearchService searchService = new SearchService();
    
    @FXML
    private void searchRoomsHandler(ActionEvent event) {
        parent.setDisable(true);
        searchService.setSearchInfo(locationSelect.getValue(), startDateSelect.getValue(), endDateSelect.getValue());
        searchService.restart();
    }
    
    @FXML
    private void cancelHandler(ActionEvent event) {
        manager.setScreen("CustomerMenuView", null);
    }
    
    @FXML
    private void nextHandler(ActionEvent event) {
        List<Room> selected = new ArrayList<>();
        for (Room room : availableRooms) {
            if (room.isSelected()) {
                selected.add(room.copy());
            }
        }
        manager.setPartialReservation(new Reservation(searchService.getStartDate(), searchService.getEndDate(), selected));
        manager.setState(ScreenManager.ReservationState.CREATE);
        manager.setScreen("ReservationDetailsView", null);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        availableLocations = FXCollections.observableArrayList();
        availableRooms = FXCollections.observableArrayList();
        locationsService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            availableLocations.setAll((List<String>)event.getSource().getValue());
        });
        
        searchService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            List<Room> rooms = (List<Room>)event.getSource().getValue();
            availableRooms.setAll(rooms);
            Observable[] dependencies = new BooleanProperty[rooms.size()];
            for (int i = 0; i < dependencies.length; i++) {
                dependencies[i] = rooms.get(i).selectedProperty();
            }
            nextButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                boolean selected = false;
                for (Room room : availableRooms) {
                    if (room.isSelected()) {
                        selected = true;
                        break;
                    }
                }
                return !selected;
            }, dependencies));
        });
        
        searchButton.disableProperty().bind(Bindings.createBooleanBinding(() -> 
            locationSelect.getValue() == null ||
            startDateSelect.getValue() == null ||
            endDateSelect.getValue() == null,
        locationSelect.valueProperty(), startDateSelect.valueProperty(), endDateSelect.valueProperty()));
        
        locationSelect.setItems(availableLocations);
        availableRoomsTable.setItems(availableRooms);
    }    

    @Override
    public void onSet(List arguments) {
        locationsService.restart();
    }
    
    @Override
    public void cleanUp() {
        availableLocations.clear();
        startDateSelect.setValue(null);
        endDateSelect.setValue(null);
        availableRooms.clear();
        manager.setState(ScreenManager.ReservationState.NONE);
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
}
