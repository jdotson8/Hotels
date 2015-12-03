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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ReservationReportViewController implements  ScreenController, Initializable {
    private static String reportQuery =
            "SELECT Res_Location, COUNT(*) "
            + "FROM Reservation "
            + "WHERE MONTH(Start_Date) = '%d' "
            + "GROUP BY Res_Location;";
    
    @FXML
    private Pane parent;
    
    @FXML
    private ComboBox<ScreenManager.Month> monthSelect;
    
    @FXML
    private Button reportButton;
    
    @FXML
    private TableView reportTable;
    
    @FXML
    private TableColumn locationColumn;
    
    @FXML
    private TableColumn reservationsColumn;
    
    private ScreenManager manager;
    
    private ObservableList<ResReportEntry> entries;
    
    private class ReportService extends Service<List<ResReportEntry>> {
        private int date;
        
        public void setReportInfo(int date) {
            this.date = date;
        }
        
        @Override
        protected Task<List<ResReportEntry>> createTask() {
            return new Task<List<ResReportEntry>>() {
                @Override
                protected List<ResReportEntry> call() throws Exception {
                    List<ResReportEntry> result = new ArrayList<>();
                    try (Connection con = manager.openConnection();
                        Statement s = con.createStatement();) {
                        String query = String.format(reportQuery, date);
                        ResultSet rs = s.executeQuery(query);
                        while (rs.next()) {
                            result.add(new ResReportEntry(rs.getString(1), rs.getInt(2)));
                        }
                    } catch (Exception ex) {
                        throw ex;
                    }
                    return result;
                }
            };
        }         
    }
    private ReportService reportService = new ReportService();
    
    @FXML
    private void reportHandler(ActionEvent event) {
        parent.setDisable(true);
        reportService.setReportInfo(monthSelect.getValue().ordinal() + 1);
        reportService.restart();
    }
    
    @FXML
    private void backHandler(ActionEvent event) {
        manager.setScreen("ManagerMenuView", null);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monthSelect.setItems(FXCollections.observableArrayList(ScreenManager.Month.values()));
        entries = FXCollections.observableArrayList();
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        reservationsColumn.setCellValueFactory(new PropertyValueFactory<>("resCount"));
        reportTable.setItems(entries);
        
        reportService.setOnSucceeded((final WorkerStateEvent event) -> {
            parent.setDisable(false);
            entries.setAll((List<ResReportEntry>)event.getSource().getValue());
        });
        
        reportButton.disableProperty().bind(Bindings.createBooleanBinding(() -> 
            monthSelect.getValue() == null, monthSelect.valueProperty()));
    }    

    @Override
    public void onSet(List arguments) {
        parent.setDisable(false);
    }

    @Override
    public void cleanUp() {
        
    }

    @Override
    public void setManager(ScreenManager manager) {
        this.manager = manager;
    }
}
