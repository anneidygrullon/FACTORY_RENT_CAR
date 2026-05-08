package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Reservacion;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.LinkedHashMap;

public class ReservaController {

    Conexion conexion = new Conexion();

    // ── Formulario ────────────────────────────────────────────────────────
    @FXML private TextField        txtIdCliente;
    @FXML private TextField        txtNombreCliente;
    @FXML private TextField        txtIdVehiculo;
    @FXML private TextField        txtInfoVehiculo;
    @FXML private DatePicker       dpFechaInicio;
    @FXML private DatePicker       dpFechaDevolucion;
    @FXML private ComboBox<String> cmbSeguro;
    @FXML private TextField        txtDescuento;
    @FXML private TextField        txtMontoApartado;
    @FXML private Label            lblMontoTotal;
    @FXML private Label            lblMontoPendiente;
    @FXML private Label            lblEstado;
    @FXML private Label            lblDias;
    @FXML private TextField        txtBuscar;
    @FXML private Label            lblConteo;
    @FXML private Label            lblTituloFormulario;

    // ── Tabla ─────────────────────────────────────────────────────────────
    @FXML private TableView<Reservacion>           tablaReservaciones;
    @FXML private TableColumn<Reservacion, Number> colId;
    @FXML private TableColumn<Reservacion, String> colCliente;
    @FXML private TableColumn<Reservacion, String> colVehiculo;
    @FXML private TableColumn<Reservacion, String> colFechaInicio;
    @FXML private TableColumn<Reservacion, String> colFechaDevolucion;
    @FXML private TableColumn<Reservacion, Number> colMontoTotal;
    @FXML private TableColumn<Reservacion, Number> colPendiente;
    @FXML private TableColumn<Reservacion, String> colEstado;

    // ── Datos internos ────────────────────────────────────────────────────
    private final ObservableList<Reservacion>    listaReservaciones    = FXCollections.observableArrayList();
    private int                                  idReservaSeleccionada = -1;
    private final LinkedHashMap<String, Integer> mapaSeguros           = new LinkedHashMap<>();

    // ── Inicializar ───────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        dpFechaInicio.setValue(java.time.LocalDate.now());
        dpFechaDevolucion.setValue(java.time.LocalDate.now().plusDays(1));

        colId.setCellValueFactory(c -> c.getValue().idReservaProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoProperty());
        colFechaInicio.setCellValueFactory(c -> c.getValue().fechaInicioProperty());
        colFechaDevolucion.setCellValueFactory(c -> c.getValue().fechaDevolucionProperty());
        colMontoTotal.setCellValueFactory(c -> c.getValue().montoTotalProperty());
        colPendiente.setCellValueFactory(c -> c.getValue().montoPendienteProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());

        // Colores de estado en la tabla
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Activa"     -> setStyle("-fx-text-fill: #1565C0; -fx-font-weight: bold;");
                    case "Completada" -> setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                    case "Vencida"    -> setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                    default           -> setStyle("");
                }
            }
        });

        tablaReservaciones.setItems(listaReservaciones);

        // Selección en tabla → carga formulario
        tablaReservaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if (newSel != null) cargarEnFormulario(newSel); });

        // Recalculo automático al cambiar fechas/seguro/montos
        dpFechaInicio.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        dpFechaDevolucion.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        cmbSeguro.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        txtDescuento.textProperty().addListener((o, ov, nv) -> recalcularMonto());
        txtMontoApartado.textProperty().addListener((o, ov, nv) -> recalcularMonto());

        cargarSeguros();
        cargarReservaciones();
    }

    // ── Cargar seguros desde BD ───────────────────────────────────────────
    private void cargarSeguros() {
        mapaSeguros.clear();
        cmbSeguro.getItems().clear();
        String sql = "SELECT pk_id_seguro, nombre, costo FROM TBL_PLAN_SEGURO ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("nombre") +
                        " (RD$ " + String.format("%.2f", rs.getDouble("costo")) + "/día)";
                mapaSeguros.put(label, rs.getInt("pk_id_seguro"));
                cmbSeguro.getItems().add(label);
            }
        } catch (SQLException e) {
            cmbSeguro.getItems().add("Sin seguros registrados");
        }
    }

    // ── Buscar cliente por ID ─────────────────────────────────────────────
    @FXML
    public void onBuscarCliente(ActionEvent ignored) {
        if (txtIdCliente.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingresa el ID del cliente."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT nombre FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdCliente.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtNombreCliente.setText(rs.getString("nombre"));
            else { JOptionPane.showMessageDialog(null, "Cliente no encontrado."); txtNombreCliente.clear(); }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de cliente inválido.");
        }
    }

    // ── Buscar vehículo por ID ────────────────────────────────────────────
    @FXML
    public void onBuscarVehiculo(ActionEvent ignored) {
        if (txtIdVehiculo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingresa el ID del vehículo."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT marca, modelo, estado, precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdVehiculo.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtInfoVehiculo.setText(
                        rs.getString("marca") + " " + rs.getString("modelo") +
                                " — " + rs.getString("estado") +
                                " | RD$ " + String.format("%.2f", rs.getDouble("precio_x_dia")) + "/día");
                recalcularMonto();
            } else {
                JOptionPane.showMessageDialog(null, "Vehículo no encontrado.");
                txtInfoVehiculo.clear();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de vehículo inválido.");
        }
    }

    // ── Recalcular montos en tiempo real ──────────────────────────────────
    private void recalcularMonto() {
        if (dpFechaInicio.getValue() == null || dpFechaDevolucion.getValue() == null) return;
        if (txtIdVehiculo.getText().isBlank()) return;

        long dias = java.time.temporal.ChronoUnit.DAYS.between(
                dpFechaInicio.getValue(), dpFechaDevolucion.getValue());
        if (dias <= 0) {
            lblDias.setText("⚠ Fechas inválidas");
            lblDias.setStyle("-fx-text-fill: #C62828;");
            return;
        }
        lblDias.setText(dias + " día(s)");
        lblDias.setStyle("-fx-text-fill: #1565C0; -fx-font-weight: bold;");

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdVehiculo.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return;
            double subtotal  = rs.getDouble("precio_x_dia") * dias;
            double total     = subtotal - subtotal * (parseDouble(txtDescuento.getText()) / 100.0);
            double pendiente = Math.max(0, total - parseDouble(txtMontoApartado.getText()));
            lblMontoTotal.setText("RD$ " + String.format("%.2f", total));
            lblMontoPendiente.setText("RD$ " + String.format("%.2f", pendiente));
            actualizarEtiquetaEstado(pendiente, dpFechaDevolucion.getValue().toString());
        } catch (SQLException | NumberFormatException ignored2) { /* silencioso */ }
    }

    // ── Cargar tabla de reservaciones ─────────────────────────────────────
    @FXML
    public void cargarReservaciones() {
        listaReservaciones.clear();
        idReservaSeleccionada = -1;
        String sql =
                "SELECT r.pk_id_reserva, " +
                        "CONVERT(VARCHAR(10), r.fecha_inicio,    120) AS fecha_inicio, " +
                        "CONVERT(VARCHAR(10), r.fech_devolucion, 120) AS fecha_devolucion, " +
                        "r.monto_total, r.monto_apartado, r.monto_pendiente, " +
                        "ISNULL(r.descuento, 0) AS descuento, " +
                        "c.nombre AS nombre_cliente, " +
                        "ISNULL(v.marca + ' ' + v.modelo, '—') AS vehiculo, " +
                        "s.nombre AS seguro, " +
                        "r.fk_pk_id_cliente, r.fk_pk_id_seguro, r.fk_pk_id_contrato, " +
                        "ISNULL(rv.fk_id_vehiculo, 0) AS fk_id_vehiculo " +
                        "FROM TBL_RESERVACION r " +
                        "JOIN TBL_CLIENTE     c  ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                        "JOIN TBL_PLAN_SEGURO s  ON s.pk_id_seguro  = r.fk_pk_id_seguro " +
                        "LEFT JOIN TBL_RESERVA_VEHI rv ON rv.fk_pk_id_reserva = r.pk_id_reserva " +
                        "LEFT JOIN TBL_VEHICULO      v  ON v.id_vehiculo       = rv.fk_id_vehiculo " +
                        "ORDER BY r.pk_id_reserva DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaReservaciones.add(new Reservacion(
                        rs.getInt("pk_id_reserva"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_devolucion"),
                        rs.getDouble("monto_total"),
                        rs.getDouble("monto_apartado"),
                        rs.getDouble("monto_pendiente"),
                        rs.getDouble("descuento"),
                        rs.getString("nombre_cliente"),
                        rs.getString("vehiculo"),
                        rs.getString("seguro"),
                        rs.getInt("fk_pk_id_cliente"),
                        rs.getInt("fk_pk_id_seguro"),
                        rs.getInt("fk_pk_id_contrato"),
                        rs.getInt("fk_id_vehiculo")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar reservaciones: " + e.getMessage());
        }
        actualizarConteo();
    }

    // ── Buscar / filtrar en tabla ─────────────────────────────────────────
    @FXML
    public void fnBuscar(ActionEvent ignored) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) { tablaReservaciones.setItems(listaReservaciones); actualizarConteo(); return; }
        ObservableList<Reservacion> filtradas = FXCollections.observableArrayList();
        for (Reservacion r : listaReservaciones) {
            if (r.getCliente().toLowerCase().contains(busqueda)
                    || r.getVehiculo().toLowerCase().contains(busqueda)
                    || r.getEstado().toLowerCase().contains(busqueda)
                    || String.valueOf(r.getIdReserva()).contains(busqueda))
                filtradas.add(r);
        }
        tablaReservaciones.setItems(filtradas);
        actualizarConteo();
    }

    // ── Filtros rápidos por estado ────────────────────────────────────────
    @FXML public void filtrarTodas(ActionEvent ignored)      { tablaReservaciones.setItems(listaReservaciones); actualizarConteo(); }
    @FXML public void filtrarActivas(ActionEvent ignored)    { filtrarPorEstado("Activa"); }
    @FXML public void filtrarVencidas(ActionEvent ignored)   { filtrarPorEstado("Vencida"); }
    @FXML public void filtrarCompletadas(ActionEvent ignored){ filtrarPorEstado("Completada"); }

    private void filtrarPorEstado(String estado) {
        ObservableList<Reservacion> filtradas = FXCollections.observableArrayList();
        for (Reservacion r : listaReservaciones) {
            if (r.getEstado().equalsIgnoreCase(estado)) filtradas.add(r);
        }
        tablaReservaciones.setItems(filtradas);
        actualizarConteo();
    }

    private void actualizarConteo() {
        if (lblConteo != null)
            lblConteo.setText(tablaReservaciones.getItems().size() + " registro(s)");
    }

    // ── Guardar nueva reservación ─────────────────────────────────────────
    @FXML
    public void onGuardarReserva(ActionEvent ignored) {
        if (!validarFormulario()) return;
        try {
            int    idCliente  = Integer.parseInt(txtIdCliente.getText().trim());
            int    idVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
            int    idSeguro   = mapaSeguros.getOrDefault(cmbSeguro.getValue(), 1);
            double descPct    = parseDouble(txtDescuento.getText());
            long   dias       = java.time.temporal.ChronoUnit.DAYS.between(
                    dpFechaInicio.getValue(), dpFechaDevolucion.getValue());

            try (Connection con = conexion.establecerConexion()) {

                // 1. Precio del vehículo
                PreparedStatement psVeh = con.prepareStatement(
                        "SELECT precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?");
                psVeh.setInt(1, idVehiculo);
                ResultSet rsVeh = psVeh.executeQuery();
                if (!rsVeh.next()) { JOptionPane.showMessageDialog(null, "Vehículo no encontrado."); return; }
                double subtotal  = rsVeh.getDouble("precio_x_dia") * dias;
                double total     = subtotal - subtotal * (descPct / 100.0);
                double apartado  = parseDouble(txtMontoApartado.getText());
                double pendiente = Math.max(0, total - apartado);

                // 2. Crear contrato básico
                PreparedStatement psCon = con.prepareStatement(
                        "INSERT INTO TBL_CONTRATO (fecha, condicion) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                psCon.setDate(1, Date.valueOf(dpFechaInicio.getValue()));
                psCon.setString(2, "Reservación estándar");
                psCon.executeUpdate();
                int idContrato = -1;
                ResultSet kc = psCon.getGeneratedKeys();
                if (kc.next()) idContrato = kc.getInt(1);

                // 3. Próximo ID manual (tabla sin IDENTITY)
                PreparedStatement psMaxId = con.prepareStatement(
                        "SELECT ISNULL(MAX(pk_id_reserva), 0) + 1 AS next_id FROM TBL_RESERVACION");
                ResultSet rsMaxId = psMaxId.executeQuery();
                int nextId = rsMaxId.next() ? rsMaxId.getInt("next_id") : 1;

                // 4. Insertar reservación
                PreparedStatement psRes = con.prepareStatement(
                        "INSERT INTO TBL_RESERVACION " +
                                "(pk_id_reserva, fecha_inicio, fech_devolucion, monto_total, " +
                                " monto_apartado, monto_pendiente, descuento, " +
                                " fk_pk_id_seguro, fk_pk_id_contrato, fk_pk_id_cliente) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?)");
                psRes.setInt(1, nextId);
                psRes.setDate(2, Date.valueOf(dpFechaInicio.getValue()));
                psRes.setDate(3, Date.valueOf(dpFechaDevolucion.getValue()));
                psRes.setDouble(4, total);
                psRes.setDouble(5, apartado);
                psRes.setDouble(6, pendiente);
                psRes.setDouble(7, descPct);
                psRes.setInt(8, idSeguro);
                psRes.setInt(9, idContrato);
                psRes.setInt(10, idCliente);
                psRes.executeUpdate();

                // 5. Vincular vehículo a la reserva
                PreparedStatement psRV = con.prepareStatement(
                        "INSERT INTO TBL_RESERVA_VEHI (cantidad, fk_pk_id_reserva, fk_id_vehiculo) VALUES (1,?,?)");
                psRV.setInt(1, nextId);
                psRV.setInt(2, idVehiculo);
                psRV.executeUpdate();

                // 6. Marcar vehículo como Reservado
                PreparedStatement psEst = con.prepareStatement(
                        "UPDATE TBL_VEHICULO SET estado = 'Reservado' WHERE id_vehiculo = ?");
                psEst.setInt(1, idVehiculo);
                psEst.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        "✔ Reservación #" + nextId + " registrada correctamente.\n" +
                                "Total: RD$ " + String.format("%.2f", total) +
                                "  |  Pendiente: RD$ " + String.format("%.2f", pendiente));
                limpiar();
                cargarReservaciones();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Verifica que los IDs y montos sean numéricos.");
        }
    }

    // ── Editar reservación seleccionada ───────────────────────────────────
    @FXML
    public void onEditarReserva(ActionEvent ignored) {
        if (idReservaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una reservación de la tabla primero."); return;
        }
        if (!validarFormulario()) return;
        try {
            int    idSeguro  = mapaSeguros.getOrDefault(cmbSeguro.getValue(), 1);
            double descPct   = parseDouble(txtDescuento.getText());
            long   dias      = java.time.temporal.ChronoUnit.DAYS.between(
                    dpFechaInicio.getValue(), dpFechaDevolucion.getValue());

            try (Connection con = conexion.establecerConexion()) {
                PreparedStatement psVeh = con.prepareStatement(
                        "SELECT precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?");
                psVeh.setInt(1, Integer.parseInt(txtIdVehiculo.getText().trim()));
                ResultSet rsVeh = psVeh.executeQuery();
                if (!rsVeh.next()) { JOptionPane.showMessageDialog(null, "Vehículo no encontrado."); return; }
                double subtotal  = rsVeh.getDouble("precio_x_dia") * dias;
                double total     = subtotal - subtotal * (descPct / 100.0);
                double apartado  = parseDouble(txtMontoApartado.getText());
                double pendiente = Math.max(0, total - apartado);

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE TBL_RESERVACION SET " +
                                "fecha_inicio=?, fech_devolucion=?, monto_total=?, " +
                                "monto_apartado=?, monto_pendiente=?, descuento=?, " +
                                "fk_pk_id_seguro=?, fk_pk_id_cliente=? " +
                                "WHERE pk_id_reserva=?");
                ps.setDate(1, Date.valueOf(dpFechaInicio.getValue()));
                ps.setDate(2, Date.valueOf(dpFechaDevolucion.getValue()));
                ps.setDouble(3, total);
                ps.setDouble(4, apartado);
                ps.setDouble(5, pendiente);
                ps.setDouble(6, descPct);
                ps.setInt(7, idSeguro);
                ps.setInt(8, Integer.parseInt(txtIdCliente.getText().trim()));
                ps.setInt(9, idReservaSeleccionada);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null,
                        "✔ Reservación #" + idReservaSeleccionada + " actualizada correctamente.");
                limpiar();
                cargarReservaciones();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editar: " + e.getMessage());
        }
    }

    // ── Eliminar reservación seleccionada ─────────────────────────────────
    @FXML
    public void onEliminarReserva(ActionEvent ignored) {
        if (idReservaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una reservación primero."); return;
        }
        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Eliminar la reservación #" + idReservaSeleccionada + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = conexion.establecerConexion()) {

            // Liberar vehículo vinculado
            PreparedStatement psVehId = con.prepareStatement(
                    "SELECT fk_id_vehiculo FROM TBL_RESERVA_VEHI WHERE fk_pk_id_reserva = ?");
            psVehId.setInt(1, idReservaSeleccionada);
            ResultSet rsV = psVehId.executeQuery();
            if (rsV.next()) {
                PreparedStatement psLib = con.prepareStatement(
                        "UPDATE TBL_VEHICULO SET estado = 'Disponible' WHERE id_vehiculo = ?");
                psLib.setInt(1, rsV.getInt("fk_id_vehiculo"));
                psLib.executeUpdate();
            }

            // Borrar en orden por FK
            String[] sqlsEliminar = {
                    "DELETE FROM TBL_RESERVA_VEHI WHERE fk_pk_id_reserva = ?",
                    "DELETE FROM TBL_OBJ_RESERVA  WHERE fk_pk_id_reserva = ?",
                    "DELETE FROM TBL_RESERVACION   WHERE pk_id_reserva    = ?"
            };
            for (String sqlDel : sqlsEliminar) {
                PreparedStatement ps = con.prepareStatement(sqlDel);
                ps.setInt(1, idReservaSeleccionada);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "✔ Reservación eliminada correctamente.");
            limpiar();
            cargarReservaciones();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
        }
    }

    // ── Limpiar formulario ────────────────────────────────────────────────
    @FXML
    public void limpiar() {
        txtIdCliente.clear();
        txtNombreCliente.clear();
        txtIdVehiculo.clear();
        txtInfoVehiculo.clear();
        txtDescuento.clear();
        txtMontoApartado.clear();
        txtBuscar.clear();
        cmbSeguro.setValue(null);
        dpFechaInicio.setValue(java.time.LocalDate.now());
        dpFechaDevolucion.setValue(java.time.LocalDate.now().plusDays(1));
        lblMontoTotal.setText("RD$ 0.00");
        lblMontoPendiente.setText("RD$ 0.00");
        lblDias.setText("—");
        if (lblEstado != null) { lblEstado.setText(""); lblEstado.setStyle(""); }
        idReservaSeleccionada = -1;
        tablaReservaciones.getSelectionModel().clearSelection();
        tablaReservaciones.setItems(listaReservaciones);
    }

    // ── Cargar fila seleccionada en el formulario ─────────────────────────
    private void cargarEnFormulario(Reservacion r) {
        idReservaSeleccionada = r.getIdReserva();
        txtIdCliente.setText(String.valueOf(r.getIdCliente()));
        txtNombreCliente.setText(r.getCliente());
        txtIdVehiculo.setText(r.getIdVehiculo() > 0 ? String.valueOf(r.getIdVehiculo()) : "");
        txtInfoVehiculo.setText(r.getVehiculo());
        txtDescuento.setText(r.getDescuento() > 0 ? String.format("%.2f", r.getDescuento()) : "");
        txtMontoApartado.setText(String.format("%.2f", r.getMontoApartado()));
        lblMontoTotal.setText("RD$ " + String.format("%.2f", r.getMontoTotal()));
        lblMontoPendiente.setText("RD$ " + String.format("%.2f", r.getMontoPendiente()));
        for (String key : mapaSeguros.keySet()) {
            if (mapaSeguros.get(key).equals(r.getIdSeguro())) { cmbSeguro.setValue(key); break; }
        }
        try {
            dpFechaInicio.setValue(java.time.LocalDate.parse(r.getFechaInicio().substring(0, 10)));
            dpFechaDevolucion.setValue(java.time.LocalDate.parse(r.getFechaDevolucion().substring(0, 10)));
        } catch (Exception ignored2) { /* fecha inválida — ignorar */ }
        actualizarEtiquetaEstado(r.getMontoPendiente(), r.getFechaDevolucion());
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void actualizarEtiquetaEstado(double pendiente, String fechaDev) {
        if (lblEstado == null) return;
        try {
            java.time.LocalDate dev = java.time.LocalDate.parse(fechaDev.substring(0, 10));
            boolean vencida = dev.isBefore(java.time.LocalDate.now());
            boolean saldada = pendiente <= 0.001;
            if (saldada)      { lblEstado.setText("● Completada"); lblEstado.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;"); }
            else if (vencida) { lblEstado.setText("● Vencida");    lblEstado.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;"); }
            else              { lblEstado.setText("● Activa");     lblEstado.setStyle("-fx-text-fill: #1565C0; -fx-font-weight: bold;"); }
        } catch (Exception e) {
            lblEstado.setText("");
        }
    }

    private boolean validarFormulario() {
        if (txtIdCliente.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El ID del Cliente es obligatorio."); return false;
        }
        if (txtIdVehiculo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El ID del Vehículo es obligatorio."); return false;
        }
        if (cmbSeguro.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Selecciona un plan de seguro."); return false;
        }
        if (dpFechaInicio.getValue() == null || dpFechaDevolucion.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Las fechas son obligatorias."); return false;
        }
        if (!dpFechaDevolucion.getValue().isAfter(dpFechaInicio.getValue())) {
            JOptionPane.showMessageDialog(null, "La fecha de devolución debe ser posterior a la de inicio."); return false;
        }
        try {
            Integer.parseInt(txtIdCliente.getText().trim());
            Integer.parseInt(txtIdVehiculo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los IDs deben ser números enteros."); return false;
        }
        return true;
    }

    private double parseDouble(String txt) {
        if (txt == null || txt.isBlank()) return 0;
        try { return Double.parseDouble(txt.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}