package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReclamoRegistroController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtIdCliente;
    @FXML private TextField txtCliente;
    @FXML private TextField txtIdEmpleado;
    @FXML private TextField txtEmpleado;
    @FXML private TextField txtMotivo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    @FXML
    public void initialize() {
        cmbEstado.getItems().addAll("Pendiente", "En revisión", "Resuelto", "Rechazado");
        cmbEstado.setValue("Pendiente");
    }

    @FXML
    private void buscarCliente(ActionEvent event) {
        String idText = txtIdCliente.getText().trim();
        if (idText.isEmpty()) {
            advertencia("Ingrese un ID de cliente.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            advertencia("ID inválido.");
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
                advertencia("Cliente no encontrado.");
                txtCliente.clear();
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void buscarEmpleado(ActionEvent event) {
        String idText = txtIdEmpleado.getText().trim();
        if (idText.isEmpty()) {
            advertencia("Ingrese un ID de empleado.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            advertencia("ID inválido.");
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
                advertencia("Empleado no encontrado.");
                txtEmpleado.clear();
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
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

        try (Connection con = conexion.establecerConexion()) {
            con.setAutoCommit(false);

            // Insertar historial de reclamo (con fecha actual)
            int idHistorial;
            String sqlNextHist = "SELECT ISNULL(MAX(pk_id_hist_reclamo), 0) + 1 AS next_id FROM TBL_HISTORIAL_RECLAMOS";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextHist);
                 ResultSet rsNext = psNext.executeQuery()) {
                idHistorial = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlHistorial = "INSERT INTO TBL_HISTORIAL_RECLAMOS (pk_id_hist_reclamo, descripcion, motivo, fecha) VALUES (?, ?, ?, ?)";
            PreparedStatement psHistorial = con.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, idHistorial);
            psHistorial.setString(2, descripcion);
            psHistorial.setString(3, motivo);
            psHistorial.setDate(4, Date.valueOf(LocalDate.now()));
            psHistorial.executeUpdate();

            // Insertar reclamo
            int idReclamo;
            String sqlNextReclamo = "SELECT ISNULL(MAX(pk_id_reclamo), 0) + 1 AS next_id FROM TBL_RECLAMACION";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextReclamo);
                 ResultSet rsNext = psNext.executeQuery()) {
                idReclamo = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlReclamo = "INSERT INTO TBL_RECLAMACION (pk_id_reclamo, estado, motivo, descripcion, fk_pk_id_cliente, fk_pk_id_hist_reclamo, fk_pk_id_empleado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psReclamo = con.prepareStatement(sqlReclamo);
            psReclamo.setInt(1, idReclamo);
            psReclamo.setString(2, estado);
            psReclamo.setString(3, motivo);
            psReclamo.setString(4, descripcion);
            psReclamo.setInt(5, idCliente);
            psReclamo.setInt(6, idHistorial);
            psReclamo.setInt(7, idEmpleado);
            psReclamo.executeUpdate();

            con.commit();
            informacion("Reclamo registrado con ID: " + idReclamo);
            limpiar(event);
        } catch (SQLException e) {
            error("Error al registrar: " + e.getMessage());
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
            advertencia("El ID del cliente es obligatorio.");
            return false;
        }
        if (txtIdEmpleado.getText().isBlank()) {
            advertencia("El ID del empleado es obligatorio.");
            return false;
        }
        if (txtMotivo.getText().isBlank()) {
            advertencia("El motivo es obligatorio.");
            return false;
        }
        try {
            Integer.parseInt(txtIdCliente.getText().trim());
            Integer.parseInt(txtIdEmpleado.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Los IDs deben ser números.");
            return false;
        }
        return true;
    }
}
