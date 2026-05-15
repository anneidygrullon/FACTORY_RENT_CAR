package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class IncidenciaRegistroController {

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtIdReserva;
    @FXML private Label lblReservaInfo;
    @FXML private TextField txtIdEmpleado;
    @FXML private Label lblEmpleadoInfo;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtDescripcion;

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Daño", "Accidente", "Multa", "Retraso", "Otro");
        dpFecha.setValue(LocalDate.now());
    }

    @FXML
    private void buscarReserva(ActionEvent event) {
        String idText = txtIdReserva.getText().trim();
        if (idText.isEmpty()) {
            advertencia("Ingrese un ID de reserva.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            advertencia("ID inválido.");
            return;
        }
        String sql = "SELECT pk_id_reserva, c.nombre AS cliente FROM TBL_RESERVACION r " +
                "JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente WHERE r.pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblReservaInfo.setText("Reserva #" + id + " - Cliente: " + rs.getString("cliente"));
            } else {
                advertencia("Reserva no encontrada.");
                lblReservaInfo.setText("");
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
                lblEmpleadoInfo.setText(rs.getString("nombre"));
            } else {
                advertencia("Empleado no encontrado.");
                lblEmpleadoInfo.setText("");
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void registrarIncidencia(ActionEvent event) {
        if (!validarCampos()) return;

        int idReserva = Integer.parseInt(txtIdReserva.getText().trim());
        int idEmpleado = Integer.parseInt(txtIdEmpleado.getText().trim());
        String tipo = cmbTipo.getValue();
        double monto = Double.parseDouble(txtMonto.getText().trim());
        LocalDate fecha = dpFecha.getValue();
        String descripcion = txtDescripcion.getText().trim();

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            int idHistorial;
            try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_hist_incidencia), 0) + 1 AS next_id FROM TBL_HISTORIAL_INCIDENCIA");
                 ResultSet rsNext = psNext.executeQuery()) {
                idHistorial = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlHistorial = "INSERT INTO TBL_HISTORIAL_INCIDENCIA (pk_id_hist_incidencia, fecha, descripcion) VALUES (?, ?, ?)";
            try (PreparedStatement psHist = con.prepareStatement(sqlHistorial)) {
                psHist.setInt(1, idHistorial);
                psHist.setDate(2, Date.valueOf(fecha));
                psHist.setString(3, descripcion);
                psHist.executeUpdate();
            }

            int idIncidencia;
            try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_incidencia), 0) + 1 AS next_id FROM TBL_INCIDENCIA");
                 ResultSet rsNext = psNext.executeQuery()) {
                idIncidencia = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlIncidencia = "INSERT INTO TBL_INCIDENCIA (pk_id_incidencia, tipo, monto, fecha, descripcion, " +
                    "fk_pk_id_reserva, fk_pk_id_hist_incidencia, fk_pk_id_empleado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInc = con.prepareStatement(sqlIncidencia)) {
                psInc.setInt(1, idIncidencia);
                psInc.setString(2, tipo);
                psInc.setDouble(3, monto);
                psInc.setDate(4, Date.valueOf(fecha));
                psInc.setString(5, descripcion);
                psInc.setInt(6, idReserva);
                psInc.setInt(7, idHistorial);
                psInc.setInt(8, idEmpleado);
                psInc.executeUpdate();
            }

            con.commit();
            informacion("Incidencia registrada con ID: " + idIncidencia);
            limpiar(event);
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            error("Error al registrar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtIdReserva.clear();
        lblReservaInfo.setText("");
        txtIdEmpleado.clear();
        lblEmpleadoInfo.setText("");
        cmbTipo.setValue(null);
        txtMonto.clear();
        dpFecha.setValue(LocalDate.now());
        txtDescripcion.clear();
    }

    private boolean validarCampos() {
        if (txtIdReserva.getText().isBlank()) {
            advertencia("El ID de reserva es obligatorio.");
            return false;
        }
        if (txtIdEmpleado.getText().isBlank()) {
            advertencia("El ID de empleado es obligatorio.");
            return false;
        }
        if (cmbTipo.getValue() == null) {
            advertencia("Seleccione un tipo de incidencia.");
            return false;
        }
        if (txtMonto.getText().isBlank()) {
            advertencia("El monto es obligatorio.");
            return false;
        }
        if (dpFecha.getValue() == null) {
            advertencia("La fecha es obligatoria.");
            return false;
        }
        try {
            Integer.parseInt(txtIdReserva.getText().trim());
            Integer.parseInt(txtIdEmpleado.getText().trim());
            Double.parseDouble(txtMonto.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("IDs o monto inválidos.");
            return false;
        }
        return true;
    }
}
