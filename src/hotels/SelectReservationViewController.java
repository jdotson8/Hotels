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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
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
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class SelectReservationViewController implements ScreenController, Initializable {
    
    @FXML
    private Pane parent;
    
    @FXML
    private ComboBox reservationSelect;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button nextButton;
    
    private ScreenManager manager;
    private ObservableList<Reservation> availableReservations;
    
    private Service<List<Reservation>> reservationsService = new Service<List<Reservation>>() {
        @Override
        protected Task<List<Reservation>> createTask() {
            return new Task<List<Reservation>>() {
                @Override
                protected List<Reservation> call() throws Exception {
                    List<Reservation> reservations = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        ResultSet rs = s.executeQuery(manager.getReservationQuery());
                        while(rs.next()) {
                            int resID = rs.getInt(1);
                            LocalDate startDate = rs.getDate(2).toLocalDate();
                            LocalDate endDate = rs.getDate(3).toLocalDate();
                            List<Room> rooms = new ArrayList<>();
                            do {
                                Room r = new Room(rs.getInt(5), rs.getString(6),
                                        rs.getString(8), rs.getInt(7), rs.getDouble(9),
                                        rs.getDouble(10));
                                r.setSelected(rs.getBoolean(4));
                                rooms.add(r);
                            } while(rs.next() && resID == rs.getInt(1));
                            if (LocalDate.now().until(endDate, ChronoUnit.DAYS) > 0) {
                                Reservation res = new Reservation(startDate, endDate, rooms);
                                res.setResID(resID);
                                reservations.add(res);
                            }
                            rs.previous();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                    return reservations;
                }
            };
        }         
    };
    
    @FXML
    private void nextHandler(ActionEvent event) {
        manager.setPartialReservation((Reservation)reservationSelect.getValue());
        if (manager.getState() == ScreenManager.ReservationState.UPDATE) {
            manager.setScreen("UpdateReservationView", null);
        } else if (manager.getState() == ScreenManager.ReservationState.CANCEL) {
            manager.setScreen("CancelReservationView", null);
        }
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("CustomerMenuView", null);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        availableReservations = FXCollections.observableArrayList();
        reservationSelect.setItems(availableReservations);
        BooleanBinding disableNext = Bindings.createBooleanBinding(() -> {
            if (reservationSelect.getValue() == null) {
                errorLabel.setVisible(false);
                return true;
            } else {
                Reservation res = (Reservation) reservationSelect.getValue();
                if (manager.getState() == ScreenManager.ReservationState.UPDATE &&
                        LocalDate.now().until(res.getStartDate(), ChronoUnit.DAYS) <= 3) {
                    errorLabel.setVisible(true);
                    return true;
                } else {
                    errorLabel.setVisible(false);
                    return false;
                }
            }
        }, reservationSelect.valueProperty());
        nextButton.disableProperty().bind(disableNext);
        reservationsService.setOnSucceeded((final WorkerStateEvent event) -> {
            availableReservations.setAll((List<Reservation>)event.getSource().getValue());
            parent.setDisable(false);
        });
    }    

    @Override
    public void onSet(List arguments) {
        reservationsService.restart();
    }

    @Override
    public void cleanUp() {
        availableReservations.clear();
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
    
}
