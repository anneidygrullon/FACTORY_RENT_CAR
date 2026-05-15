package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VentaRegistroController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtMontoTotal;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbSuplidor;
    @FXML private TextField txtIdVehiculo;
    @FXML private Label lblVehiculoInfo;
    @FXML private Label lblVehiculoId;

    private Map<String, Integer> mapaSuplidores = new HashMap<>();
    private int idVehiculoSeleccionado = -1;

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Contado", "Crédito", "Factura");
        cargarSuplidores();
    }

    private void cargarSuplidores() {
        cmbSuplidor.getItems().clear();
        mapaSuplidores.clear();
        String sql = "SELECT id_suministrador, nombre FROM TBL_SUMINISTRADOR ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_suministrador");
                String nombre = rs.getString("nombre");
                cmbSuplidor.getItems().add(nombre);
                mapaSuplidores.put(nombre, id);
            }
        } catch (SQLException e) {
            error("Error cargando suplidores: " + e.getMessage());
        }
    }

    @FXML
    private void buscarVehiculo(ActionEvent event) {
        String idText = txtIdVehiculo.getText().trim();
        if (idText.isEmpty()) { advertencia("Ingrese un ID de vehículo."); return; }
        int id;
        try { id = Integer.parseInt(idText); } catch (NumberFormatException e) {
            advertencia("ID inválido."); return;
        }
        String sql = "SELECT id_vehiculo, marca, modelo, num_placa FROM TBL_VEHICULO WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idVehiculoSeleccionado = id;
                lblVehiculoInfo.setText(rs.getString("marca") + " " + rs.getString("modelo") + " - " + rs.getString("num_placa"));
                lblVehiculoId.setText("ID: " + id);
            } else {
                advertencia("Vehículo no encontrado.");
                idVehiculoSeleccionado = -1;
                lblVehiculoInfo.setText("");
                lblVehiculoId.setText("");
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void registrarVenta(ActionEvent event) {
        if (!validarCampos()) return;

        double montoTotal;
        try {
            montoTotal = Double.parseDouble(txtMontoTotal.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Monto total inválido.");
            return;
        }

        if (idVehiculoSeleccionado == -1) {
            advertencia("Debe seleccionar un vehículo.");
            return;
        }

        String tipo = cmbTipo.getValue();
        String descripcion = txtDescripcion.getText().trim();
        String suplidorNombre = cmbSuplidor.getValue();
        int idSuplidor = mapaSuplidores.get(suplidorNombre);

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            int idVenta;
            try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_venta), 0) + 1 AS next_id FROM TBL_VENTA");
                 ResultSet rsNext = psNext.executeQuery()) {
                idVenta = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }

            String sqlVenta = "INSERT INTO TBL_VENTA (pk_id_venta, monto_total, tipo, descripcion, fk_id_suministrador) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlVenta)) {
                ps.setInt(1, idVenta);
                ps.setDouble(2, montoTotal);
                ps.setString(3, tipo);
                ps.setString(4, descripcion);
                ps.setInt(5, idSuplidor);
                ps.executeUpdate();
            }

            try (PreparedStatement psUpd = con.prepareStatement("UPDATE TBL_VEHICULO SET estado = 'Vendido' WHERE id_vehiculo = ?")) {
                psUpd.setInt(1, idVehiculoSeleccionado);
                psUpd.executeUpdate();
            }

            con.commit();
            informacion("Venta registrada con ID: " + idVenta + ". Vehículo marcado como Vendido.");
            limpiar(event);
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            error("Error al registrar venta: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtMontoTotal.clear();
        cmbTipo.setValue(null);
        txtDescripcion.clear();
        cmbSuplidor.setValue(null);
        txtIdVehiculo.clear();
        lblVehiculoInfo.setText("");
        lblVehiculoId.setText("");
        idVehiculoSeleccionado = -1;
    }

    private boolean validarCampos() {
        if (txtMontoTotal.getText().isBlank()) {
            advertencia("Monto total obligatorio."); return false;
        }
        if (cmbTipo.getValue() == null) {
            advertencia("Seleccione tipo de venta."); return false;
        }
        if (cmbSuplidor.getValue() == null) {
            advertencia("Seleccione un suplidor."); return false;
        }
        try {
            Double.parseDouble(txtMontoTotal.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Monto total debe ser un número válido."); return false;
        }
        return true;
    }
}
