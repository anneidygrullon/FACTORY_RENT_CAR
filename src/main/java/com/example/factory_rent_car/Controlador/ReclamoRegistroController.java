package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class ReclamoRegistroController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtIdCliente;
    @FXML private TextField txtCliente;
    @FXML private TextField txtIdEmpleado;
    @FXML private TextField txtEmpleado;
    @FXML private TextField txtMotivo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    @FXML
    public void initialize() {
        cmbEstado.setValue("Pendiente");
    }

    @FXML
    private void buscarCliente(ActionEvent event) {
        String idText = txtIdCliente.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de cliente.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }
        String sql = "SELECT nombre FROM TBL_CLIENTE WHERE pk_id_cliente = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtCliente.setText(rs.getString("nombre"));
            } else {
                JOptionPane.showMessageDialog(null, "Cliente no encontrado.");
                txtCliente.clear();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void buscarEmpleado(ActionEvent event) {
        String idText = txtIdEmpleado.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de empleado.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }
        String sql = "SELECT nombre FROM TBL_EMPLEADO WHERE pk_id_empleado = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtEmpleado.setText(rs.getString("nombre"));
            } else {
                JOptionPane.showMessageDialog(null, "Empleado no encontrado.");
                txtEmpleado.clear();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void registrarReclamo(ActionEvent event) {
        if (!validarCampos()) return;

        int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
        int idEmpleado = Integer.parseInt(txtIdEmpleado.getText().trim());
        String motivo = txtMotivo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String estado = cmbEstado.getValue();

            con.setAutoCommit(false);

            int idHistorial = -1;
            if (rsHist.next()) idHistorial = rsHist.getInt(1);

                    "VALUES (?, ?, ?, ?, ?, ?)";
            int idReclamo = -1;

            con.commit();
            JOptionPane.showMessageDialog(null, "Reclamo registrado con ID: " + idReclamo);
            limpiar(event);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtIdCliente.clear();
        txtCliente.clear();
        txtIdEmpleado.clear();
        txtEmpleado.clear();
        txtMotivo.clear();
        txtDescripcion.clear();
        cmbEstado.setValue("Pendiente");
    }

    private boolean validarCampos() {
        if (txtIdCliente.getText().isBlank()) {
            return false;
        }
        if (txtIdEmpleado.getText().isBlank()) {
            return false;
        }
        if (txtMotivo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El motivo es obligatorio.");
            return false;
        }
        try {
            Integer.parseInt(txtIdCliente.getText().trim());
            Integer.parseInt(txtIdEmpleado.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}