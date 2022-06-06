/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Usuario;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class UsuarioController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApePaterno;

    @FXML
    private TextField txtApeMaterno;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtContrasena;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Usuario> tblUsuario;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colUsuario;

    @FXML
    private TableColumn<Usuario, String> colContrasena;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private Button btnPrincipal;

    ObservableList<Usuario> userList;
    ObservableList<Usuario> filtroUser;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mapeado de columnas de la tabla tblUsuario
        this.colNombre.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getNombre() + " "
                + param.getValue().getApellidoPaterno() + " " + param.getValue().getApellidoMaterno()));
        this.colUsuario.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getNombreUsuario()));
        this.colContrasena.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getContrasenia()));
        this.colRol.setCellValueFactory(parm -> new SimpleObjectProperty<>(parm.getValue().getRol()));

        ///Inicializamos la estructura de datos para poder utilizarla 
        userList = FXCollections.observableArrayList();
        filtroUser = FXCollections.observableArrayList();

        getAllUsers();

        ObservableList<String> roles = FXCollections.observableArrayList(
                "Admin",
                "Colaborador"
        );
        cmbRol.setItems(roles);
    }

    public void insertUser() {
        String nombre, apePaterno, apeMaterno, usuario, contrasenia, rol;

        nombre = txtNombre.getText().toString();
        apePaterno = txtApePaterno.getText().toString();
        apeMaterno = txtApeMaterno.getText().toString();
        usuario = txtUsuario.getText().toString();
        contrasenia = txtContrasena.getText().toString();
        rol = cmbRol.getSelectionModel().getSelectedItem();

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que realizara la inserción del registro:
        String sql = "INSERT INTO usuario(nombre, apellidoPaterno, apellidoMaterno, nombreUsuario, contrasenia, rol)"
                + "VALUES(?, ?, ?, ?, ?, ?);";

        try {
            // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
            // que se genera al realizar la inserción del registro.
            preparedStatement = connection.prepareStatement(sql);

            //Llenamos el valor de cada campo de la consulta SQL definida antes:
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apePaterno);
            preparedStatement.setString(3, apeMaterno);
            preparedStatement.setString(4, usuario);
            preparedStatement.setString(5, contrasenia);
            preparedStatement.setString(6, rol);

            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Usuarios");
            alert.setHeaderText("Usuario registrado correctamente.");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllUsers();
        clearField();

    }

    public void updateUser(ActionEvent event) throws SQLException {
        String nombre, apePaterno, apeMaterno, usuario, contrasenia, rol;

        int idDelete = tblUsuario.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblUsuario.getItems().get(idDelete).getIdUsuario()));

        nombre = txtNombre.getText().toString();
        apePaterno = txtApePaterno.getText().toString();
        apeMaterno = txtApeMaterno.getText().toString();
        usuario = txtUsuario.getText().toString();
        contrasenia = txtContrasena.getText().toString();
        rol = cmbRol.getSelectionModel().getSelectedItem();

        //Definimos la consulta SQL que realiza la inserción del registro:
        String sql = "UPDATE usuario SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, nombreUsuario = ?,"
                + "contrasenia = ?, rol = ? WHERE idUsuario = ?;";

        // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
        // que se genera al realizar la inserción del registro.
        preparedStatement = connection.prepareStatement(sql);

        //Llenamos el valor de cada campo de la consulta SQL definida antes:
        preparedStatement.setString(1, nombre);
        preparedStatement.setString(2, apePaterno);
        preparedStatement.setString(3, apeMaterno);
        preparedStatement.setString(4, usuario);
        preparedStatement.setString(5, contrasenia);
        preparedStatement.setString(6, rol);
        preparedStatement.setInt(7, id);
        System.out.println(idDelete+" "+id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Usuarios");

        alert.setHeaderText("¡Actualizado!");
        alert.setContentText("¡Se actualizo correctamente el registro!");

        alert.showAndWait();
        System.out.println(id);

        getAllUsers();
        clearField();
    }

    public void deleteUser(ActionEvent event) throws SQLException {
        int idDelete = tblUsuario.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblUsuario.getItems().get(idDelete).getIdUsuario()));

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();
        
        String sql = "UPDATE usuario SET estatus = 0 WHERE idUsuario = ?;";
        
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Usuarios");
        alert.setHeaderText("¡Eliminado!");
        alert.setContentText("¡Se eliminó el registro correctamente!");
        alert.showAndWait();
        
        getAllUsers();
        clearField();
        
    }

    public void getAllUsers() {
        //Aquí guardatemos los objetos de tipo Pintura. Una lista es un contenedor dinámico de objetos.
        ObservableList<Usuario> obu = FXCollections.observableArrayList();

        //Definimos la consulta SQL:
        String sql = "SELECT * FROM usuario WHERE estatus = 1;";

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(resultSet.getInt("idUsuario"));
                u.setNombre(resultSet.getString("nombre"));
                u.setApellidoPaterno(resultSet.getString("apellidoPaterno"));
                u.setApellidoMaterno(resultSet.getString("apellidoMaterno"));
                u.setNombreUsuario(resultSet.getString("nombreUsuario"));
                u.setContrasenia(resultSet.getString("contrasenia"));
                u.setRol(resultSet.getString("rol"));
                obu.add(u);
            }

            tblUsuario.setItems(obu);

            userList = obu;
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showDetailUser() {
        Usuario u = this.tblUsuario.getSelectionModel().getSelectedItem();

        if (u != null) {
            this.txtNombre.setText(u.getNombre());
            this.txtApePaterno.setText(u.getApellidoPaterno());
            this.txtApeMaterno.setText(u.getApellidoMaterno());
            this.txtUsuario.setText(u.getNombreUsuario());
            this.txtContrasena.setText(u.getContrasenia());
            this.cmbRol.getSelectionModel().select(u.getRol());
        }
    }

    public void clearField() {
        txtNombre.setText("");
        txtApePaterno.setText("");
        txtApeMaterno.setText("");
        txtUsuario.setText("");
        txtContrasena.setText("");
        cmbRol.getSelectionModel().clearSelection();
    }

    public void backToPrincipal() {

        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/Principal.fxml"));

            // Cargo el padre
            Parent root = loader.load();

            // Obtengo el controlador
            PrincipalController controlador = loader.getController();

            // Creo la scene y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Asocio el stage con el scene
            stage.setScene(scene);
            stage.show();

            // Indico que debe hacer al cerrar
            stage.setOnCloseRequest(e -> controlador.closeWindows());
            //Ciero la ventana donde estoy
            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();
        } catch (IOException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*Botones para Cerrar ventana*/
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
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
