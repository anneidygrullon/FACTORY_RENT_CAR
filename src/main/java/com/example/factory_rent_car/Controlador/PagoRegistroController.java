package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PagoRegistroController {

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtReservaId;
    @FXML private TextField txtCliente;
    @FXML private TextField txtMontoTotal;
    @FXML private TextField txtMontoPagado;
    @FXML private TextField txtMontoPendiente;
    @FXML private TextField txtMontoAPagar;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox<String> cmbCuenta;

    private Map<String, Integer> mapaMetodosPago = new HashMap<>();
    private Map<String, Integer> mapaCuentas = new HashMap<>();
    private int reservaActualId = -1;
    private double montoPendienteActual = 0;
    private double totalPagadoActual = 0;

    @FXML
    public void initialize() {
        cargarMetodosPago();
        cargarCuentas();
    }

    private void cargarMetodosPago() {
        String sql = "SELECT id_metodo_pago, tipo FROM TBL_METODO_PAGO";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_metodo_pago");
                String tipo = rs.getString("tipo");
                cmbMetodoPago.getItems().add(tipo);
                mapaMetodosPago.put(tipo, id);
            }
        } catch (SQLException e) {
            error("Error cargando métodos de pago: " + e.getMessage());
        }
    }

    private void cargarCuentas() {
        String sql = "SELECT id_cuenta, numero, banco FROM TBL_CUENTA";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_cuenta");
                String cuenta = rs.getString("numero") + " - " + rs.getString("banco");
                cmbCuenta.getItems().add(cuenta);
                mapaCuentas.put(cuenta, id);
            }
        } catch (SQLException e) {
            error("Error cargando cuentas: " + e.getMessage());
        }
    }

    @FXML
    private void buscarReserva(ActionEvent event) {
        String idText = txtReservaId.getText().trim();
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

        String sql = "SELECT r.pk_id_reserva, r.monto_total, r.monto_apartado, r.monto_pendiente, " +
                "c.nombre AS cliente_nombre " +
                "FROM TBL_RESERVACION r " +
                "LEFT JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "WHERE r.pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reservaActualId = id;
                txtCliente.setText(rs.getString("cliente_nombre"));
                txtMontoTotal.setText(String.format("%.2f", rs.getDouble("monto_total")));
                txtMontoPagado.setText(String.format("%.2f", rs.getDouble("monto_total") - rs.getDouble("monto_pendiente")));
                totalPagadoActual = rs.getDouble("monto_total") - rs.getDouble("monto_pendiente");
                double pendiente = rs.getDouble("monto_pendiente");
                montoPendienteActual = pendiente;
                txtMontoPendiente.setText(String.format("%.2f", pendiente));
                txtMontoAPagar.setText(String.format("%.2f", pendiente));
            } else {
                advertencia("Reserva no encontrada.");
                limpiarPago();
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void registrarPago(ActionEvent event) {
        if (reservaActualId == -1) {
            advertencia("Primero busque una reserva válida.");
            return;
        }

        String metodoPagoStr = cmbMetodoPago.getValue();
        String cuentaStr = cmbCuenta.getValue();

        if (metodoPagoStr == null) {
            advertencia("Seleccione un método de pago.");
            return;
        }
        if (cuentaStr == null) {
            advertencia("Seleccione una cuenta bancaria.");
            return;
        }

        int metodoPagoId = mapaMetodosPago.get(metodoPagoStr);
        int cuentaId = mapaCuentas.get(cuentaStr);

        double montoAPagar;
        try {
            montoAPagar = Double.parseDouble(txtMontoAPagar.getText().trim());
            if (montoAPagar <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            advertencia("Monto a pagar inválido.");
            return;
        }

        if (montoAPagar > montoPendienteActual) {
            advertencia("El monto a pagar no puede exceder el pendiente.");
            return;
        }

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            int facturaId = obtenerFacturaId(con, reservaActualId);

            int pagoId;
            String sqlNextPago = "SELECT ISNULL(MAX(id_pago), 0) + 1 AS next_id FROM TBL_PAGO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextPago);
                 ResultSet rsNext = psNext.executeQuery()) {
                pagoId = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }

            String sqlPago = "INSERT INTO TBL_PAGO (id_pago, fecha, tipo, monto, fk_id_factura, fk_id_metodo_pago, fk_id_cuenta) " +
                    "VALUES (?, ?, 'Pago', ?, ?, ?, ?)";
            PreparedStatement psPago = con.prepareStatement(sqlPago);
            psPago.setInt(1, pagoId);
            psPago.setDate(2, Date.valueOf(LocalDate.now()));
            psPago.setDouble(3, montoAPagar);
            psPago.setInt(4, facturaId);
            psPago.setInt(5, metodoPagoId);
            psPago.setInt(6, cuentaId);
            psPago.executeUpdate();

            double nuevoPendiente = montoPendienteActual - montoAPagar;
            if (nuevoPendiente < 0) nuevoPendiente = 0;
            String sqlUpdateReserva = "UPDATE TBL_RESERVACION SET monto_pendiente = ? WHERE pk_id_reserva = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdateReserva);
            psUpdate.setDouble(1, nuevoPendiente);
            psUpdate.setInt(2, reservaActualId);
            psUpdate.executeUpdate();

            con.commit();
            informacion("Pago registrado correctamente.");
            limpiarPago();

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            error("Error al registrar pago: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private int obtenerFacturaId(Connection con, int reservaId) throws SQLException {
        String sqlFind = "SELECT f.id_factura FROM TBL_FACTURA f " +
                "JOIN TBL_DETALLE_FACTURA df ON df.pk_id_det_factura = f.fk_pk_id_det_factura " +
                "WHERE df.fk_pk_id_objeto = ?";
        PreparedStatement psFind = con.prepareStatement(sqlFind);
        psFind.setInt(1, reservaId);
        ResultSet rs = psFind.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_factura");
        }

        int detalleId;
        String sqlNextDet = "SELECT ISNULL(MAX(pk_id_det_factura), 0) + 1 AS next_id FROM TBL_DETALLE_FACTURA";
        try (PreparedStatement psNext = con.prepareStatement(sqlNextDet);
             ResultSet rsNext = psNext.executeQuery()) {
            detalleId = rsNext.next() ? rsNext.getInt("next_id") : 1;
        }

        String sqlDetalle = "INSERT INTO TBL_DETALLE_FACTURA (pk_id_det_factura, cantidad, fk_pk_id_objeto, fk_pk_id_seguro) " +
                "VALUES (?, 1, ?, (SELECT fk_pk_id_seguro FROM TBL_RESERVACION WHERE pk_id_reserva = ?))";
        PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
        psDetalle.setInt(1, detalleId);
        psDetalle.setInt(2, reservaId);
        psDetalle.setInt(3, reservaId);
        psDetalle.executeUpdate();

        int facturaId;
        String sqlNextFact = "SELECT ISNULL(MAX(id_factura), 0) + 1 AS next_id FROM TBL_FACTURA";
        try (PreparedStatement psNext = con.prepareStatement(sqlNextFact);
             ResultSet rsNext = psNext.executeQuery()) {
            facturaId = rsNext.next() ? rsNext.getInt("next_id") : 1;
        }

        String sqlFactura = "INSERT INTO TBL_FACTURA (id_factura, monto_total, fecha, fk_pk_id_det_factura) " +
                "VALUES (?, (SELECT monto_total FROM TBL_RESERVACION WHERE pk_id_reserva = ?), ?, ?)";
        PreparedStatement psFactura = con.prepareStatement(sqlFactura);
        psFactura.setInt(1, facturaId);
        psFactura.setInt(2, reservaId);
        psFactura.setDate(3, Date.valueOf(LocalDate.now()));
        psFactura.setInt(4, detalleId);
        psFactura.executeUpdate();
        return facturaId;
    }

    private void limpiarPago() {
        reservaActualId = -1;
        montoPendienteActual = 0;
        totalPagadoActual = 0;
        txtReservaId.clear();
        txtCliente.clear();
        txtMontoTotal.clear();
        txtMontoPagado.clear();
        txtMontoPendiente.clear();
        txtMontoAPagar.clear();
        cmbMetodoPago.setValue(null);
        cmbCuenta.setValue(null);
    }

    @FXML
    private void limpiar(ActionEvent event) {
        limpiarPago();
    }

    @FXML
    private void irAConsultaPagos(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Consulta de Pagos", mainController.getSubMenuPagoConsulta(), "PagoConsulta.fxml");
        }
    }

    @FXML
    private void irANotaCredito(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Notas de Crédito", mainController.getSubMenuNotaCredito(), "NotaCredito.fxml");
        }
    }
}
