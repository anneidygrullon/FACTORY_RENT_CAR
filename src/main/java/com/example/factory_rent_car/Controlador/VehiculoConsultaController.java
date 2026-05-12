package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Vehiculo;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;

public class VehiculoConsultaController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colId;
    @FXML private TableColumn<Vehiculo, String> colMarca;
    @FXML private TableColumn<Vehiculo, String> colModelo;
    @FXML private TableColumn<Vehiculo, String> colPlaca;
    @FXML private TableColumn<Vehiculo, String> colColor;
    @FXML private TableColumn<Vehiculo, Double> colKilometraje;
    @FXML private TableColumn<Vehiculo, String> colCombustible;
    @FXML private TableColumn<Vehiculo, Integer> colPasajeros;
    @FXML private TableColumn<Vehiculo, String> colEstado;
    @FXML private TableColumn<Vehiculo, Double> colPrecio;
    @FXML private ComboBox<String> cmbEstado;

    private final ObservableList<Vehiculo> listaVehiculos = FXCollections.observableArrayList();
    private Vehiculo vehiculoSeleccionado;

    @FXML
    public void initialize() {
        // Configurar columnas con enlace directo
        colId.setCellValueFactory(c -> c.getValue().idVehiculoProperty().asObject());
        colMarca.setCellValueFactory(c -> c.getValue().marcaProperty());
        colModelo.setCellValueFactory(c -> c.getValue().modeloProperty());
        colPlaca.setCellValueFactory(c -> c.getValue().numPlacaProperty());
        colColor.setCellValueFactory(c -> c.getValue().colorProperty());
        colKilometraje.setCellValueFactory(c -> c.getValue().kilometrajeProperty().asObject());
        colCombustible.setCellValueFactory(c -> c.getValue().tipoCombustibleProperty());
        colPasajeros.setCellValueFactory(c -> c.getValue().maxPasajerosProperty().asObject());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colPrecio.setCellValueFactory(c -> c.getValue().precioPorDiaProperty().asObject());

        tablaVehiculos.setItems(listaVehiculos);
        tablaVehiculos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    vehiculoSeleccionado = newVal;
                    if (newVal != null) {
                        cmbEstado.setValue(newVal.getEstado());
                    }
                });

        cargarVehiculos();
    }

    private void cargarVehiculos() {
        listaVehiculos.clear();
        String sql = "SELECT id_vehiculo, marca, modelo, serie, num_placa, color, kilometraje, " +
                "tp_combustible, max_pasajeros, estado, precio_x_dia, fk_id_poliza, fk_pk_id_compra " +
                "FROM TBL_VEHICULO ORDER BY id_vehiculo";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo(
                        rs.getInt("id_vehiculo"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("serie"),
                        rs.getString("num_placa"),
                        rs.getString("color"),
                        rs.getDouble("kilometraje"),
                        rs.getString("tp_combustible"),
                        rs.getInt("max_pasajeros"),
                        rs.getString("estado"),
                        rs.getDouble("precio_x_dia"),
                        rs.getInt("fk_id_poliza"),
                        rs.getInt("fk_pk_id_compra")
                );
                listaVehiculos.add(v);
            }
            tablaVehiculos.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando vehículos: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarVehiculos();
            return;
        }
        ObservableList<Vehiculo> filtrados = FXCollections.observableArrayList();
        for (Vehiculo v : listaVehiculos) {
            if (v.getMarca().toLowerCase().contains(filtro) ||
                    v.getModelo().toLowerCase().contains(filtro) ||
                    v.getNumPlaca().toLowerCase().contains(filtro)) {
                filtrados.add(v);
            }
        }
        tablaVehiculos.setItems(filtrados);
        tablaVehiculos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarVehiculos();
    }

    @FXML
    private void actualizarEstado(ActionEvent event) {
        if (vehiculoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un vehículo de la tabla.");
            return;
        }
        String nuevoEstado = cmbEstado.getValue();
        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Seleccione un estado válido.");
            return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("UPDATE TBL_VEHICULO SET estado = ? WHERE id_vehiculo = ?")) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, vehiculoSeleccionado.getIdVehiculo());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Estado actualizado correctamente.");
                vehiculoSeleccionado.setEstado(nuevoEstado);
                tablaVehiculos.refresh();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
        }
    }
}