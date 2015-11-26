/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

/**
 *
 * @author Administrator
 */
public class User {
    private String username;
    private boolean isManager;
    
    public User(String username, boolean isManager) {
        this.username = username;
        this.isManager = isManager;
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean isManager() {
        return isManager;
    }
}
