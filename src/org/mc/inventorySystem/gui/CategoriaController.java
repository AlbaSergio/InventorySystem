/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Categoria;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class CategoriaController implements Initializable {

    @FXML
    private TextField txtCategoria;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnEliminar;

    @FXML
    private JFXTextField txtBuscar;

    @FXML
    private TableView<Categoria> tblCategoria;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    private Button btnPrincipal;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    ObservableList<Categoria> categoriaList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Mapeo de columnas de la tabla tblCategoria
        this.colNombre.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getNombre()));

        categoriaList = FXCollections.observableArrayList();

        getAllCategories();
    }

    public void getAllCategories() {
        //Aquí guardatemos los objetos de tipo Categoría. Una lista es un contenedor dinámico de objetos.
        ObservableList<Categoria> obc = FXCollections.observableArrayList();

        //Definimos la consulta SQL:
        String sql = "SELECT * FROM categoria WHERE estatus = 1";

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(resultSet.getInt("idCategoria"));
                c.setNombre(resultSet.getString("nombre"));
                obc.add(c);
            }

            tblCategoria.setItems(obc);

            categoriaList = obc;
        } catch (SQLException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
     public void closeWindows() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/Principal.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.show();

            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();

        } catch (IOException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
