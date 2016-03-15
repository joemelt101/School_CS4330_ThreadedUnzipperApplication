/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bunzipper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.UIStage;

/**
 *
 * @author Jared
 */
public class Jomr5bUnzipper extends Application
{
    
    @Override
    public void start(Stage stage) throws Exception
    {   
        UIStage uiStage = new UIStage(stage);
        uiStage.displayScene(uiStage.loadScene("Home", getClass().getResource("Home.fxml")));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
