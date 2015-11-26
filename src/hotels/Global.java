/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Global {
    public static String URL = "jdbc:mysql://academic-mysql.cc.gatech.edu/cs4400_Group_9";
    public static String USERNAME = "cs4400_Group_9";
    public static String PASSWORD = "nnKXZ0Y2";
    private static Global global = new Global();
    private double width;
    private double height;
  
    private Global() {
        //Singleton consturctor
    }
    
    public static Global getInstance() {
        return global;
    }
    
    public double getAspectRatio() {
        return global.width / global.height;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setScreenDimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public void initConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
 
}
