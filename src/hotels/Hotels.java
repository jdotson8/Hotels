/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Administrator
 */
public class Hotels extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ScreenManager manager = new ScreenManager(1024, 720);
        manager.loadScreen("LoginView", "LoginView.fxml");
        manager.loadScreen("UserRegistrationView", "UserRegistrationView.fxml");
        manager.loadScreen("CustomerMenuView", "CustomerMenuView.fxml");
        manager.loadScreen("ManagerMenuView", "ManagerMenuView.fxml");
        manager.loadScreen("MakeReservationView", "MakeReservationView.fxml");
        manager.loadScreen("ReservationDetailsView", "ReservationDetailsView.fxml");
        manager.loadScreen("PaymentView", "PaymentView.fxml");
        manager.loadScreen("ConfirmationView", "ConfirmationView.fxml");
        manager.loadScreen("SelectReservationView", "SelectReservationView.fxml");
        manager.loadScreen("UpdateReservationView", "UpdateReservationView.fxml");
        manager.loadScreen("CancelReservationView", "CancelReservationView.fxml");
        manager.loadScreen("CreateReviewView", "CreateReviewView.fxml");
        manager.loadScreen("ReadReviewView", "ReadReviewView.fxml");
        manager.loadScreen("ReservationReportView", "ReservationReportView.fxml");
        manager.loadScreen("RoomReportView", "RoomReportView.fxml");
        manager.loadScreen("RevenueReportView", "RevenueReportView.fxml");
        Scene scene = new Scene(manager.getRoot(), 1024, 720);
        
        stage.setScene(scene);
        stage.show();
        manager.setScreen("LoginView", null);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
