package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.ControlVehi;
import com.example.factory_rent_car.Database.Conexion;
import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntregaVehiculoController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<ControlVehi> tablaEntregas;
    @FXML private TableColumn<ControlVehi, Integer> colId;
    @FXML private TableColumn<ControlVehi, LocalDate> colFecha;
    @FXML private TableColumn<ControlVehi, String> colCliente;
    @FXML private TableColumn<ControlVehi, String> colVehiculo;
    @FXML private TableColumn<ControlVehi, Double> colCombustible;
    @FXML private TableColumn<ControlVehi, String> colTipo;
    @FXML private TableColumn<ControlVehi, String> colEmpleado;
    @FXML private TableColumn<ControlVehi, String> colDireccion;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdReserva;
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtIdVehiculo;
    @FXML private TextField txtIdEmpleado;
    @FXML private TextField txtCliente;
    @FXML private TextField txtVehiculo;
    @FXML private TextField txtEmpleado;
    @FXML private ComboBox<String> cmbDireccion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtNivelCombustible;
    @FXML private ComboBox<String> cmbTipo;

    private final ObservableList<ControlVehi> listaControles = FXCollections.observableArrayList();
    private ControlVehi controlSeleccionado;
    private Map<String, Integer> mapaDirecciones = new HashMap<>();

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Salida", "Entrada");

        colId.setCellValueFactory(c -> c.getValue().idControlProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoInfoProperty());
        colCombustible.setCellValueFactory(c -> c.getValue().nivelCombustibleProperty().asObject());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colEmpleado.setCellValueFactory(c -> c.getValue().empleadoNombreProperty());
        colDireccion.setCellValueFactory(c -> c.getValue().direccionCompletaProperty());

        tablaEntregas.setItems(listaControles);
        tablaEntregas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarDirecciones();
        cargarEntregas();
    }

    private void cargarDirecciones() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Map<String, Integer> tempMap = new HashMap<>();
                List<String> tempList = new ArrayList<>();
                String sql = "SELECT d.pk_id_direccion, d.calle_avenida, d.num_edificio_casa, c.nombre AS ciudad " +
                        "FROM TBL_DIRECCION d " +
                        "LEFT JOIN TBL_CIUDAD c ON c.pk_id_ciudad = d.fk_pk_id_ciudad " +
                        "ORDER BY d.pk_id_direccion";
                try (Connection con = conexion.establecerConexion();
                     PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("pk_id_direccion");
                        String dir = rs.getString("calle_avenida") +
                                (rs.getString("num_edificio_casa") != null ? " " + rs.getString("num_edificio_casa") : "") +
                                ", " + rs.getString("ciudad");
                        tempList.add(dir);
                        tempMap.put(dir, id);
                    }
                } catch (SQLException e) {
                    Platform.runLater(() -> error("Error cargando direcciones: " + e.getMessage()));
                }
                Platform.runLater(() -> {
                    mapaDirecciones.clear();
                    mapaDirecciones.putAll(tempMap);
                    cmbDireccion.getItems().setAll(tempList);
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void cargarEntregas() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<ControlVehi> tempList = new ArrayList<>();
                String sql = "SELECT cv.id_control, cv.fecha, cv.nivel_combustible, cv.tipo, " +
                        "cv.fk_pk_id_direccion, cv.fk_pk_id_reserva, cv.fk_pk_id_empleado, " +
                        "c.nombre AS cliente_nombre, " +
                        "v.marca + ' ' + v.modelo AS vehiculo_info, " +
                        "e.nombre AS empleado_nombre, " +
                        "d.calle_avenida + ' ' + ISNULL(d.num_edificio_casa, '') + ', ' + ci.nombre AS direccion_completa " +
                        "FROM TBLCONTROL_VEHI cv " +
                        "LEFT JOIN TBL_RESERVACION r ON r.pk_id_reserva = cv.fk_pk_id_reserva " +
                        "LEFT JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                        "LEFT JOIN TBL_RESERVA_VEHI rv ON rv.fk_pk_id_reserva = r.pk_id_reserva " +
                        "LEFT JOIN TBL_VEHICULO v ON v.id_vehiculo = rv.fk_id_vehiculo " +
                        "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = cv.fk_pk_id_empleado " +
                        "LEFT JOIN TBL_DIRECCION d ON d.pk_id_direccion = cv.fk_pk_id_direccion " +
                        "LEFT JOIN TBL_CIUDAD ci ON ci.pk_id_ciudad = d.fk_pk_id_ciudad " +
                        "ORDER BY cv.id_control DESC";
                try (Connection con = conexion.establecerConexion();
                     PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        tempList.add(new ControlVehi(
                                rs.getInt("id_control"),
                                rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                                rs.getDouble("nivel_combustible"),
                                rs.getString("tipo"),
                                rs.getInt("fk_pk_id_direccion"),
                                rs.getInt("fk_pk_id_reserva"),
                                rs.getInt("fk_pk_id_empleado"),
                                rs.getString("cliente_nombre"),
                                rs.getString("vehiculo_info"),
                                rs.getString("empleado_nombre"),
                                rs.getString("direccion_completa")
                        ));
                    }
                } catch (SQLException e) {
                    Platform.runLater(() -> error("Error cargando entregas: " + e.getMessage()));
                }
                List<ControlVehi> finalList = tempList;
                Platform.runLater(() -> {
                    listaControles.setAll(finalList);
                    tablaEntregas.refresh();
                    System.out.println("Entregas cargadas: " + finalList.size());
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void cargarEnFormulario(ControlVehi cv) {
        controlSeleccionado = cv;
        txtIdReserva.setText(String.valueOf(cv.getIdReserva()));
        txtIdEmpleado.setText(String.valueOf(cv.getIdEmpleado()));
        txtCliente.setText(cv.getClienteNombre());
        txtVehiculo.setText(cv.getVehiculoInfo());
        txtEmpleado.setText(cv.getEmpleadoNombre());
        dpFecha.setValue(cv.getFecha());
        txtNivelCombustible.setText(String.valueOf(cv.getNivelCombustible()));
        cmbTipo.setValue(cv.getTipo());

        // Selecciona en el ComboBox la dirección guardada
        String direccionActual = cv.getDireccionCompleta();
        if (direccionActual != null && !direccionActual.isEmpty()) {
            cmbDireccion.setValue(direccionActual);
        } else {
            cmbDireccion.setValue(null);
        }
    }

    // Búsqueda por ID
    @FXML
    private void onBuscarReserva(ActionEvent ignored) {
        if (txtIdReserva.getText().isBlank()) {
            advertencia("Ingresa el ID de la reserva."); return;
        }
        int idReserva;
        try { idReserva = Integer.parseInt(txtIdReserva.getText().trim()); }
        catch (NumberFormatException e) { advertencia("ID inválido."); return; }

        String sql = "SELECT r.pk_id_reserva, r.fecha_inicio, r.fech_devolucion, " +
                "c.pk_id_cliente, c.nombre AS cliente_nombre, " +
                "v.id_vehiculo, v.marca, v.modelo, v.estado, " +
                "e.pk_id_empleado, e.nombre AS empleado_nombre " +
                "FROM TBL_RESERVACION r " +
                "JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "LEFT JOIN TBL_RESERVA_VEHI rv ON rv.fk_pk_id_reserva = r.pk_id_reserva " +
                "LEFT JOIN TBL_VEHICULO v ON v.id_vehiculo = rv.fk_id_vehiculo " +
                "LEFT JOIN TBLCONTROL_VEHI cv ON cv.fk_pk_id_reserva = r.pk_id_reserva " +
                "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = cv.fk_pk_id_empleado " +
                "WHERE r.pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtIdCliente.setText(String.valueOf(rs.getInt("pk_id_cliente")));
                txtCliente.setText(rs.getString("cliente_nombre"));
                txtIdVehiculo.setText(String.valueOf(rs.getInt("id_vehiculo")));
                txtVehiculo.setText(rs.getString("marca") + " " + rs.getString("modelo") + " — " + rs.getString("estado"));
                txtIdEmpleado.setText(String.valueOf(rs.getInt("pk_id_empleado")));
                txtEmpleado.setText(rs.getString("empleado_nombre"));
                if (dpFecha != null) {
                    try { dpFecha.setValue(rs.getDate("fecha_inicio") != null ? rs.getDate("fecha_inicio").toLocalDate() : LocalDate.now()); }
                    catch (Exception ex) { dpFecha.setValue(LocalDate.now()); }
                }
            } else {
                advertencia("Reserva no encontrada.");
            }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        }
    }

    @FXML
    private void onBuscarCliente(ActionEvent ignored) {
        if (txtIdCliente.getText().isBlank()) {
            advertencia("Ingresa el ID del cliente."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdCliente.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtCliente.setText(rs.getString("nombre"));
            else { advertencia("Cliente no encontrado."); txtCliente.clear(); }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("ID de cliente inválido.");
        }
    }

    @FXML
    private void onBuscarVehiculo(ActionEvent ignored) {
        if (txtIdVehiculo.getText().isBlank()) {
            advertencia("Ingresa el ID del vehículo."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT marca, modelo, estado FROM TBL_VEHICULO WHERE id_vehiculo = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdVehiculo.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtVehiculo.setText(rs.getString("marca") + " " + rs.getString("modelo") + " — " + rs.getString("estado"));
            else { advertencia("Vehículo no encontrado."); txtVehiculo.clear(); }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("ID de vehículo inválido.");
        }
    }

    @FXML
    private void onBuscarEmpleado(ActionEvent ignored) {
        if (txtIdEmpleado.getText().isBlank()) {
            advertencia("Ingresa el ID del empleado."); return;
        }
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM TBL_EMPLEADO WHERE pk_id_empleado = ?")) {
            ps.setInt(1, Integer.parseInt(txtIdEmpleado.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtEmpleado.setText(rs.getString("nombre"));
            else { advertencia("Empleado no encontrado."); txtEmpleado.clear(); }
        } catch (SQLException e) {
            error("Error BD: " + e.getMessage());
        } catch (NumberFormatException e) {
            advertencia("ID de empleado inválido.");
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarEntregas();
            return;
        }
        ObservableList<ControlVehi> filtrados = FXCollections.observableArrayList();
        for (ControlVehi cv : listaControles) {
            if (cv.getClienteNombre().toLowerCase().contains(filtro) ||
                    cv.getVehiculoInfo().toLowerCase().contains(filtro) ||
                    cv.getEmpleadoNombre().toLowerCase().contains(filtro)) {
                filtrados.add(cv);
            }
        }
        tablaEntregas.setItems(filtrados);
        tablaEntregas.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarEntregas();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void actualizarEntrega(ActionEvent event) {
        if (controlSeleccionado == null) {
            advertencia("Seleccione un registro de la tabla.");
            return;
        }
        try {
            LocalDate fecha = dpFecha.getValue();
            double nivel = Double.parseDouble(txtNivelCombustible.getText().trim());
            String tipo = cmbTipo.getValue();
            String direccionSeleccionada = cmbDireccion.getValue();
            Integer idDireccion = mapaDirecciones.get(direccionSeleccionada);

            String sql = "UPDATE TBLCONTROL_VEHI SET fecha=?, nivel_combustible=?, tipo=?, fk_pk_id_direccion=? WHERE id_control=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(fecha));
                ps.setDouble(2, nivel);
                ps.setString(3, tipo);
                if (idDireccion != null) ps.setInt(4, idDireccion);
                else ps.setNull(4, Types.INTEGER);
                ps.setInt(5, controlSeleccionado.getIdControl());
                ps.executeUpdate();
                informacion("Registro actualizado.");
                cargarEntregas();
            }
        } catch (Exception e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void marcarEntregado(ActionEvent event) {
        if (controlSeleccionado == null) {
            advertencia("Seleccione un registro de la tabla.");
            return;
        }
        int idVehiculo = -1;
        String sqlVeh = "SELECT rv.fk_id_vehiculo FROM TBL_RESERVA_VEHI rv WHERE rv.fk_pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlVeh)) {
            ps.setInt(1, controlSeleccionado.getIdReserva());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) idVehiculo = rs.getInt("fk_id_vehiculo");
            else {
                advertencia("No se encontró el vehículo de esta reserva.");
                return;
            }
        } catch (SQLException e) {
            error("Error al obtener vehículo: " + e.getMessage());
            return;
        }

        String sqlUpdate = "UPDATE TBL_VEHICULO SET estado = 'En Uso' WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, idVehiculo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                informacion("Vehículo marcado como ENTREGADO correctamente.");
            } else {
                advertencia("No se pudo actualizar el estado.");
            }
        } catch (SQLException e) {
            error("Error al actualizar estado: " + e.getMessage());
        }
    }

    @FXML
    private void marcarDevuelto(ActionEvent event) {
        if (controlSeleccionado == null) {
            advertencia("Seleccione un registro de la tabla.");
            return;
        }
        int idVehiculo = -1;
        String sqlVeh = "SELECT rv.fk_id_vehiculo FROM TBL_RESERVA_VEHI rv WHERE rv.fk_pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlVeh)) {
            ps.setInt(1, controlSeleccionado.getIdReserva());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) idVehiculo = rs.getInt("fk_id_vehiculo");
            else {
                advertencia("No se encontró el vehículo de esta reserva.");
                return;
            }
        } catch (SQLException e) {
            error("Error al obtener vehículo: " + e.getMessage());
            return;
        }

        String sqlUpdate = "UPDATE TBL_VEHICULO SET estado = 'Disponible' WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, idVehiculo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                informacion("Vehículo marcado como DISPONIBLE correctamente.");
            } else {
                advertencia("No se pudo actualizar el estado.");
            }
        } catch (SQLException e) {
            error("Error al actualizar estado: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        controlSeleccionado = null;
        txtIdReserva.clear();
        txtIdCliente.clear();
        txtIdVehiculo.clear();
        txtIdEmpleado.clear();
        txtCliente.clear();
        txtVehiculo.clear();
        txtEmpleado.clear();
        cmbDireccion.setValue(null);
        dpFecha.setValue(null);
        txtNivelCombustible.clear();
        cmbTipo.setValue(null);
        tablaEntregas.getSelectionModel().clearSelection();
    }
}
