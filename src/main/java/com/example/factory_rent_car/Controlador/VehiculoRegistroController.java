package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;

public class VehiculoRegistroController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtBuscarId;
    @FXML private Label lblInfo;
    @FXML private Label lblTitulo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtSerie;
    @FXML private TextField txtPlaca;
    @FXML private TextField txtColor;
    @FXML private TextField txtKilometraje;
    @FXML private ComboBox<String> cmbCombustible;
    @FXML private TextField txtMaxPasajeros;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtIdPoliza;
    @FXML private TextField txtIdCompra;

    private int vehiculoIdActual = -1;  // -1 = nuevo registro

    @FXML
    public void initialize() {
        cmbCombustible.getItems().addAll("Gasolina", "Diesel", "Eléctrico", "Híbrido");
        cmbEstado.getItems().addAll("Disponible", "Reservado", "En Mantenimiento", "En Limpieza", "No Apto");
    }

    @FXML
    private void buscarVehiculo(ActionEvent event) {
        String idText = txtBuscarId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de vehículo.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }
        String sql = "SELECT id_vehiculo, marca, modelo, serie, num_placa, color, kilometraje, tp_combustible, " +
                "max_pasajeros, estado, precio_x_dia, fk_id_poliza, fk_pk_id_compra " +
                "FROM TBL_VEHICULO WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vehiculoIdActual = id;
                lblInfo.setText("Editando ID: " + id);
                lblTitulo.setText("Editando Vehículo #" + id);
                txtMarca.setText(rs.getString("marca"));
                txtModelo.setText(rs.getString("modelo"));
                txtSerie.setText(rs.getString("serie"));
                txtPlaca.setText(rs.getString("num_placa"));
                txtColor.setText(rs.getString("color"));
                txtKilometraje.setText(String.valueOf(rs.getDouble("kilometraje")));
                cmbCombustible.setValue(rs.getString("tp_combustible"));
                txtMaxPasajeros.setText(String.valueOf(rs.getInt("max_pasajeros")));
                cmbEstado.setValue(rs.getString("estado"));
                txtPrecio.setText(String.valueOf(rs.getDouble("precio_x_dia")));
                txtIdPoliza.setText(String.valueOf(rs.getInt("fk_id_poliza")));
                txtIdCompra.setText(String.valueOf(rs.getInt("fk_pk_id_compra")));
            } else {
                JOptionPane.showMessageDialog(null, "No existe vehículo con ID " + id);
                limpiar(event);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void guardarVehiculo(ActionEvent event) {
        if (!validarCampos()) return;

        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String serie = txtSerie.getText().trim().isEmpty() ? null : txtSerie.getText().trim();
        String placa = txtPlaca.getText().trim();
        String color = txtColor.getText().trim().isEmpty() ? null : txtColor.getText().trim();
        double kilometraje = 0;
        try { kilometraje = Double.parseDouble(txtKilometraje.getText().trim()); } catch (NumberFormatException e) {}
        String combustible = cmbCombustible.getValue();
        int maxPasajeros = 5;
        try { maxPasajeros = Integer.parseInt(txtMaxPasajeros.getText().trim()); } catch (NumberFormatException e) {}
        String estado = cmbEstado.getValue();
        double precio = 0;
        try { precio = Double.parseDouble(txtPrecio.getText().trim()); } catch (NumberFormatException e) {}
        int idPoliza;
        try { idPoliza = Integer.parseInt(txtIdPoliza.getText().trim()); } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de póliza inválido."); return;
        }
        int idCompra;
        try { idCompra = Integer.parseInt(txtIdCompra.getText().trim()); } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de compra inválido."); return;
        }

        if (vehiculoIdActual == -1) {
            // INSERT
            String sql = "INSERT INTO TBL_VEHICULO (marca, modelo, serie, num_placa, color, kilometraje, " +
                    "tp_combustible, max_pasajeros, estado, precio_x_dia, fk_id_poliza, fk_pk_id_compra) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, marca);
                ps.setString(2, modelo);
                ps.setString(3, serie);
                ps.setString(4, placa);
                ps.setString(5, color);
                ps.setDouble(6, kilometraje);
                ps.setString(7, combustible);
                ps.setInt(8, maxPasajeros);
                ps.setString(9, estado);
                ps.setDouble(10, precio);
                ps.setInt(11, idPoliza);
                ps.setInt(12, idCompra);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) JOptionPane.showMessageDialog(null, "Vehículo registrado con ID: " + rs.getInt(1));
                else JOptionPane.showMessageDialog(null, "Vehículo registrado.");
                limpiar(event);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al registrar: " + e.getMessage());
            }
        } else {
            // UPDATE
            String sql = "UPDATE TBL_VEHICULO SET marca=?, modelo=?, serie=?, num_placa=?, color=?, kilometraje=?, " +
                    "tp_combustible=?, max_pasajeros=?, estado=?, precio_x_dia=?, fk_id_poliza=?, fk_pk_id_compra=? " +
                    "WHERE id_vehiculo = ?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, marca);
                ps.setString(2, modelo);
                ps.setString(3, serie);
                ps.setString(4, placa);
                ps.setString(5, color);
                ps.setDouble(6, kilometraje);
                ps.setString(7, combustible);
                ps.setInt(8, maxPasajeros);
                ps.setString(9, estado);
                ps.setDouble(10, precio);
                ps.setInt(11, idPoliza);
                ps.setInt(12, idCompra);
                ps.setInt(13, vehiculoIdActual);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Vehículo actualizado.");
                limpiar(event);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarVehiculo(ActionEvent event) {
        if (vehiculoIdActual == -1) {
            JOptionPane.showMessageDialog(null, "No hay vehículo seleccionado para eliminar.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Eliminar el vehículo #" + vehiculoIdActual + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_VEHICULO WHERE id_vehiculo = ?")) {
            ps.setInt(1, vehiculoIdActual);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Vehículo eliminado.");
            limpiar(event);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtBuscarId.clear();
        lblInfo.setText("");
        lblTitulo.setText("Nuevo Vehículo");
        txtMarca.clear();
        txtModelo.clear();
        txtSerie.clear();
        txtPlaca.clear();
        txtColor.clear();
        txtKilometraje.clear();
        cmbCombustible.setValue(null);
        txtMaxPasajeros.clear();
        cmbEstado.setValue(null);
        txtPrecio.clear();
        txtIdPoliza.clear();
        txtIdCompra.clear();
        vehiculoIdActual = -1;
    }

    private boolean validarCampos() {
        if (txtMarca.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Marca obligatoria."); return false;
        }
        if (txtModelo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Modelo obligatorio."); return false;
        }
        if (txtPlaca.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Placa obligatoria."); return false;
        }
        if (cmbCombustible.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione combustible."); return false;
        }
        if (txtMaxPasajeros.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Máx. pasajeros obligatorio."); return false;
        }
        if (cmbEstado.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione estado."); return false;
        }
        if (txtPrecio.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Precio obligatorio."); return false;
        }
        if (txtIdPoliza.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "ID de póliza obligatorio."); return false;
        }
        if (txtIdCompra.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "ID de compra obligatorio."); return false;
        }
        return true;
    }
}