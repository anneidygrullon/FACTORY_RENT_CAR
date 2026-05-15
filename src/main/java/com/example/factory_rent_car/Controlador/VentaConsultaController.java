package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Venta;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javax.swing.*;
import java.sql.*;

public class VentaConsultaController {

    private MainLayoutController mainController;
    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Double> colMonto;
    @FXML private TableColumn<Venta, String> colTipo;
    @FXML private TableColumn<Venta, String> colDescripcion;
    @FXML private TableColumn<Venta, String> colSuplidor;

    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idVentaProperty().asObject());
        colMonto.setCellValueFactory(c -> c.getValue().montoTotalProperty().asObject());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colDescripcion.setCellValueFactory(c -> c.getValue().descripcionProperty());
        colSuplidor.setCellValueFactory(c -> c.getValue().suministradorNombreProperty());

        tablaVentas.setItems(listaVentas);
        cargarVentas();
    }

    private void cargarVentas() {
        listaVentas.clear();
        String sql = "SELECT v.pk_id_venta, v.monto_total, v.tipo, v.descripcion, v.fk_id_suministrador, " +
                "s.nombre AS suplidor_nombre " +
                "FROM TBL_VENTA v " +
                "LEFT JOIN TBL_SUMINISTRADOR s ON s.id_suministrador = v.fk_id_suministrador " +
                "ORDER BY v.pk_id_venta DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaVentas.add(new Venta(
                        rs.getInt("pk_id_venta"),
                        rs.getDouble("monto_total"),
                        rs.getString("tipo"),
                        rs.getString("descripcion"),
                        rs.getInt("fk_id_suministrador"),
                        rs.getString("suplidor_nombre")
                ));
            }
            tablaVentas.refresh();
        } catch (SQLException e) {
            error("Error cargando ventas: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarVentas();
            return;
        }
        ObservableList<Venta> filtrados = FXCollections.observableArrayList();
        for (Venta v : listaVentas) {
            if (v.getTipo().toLowerCase().contains(filtro) ||
                    v.getSuministradorNombre().toLowerCase().contains(filtro) ||
                    v.getDescripcion().toLowerCase().contains(filtro)) {
                filtrados.add(v);
            }
        }
        tablaVentas.setItems(filtrados);
        tablaVentas.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarVentas();
    }
}
