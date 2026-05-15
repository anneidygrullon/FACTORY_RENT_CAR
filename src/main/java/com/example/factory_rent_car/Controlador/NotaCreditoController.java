package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.NotaCredito;
import com.example.factory_rent_car.Database.Conexion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NotaCreditoController {

    private MainLayoutController mainController;
    Conexion conexion = Conexion.getInstance();

    // Tabla de notas de crédito
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

    // Formulario de nota de crédito
    @FXML private TextField txtReservaIdNota;
    @FXML private TextField txtClienteNota;
    @FXML private TextField txtTotalPagado;
    @FXML private TextField txtDiasNoUsados;
    @FXML private TextField txtMontoCredito;
    @FXML private TextField txtMotivoCredito;

    private final ObservableList<NotaCredito> listaNotas = FXCollections.observableArrayList();

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colIdNota.setCellValueFactory(c -> c.getValue().idNotaProperty().asObject());
        colFechaNota.setCellValueFactory(c -> c.getValue().fechaProperty());
        colClienteNota.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colReservaNota.setCellValueFactory(c -> c.getValue().reservaInfoProperty());
        colMontoNota.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        colMotivoNota.setCellValueFactory(c -> c.getValue().motivoProperty());
        colUsadoNota.setCellValueFactory(c -> c.getValue().usadoProperty());

        tablaNotasCredito.setItems(listaNotas);

        tableContainerNotas.setVisible(true);
        btnToggleTableNotas.setText("\uD83D\uDCCB Ocultar Tabla");

        cargarNotasCredito();
    }

    private void cargarNotasCredito() {
        listaNotas.clear();
        String sql = "SELECT pk_id_nota, tipo, motivo, fecha, descripcion, fk_id_devolucion " +
                "FROM TBL_NOTAS_FINANCIERAS ORDER BY pk_id_nota DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idDev = rs.getInt("fk_id_devolucion");
                boolean devNull = rs.wasNull();
                listaNotas.add(new NotaCredito(
                        rs.getInt("pk_id_nota"),
                        rs.getString("tipo"),
                        rs.getString("motivo"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("descripcion"),
                        devNull ? 0 : idDev,
                        0.0, "",
                        devNull ? "\u2014" : "Devoluci\u00f3n #" + idDev,
                        false
                ));
            }
            tablaNotasCredito.refresh();
        } catch (SQLException e) {
            error("Error cargando notas de cr\u00e9dito: " + e.getMessage());
        }
    }

    @FXML
    private void buscarReservaNota(ActionEvent event) {
        String idText = txtReservaIdNota.getText().trim();
        if (idText.isEmpty()) {
            advertencia("Ingrese un ID de reserva.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            advertencia("ID inv\u00e1lido.");
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

                LocalDate fechaFin = rs.getDate("fech_devolucion").toLocalDate();
                LocalDate hoy = LocalDate.now();
                if (hoy.isBefore(fechaFin)) {
                    long diasNoUsados = ChronoUnit.DAYS.between(hoy, fechaFin);
                    txtDiasNoUsados.setText(String.valueOf(diasNoUsados));
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
                advertencia("Reserva no encontrada.");
                limpiarNota();
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void crearNotaCredito(ActionEvent event) {
        String reservaIdText = txtReservaIdNota.getText().trim();
        if (reservaIdText.isEmpty()) {
            advertencia("Primero busque una reserva v\u00e1lida.");
            return;
        }
        int reservaId = Integer.parseInt(reservaIdText);
        String motivo = txtMotivoCredito.getText().trim();
        if (motivo.isEmpty()) {
            motivo = "Devoluci\u00f3n anticipada";
        }
        double montoCredito;
        try {
            montoCredito = Double.parseDouble(txtMontoCredito.getText().trim());
            if (montoCredito <= 0) {
                advertencia("El monto a acreditar debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            advertencia("Monto inv\u00e1lido.");
            return;
        }

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            int reclamoId;
            String sqlNextReclamo = "SELECT ISNULL(MAX(pk_id_reclamo), 0) + 1 AS next_id FROM TBL_RECLAMACION";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextReclamo);
                 ResultSet rsNext = psNext.executeQuery()) {
                reclamoId = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }

            String sqlReclamo = "INSERT INTO TBL_RECLAMACION (pk_id_reclamo, estado, motivo, descripcion, fk_pk_id_cliente, fk_pk_id_hist_reclamo, fk_pk_id_empleado) " +
                    "VALUES (?, 'Pendiente', ?, ?, ?, 1, 1)";
            PreparedStatement psReclamo = con.prepareStatement(sqlReclamo);
            psReclamo.setInt(1, reclamoId);
            psReclamo.setString(2, motivo);
            psReclamo.setString(3, "Devoluci\u00f3n anticipada - Nota de cr\u00e9dito generada");
            psReclamo.setInt(4, obtenerClienteIdPorReserva(reservaId));
            psReclamo.executeUpdate();

            int devolucionId;
            String sqlNextDev = "SELECT ISNULL(MAX(id_devolucion), 0) + 1 AS next_id FROM TBL_DEVOLUCION";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextDev);
                 ResultSet rsNext = psNext.executeQuery()) {
                devolucionId = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }

            String sqlDevolucion = "INSERT INTO TBL_DEVOLUCION (id_devolucion, fecha, razon, descripcion, fk_pk_id_reclamo) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDevolucion = con.prepareStatement(sqlDevolucion);
            psDevolucion.setInt(1, devolucionId);
            psDevolucion.setDate(2, Date.valueOf(LocalDate.now()));
            psDevolucion.setString(3, motivo);
            psDevolucion.setString(4, "Generaci\u00f3n de nota de cr\u00e9dito por devoluci\u00f3n anticipada");
            psDevolucion.setInt(5, reclamoId);
            psDevolucion.executeUpdate();

            int notaId;
            String sqlNextNota = "SELECT ISNULL(MAX(pk_id_nota), 0) + 1 AS next_id FROM TBL_NOTAS_FINANCIERAS";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextNota);
                 ResultSet rsNext = psNext.executeQuery()) {
                notaId = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }

            String sqlNota = "INSERT INTO TBL_NOTAS_FINANCIERAS (pk_id_nota, tipo, motivo, fecha, descripcion, fk_id_devolucion) " +
                    "VALUES (?, 'Cr\u00e9dito', ?, ?, ?, ?)";
            PreparedStatement psNota = con.prepareStatement(sqlNota);
            psNota.setInt(1, notaId);
            psNota.setString(2, motivo);
            psNota.setDate(3, Date.valueOf(LocalDate.now()));
            psNota.setString(4, "Nota de cr\u00e9dito por devoluci\u00f3n anticipada");
            psNota.setInt(5, devolucionId);
            psNota.executeUpdate();

            con.commit();
            informacion("Nota de cr\u00e9dito creada exitosamente por RD$ " + String.format("%.2f", montoCredito));
            limpiarNota();
            cargarNotasCredito();
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            error("Error al crear nota de cr\u00e9dito: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
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
    private void limpiarNota(ActionEvent event) {
        limpiarNota();
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
    private void toggleTableNotasVisibility(ActionEvent event) {
        boolean visible = tableContainerNotas.isVisible();
        tableContainerNotas.setVisible(!visible);
        tableContainerNotas.setManaged(!visible);
        btnToggleTableNotas.setText(visible ? "\uD83D\uDCCB Mostrar Tabla" : "\uD83D\uDCCB Ocultar Tabla");
    }

    @FXML
    private void irAConsultaPagos(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Consulta de Pagos", mainController.getSubMenuPagoConsulta(), "PagoConsulta.fxml");
        }
    }

    @FXML
    private void irARegistroPago(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Registro de Pago", mainController.getSubMenuPagoRegistro(), "PagoRegistro.fxml");
        }
    }
}
