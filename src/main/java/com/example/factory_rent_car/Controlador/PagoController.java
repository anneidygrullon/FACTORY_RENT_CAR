package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Pago;
import com.example.factory_rent_car.Modelo.NotaCredito;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class PagoController {

    Conexion conexion = new Conexion();

    // Componentes Tabla Pagos
    @FXML private TextField txtBuscar;
    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, Integer> colIdPago;
    @FXML private TableColumn<Pago, LocalDate> colFechaPago;
    @FXML private TableColumn<Pago, String> colClientePago;
    @FXML private TableColumn<Pago, String> colReservaPago;
    @FXML private TableColumn<Pago, String> colMetodoPago;
    @FXML private TableColumn<Pago, String> colCuentaPago;
    @FXML private TableColumn<Pago, Double> colMontoPago;
    @FXML private TableColumn<Pago, String> colTipoPago;
    @FXML private VBox tableContainerPagos;
    @FXML private Button btnToggleTablePagos;

    // Componentes Tabla Notas de Crédito
    @FXML private TableView<NotaCredito> tablaNotasCredito;
    @FXML private TableColumn<NotaCredito, Integer> colIdNota;
    @FXML private TableColumn<NotaCredito, LocalDate> colFechaNota;
    @FXML private TableColumn<NotaCredito, String> colClienteNota;
    @FXML private TableColumn<NotaCredito, String> colReservaNota;
    @FXML private TableColumn<NotaCredito, Double> colMontoNota;
    @FXML private TableColumn<NotaCredito, String> colMotivoNota;
    @FXML private TableColumn<NotaCredito, Boolean> colUsadoNota;
    @FXML private VBox tableContainerNotas;
    @FXML private Button btnToggleTableNotas;

    // Componentes Registro de Pago
    @FXML private TextField txtReservaId;
    @FXML private TextField txtCliente;
    @FXML private TextField txtMontoTotal;
    @FXML private TextField txtMontoPagado;
    @FXML private TextField txtMontoPendiente;
    @FXML private TextField txtNotasDisponibles;
    @FXML private ComboBox<String> cmbNotaCredito;
    @FXML private TextField txtMontoAPagar;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox<String> cmbCuenta;

    // Componentes Nota de Crédito
    @FXML private TextField txtReservaIdNota;
    @FXML private TextField txtClienteNota;
    @FXML private TextField txtTotalPagado;
    @FXML private TextField txtDiasNoUsados;
    @FXML private TextField txtMontoCredito;
    @FXML private TextField txtMotivoCredito;

    private final ObservableList<Pago> listaPagos = FXCollections.observableArrayList();
    private final ObservableList<NotaCredito> listaNotas = FXCollections.observableArrayList();
    private Map<String, Integer> mapaMetodosPago = new HashMap<>();
    private Map<String, Integer> mapaCuentas = new HashMap<>();
    private Map<String, Integer> mapaNotasCredito = new HashMap<>();
    private int reservaActualId = -1;
    private double montoPendienteActual = 0;
    private double totalPagadoActual = 0;

    @FXML
    public void initialize() {
        // Configurar columnas Pagos
        colIdPago.setCellValueFactory(c -> c.getValue().idPagoProperty().asObject());
        colFechaPago.setCellValueFactory(c -> c.getValue().fechaProperty());
        colClientePago.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colReservaPago.setCellValueFactory(c -> c.getValue().reservaInfoProperty());
        colMetodoPago.setCellValueFactory(c -> c.getValue().metodoPagoNombreProperty());
        colCuentaPago.setCellValueFactory(c -> c.getValue().cuentaInfoProperty());
        colMontoPago.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        colTipoPago.setCellValueFactory(c -> c.getValue().tipoProperty());

        // Configurar columnas Notas de Crédito
        colIdNota.setCellValueFactory(c -> c.getValue().idNotaProperty().asObject());
        colFechaNota.setCellValueFactory(c -> c.getValue().fechaProperty());
        colClienteNota.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colReservaNota.setCellValueFactory(c -> c.getValue().reservaInfoProperty());
        colMontoNota.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        colMotivoNota.setCellValueFactory(c -> c.getValue().motivoProperty());
        colUsadoNota.setCellValueFactory(c -> c.getValue().usadoProperty());

        tablaPagos.setItems(listaPagos);
        tablaNotasCredito.setItems(listaNotas);

        tableContainerPagos.setVisible(true);
        btnToggleTablePagos.setText("📋 Ocultar Tabla");
        tableContainerNotas.setVisible(true);
        btnToggleTableNotas.setText("📋 Ocultar Tabla");

        cargarMetodosPago();
        cargarCuentas();
        cargarPagos();
        cargarNotasCredito();
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
            JOptionPane.showMessageDialog(null, "Error cargando métodos de pago: " + e.getMessage());
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
            JOptionPane.showMessageDialog(null, "Error cargando cuentas: " + e.getMessage());
        }
    }

    private void cargarPagos() {
        listaPagos.clear();
        String sql = "SELECT p.id_pago, p.fecha, p.tipo, p.monto, p.fk_id_factura, " +
                "p.fk_id_metodo_pago, p.fk_id_cuenta, mp.tipo AS metodo_nombre, " +
                "c.numero + ' - ' + c.banco AS cuenta_info, " +
                "r.pk_id_reserva AS reserva_id, cl.nombre AS cliente_nombre " +
                "FROM TBL_PAGO p " +
                "LEFT JOIN TBL_METODO_PAGO mp ON mp.id_metodo_pago = p.fk_id_metodo_pago " +
                "LEFT JOIN TBL_CUENTA c ON c.id_cuenta = p.fk_id_cuenta " +
                "LEFT JOIN TBL_FACTURA f ON f.id_factura = p.fk_id_factura " +
                "LEFT JOIN TBL_DETALLE_FACTURA df ON df.pk_id_det_factura = f.fk_pk_id_det_factura " +
                "LEFT JOIN TBL_PLAN_SEGURO ps ON ps.pk_id_seguro = df.fk_pk_id_seguro " +
                "LEFT JOIN TBL_RESERVACION r ON r.pk_id_reserva = df.fk_pk_id_objeto " +
                "LEFT JOIN TBL_CLIENTE cl ON cl.pk_id_cliente = r.fk_pk_id_cliente " +
                "ORDER BY p.id_pago DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pago p = new Pago(
                        rs.getInt("id_pago"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        rs.getInt("fk_id_factura"),
                        rs.getInt("fk_id_metodo_pago"),
                        rs.getInt("fk_id_cuenta"),
                        rs.getString("metodo_nombre"),
                        rs.getString("cuenta_info"),
                        "Reserva #" + rs.getInt("reserva_id"),
                        rs.getString("cliente_nombre")
                );
                listaPagos.add(p);
            }
            tablaPagos.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando pagos: " + e.getMessage());
        }
    }

    private void cargarNotasCredito() {
        listaNotas.clear();
        String sql = "SELECT n.pk_id_nota, n.tipo, n.motivo, n.fecha, n.descripcion, " +
                "n.fk_id_devolucion, d.monto, cl.nombre AS cliente_nombre, " +
                "r.pk_id_reserva AS reserva_id " +
                "FROM TBL_NOTAS_FINANCIERAS n " +
                "LEFT JOIN TBL_DEVOLUCION d ON d.id_devolucion = n.fk_id_devolucion " +
                "LEFT JOIN TBL_RECLAMACION rec ON rec.pk_id_reclamo = d.fk_pk_id_reclamo " +
                "LEFT JOIN TBL_CLIENTE cl ON cl.pk_id_cliente = rec.fk_pk_id_cliente " +
                "LEFT JOIN TBL_RESERVACION r ON r.pk_id_reserva = rec.fk_pk_id_reclamo " +
                "ORDER BY n.pk_id_nota DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NotaCredito n = new NotaCredito(
                        rs.getInt("pk_id_nota"),
                        rs.getString("tipo"),
                        rs.getString("motivo"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("descripcion"),
                        rs.getInt("fk_id_devolucion"),
                        rs.getDouble("monto"),
                        rs.getString("cliente_nombre"),
                        "Reserva #" + rs.getInt("reserva_id"),
                        false
                );
                listaNotas.add(n);
            }
            tablaNotasCredito.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando notas de crédito: " + e.getMessage());
        }
    }

    @FXML
    private void buscarReserva(ActionEvent event) {
        String idText = txtReservaId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de reserva.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }

        String sql = "SELECT r.pk_id_reserva, r.monto_total, r.monto_apartado, r.monto_pendiente, " +
                "c.nombre AS cliente_nombre, " +
                "COALESCE(SUM(n.monto), 0) AS notas_disponibles " +
                "FROM TBL_RESERVACION r " +
                "LEFT JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "LEFT JOIN TBL_NOTAS_FINANCIERAS n ON n.fk_id_devolucion IN " +
                "(SELECT d.id_devolucion FROM TBL_DEVOLUCION d WHERE d.fk_pk_id_reclamo IN " +
                "(SELECT rec.pk_id_reclamo FROM TBL_RECLAMACION rec WHERE rec.fk_pk_id_cliente = c.pk_id_cliente)) " +
                "WHERE r.pk_id_reserva = ? " +
                "GROUP BY r.pk_id_reserva, r.monto_total, r.monto_apartado, r.monto_pendiente, c.nombre";
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
                double notas = rs.getDouble("notas_disponibles");
                txtNotasDisponibles.setText(String.format("%.2f", notas));
                txtMontoAPagar.setText(String.format("%.2f", pendiente));

                // Cargar notas de crédito disponibles para este cliente
                cargarNotasCreditoCliente(rs.getInt("pk_id_reserva"));
            } else {
                JOptionPane.showMessageDialog(null, "Reserva no encontrada.");
                limpiarPago();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void cargarNotasCreditoCliente(int reservaId) {
        cmbNotaCredito.getItems().clear();
        mapaNotasCredito.clear();
        String sql = "SELECT n.pk_id_nota, n.monto " +
                "FROM TBL_NOTAS_FINANCIERAS n " +
                "WHERE n.fk_id_devolucion IN " +
                "(SELECT d.id_devolucion FROM TBL_DEVOLUCION d WHERE d.fk_pk_id_reclamo IN " +
                "(SELECT rec.pk_id_reclamo FROM TBL_RECLAMACION rec WHERE rec.fk_pk_id_cliente = " +
                "(SELECT r.fk_pk_id_cliente FROM TBL_RESERVACION r WHERE r.pk_id_reserva = ?)))";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nota = "Nota #" + rs.getInt("pk_id_nota") + " - RD$ " + String.format("%.2f", rs.getDouble("monto"));
                cmbNotaCredito.getItems().add(nota);
                mapaNotasCredito.put(nota, rs.getInt("pk_id_nota"));
            }
            cmbNotaCredito.getItems().add("Ninguna");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando notas: " + e.getMessage());
        }
    }

    @FXML
    private void registrarPago(ActionEvent event) {
        if (reservaActualId == -1) {
            JOptionPane.showMessageDialog(null, "Primero busque una reserva válida.");
            return;
        }

        String metodoPagoStr = cmbMetodoPago.getValue();
        String cuentaStr = cmbCuenta.getValue();
        String notaStr = cmbNotaCredito.getValue();

        if (metodoPagoStr == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un método de pago.");
            return;
        }
        if (cuentaStr == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una cuenta bancaria.");
            return;
        }

        int metodoPagoId = mapaMetodosPago.get(metodoPagoStr);
        int cuentaId = mapaCuentas.get(cuentaStr);

        double montoAPagar;
        try {
            montoAPagar = Double.parseDouble(txtMontoAPagar.getText().trim());
            if (montoAPagar <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Monto a pagar inválido.");
            return;
        }

        double montoConNota = montoPendienteActual;
        if (notaStr != null && !notaStr.equals("Ninguna")) {
            Integer notaId = mapaNotasCredito.get(notaStr);
            if (notaId != null) {
                montoConNota = aplicarNotaCredito(notaId);
            }
        }

        if (montoAPagar > montoConNota) {
            JOptionPane.showMessageDialog(null, "El monto a pagar no puede exceder el pendiente después de aplicar nota de crédito.");
            return;
        }

        try (Connection con = conexion.establecerConexion()) {
            con.setAutoCommit(false);

            // Obtener o crear factura
            int facturaId = obtenerFacturaId(con, reservaActualId);

            // Registrar pago
            String sqlPago = "INSERT INTO TBL_PAGO (fecha, tipo, monto, fk_id_factura, fk_id_metodo_pago, fk_id_cuenta) " +
                    "VALUES (?, 'Pago', ?, ?, ?, ?)";
            PreparedStatement psPago = con.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS);
            psPago.setDate(1, Date.valueOf(LocalDate.now()));
            psPago.setDouble(2, montoAPagar);
            psPago.setInt(3, facturaId);
            psPago.setInt(4, metodoPagoId);
            psPago.setInt(5, cuentaId);
            psPago.executeUpdate();

            // Actualizar monto pendiente de la reserva
            double nuevoPendiente = montoPendienteActual - montoAPagar;
            if (nuevoPendiente < 0) nuevoPendiente = 0;
            String sqlUpdateReserva = "UPDATE TBL_RESERVACION SET monto_pendiente = ? WHERE pk_id_reserva = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdateReserva);
            psUpdate.setDouble(1, nuevoPendiente);
            psUpdate.setInt(2, reservaActualId);
            psUpdate.executeUpdate();

            con.commit();
            JOptionPane.showMessageDialog(null, "Pago registrado correctamente.");
            limpiarPago();
            cargarPagos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int obtenerFacturaId(Connection con, int reservaId) throws SQLException {
        // Buscar factura existente para esta reserva
        String sqlFind = "SELECT f.id_factura FROM TBL_FACTURA f " +
                "JOIN TBL_DETALLE_FACTURA df ON df.pk_id_det_factura = f.fk_pk_id_det_factura " +
                "WHERE df.fk_pk_id_objeto = ?";
        PreparedStatement psFind = con.prepareStatement(sqlFind);
        psFind.setInt(1, reservaId);
        ResultSet rs = psFind.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_factura");
        }

        // Crear detalle de factura
        String sqlDetalle = "INSERT INTO TBL_DETALLE_FACTURA (cantidad, fk_pk_id_objeto, fk_pk_id_seguro) " +
                "VALUES (1, ?, (SELECT fk_pk_id_seguro FROM TBL_RESERVACION WHERE pk_id_reserva = ?))";
        PreparedStatement psDetalle = con.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS);
        psDetalle.setInt(1, reservaId);
        psDetalle.setInt(2, reservaId);
        psDetalle.executeUpdate();
        ResultSet rsDetalle = psDetalle.getGeneratedKeys();
        int detalleId = rsDetalle.next() ? rsDetalle.getInt(1) : -1;

        // Crear factura
        String sqlFactura = "INSERT INTO TBL_FACTURA (monto_total, fecha, fk_pk_id_det_factura) " +
                "VALUES ((SELECT monto_total FROM TBL_RESERVACION WHERE pk_id_reserva = ?), ?, ?)";
        PreparedStatement psFactura = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
        psFactura.setInt(1, reservaId);
        psFactura.setDate(2, Date.valueOf(LocalDate.now()));
        psFactura.setInt(3, detalleId);
        psFactura.executeUpdate();
        ResultSet rsFactura = psFactura.getGeneratedKeys();
        return rsFactura.next() ? rsFactura.getInt(1) : -1;
    }

    private double aplicarNotaCredito(int notaId) {
        try (Connection con = conexion.establecerConexion()) {
            String sql = "SELECT monto FROM TBL_NOTAS_FINANCIERAS WHERE pk_id_nota = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, notaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("monto");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al aplicar nota: " + e.getMessage());
        }
        return 0;
    }

    @FXML
    private void buscarReservaNota(ActionEvent event) {
        String idText = txtReservaIdNota.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de reserva.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }

        String sql = "SELECT r.pk_id_reserva, r.monto_total, r.monto_pendiente, r.fecha_inicio, r.fech_devolucion, " +
                "c.nombre AS cliente_nombre " +
                "FROM TBL_RESERVACION r " +
                "JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "WHERE r.pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtClienteNota.setText(rs.getString("cliente_nombre"));
                double totalPagado = rs.getDouble("monto_total") - rs.getDouble("monto_pendiente");
                txtTotalPagado.setText(String.format("%.2f", totalPagado));

                // Calcular días no utilizados (si la reserva terminó antes)
                LocalDate fechaFin = rs.getDate("fech_devolucion").toLocalDate();
                LocalDate hoy = LocalDate.now();
                if (hoy.isBefore(fechaFin)) {
                    long diasNoUsados = ChronoUnit.DAYS.between(hoy, fechaFin);
                    txtDiasNoUsados.setText(String.valueOf(diasNoUsados));
                    // Calcular monto a acreditar (proporcional por día)
                    double total = rs.getDouble("monto_total");
                    long diasTotales = ChronoUnit.DAYS.between(rs.getDate("fecha_inicio").toLocalDate(), fechaFin);
                    double montoPorDia = total / diasTotales;
                    double montoCredito = montoPorDia * diasNoUsados;
                    txtMontoCredito.setText(String.format("%.2f", montoCredito));
                } else {
                    txtDiasNoUsados.setText("0");
                    txtMontoCredito.setText("0.00");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Reserva no encontrada.");
                limpiarNota();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void crearNotaCredito(ActionEvent event) {
        String reservaIdText = txtReservaIdNota.getText().trim();
        if (reservaIdText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Primero busque una reserva válida.");
            return;
        }
        int reservaId = Integer.parseInt(reservaIdText);
        String motivo = txtMotivoCredito.getText().trim();
        if (motivo.isEmpty()) {
            motivo = "Devolución anticipada";
        }
        double montoCredito;
        try {
            montoCredito = Double.parseDouble(txtMontoCredito.getText().trim());
            if (montoCredito <= 0) {
                JOptionPane.showMessageDialog(null, "El monto a acreditar debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Monto inválido.");
            return;
        }

        try (Connection con = conexion.establecerConexion()) {
            con.setAutoCommit(false);

            // Crear reclamo
            String sqlReclamo = "INSERT INTO TBL_RECLAMACION (estado, motivo, descripcion, fk_pk_id_cliente, fk_pk_id_hist_reclamo, fk_pk_id_empleado) " +
                    "VALUES ('Pendiente', ?, ?, ?, 1, 1)";
            PreparedStatement psReclamo = con.prepareStatement(sqlReclamo, Statement.RETURN_GENERATED_KEYS);
            psReclamo.setString(1, motivo);
            psReclamo.setString(2, "Devolución anticipada - Nota de crédito generada");
            psReclamo.setInt(3, obtenerClienteIdPorReserva(reservaId));
            psReclamo.executeUpdate();
            ResultSet rsReclamo = psReclamo.getGeneratedKeys();
            int reclamoId = rsReclamo.next() ? rsReclamo.getInt(1) : -1;

            // Crear devolución
            String sqlDevolucion = "INSERT INTO TBL_DEVOLUCION (fecha, razon, descripcion, fk_pk_id_reclamo) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement psDevolucion = con.prepareStatement(sqlDevolucion, Statement.RETURN_GENERATED_KEYS);
            psDevolucion.setDate(1, Date.valueOf(LocalDate.now()));
            psDevolucion.setString(2, motivo);
            psDevolucion.setString(3, "Generación de nota de crédito por devolución anticipada");
            psDevolucion.setInt(4, reclamoId);
            psDevolucion.executeUpdate();
            ResultSet rsDevolucion = psDevolucion.getGeneratedKeys();
            int devolucionId = rsDevolucion.next() ? rsDevolucion.getInt(1) : -1;

            // Crear nota de crédito
            String sqlNota = "INSERT INTO TBL_NOTAS_FINANCIERAS (tipo, motivo, fecha, descripcion, fk_id_devolucion) " +
                    "VALUES ('Crédito', ?, ?, ?, ?)";
            PreparedStatement psNota = con.prepareStatement(sqlNota);
            psNota.setString(1, motivo);
            psNota.setDate(2, Date.valueOf(LocalDate.now()));
            psNota.setString(3, "Nota de crédito por devolución anticipada");
            psNota.setInt(4, devolucionId);
            psNota.executeUpdate();

            con.commit();
            JOptionPane.showMessageDialog(null, "Nota de crédito creada exitosamente por RD$ " + String.format("%.2f", montoCredito));
            limpiarNota();
            cargarNotasCredito();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear nota de crédito: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int obtenerClienteIdPorReserva(int reservaId) throws SQLException {
        String sql = "SELECT fk_pk_id_cliente FROM TBL_RESERVACION WHERE pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("fk_pk_id_cliente") : -1;
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarPagos();
            return;
        }
        ObservableList<Pago> filtrados = FXCollections.observableArrayList();
        for (Pago p : listaPagos) {
            if (p.getClienteNombre().toLowerCase().contains(filtro) ||
                    p.getReservaInfo().toLowerCase().contains(filtro) ||
                    p.getMetodoPagoNombre().toLowerCase().contains(filtro)) {
                filtrados.add(p);
            }
        }
        tablaPagos.setItems(filtrados);
        tablaPagos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarPagos();
    }

    @FXML
    private void toggleTablePagosVisibility(ActionEvent event) {
        boolean visible = tableContainerPagos.isVisible();
        tableContainerPagos.setVisible(!visible);
        tableContainerPagos.setManaged(!visible);
        btnToggleTablePagos.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void toggleTableNotasVisibility(ActionEvent event) {
        boolean visible = tableContainerNotas.isVisible();
        tableContainerNotas.setVisible(!visible);
        tableContainerNotas.setManaged(!visible);
        btnToggleTableNotas.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void limpiar() {
        limpiarPago();
        limpiarNota();
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
        txtNotasDisponibles.clear();
        txtMontoAPagar.clear();
        cmbNotaCredito.getItems().clear();
        cmbMetodoPago.setValue(null);
        cmbCuenta.setValue(null);
    }

    private void limpiarNota() {
        txtReservaIdNota.clear();
        txtClienteNota.clear();
        txtTotalPagado.clear();
        txtDiasNoUsados.clear();
        txtMontoCredito.clear();
        txtMotivoCredito.clear();
    }

    @FXML
    private void limpiarNota(ActionEvent event) {
        limpiarNota();
    }
}