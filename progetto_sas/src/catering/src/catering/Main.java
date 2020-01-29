/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catering;

import catering.businesslogic.CateringAppManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Davide
 */
public class Main extends Application {
    
    private Stage primaryStage;
    private CateringAppManager app;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        */
        
        this.primaryStage = primaryStage;
        this.app = CateringAppManager.getInstance();

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent main = mainLoader.load();
        Scene mainScene = new Scene(main);

        MainController mainController = mainLoader.getController();

        primaryStage.setScene(mainScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        // primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
