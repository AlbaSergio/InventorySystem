/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Sergio Alba Arguello
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/mc/inventorySystem/gui/fxml/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image("/org/mc/inventorySystem/res/logoMC.png"));
        primaryStage.setTitle("Sistema de Control");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
