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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Proveedor;

/**
 * FXML Controller class
 *
 * @author Sergio Alba Arguello
 */
public class ProveedorController implements Initializable {

    @FXML
    private TextField txtEmpresa;

    @FXML
    private TextField txtContacto;

    @FXML
    private TextField txtDireccion;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Proveedor> tblProveedor;

    @FXML
    private TableColumn<Proveedor, String> colNombre;

    @FXML
    private TableColumn<Proveedor, String> colContacto;

    @FXML
    private TableColumn<Proveedor, String> colDireccion;

    @FXML
    private JFXTextField txtBuscar;

    @FXML
    private Button btnPrincipal;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    ObservableList<Proveedor> proveedorList;
    ObservableList<Proveedor> filtroProveedor;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Mapeado de columnas de la tabla tblProveedor
        this.colNombre.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getEmpresa()));
        this.colContacto.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getContacto()));
        this.colDireccion.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getDireccion()));

        proveedorList = FXCollections.observableArrayList();
        filtroProveedor = FXCollections.observableArrayList();

        getAllProviders();
    }

    /*
     En este método se hace la carga dinámica de los objetos de tipo Proveedor 
     que se encuentran dentro de la BD en base a su estatus recordando
     que el estatus en 1 quiere decir que se encuentrá activo y el 0 es que 
     ya no se encuentra activo dentro del inventario.
     */
    public void getAllProviders() {
        //Aquí guardatemos los objetos de tipo Proveedor. Una lista es un contenedor dinámico de objetos.
        ObservableList<Proveedor> obp = FXCollections.observableArrayList();

        //Definimos la consulta SQL:
        String sql = "SELECT * FROM proveedor WHERE estatus = 1";

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(resultSet.getInt("idProveedor"));
                p.setEmpresa(resultSet.getString("empresa"));
                p.setContacto(resultSet.getString("contacto"));
                p.setDireccion(resultSet.getString("direccion"));
                obp.add(p);
            }

            tblProveedor.setItems(obp);

            proveedorList = obp;
        } catch (SQLException ex) {
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insertProviders(ActionEvent event) {
        String nombreEmpresa, contacto, direccion;

        nombreEmpresa = txtEmpresa.getText().toString();
        contacto = txtContacto.getText().toString();
        direccion = txtDireccion.getText().toString();

        //Con este objeto abrimos la conexion a la base de datos
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que realizará la inserción del registro:
        String sql = "INSERT INTO proveedor( empresa, contacto, direccion)"
                + "VALUES(?, ?, ?)";

        try {
            // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
            // que se genera al realizar la inserción del registro.
            preparedStatement = connection.prepareStatement(sql);

            //Llenamos el valor de cada campo de la consulta SQL definida:
            preparedStatement.setString(1, nombreEmpresa);
            preparedStatement.setString(2, contacto);
            preparedStatement.setString(3, direccion);

            //Ejecutamos la consulta SQL:
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Proveedores");
            alert.setHeaderText("Proveedor registrado correctamente.");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllProviders();
        clearfields();
    }

    public void updateProviders(ActionEvent event) {
        String nombreEmpresa, contacto, direccion;

        int idDelete = tblProveedor.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblProveedor.getItems().get(idDelete).getIdProveedor()));

        nombreEmpresa = txtEmpresa.getText().toString();
        contacto = txtContacto.getText().toString();
        direccion = txtDireccion.getText().toString();

        String sql = "UPDATE proveedor SET empresa = ?, contacto = ?, direccion = ? WHERE idProveedor = ?";

        try {
            // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
            // que se genera al realizar la inserción del registro.
            preparedStatement = connection.prepareStatement(sql);

            //Llenamos el valor de cada campo de la consulta SQL definida:
            preparedStatement.setString(1, nombreEmpresa);
            preparedStatement.setString(2, contacto);
            preparedStatement.setString(3, direccion);
            preparedStatement.setInt(4, id);

            //Ejecutamos la consulta SQL:
            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Proveedores");
            alert.setHeaderText("¡Actualizado!");
            alert.setContentText("¡Se actualizo correctamente el registro!");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllProviders();
        clearfields();

    }

    public void deletProviders(ActionEvent event) throws SQLException {
        int idDelete = tblProveedor.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblProveedor.getItems().get(idDelete).getIdProveedor()));

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        String sql = "UPDATE proveedor SET estatus = 0 WHERE idProveedor = ?";

        // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
        // que se genera al realizar la inserción del registro.
        preparedStatement = connection.prepareStatement(sql);

        //Llenamos el valor de cada campo de la consulta SQL definida:
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Proveedores");
        alert.setHeaderText("¡Eliminado!");
        alert.setContentText("¡Se elininó correctamente el registro!");
        alert.showAndWait();

        getAllProviders();
        clearfields();
    }

    public void showDetailProviders() {
        Proveedor p = this.tblProveedor.getSelectionModel().getSelectedItem();

        if (p != null) {
            this.txtEmpresa.setText(p.getEmpresa());
            this.txtContacto.setText(p.getContacto());
            this.txtDireccion.setText(p.getDireccion());
        }

    }

    @FXML
    private void filterTable(KeyEvent event) {
        String filtro = this.txtBuscar.getText();

        if (filtro.isEmpty()) {
            this.tblProveedor.setItems(proveedorList);
        } else {
            //Limpiamos la lista
            this.filtroProveedor.clear();

            for (Proveedor p : this.proveedorList) {
                if (p.getEmpresa().contains(filtro) || p.getContacto().contains(filtro)
                        || p.getDireccion().contains(filtro)) {

                    this.filtroProveedor.add(p);
                }
            }

            this.tblProveedor.setItems(filtroProveedor);
        }
    }

    public void clearfields() {
        txtEmpresa.setText("");
        txtContacto.setText("");
        txtDireccion.setText("");
    }

    public Proveedor fill(ResultSet rs) throws SQLException {
        //Creamos una nueva insatncia de Proveedor:
        Proveedor p = new Proveedor();

        //Llenamos sus propiedades:
        p.setIdProveedor(rs.getInt("idProveedor"));
        p.setEmpresa(rs.getString("empresa"));
        p.setContacto(rs.getString("contacto"));
        p.setDireccion(rs.getString("direccion"));
        p.setEstatus(rs.getInt("estatus"));

        //Devolvemos el objeto de tipo Proveedor;
        return p;
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
            stage.getIcons().add(new Image("/org/mc/inventorySystem/res/logoMC.png"));
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
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*Botones para Cerrar ventana*/
    public void closeWindows() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/PrincipalAdmin.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/org/mc/inventorySystem/res/logoMC.png"));
            stage.setTitle("Sistema de Control");
            stage.setScene(scene);
            stage.show();

            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
