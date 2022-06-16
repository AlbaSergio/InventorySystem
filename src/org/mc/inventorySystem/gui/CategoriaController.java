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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
    private TextArea txtDescripcion;

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
    private TableColumn<Categoria, String> colDescripcion;

    @FXML
    private Button btnPrincipal;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    ObservableList<Categoria> categoriaList;
    ObservableList<Categoria> filtroCategoria;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Mapeo de columnas de la tabla tblCategoria
        this.colNombre.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getNombreCategoria()));
        this.colDescripcion.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getDescripcion()));
        categoriaList = FXCollections.observableArrayList();
        filtroCategoria = FXCollections.observableArrayList();

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
                c.setNombreCategoria(resultSet.getString("nombreCategoria"));
                c.setDescripcion(resultSet.getString("descripcion"));
                obc.add(c);
            }

            tblCategoria.setItems(obc);

            categoriaList = obc;
        } catch (SQLException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
    En este método se hace la inserción de Objetos de tipo Categoria para los registros
    podemos hacer diversas inserciones y al realizarse nos dara un Alert donde podremos 
    ver que la insercion se esta haciendo de manera correcta, en caso de no ser asi recibiremos un 
    Alert avisandonos que algo salio mal y debemos verificar.
     */
    public void insertCategories(ActionEvent event) {
        String nombre, descripcion;
        nombre = txtCategoria.getText().toString();
        descripcion = txtDescripcion.getText().toString();

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que realizara la inserción del registro:
        String sql = "INSERT INTO categoria(nombreCategoria, descripcion) VALUES(?, ?)";

        try {
            // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
            // que se genera al realizar la inserción del registro.
            preparedStatement = connection.prepareStatement(sql);

            //Llenamos el valor de cada campo de la consulta SQL definida antes:
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);

            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Categoría");
            alert.setHeaderText("Categoría registrada correctamente.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllCategories();
        clearField();
    }

    public void updateCategories() throws SQLException {
        String nombre, descripcion;

        int idDelete = tblCategoria.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblCategoria.getItems().get(idDelete).getIdCategoria()));

        nombre = txtCategoria.getText().toString();
        descripcion = txtDescripcion.getText().toString();

        //Definimos la consulta SQL que realiza la inserción del registro:
        String sql = "UPDATE categoria SET nombreCategoria = ?, descripcion = ? WHERE idCategoria = ?";

        // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
        // que se genera al realizar la inserción del registro.
        preparedStatement = connection.prepareStatement(sql);

        //Llenamos el valor de cada campo de la consulta SQL definida antes:
        preparedStatement.setString(1, nombre);
        preparedStatement.setString(2, descripcion);
        preparedStatement.setInt(3, id);

        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Categoría");

        alert.setHeaderText("Actualizada");
        alert.setContentText("¡Se actualizo correctamente el registro!");

        alert.showAndWait();

        getAllCategories();
        clearField();
    }

    public void deleteCategories() throws SQLException {
        int idDelete = tblCategoria.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblCategoria.getItems().get(idDelete).getIdCategoria()));

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        String sql = "UPDATE categoria SET estatus = 0 WHERE idCategoria = ?;";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Categoría");

        alert.setHeaderText("Eliminada");
        alert.setContentText("¡Se elimino correctamente el registro!");

        alert.showAndWait();

        getAllCategories();
        clearField();

    }

    public void showDetailCategories() {
        Categoria c = this.tblCategoria.getSelectionModel().getSelectedItem();

        if (c != null) {
            this.txtCategoria.setText(c.getNombreCategoria());
            this.txtDescripcion.setText(c.getDescripcion());
        }
    }

    @FXML
    private void filterTable(KeyEvent event) {
        String filtro = this.txtBuscar.getText();

        if (filtro.isEmpty()) {
            this.tblCategoria.setItems(categoriaList);
        } else {
            //Limpiamos la lista
            this.filtroCategoria.clear();

            for (Categoria c : this.categoriaList) {
                if (c.getNombreCategoria().contains(filtro) || c.getDescripcion().contains(filtro)) {
                    this.filtroCategoria.add(c);
                }
            }

            this.tblCategoria.setItems(filtroCategoria);
        }
    }

    public void clearField() {
        txtCategoria.setText("");
        txtDescripcion.setText("");
    }

    public void backToPrincipal() {

        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/PrincipalAdmin.fxml"));

            // Cargo el padre
            Parent root = loader.load();

            // Obtengo el controlador
            PrincipalController controlador = loader.getController();

            // Creo la scene y el stage            
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Asocio el stage con el scene
            stage.setTitle("Sistema de Control");
            stage.setScene(scene);
            stage.show();

            // Indico que debe hacer al cerrar
            stage.setOnCloseRequest(e -> controlador.closeWindows());
            //Ciero la ventana donde estoy
            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();
        } catch (IOException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeWindows() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/PrincipalAdmin.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle("Sistema de Control");
            stage.setScene(scene);
            stage.show();

            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();

        } catch (IOException ex) {
            Logger.getLogger(CategoriaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
