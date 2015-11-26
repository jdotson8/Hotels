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
        ScreenManager controller = new ScreenManager(1024, 720);
        controller.loadScreen("LoginView", "LoginView.fxml");
        controller.loadScreen("UserRegistrationView", "UserRegistrationView.fxml");
        controller.loadScreen("CustomerMenuView", "CustomerMenuView.fxml");
        controller.loadScreen("ManagerMenuView", "ManagerMenuView.fxml");
        controller.loadScreen("MakeReservationView", "MakeReservationView.fxml");
        Scene scene = new Scene(controller.getRoot(), 1024, 720);
        
        stage.setScene(scene);
        stage.show();
        controller.setScreen("LoginView");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
