package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Limpieza;
import com.example.factory_rent_car.Database.Conexion;
import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LimpiezaController {

    Conexion conexion = Conexion.getInstance();

    // Campos de búsqueda y tabla
    @FXML private TextField txtBuscar;
    @FXML private TableView<Limpieza> tablaLimpiezas;
    @FXML private TableColumn<Limpieza, Integer> colId;
    @FXML private TableColumn<Limpieza, LocalDate> colFecha;
    @FXML private TableColumn<Limpieza, String> colVehiculo;
    @FXML private TableColumn<Limpieza, String> colTipo;
    @FXML private TableColumn<Limpieza, String> colObservaciones;
    @FXML private TableColumn<Limpieza, String> colEmpleado;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdLimpieza;
    @FXML private TextField txtIdVehiculo;
    @FXML private TextField txtVehiculoInfo;
    @FXML private TextField txtIdEmpleado;
    @FXML private TextField txtEmpleadoInfo;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextArea txtObservaciones;

    private final ObservableList<Limpieza> listaLimpiezas = FXCollections.observableArrayList();
    private Limpieza limpiezaSeleccionada;
    private Map<Integer, String> mapaVehiculos = new HashMap<>();
    private Map<Integer, String> mapaEmpleados = new HashMap<>();

    @FXML
    private void nuevo() {
        limpiar(null);
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idLimpiezaProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoInfoProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colObservaciones.setCellValueFactory(c -> c.getValue().observacionesProperty());
        colEmpleado.setCellValueFactory(c -> c.getValue().empleadoNombreProperty());

        // Tipos de limpieza disponibles
        cmbTipo.getItems().addAll("Interior", "Exterior", "Completa", "Tapicería", "Motor");

        tablaLimpiezas.setItems(listaLimpiezas);
        tablaLimpiezas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarLimpiezas();
    }

    private void cargarLimpiezas() {
        listaLimpiezas.clear();
        String sql = "SELECT l.pk_id_limpieza, l.fecha, l.tipo, l.observaciones, " +
                "l.fk_id_vehiculo, l.fk_pk_id_empleado, " +
                "v.marca + ' ' + v.modelo + ' - ' + v.num_placa AS vehiculo_info, " +
                "e.nombre AS empleado_nombre " +
                "FROM TBL_LIMPIEZA l " +
                "LEFT JOIN TBL_VEHICULO v ON v.id_vehiculo = l.fk_id_vehiculo " +
                "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = l.fk_pk_id_empleado " +
                "ORDER BY l.pk_id_limpieza DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Limpieza l = new Limpieza(
                        rs.getInt("pk_id_limpieza"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("tipo"),
                        rs.getString("observaciones"),
                        rs.getInt("fk_id_vehiculo"),
                        rs.getInt("fk_pk_id_empleado"),
                        rs.getString("vehiculo_info"),
                        rs.getString("empleado_nombre")
                );
                listaLimpiezas.add(l);
            }
            tablaLimpiezas.refresh();
        } catch (SQLException e) {
            error("Error cargando limpiezas: " + e.getMessage());
        }
    }

    @FXML
    private void buscarVehiculo(ActionEvent event) {
        String idText = txtIdVehiculo.getText().trim();
        if (idText.isEmpty()) {
            advertencia("Ingrese un ID de vehículo.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            advertencia("ID inválido.");
            return;
        }
        String sql = "SELECT marca, modelo, num_placa FROM TBL_VEHICULO WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String info = rs.getString("marca") + " " + rs.getString("modelo") + " - " + rs.getString("num_placa");
                txtVehiculoInfo.setText(info);
            } else {
                advertencia("Vehículo no encontrado.");
                txtVehiculoInfo.clear();
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
                txtEmpleadoInfo.setText(rs.getString("nombre"));
            } else {
                advertencia("Empleado no encontrado.");
                txtEmpleadoInfo.clear();
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(Limpieza l) {
        limpiezaSeleccionada = l;
        txtIdLimpieza.setText(String.valueOf(l.getIdLimpieza()));
        txtIdVehiculo.setText(String.valueOf(l.getIdVehiculo()));
        txtVehiculoInfo.setText(l.getVehiculoInfo());
        txtIdEmpleado.setText(String.valueOf(l.getIdEmpleado()));
        txtEmpleadoInfo.setText(l.getEmpleadoNombre());
        dpFecha.setValue(l.getFecha());
        cmbTipo.setValue(l.getTipo());
        txtObservaciones.setText(l.getObservaciones());
        lblTitulo.setText("Editando Limpieza #" + l.getIdLimpieza());
    }

    @FXML
    private void guardarLimpieza(ActionEvent event) {
        if (!validarCampos()) return;

        int idVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
        int idEmpleado = Integer.parseInt(txtIdEmpleado.getText().trim());
        LocalDate fecha = dpFecha.getValue();
        String tipo = cmbTipo.getValue();
        String observaciones = txtObservaciones.getText().trim();

        if (limpiezaSeleccionada == null) {

            String sql = "INSERT INTO TBL_LIMPIEZA (pk_id_limpieza, fecha, tipo, observaciones, fk_id_vehiculo, fk_pk_id_empleado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                int idLimpieza;
                try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_limpieza), 0) + 1 AS next_id FROM TBL_LIMPIEZA");
                     ResultSet rsNext = psNext.executeQuery()) {
                    idLimpieza = rsNext.next() ? rsNext.getInt("next_id") : 1;
                }
                ps.setInt(1, idLimpieza);
                ps.setDate(2, Date.valueOf(fecha));
                ps.setString(3, tipo);
                ps.setString(4, observaciones);
                ps.setInt(5, idVehiculo);
                ps.setInt(6, idEmpleado);
                ps.executeUpdate();
                informacion("Limpieza registrada con ID: " + idLimpieza);
                limpiar(event);
                cargarLimpiezas();
            } catch (SQLException e) {
                error("Error al guardar: " + e.getMessage());
            }
        } else {

            String sql = "UPDATE TBL_LIMPIEZA SET fecha=?, tipo=?, observaciones=?, fk_id_vehiculo=?, fk_pk_id_empleado=? " +
                    "WHERE pk_id_limpieza=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(fecha));
                ps.setString(2, tipo);
                ps.setString(3, observaciones);
                ps.setInt(4, idVehiculo);
                ps.setInt(5, idEmpleado);
                ps.setInt(6, limpiezaSeleccionada.getIdLimpieza());
                ps.executeUpdate();
                informacion("Limpieza actualizada.");
                limpiar(event);
                cargarLimpiezas();
            } catch (SQLException e) {
                error("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarLimpieza(ActionEvent event) {
        if (limpiezaSeleccionada == null) {
            advertencia("Seleccione un registro de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar la limpieza #" + limpiezaSeleccionada.getIdLimpieza() + "?")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_LIMPIEZA WHERE pk_id_limpieza = ?")) {
            ps.setInt(1, limpiezaSeleccionada.getIdLimpieza());
            ps.executeUpdate();
            informacion("Limpieza eliminada.");
            limpiar(event);
            cargarLimpiezas();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarLimpiezas();
            return;
        }
        ObservableList<Limpieza> filtrados = FXCollections.observableArrayList();
        for (Limpieza l : listaLimpiezas) {
            if (l.getVehiculoInfo().toLowerCase().contains(filtro) ||
                    l.getEmpleadoNombre().toLowerCase().contains(filtro) ||
                    l.getTipo().toLowerCase().contains(filtro)) {
                filtrados.add(l);
            }
        }
        tablaLimpiezas.setItems(filtrados);
        tablaLimpiezas.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarLimpiezas();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void limpiar(ActionEvent event) {
        limpiezaSeleccionada = null;
        txtIdLimpieza.clear();
        txtIdVehiculo.clear();
        txtVehiculoInfo.clear();
        txtIdEmpleado.clear();
        txtEmpleadoInfo.clear();
        dpFecha.setValue(null);
        cmbTipo.setValue(null);
        txtObservaciones.clear();
        lblTitulo.setText("Registro de Limpieza");
        tablaLimpiezas.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtIdVehiculo.getText().isBlank()) {
            advertencia("El ID del vehículo es obligatorio.");
            return false;
        }
        if (txtIdEmpleado.getText().isBlank()) {
            advertencia("El ID del empleado es obligatorio.");
            return false;
        }
        if (dpFecha.getValue() == null) {
            advertencia("La fecha es obligatoria.");
            return false;
        }
        if (cmbTipo.getValue() == null) {
            advertencia("Seleccione un tipo de limpieza.");
            return false;
        }
        try {
            Integer.parseInt(txtIdVehiculo.getText().trim());
            Integer.parseInt(txtIdEmpleado.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Los IDs deben ser números enteros.");
            return false;
        }
        return true;
    }
}
