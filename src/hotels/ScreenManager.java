/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class ScreenManager extends AnimationTimer {
    private static String URL = "jdbc:mysql://academic-mysql.cc.gatech.edu/cs4400_Group_9";
    private static String ACCESSOR = "cs4400_Group_9";
    private static String PASSWORD = "nnKXZ0Y2";
    
    private static final int DURATION =  10;
    private static final int IMAGE_COUNT = 7;
    private static final int BLUR_THRESHOLD = 25;
    private static final int BLUR_ITERATIONS = 2;
    private static final double TRANSITION_DURATION = 1;
    private static final double BORDER_SIZE = 32;
    
    private Pane root;
    private ImageView slideShow;
    private ImageView foggy;
    private Pane foggyPane;
    private Rectangle foggyClip;
    private Image next;
    
    private HashMap<String, Pane> screens;
    private HashMap<String, ScreenController> controllers;
    private Pane screenPane;
    private Rectangle screenBackground;
    
    private Random random;
    private int switchTimer;
    private double direction;
    private double speed;
    
    private double screenWidth;
    private double screenHeight;
    private double aspectRatio;
    
    private User user;
    private Reservation partialReservation;
    
    private Service<Image> nextLoader = new Service() {
        @Override
        protected Task<Image> createTask() {
            return new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(getClass().getResource(String.format("/images/image%d.jpg", random.nextInt(IMAGE_COUNT))).toExternalForm());
                }
            };
        }
                
    };
    
    public ScreenManager(float width, float height) {
        initSQLDriver();
        
        screenWidth = width;
        screenHeight = height;
        aspectRatio = width / height;
        random = new Random();
        
        screens = new HashMap<>();
        controllers = new HashMap<>();
        
        slideShow = new ImageView();
        foggy = new ImageView();
        foggyPane = new Pane();
        foggyClip = new Rectangle();
        screenPane = new Pane();
        screenBackground = new Rectangle();
        
        root = new Pane();
        root.getChildren().addAll(slideShow, foggyPane, screenBackground, screenPane);
        foggyPane.getChildren().add(foggy);
        
        nextLoader.setOnSucceeded((final WorkerStateEvent event) -> {
            next = (Image)event.getSource().getValue();
        });
        
        slideShow.setFitWidth(width);
        slideShow.setFitHeight(height);
        next = new Image(getClass().getResource(String.format("/images/image%d.jpg", random.nextInt(IMAGE_COUNT) + 1)).toExternalForm());
        setImage();
        
        foggy.fitWidthProperty().bind(slideShow.fitWidthProperty());
        foggy.fitHeightProperty().bind(slideShow.fitHeightProperty());
        foggy.imageProperty().bind(slideShow.imageProperty());
        foggy.viewportProperty().bind(slideShow.viewportProperty());
        foggy.opacityProperty().bind(slideShow.opacityProperty());
        foggy.setEffect(new BoxBlur(BLUR_THRESHOLD, BLUR_THRESHOLD, BLUR_ITERATIONS));
        
        foggyPane.setClip(foggyClip);
        foggyPane.setStyle("-fx-background-color: #FFFFFF;");
        
        foggyClip.layoutXProperty().bind(screenBackground.layoutXProperty());
        foggyClip.layoutYProperty().bind(screenBackground.layoutYProperty());
        foggyClip.widthProperty().bind(screenBackground.widthProperty());
        foggyClip.heightProperty().bind(screenBackground.heightProperty());
        
        screenBackground.setLayoutX(BORDER_SIZE);
        screenBackground.setLayoutY(BORDER_SIZE);
        screenBackground.setFill(Color.ALICEBLUE);
        screenBackground.setOpacity(0.6);
        
        screenPane.setLayoutX(BORDER_SIZE);
        screenPane.setLayoutY(BORDER_SIZE);
        
        switchTimer = 60 * DURATION;
        start();
    }    

    @Override
    public void handle(long now) {
        switchTimer--;
        if (switchTimer <= 0) {
            setImage();
            switchTimer = 60 * DURATION;
        } else {
            Rectangle2D oldView = slideShow.getViewport();
            Rectangle2D newView = new Rectangle2D(
                    oldView.getMinX() + speed * Math.cos(direction),
                    oldView.getMinY() + speed * Math.sin(direction),
                    oldView.getWidth(),
                    oldView.getHeight());
            slideShow.setViewport(newView);
            if (switchTimer >= 0.95 * 60 * DURATION) {
                slideShow.setOpacity(1 - (switchTimer - 0.95 * 60 * DURATION) / (0.05 * 60 * DURATION));
            } else if (switchTimer <= 0.05 * 60 * DURATION) {
                slideShow.setOpacity(switchTimer / (0.05 * 60 * DURATION));
            }
        }
    }
    
    private void setImage() {
        double ratio = next.getHeight() * aspectRatio / next.getWidth();
        double width, height;
        if (ratio > 1) {
            height = 0.75 * next.getHeight();
            width = aspectRatio * height;
        } else {
            width = 0.75 * next.getWidth();
            height = width / aspectRatio;
        }
        double fromX = random.nextDouble() * (next.getWidth() - width);
        double fromY = random.nextDouble() * (next.getHeight() - height);
        double toX = random.nextDouble() * (next.getWidth() - width);
        double toY = random.nextDouble() * (next.getHeight() - height);
        direction = Math.atan2(toY - fromY, toX - fromX);
        speed = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2)) / (60 * DURATION);
        slideShow.setImage(next);
        slideShow.setViewport(new Rectangle2D(fromX, fromY, width, height));
        nextLoader.restart();
    }
    
    public Pane getRoot() {
        return root;
    }
    
    public void loadScreen(String name, String fileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(String.format("/fxml/%s", fileName)));
            Pane parent = (Pane) loader.load();
            ScreenController controller = (ScreenController) loader.getController();
            controller.setManager(this);
            screens.put(name, parent);
            controllers.put(name, controller);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void setScreen(String name, List arguments) {
        Pane nextScreen = screens.get(name);
        ScreenController controller = controllers.get(name);
        if (nextScreen != null && controller != null) {
            controller.onSet(arguments);
            if (!screenPane.getChildren().isEmpty()) {
                screenPane.getChildren().get(0).setDisable(true);
                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.seconds(TRANSITION_DURATION / 3.0),
                        new KeyValue(screenPane.opacityProperty(), 1.0)
                    )
                );
                Timeline resize = new Timeline(
                    new KeyFrame(Duration.seconds(TRANSITION_DURATION / 3.0),
                        (ActionEvent event) -> {
                            nextScreen.setDisable(false);
                            fadeIn.play();
                        },
                        new KeyValue(screenBackground.widthProperty(), nextScreen.getPrefWidth()),
                        new KeyValue(screenBackground.heightProperty(), nextScreen.getPrefHeight())
                    )
                );
                Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.seconds(TRANSITION_DURATION / 3.0),
                        (ActionEvent event) -> {
                            screenPane.getChildren().clear();
                            screenPane.getChildren().add(nextScreen);
                            resize.play();
                        },
                        new KeyValue(screenPane.opacityProperty(), 0.0))
                );
                
                fadeOut.play();
            } else {
                screenBackground.setWidth(nextScreen.getPrefWidth());
                screenBackground.setHeight(nextScreen.getPrefHeight());
                screenPane.getChildren().add(nextScreen);
            }
        } else {
            System.err.println("Screen has not been loaded.");
        }
    }
    
    private void initSQLDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL, ACCESSOR, PASSWORD);
    }
    
    public void login(User user) {
        this.user = user;
    }
    
    public void setPartialReservation(Reservation partialReservation) {
        this.partialReservation = partialReservation;
    }
    
    public Reservation getPartialReservation() {
        return partialReservation;
    }
    
    public User getUser() {
        return new User(user.getUsername(), user.isManager());
    }
}
