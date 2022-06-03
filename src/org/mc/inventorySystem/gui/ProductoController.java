package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import java.awt.event.KeyAdapter;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javax.swing.JTextField;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Categoria;
import org.mc.inventorySystem.core.model.Producto;

public class ProductoController implements Initializable {

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
    private TextField txtPrecio;

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
    private Button btnPrincipal;

    @FXML
    private TableView<Producto> tblPinturas;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, Integer> colCategoria;

    @FXML
    private TableColumn<Producto, String> colCapacidad;

    @FXML
    private TableColumn<Producto, ?> colPrecio;

    ObservableList<Producto> pinturasList;
    ObservableList<Producto> filtroPintura;
    ObservableList<Categoria> categorias;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mapeado de columnas de la tabla tblPrintura
        this.colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.colMarca.setCellValueFactory(new PropertyValueFactory("marca"));
        this.colDescripcion.setCellValueFactory(new PropertyValueFactory("descripcion"));
        this.colCategoria.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getIdCategoria().getIdCategoria()));
        this.colCapacidad.setCellValueFactory(new PropertyValueFactory("capacidad"));
        this.colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));

        ///Inicializamos la estructura de datos para poder utilizarla 
        pinturasList = FXCollections.observableArrayList();
        filtroPintura = FXCollections.observableArrayList();

        getAllPaints();

        try {
            fillComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }

        ObservableList<String> capacidad = FXCollections.observableArrayList(
                "400 ml",
                "1 LT",
                "4 LT",
                "19 LT");
        cmbCapacidad.setItems(capacidad);

    }

    /*
     En este método se hace la carga dinámica de los objetos de tipo Producto 
     que se encuentran dentro de la BD en base a su estatus recordando
     que el estatus en 1 quiere decir que se encuentrá activo y el 0 es que 
     ya no se encuentra activo dentro del inventario.
     */
    public void getAllPaints() {
        //Aquí guardaremos los objetos de tipo Productos. 
        //Una lista es un contenedor dinámico de objetos.
        ObservableList<Producto> obp = FXCollections.observableArrayList();
        
        Producto p = null;
        //Definimos la consulta SQL:
        String sql = "SELECT * FROM producto WHERE estatus = 1";

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                p = fill(resultSet);
                obp.add(p);
            }

            tblPinturas.setItems(obp);

            pinturasList = obp;
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
    En este método se hace la inserción de Objetos de tipo pintura para los registros
    podemos hacer diversas inserciones y al realizarse nos dara un Alert donde podremos 
    ver que la insercion se esta haciendo de manera correcta, en caso de no ser asi recibiremos un 
    Alert avisandonos que algo salio mal y debemos verificar.
     */
    public void insertProduct(ActionEvent event) {
        String nombre, marca, descripcion, capacidad;
        Categoria categoria;
        double precio;
        nombre = txtNombre.getText().toString();
        marca = txtMarca.getText().toString();
        descripcion = txtDescripcion.getText().toString();
        categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        capacidad = cmbCapacidad.getSelectionModel().getSelectedItem();
        precio = Double.parseDouble(txtPrecio.getText());

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        //Definimos la consulta SQL que realizara la inserción del registro:
        String sql = "INSERT INTO producto(nombre, marca, descripcion, idCategoria, capacidad, precio)"
                + "VALUES(?, ?, ?, ?, ?, ?);";

        try {
            // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
            // que se genera al realizar la inserción del registro.
            preparedStatement = connection.prepareStatement(sql);

            //Llenamos el valor de cada campo de la consulta SQL definida antes:
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, marca);
            preparedStatement.setString(3, descripcion);
            preparedStatement.setInt(4, categoria.getIdCategoria());
            preparedStatement.setString(5, capacidad);
            preparedStatement.setDouble(6, precio);

            preparedStatement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Productos");
            alert.setHeaderText("Producto registrado correctamente.");
            alert.showAndWait();

        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }

        getAllPaints();
        clearField();
    }

    /*
    Este método tiene como función principal llevar a cabo eliminaciones lógicas, es decir que
    no se quitan fisicamente de la base de datos lo que se hace es un UPDATE de el campo estatus
    ya que se encuentra en 1 que quiere decir que esta activo, al eliminarlo cambiamos su estatus 
    a 0 que quiere decir que esta inactivo o descontinuado.
     */
    public void deleteProduct(ActionEvent event) throws SQLException {
        int idDelete = tblPinturas.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblPinturas.getItems().get(idDelete).getIdProducto()));

        //Con este objeto abrimos la conexión a la base de datos 
        connection = MySQLConnection.open();

        String sql = "UPDATE producto SET estatus = 0 WHERE idProducto = ?;";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Pinturas");
        alert.setHeaderText("Eliminado");
        alert.setContentText("¡Se elimino el registro correctamente!");
        alert.showAndWait();

        getAllPaints();
        clearField();

    }

    /*
    Este método tiene como función principal llevar a cabo actualizaciones en los registros 
    que se encuentren en nustra base de datos, tomando como referencia el id del regiss
     */
    public void updateProduct(ActionEvent event) throws SQLException {
        String nombre, marca, descripcion, capacidad;
        Categoria categoria;
        double precio;

        int idDelete = tblPinturas.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(tblPinturas.getItems().get(idDelete).getIdProducto()));

        nombre = txtNombre.getText().toString();
        marca = txtMarca.getText().toString();
        descripcion = txtDescripcion.getText().toString();
        categoria = cmbCategoria.getSelectionModel().getSelectedItem();
        capacidad = cmbCapacidad.getSelectionModel().getSelectedItem();
        precio = Double.parseDouble(txtPrecio.getText());

        //Definimos la consulta SQL que realiza la inserción del registro:
        String sql = "UPDATE producto SET nombre = ?, marca = ?, descripcion = ?,"
                + " idCategoria = ?, capacidad = ?, precio = ? WHERE idProducto = ?";

        // Con este objeto ejecutaremos la sentencia SQL que realiza la inserción en la tabla. Debemos especificarle que queremos que nos devuelva el ID
        // que se genera al realizar la inserción del registro.
        preparedStatement = connection.prepareStatement(sql);

        //Llenamos el valor de cada campo de la consulta SQL definida antes:
        preparedStatement.setString(1, nombre);
        preparedStatement.setString(2, marca);
        preparedStatement.setString(3, descripcion);
        preparedStatement.setInt(4, categoria.getIdCategoria());
        preparedStatement.setString(5, capacidad);
        preparedStatement.setDouble(6, precio);
        preparedStatement.setInt(7, id);
        preparedStatement.executeUpdate();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro de Pinturas");

        alert.setHeaderText("Actualizado");
        alert.setContentText("¡Se actualizo correctamente el registro!");

        alert.showAndWait();
        System.out.println(id);
        getAllPaints();
        clearField();
    }

    /*
    Este método tiene como funcion la limpieza de los campos despúes de ser utilizados, en una 
    inserción o en una actualizacion o simplemente para mantenerlos listos para cualquiera de estas acciones
     */
    public void clearField() {
        txtNombre.setText("");
        txtMarca.setText("");
        txtDescripcion.setText("");
        cmbCategoria.getSelectionModel().clearSelection();
        cmbCapacidad.getSelectionModel().clearSelection();
        txtPrecio.setText("");
    }

    /*
    Este metodo nos ayuda a mostrar el detalle de cada uno de los registros que se encuentran en nuestra 
    tabla, para asi poder ver mas detenidamente cada uno de sus campos o caracteristicas en el caso de quere eliminarlo o
    actualizarlo.
     */
    public void showDetailProduct() {
        Producto p = this.tblPinturas.getSelectionModel().getSelectedItem();

        if (p != null) {

            this.txtNombre.setText(p.getNombre());
            this.txtMarca.setText(p.getMarca());
            this.txtDescripcion.setText(p.getDescripcion());
            this.cmbCategoria.getSelectionModel().select(p.getIdCategoria());
            this.cmbCapacidad.getSelectionModel().select(p.getCapacidad());
            this.txtPrecio.setText(String.valueOf(p.getPrecio()));

        }
    }

    @FXML
    private void txtBuscar(KeyEvent event) {

        String filtro = this.txtBuscar.getText();

        if (filtro.isEmpty()) {
            this.tblPinturas.setItems(pinturasList);
        } else {
            //Limpiamos la lista
            this.filtroPintura.clear();

            for (Producto p : this.pinturasList) {
                if (p.getNombre().contains(filtro) || p.getMarca().contains(filtro)) {

                    this.filtroPintura.add(p);
                }
            }

            this.tblPinturas.setItems(filtroPintura);
        }

    }

    public void fillComboBox() throws SQLException {
        ObservableList<Categoria> categorias = FXCollections.observableArrayList();
        //Definimos la consulta SQL:
        String sql = "SELECT idCategoria, nombre FROM categoria WHERE estatus = 1";

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(resultSet.getInt("idCategoria"));
                c.setNombreCategoria(resultSet.getString("nombre"));
                categorias.add(c);
            }

            cmbCategoria.setItems(categorias);
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Categoria fillComboBox1() throws SQLException {
        ObservableList<Categoria> categorias = FXCollections.observableArrayList();
        //Definimos la consulta SQL:
        String sql = "SELECT idCategoria, nombre FROM categoria WHERE estatus = 1";
 
        Categoria c = new Categoria();
        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {                
                c.setIdCategoria(resultSet.getInt("idCategoria"));
                c.setNombreCategoria(resultSet.getString("nombre"));
                categorias.add(c);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    /**
     * Crea un objeto de tipo Producto y llena sus propiedades utilizando los
     * datos proporcionados por un ResultSet.
     *
     * @param rs
     * @return
     */
    public Producto fill(ResultSet rs) throws SQLException {
        // Creamos una nueva instancia de Pintura:
        Producto p = new Producto();
        Categoria c = new Categoria();

        c =  fillComboBox1();
        

        // Llenamos sus propiedades:
        p.setIdProducto(rs.getInt("idProducto"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setIdCategoria(c);
        p.setCapacidad(rs.getString("capacidad"));
        p.setPrecio(rs.getDouble("precio"));
        p.setEstatus(rs.getInt("estatus"));

        // Devolvemos el objeto de tipo pintura:
        return p;
    }

    public void validate() {

        RequiredFieldValidator rv = new RequiredFieldValidator();
        rv.setMessage("Este campo es obligatorio");

        NumberValidator nv = new NumberValidator();
        nv.setMessage("Este campo solo es valido con números");

        //txtPrecio.get().add(nv);
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
