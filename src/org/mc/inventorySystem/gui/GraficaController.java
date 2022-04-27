/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.mc.inventorySystem.core.MySQLConnection;

/**
 * FXML Controller class
 *
 * @author practicante02
 */
public class GraficaController implements Initializable {

    @FXML
    private Button btnCrear;

    @FXML
    private Button btnAbrir;

    @FXML
    private Button btnPrincipal;

    @FXML
    private PieChart graficos;
    ObservableList<PieChart.Data> pieChartData;

    /*Inpust tipo date*/
    @FXML
    private DatePicker dateFechaInicio;

    @FXML
    private DatePicker dateFechaFin;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void generateReport(ActionEvent event) throws FileNotFoundException, SQLException {

        ResultSet rs;
        try {
            FileOutputStream file = new FileOutputStream("Reporte" + ".pdf");
            Document documento = new Document();
            PdfWriter.getInstance(documento, file);
            documento.open();

            Paragraph parrafo = new Paragraph("Reporte de venta\n\n");
            parrafo.setAlignment(1);
            documento.add(parrafo);

            connection = MySQLConnection.open();
            String sql = "";
            PreparedStatement ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                documento.add(new Paragraph("Nombre del cliente:" + rs.getString("NombreCliente") + "\n"));
                documento.add(new Paragraph("RFC:" + rs.getString("RFC") + "\n"));
                documento.add(new Paragraph("Fecha de venta:" + rs.getString("Fecha") + "\n"));
                documento.add(new Paragraph("Producto:" + rs.getString("NombreProducto") + "\n"));
                documento.add(new Paragraph("Unidadades:" + rs.getInt("Cantidad") + "\n"));
                documento.add(new Paragraph("Precio:" + rs.getInt("Cantidad") + "\n"));
                documento.add(new Paragraph("Concepto:" + rs.getInt("Cantidad") + "\n"));
                documento.add(new Paragraph("----------------------------------------------------\n"));
            }
            documento.close();

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("¡Éxito!");
            alerta.setHeaderText(null);
            alerta.setContentText("Reporte Creado Correctamnete.");
            alerta.initStyle(StageStyle.UTILITY);
            alerta.showAndWait();

        } catch (DocumentException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("¡Error!");
            alerta.setHeaderText(null);
            alerta.setContentText("Hubo un Error al Crear el Reporte.");
            alerta.initStyle(StageStyle.UTILITY);
            alerta.showAndWait();
        }
    }

    public void openReport(ActionEvent event) {
        try {
            File path = new File("Reporte" + ".pdf");
            Desktop.getDesktop().open(path);
        } catch (IOException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("¡Error!");
            alerta.setHeaderText(null);
            alerta.setContentText("Hubo un Error al Abrir el Archivo.");
            alerta.initStyle(StageStyle.UTILITY);
            alerta.showAndWait();
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
