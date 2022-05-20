/*
 * Esta es la clase controladora donde se maneja toda la funcionalidad 
   del login.
 */
package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXProgressBar;
import java.io.IOException;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.MySQLConnection;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class LoginController implements Initializable {

    @FXML
    private AnchorPane anchor;

    @FXML
    private ImageView imgLog;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private Button btnAcceder;

    @FXML
    private Button btnSalir;

    @FXML
    private JFXProgressBar pbLogin;

    @FXML
    private Label lblLogin;

    Stage dialogStage = new Stage();
    Scene scene;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    public LoginController() throws Exception {
        connection = MySQLConnection.open();
    }

    /*
    Este método sera el evento para el boton acceder, que nos ayudará
    a comparar los valores de las cajasd de texto con los que se encuentren 
    en la base de datos pra dar un acceso adecuado a la aplicación.
     */
    public void login(ActionEvent event) throws IOException, InterruptedException {

        //Tomamos los valores de las cajas de texto y los asignamos a una variable
        String user = txtUsuario.getText().toString();
        String password = txtPassword.getText().toString();

        //Esta es la consulta sql que se enviará como petición para validar el usuario
        String sql = "SELECT * FROM usuario WHERE nombreUsuario = ? and contrasenia = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                showProgress(false);
                lblLogin.setText("Usuario no encontrado, Intente de nuevo.");
            } else {
                showProgress(true);
                Thread.sleep(3000);
                Node source = (Node) event.getSource();
                dialogStage = (Stage) source.getScene().getWindow();
                dialogStage.close();
                scene = new Scene(FXMLLoader.load(getClass().getResource("/org/mc/inventorySystem/gui/fxml/Principal.fxml")));
                dialogStage.setScene(scene);
                dialogStage.show();
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void out() {
        Stage closeWindow = (Stage) btnSalir.getScene().getWindow();
        closeWindow.close();
    }

    public static void infoBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    private void showProgress(boolean flag) {

        if (flag) {

            btnAcceder.setDisable(true);
            lblLogin.setVisible(true);
            lblLogin.setText("Iniciando Sesión, espere un momento...");
            pbLogin.setVisible(true);
        } else {

            btnAcceder.setDisable(false);
            pbLogin.setVisible(false);

        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Admin",
                "Colaborador"
        );
        cmbRol.setItems(roles);
    }

}
