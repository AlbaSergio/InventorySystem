/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Categoria;
import org.mc.inventorySystem.core.model.Inventario;
import org.mc.inventorySystem.core.model.Producto;
import org.mc.inventorySystem.core.model.Proveedor;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class InventarioController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private ComboBox<String> cmbCapacidad;

    @FXML
    private ComboBox<Proveedor> cmbProveedor;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtCantidad;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnPrincipal;

    @FXML
    private JFXTextField txtBuscar;

    @FXML
    private TableView<Inventario> tblInventario;

    @FXML
    private TableColumn<Inventario, String> colNombre;

    @FXML
    private TableColumn<Inventario, String> colMarca;

    @FXML
    private TableColumn<Inventario, Integer> colCategoria;

    @FXML
    private TableColumn<Inventario, Integer> colProveedor;

    @FXML
    private TableColumn<Inventario, String> colCapacidad;

    @FXML
    private TableColumn<Inventario, Double> colPrecio;

    @FXML
    private TableColumn<Inventario, ?> colCantidad;

    ObservableList<Inventario> inventarioList;
    ObservableList<Inventario> filtroInventario;
    ObservableList<Categoria> categorias;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Mapeado de las columnas de la tabla tblInventario:
        this.colNombre.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getNombre()));
        this.colMarca.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getMarca()));
        this.colCategoria.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getIdCategoria().getIdCategoria()));
        this.colProveedor.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getIdProveedor().getIdProveedor()));
        this.colCapacidad.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getCapacidad()));
        this.colPrecio.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getProducto().getPrecio()));
        this.colCantidad.setCellValueFactory(new PropertyValueFactory("cantidad"));

        ///Inicializamos la estructura de datos para poder utilizarla 
        filtroInventario = FXCollections.observableArrayList();
        inventarioList = FXCollections.observableArrayList();

        getAllInventory();
        try {
            fillCategories();
            fillProviders();

        } catch (SQLException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<String> capacidad = FXCollections.observableArrayList(
                "400 ml",
                "1 LT",
                "4 LT",
                "19 LT");
        cmbCapacidad.setItems(capacidad);
    }

    public void getAllInventory() {
        // Aquí se guardarán los objetos de tipo Inventario
        //Una lista es un contenedor dinámico de objetos.
        ObservableList<Inventario> obi = FXCollections.observableArrayList();

        //Definir la consulta SQL:
        String sql = "SELECT * FROM v_inventario WHERE estatus = 1;";

        Inventario i = null;
        //Con este objeto abrimos la conexión a la base de datos
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            //Recorremos el ResultSet
            while (resultSet.next()) {
                i = fill(resultSet);
                obi.add(i);
            }

            tblInventario.setItems(obi);

            inventarioList = obi;
        } catch (SQLException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void insertInventory() throws SQLException {
        String nombre, marca, descripcion, capacidad;
        Categoria categoria;
        Proveedor proveedor;
        double precio;
        int cantidad;

        nombre = txtNombre.getText().toString();
        marca = txtMarca.getText().toString();
        descripcion = txtDescripcion.getText().toString();
        categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        proveedor = cmbProveedor.getSelectionModel().getSelectedItem();
        capacidad = cmbCapacidad.getSelectionModel().getSelectedItem();
        precio = Double.parseDouble(txtPrecio.getText());
        cantidad = Integer.parseInt(txtCantidad.getText());

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que realizara la inserción del registro:
        String sql = "CALL insertarInventario(?, ?, ?, ?, ?, ?, ?," // <- Valores de Producto
                + " ?," // <- Valores de Inventario
                + " ?, ?)"; // <- Valores de retorno

        //Aquí guardaremos los ID's que se generán: 
        int idProducto = -1;
        int idInventario = -1;

        //Con este objeto invocaremos el StoredProcedure:
        CallableStatement cstmt = connection.prepareCall(sql);

        //Establecemos los parámetros de los datos personales en el orden en que
        //los pide el procedimiento almacenado comenzando en 1:
        cstmt.setString(1, nombre);
        cstmt.setString(2, marca);
        cstmt.setString(3, descripcion);
        cstmt.setInt(4, categoria.getIdCategoria());
        cstmt.setInt(5, proveedor.getIdProveedor());
        cstmt.setString(6, capacidad);
        cstmt.setDouble(7, precio);
        cstmt.setInt(8, cantidad);

        //Registramos los parámetros de salida:
        cstmt.registerOutParameter(9, Types.INTEGER);
        cstmt.registerOutParameter(10, Types.INTEGER);

        //Ejecutamos el StoredProcedure
        cstmt.executeUpdate();

        //Recuperamos los ID's generados:
        idProducto = cstmt.getInt(9);
        idInventario = cstmt.getInt(10);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Inventario");
        alert.setHeaderText("Registrado");
        alert.setContentText("¡Se guardo el registro correctamente!");
        alert.showAndWait();

        getAllInventory();
        clearField();
    }

    public void updateInventory() {
        String nombre, marca, descripcion, capacidad;
        Categoria categoria;
        Proveedor proveedor;
        double precio;
        int cantidad;
        int idDelete = tblInventario.getSelectionModel().getSelectedIndex();
        int idInvent = Integer.parseInt(String.valueOf(tblInventario.getItems().get(idDelete).getIdInventario()));
        int idProduc = Integer.parseInt(String.valueOf(tblInventario.getItems().get(idDelete).getProducto().getIdProducto()));
        
        System.out.println(Integer.parseInt(String.valueOf(tblInventario.getItems().get(idDelete).getIdInventario())));
        nombre = txtNombre.getText().toString();
        marca = txtMarca.getText().toString();
        descripcion = txtDescripcion.getText().toString();
        categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        proveedor = cmbProveedor.getSelectionModel().getSelectedItem();
        capacidad = cmbCapacidad.getSelectionModel().getSelectedItem();
        precio = Double.parseDouble(txtPrecio.getText());
        cantidad = Integer.parseInt(txtCantidad.getText());

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que invoca al Stored Procedure:
        String sql = "CALL actualizarInventario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //Con este objeto invocaremos el StoredProcedure:
        CallableStatement cstmt;
        try {
            cstmt = connection.prepareCall(sql);

            //Establecemos los parámetros de los datos del producto en el orden en que
            //los pide el procedimiento almacenado comenzando en 1:
            cstmt.setString(1, nombre);
            cstmt.setString(2, marca);
            cstmt.setString(3, descripcion);
            cstmt.setInt(4, categoria.getIdCategoria());
            cstmt.setInt(5, proveedor.getIdProveedor());
            cstmt.setString(6, capacidad);
            cstmt.setDouble(7, precio);
            cstmt.setInt(8, cantidad);

            //Registramos los valores de retorno 
            cstmt.setInt(9, idProduc);
            cstmt.setInt(10, idInvent);
            
            System.out.println(idDelete + " " +idProduc + " "+ idInvent );

            cstmt.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Inventario");
            alert.setHeaderText("Actualizado");
            alert.setContentText("¡Se actualizó el registro correctamente!");
            alert.showAndWait();
        } catch (SQLException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllInventory();
        clearField();
    }
    
    public void deleteInventory() throws SQLException {
        int idDelete = tblInventario.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblInventario.getItems().get(idDelete).getIdInventario()));

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        String sql = "UPDATE inventario SET estatus = 0 WHERE idInventario = ?;";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Inventario");
        alert.setHeaderText("Eliminado");
        alert.setContentText("¡Se elimino el registro del inventario correctamente!");
        alert.showAndWait();

        getAllInventory();
        clearField();
    }

    public void showDetailInvetory() {
        Inventario i = this.tblInventario.getSelectionModel().getSelectedItem();

        if (i != null) {
            this.txtNombre.setText(i.getProducto().getNombre());
            this.txtMarca.setText(i.getProducto().getMarca());
            this.txtDescripcion.setText(i.getProducto().getDescripcion());
            this.cmbCategoria.getSelectionModel().select((i.getProducto().getIdCategoria().getIdCategoria()) - 1);
            this.cmbProveedor.getSelectionModel().select((i.getProducto().getIdProveedor().getIdProveedor()) - 1);
            this.cmbCapacidad.getSelectionModel().select(i.getProducto().getCapacidad());
            this.txtPrecio.setText(String.valueOf(i.getProducto().getPrecio()));
            this.txtCantidad.setText(String.valueOf(i.getCantidad()));
        }

    }

    public void clearField() {

        txtNombre.setText("");
        txtMarca.setText("");
        txtDescripcion.setText("");
        cmbCategoria.getSelectionModel().clearSelection();
        cmbProveedor.getSelectionModel().clearSelection();
        cmbCapacidad.getSelectionModel().clearSelection();
        txtPrecio.setText("");
        txtCantidad.setText("");
    }

    @FXML
    private void searchInventory(KeyEvent event) {

        String filtro = this.txtBuscar.getText();

        if (filtro.isEmpty()) {
            this.tblInventario.setItems(inventarioList);
        } else {
            //Limpiamos la lista
            this.filtroInventario.clear();

            for (Inventario i : this.inventarioList) {
                if (i.getProducto().getNombre().contains(filtro) || i.getProducto().getDescripcion().contains(filtro)
                        || i.getProducto().getMarca().contains(filtro)) {

                    this.filtroInventario.add(i);
                }
            }

            this.tblInventario.setItems(filtroInventario);
        }

    }

    public void fillCategories() throws SQLException {
        ObservableList<Categoria> categorias = FXCollections.observableArrayList();
        //Definimos la consulta SQL:
        String sql = "SELECT idCategoria, nombreCategoria FROM categoria WHERE estatus = 1";

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
                categorias.add(c);
            }

            cmbCategoria.setItems(categorias);
        } catch (SQLException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fillProviders() throws SQLException {
        ObservableList<Proveedor> proveedor = FXCollections.observableArrayList();
        //Definimos la consulta SQL:
        String sql = "SELECT idProveedor, empresa FROM proveedor WHERE estatus = 1";

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
                proveedor.add(p);
            }

            cmbProveedor.setItems(proveedor);
        } catch (SQLException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*Botones para Cerrar ventana*/
    public void closeWindows() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/mc/inventorySystem/gui/fxml/Principal.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle("Sistema de Control");
            stage.setScene(scene);
            stage.show();

            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            stage.setTitle("Sistema de Control");
            stage.setScene(scene);
            stage.show();

            // Indico que debe hacer al cerrar
            stage.setOnCloseRequest(e -> controlador.closeWindows());
            //Ciero la ventana donde estoy
            Stage myStage = (Stage) this.btnPrincipal.getScene().getWindow();
            myStage.close();
        } catch (IOException ex) {
            Logger.getLogger(InventarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Inventario fill(ResultSet rs) throws SQLException {

        //Una variable temporal para crear nuevos objetos de tipo Inventario
        Inventario i = new Inventario();

        //Una variable temporal para crear nuevos objetos de tipo Producto
        Producto p = new Producto();
        Proveedor pr = new Proveedor();
        Categoria c = new Categoria();
        //Llenamos sus datos  

        // Datos del Proveedor     
        pr.setIdProveedor(rs.getInt("idProveedor"));
        //pr.setEmpresa(rs.getString("empresa"));

        // Datos de Categoría
        c.setIdCategoria(rs.getInt("idCategoria"));
//        c.setNombreCategoria(rs.getString("nombreCategoria"));

        //Datos de Producto}
        p.setIdProducto(rs.getInt("idProducto"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setIdCategoria(c);
        p.setIdProveedor(pr);
        p.setCapacidad(rs.getString("capacidad"));
        p.setPrecio(rs.getDouble("precio"));

        // Datos de Inventario
        i.setIdInventario(rs.getInt("idInventario"));
        i.setProducto(p);
        i.setCantidad(rs.getInt("cantidad"));

        //Devolver el objeto
        return i;
    }
}
