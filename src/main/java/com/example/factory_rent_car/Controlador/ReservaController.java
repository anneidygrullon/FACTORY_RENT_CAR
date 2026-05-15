package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Reservacion;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import static com.example.factory_rent_car.Util.EmailService.*;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

public class ReservaController {

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    Conexion conexion = Conexion.getInstance();

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

    // ── Tabla y Contenedor ────────────────────────────────────────────────
    @FXML private TableView<Reservacion>           tablaReservaciones;
    @FXML private TableColumn<Reservacion, Number> colId;
    @FXML private TableColumn<Reservacion, String> colCliente;
    @FXML private TableColumn<Reservacion, String> colVehiculo;
    @FXML private TableColumn<Reservacion, String> colFechaInicio;
    @FXML private TableColumn<Reservacion, String> colFechaDevolucion;
    @FXML private TableColumn<Reservacion, Number> colMontoTotal;
    @FXML private TableColumn<Reservacion, Number> colPendiente;
    @FXML private TableColumn<Reservacion, String> colEstado;
    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;

    // ── Datos internos ────────────────────────────────────────────────────
    private final ObservableList<Reservacion>    listaReservaciones    = FXCollections.observableArrayList();
    private int                                  idReservaSeleccionada = -1;
    private int                                  idVehiculoOriginal    = -1; // para edición
    private final LinkedHashMap<String, Integer> mapaSegurosId         = new LinkedHashMap<>();
    private final LinkedHashMap<String, Double>  mapaSegurosCosto      = new LinkedHashMap<>();
    private String correoCliente;

    // ── Inicializar ───────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaDevolucion.setValue(LocalDate.now().plusDays(1));

        colId.setCellValueFactory(c -> c.getValue().idReservaProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoProperty());
        colFechaInicio.setCellValueFactory(c -> c.getValue().fechaInicioProperty());
        colFechaDevolucion.setCellValueFactory(c -> c.getValue().fechaDevolucionProperty());
        colMontoTotal.setCellValueFactory(c -> c.getValue().montoTotalProperty());
        colPendiente.setCellValueFactory(c -> c.getValue().montoPendienteProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());

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
        tablaReservaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if (newSel != null) cargarEnFormulario(newSel); });

        // Eventos de recálculo
        dpFechaInicio.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        dpFechaDevolucion.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        cmbSeguro.valueProperty().addListener((o, ov, nv) -> recalcularMonto());
        txtDescuento.textProperty().addListener((o, ov, nv) -> recalcularMonto());
        txtMontoApartado.textProperty().addListener((o, ov, nv) -> recalcularMonto());

        cargarSeguros();
        cargarReservaciones();

        // Por defecto tabla visible
        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");
    }

    // ── Cargar seguros con costo diario ───────────────────────────────────
    private void cargarSeguros() {
        mapaSegurosId.clear();
        mapaSegurosCosto.clear();
        cmbSeguro.getItems().clear();
        String sql = "SELECT pk_id_seguro, nombre, costo FROM TBL_PLAN_SEGURO ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_seguro");
                double costo = rs.getDouble("costo");
                String label = rs.getString("nombre") +
                        " (RD$ " + String.format("%.2f", costo) + "/día)";
                mapaSegurosId.put(label, id);
                mapaSegurosCosto.put(label, costo);
                cmbSeguro.getItems().add(label);
            }
            if (cmbSeguro.getItems().isEmpty()) {
                advertencia("No hay planes de seguro registrados en TBL_PLAN_SEGURO.");
            }
        } catch (SQLException e) {
            error("Error al cargar seguros:\n" + e.getMessage());
        }
    }

    // ── Buscar cliente ────────────────────────────────────────────────────
    @FXML
    public void onBuscarCliente(ActionEvent ignored) {
        if (txtIdCliente.getText().isBlank()) {
            advertencia("Ingresa el ID del cliente."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT nombre, correo_electronico FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdCliente.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombreCliente.setText(rs.getString("nombre"));
                correoCliente = rs.getString("correo_electronico");
            } else {
                informacion("Cliente no encontrado.");
                txtNombreCliente.clear();
                correoCliente = null;
            }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("ID de cliente inválido.");
        }
    }

    // ── Buscar vehículo con validación de disponibilidad ──────────────────
    @FXML
    public void onBuscarVehiculo(ActionEvent ignored) {
        if (txtIdVehiculo.getText().isBlank()) {
            advertencia("Ingresa el ID del vehículo."); return;
        }
        int idVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT marca, modelo, estado, precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?")) {
            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("estado");
                // Permitir si es el mismo vehículo en edición
                if (!"Disponible".equals(estado) && (idReservaSeleccionada == -1 || idVehiculo != idVehiculoOriginal)) {
                    advertencia("Vehículo no disponible (estado: " + estado + ")");
                    txtInfoVehiculo.clear();
                    return;
                }
                txtInfoVehiculo.setText(
                        rs.getString("marca") + " " + rs.getString("modelo") +
                                " — " + estado +
                                " | RD$ " + String.format("%.2f", rs.getDouble("precio_x_dia")) + "/día");
                recalcularMonto();
            } else {
                advertencia("Vehículo no encontrado.");
                txtInfoVehiculo.clear();
            }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("ID de vehículo inválido.");
        }
    }

    // ── Recálculo total incluyendo seguro diario ──────────────────────────
    private void recalcularMonto() {
        if (dpFechaInicio.getValue() == null || dpFechaDevolucion.getValue() == null) return;
        if (txtIdVehiculo.getText().isBlank() || cmbSeguro.getValue() == null) return;

        long dias = ChronoUnit.DAYS.between(dpFechaInicio.getValue(), dpFechaDevolucion.getValue());
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

            double precioVehiculo = rs.getDouble("precio_x_dia");
            double costoSeguro = mapaSegurosCosto.getOrDefault(cmbSeguro.getValue(), 0.0);
            double subtotal = (precioVehiculo + costoSeguro) * dias;
            double descuentoPorc = parseDouble(txtDescuento.getText());
            double total = subtotal - subtotal * (descuentoPorc / 100.0);
            double apartado = parseDouble(txtMontoApartado.getText());
            double pendiente = Math.max(0, total - apartado);

            lblMontoTotal.setText("RD$ " + String.format("%.2f", total));
            lblMontoPendiente.setText("RD$ " + String.format("%.2f", pendiente));

            actualizarEtiquetaEstado(pendiente, dpFechaDevolucion.getValue().toString());

        } catch (SQLException | NumberFormatException ignored) {
            // silencioso
        }
    }

    // ── Cargar todas las reservas ─────────────────────────────────────────
    @FXML
    public void cargarReservaciones() {
        listaReservaciones.clear();
        idReservaSeleccionada = -1;
        String sql =
                "SELECT r.pk_id_reserva, " +
                        "CONVERT(VARCHAR(10), r.fecha_inicio, 120) AS fecha_inicio, " +
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
                Reservacion res = new Reservacion(
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
                );
                listaReservaciones.add(res);
            }
        } catch (SQLException e) {
            error("Error al cargar reservaciones: " + e.getMessage());
        }
        actualizarConteo();
    }

    // ── Filtrar / Buscar ──────────────────────────────────────────────────
    @FXML
    public void fnBuscar(ActionEvent ignored) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            tablaReservaciones.setItems(listaReservaciones);
            actualizarConteo();
            return;
        }
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

    // ── Guardar nueva reserva ─────────────────────────────────────────────
    @FXML
    public void onGuardarReserva(ActionEvent ignored) {
        if (!validarFormulario()) return;
        try {
            int idCliente  = Integer.parseInt(txtIdCliente.getText().trim());
            int idVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
            String seguroLabel = cmbSeguro.getValue();
            int idSeguro   = mapaSegurosId.getOrDefault(seguroLabel, 1);
            double costoSeguro = mapaSegurosCosto.getOrDefault(seguroLabel, 0.0);
            double descPct = parseDouble(txtDescuento.getText());
            long   dias    = ChronoUnit.DAYS.between(dpFechaInicio.getValue(), dpFechaDevolucion.getValue());

            try (Connection con = conexion.establecerConexion()) {
                // 1. Precio del vehículo
                PreparedStatement psVeh = con.prepareStatement(
                        "SELECT precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?");
                psVeh.setInt(1, idVehiculo);
                ResultSet rsVeh = psVeh.executeQuery();
                if (!rsVeh.next()) { advertencia("Vehículo no encontrado."); return; }
                double precioVehiculo = rsVeh.getDouble("precio_x_dia");
                double subtotal = (precioVehiculo + costoSeguro) * dias;
                double total    = subtotal - subtotal * (descPct / 100.0);
                double apartado = parseDouble(txtMontoApartado.getText());
                double pendiente = Math.max(0, total - apartado);

                // 2. Siguiente ID contrato (no es IDENTITY)
                PreparedStatement psMaxCon = con.prepareStatement(
                        "SELECT ISNULL(MAX(pk_id_contrato), 0) + 1 AS next_id FROM TBL_CONTRATO");
                ResultSet rsMaxCon = psMaxCon.executeQuery();
                int idContrato = rsMaxCon.next() ? rsMaxCon.getInt("next_id") : 1;

                // 3. Crear contrato
                PreparedStatement psCon = con.prepareStatement(
                        "INSERT INTO TBL_CONTRATO (pk_id_contrato, fecha, condicion, descripcion) VALUES (?, ?, ?, ?)");
                psCon.setInt(1, idContrato);
                psCon.setDate(2, Date.valueOf(dpFechaInicio.getValue()));
                psCon.setString(3, "Reservación estándar");
                psCon.setString(4, "Contrato generado desde reservación");
                psCon.executeUpdate();

                // 4. Siguiente ID manual para reservación
                PreparedStatement psMaxId = con.prepareStatement(
                        "SELECT ISNULL(MAX(pk_id_reserva), 0) + 1 AS next_id FROM TBL_RESERVACION");
                ResultSet rsMaxId = psMaxId.executeQuery();
                int nextId = rsMaxId.next() ? rsMaxId.getInt("next_id") : 1;

                // 5. Insertar reservación
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

                // 6. Vincular vehículo
                PreparedStatement psRV = con.prepareStatement(
                        "INSERT INTO TBL_RESERVA_VEHI (cantidad, fk_pk_id_reserva, fk_id_vehiculo) VALUES (1,?,?)");
                psRV.setInt(1, nextId);
                psRV.setInt(2, idVehiculo);
                psRV.executeUpdate();

                // 7. Marcar vehículo como Reservado
                PreparedStatement psEst = con.prepareStatement(
                        "UPDATE TBL_VEHICULO SET estado = 'Reservado' WHERE id_vehiculo = ?");
                psEst.setInt(1, idVehiculo);
                psEst.executeUpdate();

                // 8. Obtener email si no se buscó antes
                if (correoCliente == null || correoCliente.isBlank()) {
                    try (PreparedStatement psEmail = con.prepareStatement(
                            "SELECT correo_electronico FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
                        psEmail.setInt(1, idCliente);
                        ResultSet rsEmail = psEmail.executeQuery();
                        if (rsEmail.next()) correoCliente = rsEmail.getString("correo_electronico");
                    }
                }

                informacion(
                        "✔ Reservación #" + nextId + " registrada correctamente.\n" +
                                "Total: RD$ " + String.format("%.2f", total) +
                                "  |  Pendiente: RD$ " + String.format("%.2f", pendiente));

                if (correoCliente != null && !correoCliente.isBlank()) {
                    String asunto = "Confirmación de Reservación #" + nextId;
                    String cuerpo = """
                            Hola %s,

                            Tu reservación #%d ha sido confirmada.

                            Vehículo: %s
                            Fecha inicio: %s
                            Fecha devolución: %s
                            Seguro: %s
                            Total: RD$ %.2f
                            Pendiente: RD$ %.2f

                            Gracias por preferirnos.
                            Factory Rent Car
                            """.formatted(
                            txtNombreCliente.getText(), nextId,
                            txtInfoVehiculo.getText(),
                            dpFechaInicio.getValue(), dpFechaDevolucion.getValue(),
                            seguroLabel, total, pendiente);
                    enviarAsync(correoCliente, asunto, cuerpo, null, null);
                }

                limpiar();
                cargarReservaciones();
            }
        } catch (SQLException e) {
            error("Error al guardar: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("Verifica que los IDs y montos sean numéricos.");
        }
    }

    // ── Editar reserva (incluyendo cambio de vehículo) ─────────────────---
    @FXML
    public void onEditarReserva(ActionEvent ignored) {
        if (idReservaSeleccionada == -1) {
            advertencia("Selecciona una reservación de la tabla primero."); return;
        }
        if (!validarFormulario()) return;
        try {
            int nuevoIdVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            String seguroLabel = cmbSeguro.getValue();
            int idSeguro = mapaSegurosId.getOrDefault(seguroLabel, 1);
            double costoSeguro = mapaSegurosCosto.getOrDefault(seguroLabel, 0.0);
            double descPct = parseDouble(txtDescuento.getText());
            long dias = ChronoUnit.DAYS.between(dpFechaInicio.getValue(), dpFechaDevolucion.getValue());

            try (Connection con = conexion.establecerConexion()) {
                // Precio vehículo
                PreparedStatement psVeh = con.prepareStatement(
                        "SELECT precio_x_dia FROM TBL_VEHICULO WHERE id_vehiculo = ?");
                psVeh.setInt(1, nuevoIdVehiculo);
                ResultSet rsVeh = psVeh.executeQuery();
                if (!rsVeh.next()) { advertencia("Vehículo no encontrado."); return; }
                double precioVehiculo = rsVeh.getDouble("precio_x_dia");
                double subtotal = (precioVehiculo + costoSeguro) * dias;
                double total = subtotal - subtotal * (descPct / 100.0);
                double apartado = parseDouble(txtMontoApartado.getText());
                double pendiente = Math.max(0, total - apartado);

                // Actualizar reservación
                PreparedStatement psUpd = con.prepareStatement(
                        "UPDATE TBL_RESERVACION SET fecha_inicio=?, fech_devolucion=?, monto_total=?, " +
                                "monto_apartado=?, monto_pendiente=?, descuento=?, " +
                                "fk_pk_id_seguro=?, fk_pk_id_cliente=? WHERE pk_id_reserva=?");
                psUpd.setDate(1, Date.valueOf(dpFechaInicio.getValue()));
                psUpd.setDate(2, Date.valueOf(dpFechaDevolucion.getValue()));
                psUpd.setDouble(3, total);
                psUpd.setDouble(4, apartado);
                psUpd.setDouble(5, pendiente);
                psUpd.setDouble(6, descPct);
                psUpd.setInt(7, idSeguro);
                psUpd.setInt(8, idCliente);
                psUpd.setInt(9, idReservaSeleccionada);
                psUpd.executeUpdate();

                // Manejar cambio de vehículo
                if (nuevoIdVehiculo != idVehiculoOriginal) {
                    // Liberar vehículo anterior
                    PreparedStatement psOldVeh = con.prepareStatement(
                            "SELECT fk_id_vehiculo FROM TBL_RESERVA_VEHI WHERE fk_pk_id_reserva = ?");
                    psOldVeh.setInt(1, idReservaSeleccionada);
                    ResultSet rsOld = psOldVeh.executeQuery();
                    if (rsOld.next()) {
                        int oldVeh = rsOld.getInt("fk_id_vehiculo");
                        PreparedStatement psLib = con.prepareStatement(
                                "UPDATE TBL_VEHICULO SET estado = 'Disponible' WHERE id_vehiculo = ?");
                        psLib.setInt(1, oldVeh);
                        psLib.executeUpdate();
                    }
                    // Actualizar TBL_RESERVA_VEHI
                    PreparedStatement psUpdVeh = con.prepareStatement(
                            "UPDATE TBL_RESERVA_VEHI SET fk_id_vehiculo = ? WHERE fk_pk_id_reserva = ?");
                    psUpdVeh.setInt(1, nuevoIdVehiculo);
                    psUpdVeh.setInt(2, idReservaSeleccionada);
                    psUpdVeh.executeUpdate();

                    // Reservar nuevo vehículo
                    PreparedStatement psReserv = con.prepareStatement(
                            "UPDATE TBL_VEHICULO SET estado = 'Reservado' WHERE id_vehiculo = ?");
                    psReserv.setInt(1, nuevoIdVehiculo);
                    psReserv.executeUpdate();
                }

                informacion(
                        "✔ Reservación #" + idReservaSeleccionada + " actualizada correctamente.");
                limpiar();
                cargarReservaciones();
            }
        } catch (SQLException e) {
            error("Error al editar: " + e.getMessage());
        }
    }

    // ── Eliminar reserva ──────────────────────────────────────────────────
    @FXML
    public void onEliminarReserva(ActionEvent ignored) {
        if (idReservaSeleccionada == -1) {
            advertencia("Selecciona una reservación primero."); return;
        }
        if (!confirmar("¿Eliminar la reservación #" + idReservaSeleccionada + "?\nEsta acción no se puede deshacer.")) return;

        try (Connection con = conexion.establecerConexion()) {
            // Liberar vehículo
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

            // Borrar en orden FK
            String[] sqlsEliminar = {
                    "DELETE FROM TBL_RESERVA_VEHI WHERE fk_pk_id_reserva = ?",
                    "DELETE FROM TB_OBJ_RESERVA  WHERE fk_pk_id_reserva = ?",
                    "DELETE FROM TBL_RESERVACION   WHERE pk_id_reserva    = ?"
            };
            for (String sqlDel : sqlsEliminar) {
                PreparedStatement ps = con.prepareStatement(sqlDel);
                ps.setInt(1, idReservaSeleccionada);
                ps.executeUpdate();
            }

            informacion("✔ Reservación eliminada correctamente.");
            limpiar();
            cargarReservaciones();

        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    // ── Toggle Mostrar/Ocultar Tabla ──────────────────────────────────────
    @FXML
    public void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
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
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaDevolucion.setValue(LocalDate.now().plusDays(1));
        lblMontoTotal.setText("RD$ 0.00");
        lblMontoPendiente.setText("RD$ 0.00");
        lblDias.setText("—");
        lblEstado.setText("");
        lblTituloFormulario.setText("Nueva Reservación");
        idReservaSeleccionada = -1;
        idVehiculoOriginal = -1;
        correoCliente = null;
        tablaReservaciones.getSelectionModel().clearSelection();
        tablaReservaciones.setItems(listaReservaciones);
    }

    // ── Cargar fila seleccionada al formulario (edición) ──────────────────
    private void cargarEnFormulario(Reservacion r) {
        idReservaSeleccionada = r.getIdReserva();
        idVehiculoOriginal = r.getIdVehiculo();
        lblTituloFormulario.setText("✏ Editando Reservación #" + idReservaSeleccionada);

        txtIdCliente.setText(String.valueOf(r.getIdCliente()));
        txtNombreCliente.setText(r.getCliente());
        txtIdVehiculo.setText(r.getIdVehiculo() > 0 ? String.valueOf(r.getIdVehiculo()) : "");
        txtInfoVehiculo.setText(r.getVehiculo());
        txtDescuento.setText(r.getDescuento() > 0 ? String.format("%.2f", r.getDescuento()) : "");
        txtMontoApartado.setText(String.format("%.2f", r.getMontoApartado()));
        lblMontoTotal.setText("RD$ " + String.format("%.2f", r.getMontoTotal()));
        lblMontoPendiente.setText("RD$ " + String.format("%.2f", r.getMontoPendiente()));

        for (String key : mapaSegurosId.keySet()) {
            if (mapaSegurosId.get(key).equals(r.getIdSeguro())) {
                cmbSeguro.setValue(key);
                break;
            }
        }

        try {
            dpFechaInicio.setValue(LocalDate.parse(r.getFechaInicio().substring(0, 10)));
            dpFechaDevolucion.setValue(LocalDate.parse(r.getFechaDevolucion().substring(0, 10)));
        } catch (Exception ignored) { }

        actualizarEtiquetaEstado(r.getMontoPendiente(), r.getFechaDevolucion());
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void actualizarEtiquetaEstado(double pendiente, String fechaDev) {
        if (lblEstado == null) return;
        try {
            LocalDate dev = LocalDate.parse(fechaDev.substring(0, 10));
            boolean vencida = dev.isBefore(LocalDate.now());
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
            advertencia("El ID del Cliente es obligatorio."); return false;
        }
        if (txtIdVehiculo.getText().isBlank()) {
            advertencia("El ID del Vehículo es obligatorio."); return false;
        }
        if (cmbSeguro.getValue() == null) {
            advertencia("Selecciona un plan de seguro."); return false;
        }
        if (dpFechaInicio.getValue() == null || dpFechaDevolucion.getValue() == null) {
            advertencia("Las fechas son obligatorias."); return false;
        }
        if (!dpFechaDevolucion.getValue().isAfter(dpFechaInicio.getValue())) {
            advertencia("La fecha de devolución debe ser posterior a la de inicio."); return false;
        }
        try {
            Integer.parseInt(txtIdCliente.getText().trim());
            Integer.parseInt(txtIdVehiculo.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Los IDs deben ser números enteros."); return false;
        }
        return true;
    }

    private double parseDouble(String txt) {
        if (txt == null || txt.isBlank()) return 0;
        try { return Double.parseDouble(txt.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    // ── NAVEGACIÓN RÁPIDA ──────────────────────────────────────────────
    @FXML
    private void irAReservaVehiculo(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml");
    }
    @FXML
    private void irAReservaObjeto(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Objetos", mainController.getMenuReservacion(), "ReservacionObjeto.fxml");
    }
    @FXML
    private void irAIncidencias(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml");
    }
    @FXML
    private void irAClientes(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Clientes", mainController.getMenuClientes(), "Clientes.fxml");
    }

    // ── EFECTOS HOVER ──────────────────────────────────────────────────
    @FXML
    private void onCardEnter(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        card.setStyle(card.getStyle() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        card.setCursor(javafx.scene.Cursor.HAND);
    }
    @FXML
    private void onCardExit(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;", ""));
        card.setCursor(javafx.scene.Cursor.DEFAULT);
    }
}
