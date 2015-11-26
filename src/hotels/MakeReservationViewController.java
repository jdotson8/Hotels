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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class MakeReservationViewController implements ScreenController, Initializable {
    private static String locationsQuery =
            "SELECT DISTINCT Room_Location "
            + "FROM Room;";
    
    private static String roomSearchQuery = 
            "SELECT Room_Num, Room_Cat, Num_People, Cost_Per_Day "
            + "FROM Room "
            + "WHERE Room_Location = '%1$s' AND NOT EXISTS( "
                + "SELECT * "
                + "FROM Reservation "
                + "WHERE Room_Location = Res_Location AND Room_Num = Res_RoomNum AND ( "
                    + "Start_Date <= '%2$s' AND End_Date >= '%2$s' OR "
                    + "Start_Date <= '%3$s' AND End_Date >= '%3$s' OR "
                    + "Start_Date >= '%2$s' AND End_Date <= '%3$s'));";
    
    @FXML
    private ComboBox locationSelect;
    
    @FXML
    private DatePicker startDateSelect;
    
    @FXML
    private DatePicker endDateSelect;
    
    @FXML
    private Button searchRoomsButton;
    
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
    
    private ScreenManager manager;
    private ObservableList availableLocations;
    private ObservableList availableRooms;
    
    private Service<List<String>> locationsService = new Service<List<String>>() {
        @Override
        protected Task<List<String>> createTask() {
            return new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    Connection con = manager.openConnection();
                    Statement s = con.createStatement();
                    String query = locationsQuery;
                    ResultSet rs = s.executeQuery(query);
                    List<String> locations = new ArrayList<String>();
                    while (rs.next()) {
                        locations.add(rs.getString(1));
                    }
                    return locations;
                }
            };
        }
    };
    
    private class SearchService extends Service<List<Room>> {
        private String location;
        private String startDate;
        private String endDate;
        
        public void setSearchInfo(String location, String startDate, String endDate) {
            this.location = location;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        @Override
        protected Task<List<Room>> createTask() {
            return new Task<List<Room>>() {
                @Override
                protected List<Room> call() throws Exception {
                    Connection con = manager.openConnection();
                    Statement s = con.createStatement();
                    String query = String.format(roomSearchQuery, location, startDate, endDate);
                    ResultSet rs = s.executeQuery(query);
                    List<Room> rooms = new ArrayList<>();
                    while (rs.next()) {
                        rooms.add(new Room(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), 0));
                    }
                    return rooms;
                }
            };
        }         
    }
    private SearchService searchService = new SearchService();
    
    @FXML
    private void searchRoomsHandler(ActionEvent event) {
        if (startDateSelect.getValue() == null && endDateSelect.getValue() == null) {
            //Nothing for now.
        } else {
            String location = (String) locationSelect.getValue();
            String startDate = startDateSelect.getValue().format(DateTimeFormatter.ISO_DATE);
            String endDate = endDateSelect.getValue().format(DateTimeFormatter.ISO_DATE);
            searchService.setSearchInfo(location, startDate, endDate);
            searchService.restart();
        }
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("CustomerMenuView");
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
                        if (item.isBefore(LocalDate.now())) {
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
                        if (item.isBefore(startDateSelect.getValue().plusDays(1))) {
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
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        
        availableLocations = FXCollections.observableArrayList();
        availableRooms = FXCollections.observableArrayList();
        locationsService.setOnSucceeded((final WorkerStateEvent event) -> {
            availableLocations.setAll((List<String>)event.getSource().getValue());
        });
        
        searchService.setOnSucceeded((final WorkerStateEvent event) -> {
            availableRooms.setAll((List<Room>)event.getSource().getValue());
        });
        
        searchService.setOnFailed((final WorkerStateEvent event) -> {
            System.out.println(searchService.getException().getMessage());
        });
        
        locationSelect.setItems(availableLocations);
        availableRoomsTable.setItems(availableRooms);
        
        /*availableRoomsTableableTable.setItems(FXCollections.observableArrayList(
            new Room(101, "Traditional", 2, 500, 10),
            new Room(102, "Suite", 4, 1000, 10),
            new Room(103, "Family", 6, 1500, 10),
            new Room(104, "Suite", 4, 500, 10)
        ));*/
    }    

    @Override
    public void onSet() {
        locationsService.restart();
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
