package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.mc.inventorySystem.core.MySQLConnection;
import org.mc.inventorySystem.core.model.Pintura;

public class ProductoController implements Initializable {

    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextField txtMarca;
    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXComboBox<?> cmbCategoria;
    @FXML
    private JFXComboBox<?> cmbCapacidad;
    @FXML
    private JFXTextField txtPrecio;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private JFXButton btnModificar;
    @FXML
    private JFXButton btnEliminar;
    @FXML
    private JFXButton btnNuevo;
    @FXML
    private JFXTextField txtBuscar;
    @FXML
    private TableView<Pintura> tblPinturas;
    @FXML
    private TableColumn<Pintura, String> colNombre;
    @FXML
    private TableColumn<Pintura, String> colMarca;
    @FXML
    private TableColumn<Pintura, String> colDescripcion;
    @FXML
    private TableColumn<Pintura, String> colCategoria;
    @FXML
    private TableColumn<Pintura, String> colCapacidad;
    @FXML
    private TableColumn<Pintura, ?> colPrecio;

    ObservableList<Pintura> pinturasList;
    ObservableList<Pintura> filtroPintura;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mapeado de columnas de la tabla tblPrintura
        this.colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.colMarca.setCellValueFactory(new PropertyValueFactory("marca"));
        this.colDescripcion.setCellValueFactory(new PropertyValueFactory("descripcion"));
        this.colCategoria.setCellValueFactory(new PropertyValueFactory("categoria"));
        this.colCapacidad.setCellValueFactory(new PropertyValueFactory("capacidad"));
        this.colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));

        ///Inicializamos la estructura de datos para poder utilizarla 
        pinturasList = FXCollections.observableArrayList();
        filtroPintura = FXCollections.observableArrayList();

        this.tblPinturas.setItems(getAllPaints());
        
    }

    /*
     En este método se hace la carga dinámica de los objetos de tipo Pintura 
     que se encuentran dentro de la BD en base a su estatus recordando
     que el estatus en 1 quiere decir que se encuentrá activo y el 0 es que 
     ya no se encuentra activo dentro del inventario.
     */
    public ObservableList<Pintura> getAllPaints() {
        //Definimos la consulta SQL:
        String sql = "SELECT * FROM pintura WHERE estatus = 1";

        //Aquí guardatemos los objetos de tipo Pintura. Una lista es un contenedor dinámico de objetos.
        ObservableList<Pintura> obp = FXCollections.observableArrayList();

        //Creamos una variable temporal para crear nuevas instancias de Pinturas:
        Pintura p = null;

        //Con este objeto nos vamos a conectar a la Base de Datos:
        connection = MySQLConnection.open();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            // Recorremos el ResultSet:
            while (resultSet.next()) {
                p = fill(resultSet);
                pinturasList.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pinturasList;
    }

    public void showDetailProduct() {
        Pintura p = this.tblPinturas.getSelectionModel().getSelectedItem();

        if (p != null) {

            this.txtNombre.setText(p.getNombre());
            this.txtMarca.setText(p.getMarca());
            this.txtDescripcion.setText(p.getDescripcion());
            this.cmbCategoria.getSelectionModel().select((int) (Object) p.getCategoria());
            this.cmbCapacidad.getSelectionModel().select((int) (Object) p.getCapacidad());
            this.txtPrecio.setText(String.valueOf(p.getPrecio()));

        }
    }

    /**
     * Crea un objeto de tipo Producto y llena sus propiedades utilizando los
     * datos proporcionados por un ResultSet.
     *
     * @param rs
     * @return
     */
    public Pintura fill(ResultSet rs) throws SQLException {
        // Creamos una nueva instancia de Pintura:
        Pintura p = new Pintura();

        // Llenamos sus propiedades:
        p.setIdPintura(rs.getInt("idPintura"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCategoria(rs.getString("categoria"));
        p.setCapacidad(rs.getString("capacidad"));
        p.setPrecio(rs.getDouble("precio"));
        p.setEstatus(rs.getInt("estatus"));

        // Devolvemos el objeto de tipo pintura:
        return p;
    }
}
