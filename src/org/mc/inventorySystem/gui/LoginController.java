/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.mc.inventorySystem.core.model.Usuario;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class LoginController implements Initializable {
    
    
    @FXML
    private AnchorPane anchor;

    @FXML
    private JFXTextField txtUsuario;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXButton btnAcceder;

    @FXML
    private JFXButton btnSalir;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    

public void salir() {
        Stage cerraVentana = (Stage) btnSalir.getScene().getWindow();
        cerraVentana.close();
    } 


 private Usuario fillUsuario(ResultSet rs) throws SQLException {

        //Una variable temporal para crear nuevos objetos de tipo Usuario
        Usuario u = new Usuario();        

        //Llenamos sus datos:
        p.setApellidoPaterno(rs.getString("apellidoPaterno"));
        p.setApellidoMaterno(rs.getString("apellidoMaterno"));
        p.setDomicilio(rs.getString("domicilio"));
        p.setId(rs.getInt("idPersona"));
        p.setGenero(rs.getString("genero"));
        p.setNombre(rs.getString("nombre"));
        p.setRfc(rs.getString("rfc"));
        p.setTelefono(rs.getString("telefono"));

        //Creamos un nuevo objeto de tipo Usuario
        u.setContrasenia(rs.getString("contrasenia"));
        u.setId(rs.getInt("idUsuario"));
        u.setNombreUsuario(rs.getString("nombreUsuario"));
        u.setRol(rs.getString("rol"));
        u.setToken();

        //Establecemos sus datos personales
        e.setFoto(rs.getString("foto"));
        e.setId(rs.getInt("idEmpleado"));
        e.setNumeroEmpleado(rs.getString("numeroEmpleado"));
        e.setPuesto(rs.getString("puesto"));
        e.setRutaFoto(rs.getString("rutaFoto"));
        e.setEstatus(rs.getInt("estatus"));

        //Establecemos su persona:
        e.setPersona(p);

        //Establecemos su Usuario:
        e.setUsuario(u);

        return e;
    }
    
}
