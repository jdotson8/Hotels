/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotels;

import java.util.List;

/**
 *
 * @author Administrator
 */
public interface ScreenController {
    public void onSet(List arguments);
    public void cleanUp();
    public void setManager(ScreenManager manager);
}
