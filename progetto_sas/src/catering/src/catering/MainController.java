/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering;

import catering.businesslogic.CateringAppManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Davide
 */
public class MainController {
    
    @FXML
    private BorderPane mainPane;

    @FXML
    private Label userName;
    
    @FXML
    public void initialize() {
        userName.setText(CateringAppManager.userManager.getCurrentUser().toString());

        try {
            FXMLLoader menuListLoader = new FXMLLoader(getClass().getResource("eventlist.fxml"));
            Parent menuList = menuListLoader.load();
            EventListController eventListController = menuListLoader.getController();

            mainPane.setCenter(menuList);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }  
    
}
