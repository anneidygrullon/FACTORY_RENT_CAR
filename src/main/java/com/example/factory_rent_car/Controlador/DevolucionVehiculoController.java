package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.ControlVehi;
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
import java.util.HashMap;
import java.util.Map;

public class DevolucionVehiculoController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtBuscar;
    @FXML private TableView<ControlVehi> tablaDevoluciones;
    @FXML private TableColumn<ControlVehi, Integer> colId;
    @FXML private TableColumn<ControlVehi, LocalDate> colFecha;
    @FXML private TableColumn<ControlVehi, String> colCliente;
    @FXML private TableColumn<ControlVehi, String> colVehiculo;
    @FXML private TableColumn<ControlVehi, Double> colCombustible;
    @FXML private TableColumn<ControlVehi, String> colEmpleado;
    @FXML private TableColumn<ControlVehi, String> colDireccion;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdReserva;
    @FXML private TextField txtCliente;
    @FXML private TextField txtVehiculo;
    @FXML private TextField txtEmpleado;
    @FXML private ComboBox<String> cmbDireccion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtNivelCombustible;
    @FXML private TextField txtTipo; // fijo en "Entrada"

    private final ObservableList<ControlVehi> listaControles = FXCollections.observableArrayList();
    private ControlVehi controlSeleccionado;
    private Map<String, Integer> mapaDirecciones = new HashMap<>();

    @FXML
    public void initialize() {
        cargarDirecciones();
        txtTipo.setText("Entrada");

        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idControlProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoInfoProperty());
        colCombustible.setCellValueFactory(c -> c.getValue().nivelCombustibleProperty().asObject());
        colEmpleado.setCellValueFactory(c -> c.getValue().empleadoNombreProperty());
        colDireccion.setCellValueFactory(c -> c.getValue().direccionCompletaProperty());

        tablaDevoluciones.setItems(listaControles);
        tablaDevoluciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarDevoluciones();
    }

    private void cargarDirecciones() {
        mapaDirecciones.clear();
        cmbDireccion.getItems().clear();
        String sql = "SELECT d.pk_id_direccion, d.calle_avenida, d.num_edificio_casa, c.nombre AS ciudad " +
                "FROM TBL_DIRECCION d " +
                "LEFT JOIN TBL_CIUDAD c ON c.pk_id_ciudad = d.fk_pk_id_ciudad " +
                "ORDER BY d.pk_id_direccion";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_direccion");
                String direccion = rs.getString("calle_avenida") +
                        (rs.getString("num_edificio_casa") != null ? " " + rs.getString("num_edificio_casa") : "") +
                        ", " + rs.getString("ciudad");
                cmbDireccion.getItems().add(direccion);
                mapaDirecciones.put(direccion, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando direcciones: " + e.getMessage());
        }
    }

    private void cargarDevoluciones() {
        listaControles.clear();
        String sql = "SELECT cv.id_control, cv.fecha, cv.nivel_combustible, cv.tipo, " +
                "cv.fk_pk_id_direccion, cv.fk_pk_id_reserva, cv.fk_pk_id_empleado, " +
                "c.nombre AS cliente_nombre, " +
                "v.marca + ' ' + v.modelo AS vehiculo_info, " +
                "e.nombre AS empleado_nombre, " +
                "d.calle_avenida + ' ' + ISNULL(d.num_edificio_casa, '') + ', ' + ci.nombre AS direccion_completa " +
                "FROM TBL_CONTROL_VEHI cv " +
                "LEFT JOIN TBL_RESERVACION r ON r.pk_id_reserva = cv.fk_pk_id_reserva " +
                "LEFT JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "LEFT JOIN TBL_RESERVA_VEHI rv ON rv.fk_pk_id_reserva = r.pk_id_reserva " +
                "LEFT JOIN TBL_VEHICULO v ON v.id_vehiculo = rv.fk_id_vehiculo " +
                "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = cv.fk_pk_id_empleado " +
                "LEFT JOIN TBL_DIRECCION d ON d.pk_id_direccion = cv.fk_pk_id_direccion " +
                "LEFT JOIN TBL_CIUDAD ci ON ci.pk_id_ciudad = d.fk_pk_id_ciudad " +
                "WHERE cv.tipo = 'Entrada' " +
                "ORDER BY cv.id_control DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ControlVehi cv = new ControlVehi(
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
                );
                listaControles.add(cv);
            }
            tablaDevoluciones.refresh();
            System.out.println("Devoluciones cargadas: " + listaControles.size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando devoluciones: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(ControlVehi cv) {
        controlSeleccionado = cv;
        txtIdReserva.setText(String.valueOf(cv.getIdReserva()));
        txtCliente.setText(cv.getClienteNombre());
        txtVehiculo.setText(cv.getVehiculoInfo());
        txtEmpleado.setText(cv.getEmpleadoNombre());
        dpFecha.setValue(cv.getFecha());
        txtNivelCombustible.setText(String.valueOf(cv.getNivelCombustible()));
        txtTipo.setText(cv.getTipo());

        String direccionActual = cv.getDireccionCompleta();
        if (direccionActual != null && !direccionActual.isEmpty()) {
            cmbDireccion.setValue(direccionActual);
        } else {
            cmbDireccion.setValue(null);
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDevoluciones();
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
        tablaDevoluciones.setItems(filtrados);
        tablaDevoluciones.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarDevoluciones();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void actualizarDevolucion(ActionEvent event) {
        if (controlSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla.");
            return;
        }
        try {
            LocalDate fecha = dpFecha.getValue();
            double nivel = Double.parseDouble(txtNivelCombustible.getText().trim());
            String direccionSeleccionada = cmbDireccion.getValue();
            Integer idDireccion = mapaDirecciones.get(direccionSeleccionada);

            String sql = "UPDATE TBL_CONTROL_VEHI SET fecha=?, nivel_combustible=?, fk_pk_id_direccion=? WHERE id_control=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(fecha));
                ps.setDouble(2, nivel);
                if (idDireccion != null) ps.setInt(3, idDireccion);
                else ps.setNull(3, Types.INTEGER);
                ps.setInt(4, controlSeleccionado.getIdControl());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Devolución actualizada.");
                cargarDevoluciones();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void marcarDevuelto(ActionEvent event) {
        if (controlSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla.");
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
                JOptionPane.showMessageDialog(null, "No se encontró el vehículo de esta reserva.");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener vehículo: " + e.getMessage());
            return;
        }

        String sqlUpdate = "UPDATE TBL_VEHICULO SET estado = 'Disponible' WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, idVehiculo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Vehículo marcado como DISPONIBLE correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el estado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        controlSeleccionado = null;
        txtIdReserva.clear();
        txtCliente.clear();
        txtVehiculo.clear();
        txtEmpleado.clear();
        cmbDireccion.setValue(null);
        dpFecha.setValue(null);
        txtNivelCombustible.clear();
        txtTipo.setText("Entrada");
        tablaDevoluciones.getSelectionModel().clearSelection();
    }
}