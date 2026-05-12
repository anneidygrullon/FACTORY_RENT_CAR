package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;

public class VehiculoRegistroController {

    Conexion conexion = new Conexion();

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

    @FXML
    private void registrarVehiculo(ActionEvent event) {
        if (!validarCampos()) return;

        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String serie = txtSerie.getText().trim().isEmpty() ? null : txtSerie.getText().trim();
        String placa = txtPlaca.getText().trim();
        String color = txtColor.getText().trim().isEmpty() ? null : txtColor.getText().trim();
        double kilometraje;
        try {
            kilometraje = Double.parseDouble(txtKilometraje.getText().trim());
        } catch (NumberFormatException e) { kilometraje = 0; }
        String combustible = cmbCombustible.getValue();
        int maxPasajeros;
        try {
            maxPasajeros = Integer.parseInt(txtMaxPasajeros.getText().trim());
        } catch (NumberFormatException e) { maxPasajeros = 5; }
        String estado = cmbEstado.getValue();
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException e) { precio = 0; }
        int idPoliza;
        try {
            idPoliza = Integer.parseInt(txtIdPoliza.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de póliza inválido.");
            return;
        }
        int idCompra;
        try {
            idCompra = Integer.parseInt(txtIdCompra.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de compra inválido.");
            return;
        }

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
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Vehículo registrado con ID: " + rs.getInt(1));
            } else {
                JOptionPane.showMessageDialog(null, "Vehículo registrado.");
            }
            limpiar(event);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
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
    }

    private boolean validarCampos() {
        if (txtMarca.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "La marca es obligatoria.");
            return false;
        }
        if (txtModelo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El modelo es obligatorio.");
            return false;
        }
        if (txtPlaca.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "La placa es obligatoria.");
            return false;
        }
        if (cmbCombustible.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione el tipo de combustible.");
            return false;
        }
        if (txtMaxPasajeros.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingrese el máximo de pasajeros.");
            return false;
        }
        if (cmbEstado.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione el estado inicial.");
            return false;
        }
        if (txtPrecio.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingrese el precio por día.");
            return false;
        }
        if (txtIdPoliza.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID de la póliza.");
            return false;
        }
        if (txtIdCompra.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID de compra.");
            return false;
        }
        return true;
    }
}