package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Mantenimiento;
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

public class MantenimientoController {

    Conexion conexion = new Conexion();

    // Componentes de búsqueda y tabla
    @FXML private TextField txtBuscar;
    @FXML private TableView<Mantenimiento> tablaMantenimientos;
    @FXML private TableColumn<Mantenimiento, Integer> colId;
    @FXML private TableColumn<Mantenimiento, String> colVehiculo;
    @FXML private TableColumn<Mantenimiento, String> colTipo;
    @FXML private TableColumn<Mantenimiento, LocalDate> colFechaIngreso;
    @FXML private TableColumn<Mantenimiento, LocalDate> colFechaSalida;
    @FXML private TableColumn<Mantenimiento, Integer> colDias;
    @FXML private TableColumn<Mantenimiento, Double> colCosto;
    @FXML private TableColumn<Mantenimiento, String> colDescripcion;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;

    // Componentes del formulario
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdMantenimiento;
    @FXML private TextField txtIdVehiculo;
    @FXML private TextField txtVehiculoInfo;
    @FXML private DatePicker dpFechaIngreso;
    @FXML private DatePicker dpFechaSalida;
    @FXML private TextField txtDias;
    @FXML private TextField txtCosto;
    @FXML private TextArea txtDescripcion;

    // RadioButtons
    @FXML private RadioButton rbMotivo;
    @FXML private RadioButton rbIncidencia;
    @FXML private RadioButton rbRevision;
    @FXML private RadioButton rbGarantia;
    @FXML private RadioButton rbDiagnostico;
    @FXML private ToggleGroup tipoGroup;

    private final ObservableList<Mantenimiento> listaMantenimientos = FXCollections.observableArrayList();
    private Mantenimiento mantenimientoSeleccionado;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idMantenimientoProperty().asObject());
        colVehiculo.setCellValueFactory(c -> c.getValue().vehiculoInfoProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colFechaIngreso.setCellValueFactory(c -> c.getValue().fechaIngresoProperty());
        colFechaSalida.setCellValueFactory(c -> c.getValue().fechaSalidaProperty());
        colDias.setCellValueFactory(c -> c.getValue().diasDuracionProperty().asObject());
        colCosto.setCellValueFactory(c -> c.getValue().costoProperty().asObject());
        colDescripcion.setCellValueFactory(c -> c.getValue().descripcionProperty());

        tablaMantenimientos.setItems(listaMantenimientos);
        tablaMantenimientos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        // Listener para calcular días automáticamente
        dpFechaIngreso.valueProperty().addListener((obs, old, newVal) -> calcularDias());
        dpFechaSalida.valueProperty().addListener((obs, old, newVal) -> calcularDias());

        cargarMantenimientos();
    }

    private void calcularDias() {
        if (dpFechaIngreso.getValue() != null && dpFechaSalida.getValue() != null) {
            long dias = ChronoUnit.DAYS.between(dpFechaIngreso.getValue(), dpFechaSalida.getValue());
            txtDias.setText(String.valueOf(Math.max(0, dias)));
        } else {
            txtDias.clear();
        }
    }

    private void cargarMantenimientos() {
        listaMantenimientos.clear();
        String sql = "SELECT m.pk_id_mantenimiento, m.costo, m.fecha_salida, m.fecha_ingreso, " +
                "m.tipo, m.descripcion, m.fk_id_vehiculo, m.fk_pk_id_hist_mantenimiento, " +
                "v.marca + ' ' + v.modelo + ' - ' + v.num_placa AS vehiculo_info " +
                "FROM TBL_MANTENIMIENTO m " +
                "LEFT JOIN TBL_VEHICULO v ON v.id_vehiculo = m.fk_id_vehiculo " +
                "ORDER BY m.pk_id_mantenimiento DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Mantenimiento m = new Mantenimiento(
                        rs.getInt("pk_id_mantenimiento"),
                        rs.getDouble("costo"),
                        rs.getDate("fecha_salida") != null ? rs.getDate("fecha_salida").toLocalDate() : null,
                        rs.getDate("fecha_ingreso") != null ? rs.getDate("fecha_ingreso").toLocalDate() : null,
                        rs.getString("tipo"),
                        rs.getString("descripcion"),
                        rs.getInt("fk_id_vehiculo"),
                        rs.getInt("fk_pk_id_hist_mantenimiento"),
                        rs.getString("vehiculo_info")
                );
                listaMantenimientos.add(m);
            }
            tablaMantenimientos.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando mantenimientos: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(Mantenimiento m) {
        mantenimientoSeleccionado = m;
        txtIdMantenimiento.setText(String.valueOf(m.getIdMantenimiento()));
        txtIdVehiculo.setText(String.valueOf(m.getIdVehiculo()));
        txtVehiculoInfo.setText(m.getVehiculoInfo());
        dpFechaIngreso.setValue(m.getFechaIngreso());
        dpFechaSalida.setValue(m.getFechaSalida());
        txtCosto.setText(String.valueOf(m.getCosto()));
        txtDescripcion.setText(m.getDescripcion());

        // Seleccionar RadioButton según el tipo
        String tipo = m.getTipo();
        if (tipo != null) {
            switch (tipo) {
                case "Motivo de mantenimiento": rbMotivo.setSelected(true); break;
                case "Incidencia": rbIncidencia.setSelected(true); break;
                case "Revisión": rbRevision.setSelected(true); break;
                case "Garantía": rbGarantia.setSelected(true); break;
                case "Diagnóstico preventivo": rbDiagnostico.setSelected(true); break;
                default: break;
            }
        }
        lblTitulo.setText("Editando Mantenimiento #" + m.getIdMantenimiento());
    }

    @FXML
    private void buscarVehiculo(ActionEvent event) {
        String idText = txtIdVehiculo.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de vehículo.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
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
                JOptionPane.showMessageDialog(null, "Vehículo no encontrado.");
                txtVehiculoInfo.clear();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private String getTipoSeleccionado() {
        if (rbMotivo.isSelected()) return "Motivo de mantenimiento";
        if (rbIncidencia.isSelected()) return "Incidencia";
        if (rbRevision.isSelected()) return "Revisión";
        if (rbGarantia.isSelected()) return "Garantía";
        if (rbDiagnostico.isSelected()) return "Diagnóstico preventivo";
        return null;
    }

    @FXML
    private void guardarMantenimiento(ActionEvent event) {
        if (!validarFormulario()) return;

        int idVehiculo = Integer.parseInt(txtIdVehiculo.getText().trim());
        LocalDate fechaIngreso = dpFechaIngreso.getValue();
        LocalDate fechaSalida = dpFechaSalida.getValue();
        double costo = Double.parseDouble(txtCosto.getText().trim());
        String descripcion = txtDescripcion.getText().trim();
        String tipo = getTipoSeleccionado();

        // Primero, crear historial de mantenimiento (si no existe o usar existente)
        int idHistMantenimiento = -1;
        String sqlHist = "INSERT INTO TBL_HISTORIAL_MANTENIMIENTO (fecha, descripcion) VALUES (?, ?)";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlHist, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(fechaIngreso));
            ps.setString(2, descripcion);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) idHistMantenimiento = rs.getInt(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear historial: " + e.getMessage());
            return;
        }

        if (mantenimientoSeleccionado == null) {
            // INSERT
            String sql = "INSERT INTO TBL_MANTENIMIENTO (costo, fecha_salida, fecha_ingreso, tipo, descripcion, fk_id_vehiculo, fk_pk_id_hist_mantenimiento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, costo);
                ps.setDate(2, Date.valueOf(fechaSalida));
                ps.setDate(3, Date.valueOf(fechaIngreso));
                ps.setString(4, tipo);
                ps.setString(5, descripcion);
                ps.setInt(6, idVehiculo);
                ps.setInt(7, idHistMantenimiento);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Mantenimiento registrado con ID: " + rs.getInt(1));
                }
                limpiar(event);
                cargarMantenimientos();
                // Actualizar estado del vehículo a "En Mantenimiento"
                actualizarEstadoVehiculo(idVehiculo, "En Mantenimiento");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage());
            }
        } else {
            // UPDATE
            String sql = "UPDATE TBL_MANTENIMIENTO SET costo=?, fecha_salida=?, fecha_ingreso=?, tipo=?, descripcion=?, fk_id_vehiculo=? WHERE pk_id_mantenimiento=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, costo);
                ps.setDate(2, Date.valueOf(fechaSalida));
                ps.setDate(3, Date.valueOf(fechaIngreso));
                ps.setString(4, tipo);
                ps.setString(5, descripcion);
                ps.setInt(6, idVehiculo);
                ps.setInt(7, mantenimientoSeleccionado.getIdMantenimiento());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Mantenimiento actualizado.");
                limpiar(event);
                cargarMantenimientos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            }
        }
    }

    private void actualizarEstadoVehiculo(int idVehiculo, String estado) {
        String sql = "UPDATE TBL_VEHICULO SET estado = ? WHERE id_vehiculo = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idVehiculo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar estado del vehículo: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarMantenimiento(ActionEvent event) {
        if (mantenimientoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un mantenimiento de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Eliminar el mantenimiento #" + mantenimientoSeleccionado.getIdMantenimiento() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int idVehiculo = mantenimientoSeleccionado.getIdVehiculo();
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_MANTENIMIENTO WHERE pk_id_mantenimiento = ?")) {
            ps.setInt(1, mantenimientoSeleccionado.getIdMantenimiento());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Mantenimiento eliminado.");
            // Devolver vehículo a estado "Disponible" o mantener el que tenía
            actualizarEstadoVehiculo(idVehiculo, "Disponible");
            limpiar(event);
            cargarMantenimientos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarMantenimientos();
            return;
        }
        ObservableList<Mantenimiento> filtrados = FXCollections.observableArrayList();
        for (Mantenimiento m : listaMantenimientos) {
            if (m.getVehiculoInfo().toLowerCase().contains(filtro) ||
                    m.getTipo().toLowerCase().contains(filtro) ||
                    m.getDescripcion().toLowerCase().contains(filtro)) {
                filtrados.add(m);
            }
        }
        tablaMantenimientos.setItems(filtrados);
        tablaMantenimientos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarMantenimientos();
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
        mantenimientoSeleccionado = null;
        txtIdMantenimiento.clear();
        txtIdVehiculo.clear();
        txtVehiculoInfo.clear();
        dpFechaIngreso.setValue(null);
        dpFechaSalida.setValue(null);
        txtDias.clear();
        txtCosto.clear();
        txtDescripcion.clear();
        tipoGroup.selectToggle(null);
        lblTitulo.setText("Registro de Mantenimiento");
        tablaMantenimientos.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        if (txtIdVehiculo.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El ID del vehículo es obligatorio.");
            return false;
        }
        if (dpFechaIngreso.getValue() == null) {
            JOptionPane.showMessageDialog(null, "La fecha de ingreso es obligatoria.");
            return false;
        }
        if (dpFechaSalida.getValue() == null) {
            JOptionPane.showMessageDialog(null, "La fecha de salida es obligatoria.");
            return false;
        }
        if (dpFechaSalida.getValue().isBefore(dpFechaIngreso.getValue())) {
            JOptionPane.showMessageDialog(null, "La fecha de salida no puede ser anterior a la fecha de ingreso.");
            return false;
        }
        if (txtCosto.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El costo es obligatorio.");
            return false;
        }
        try {
            Double.parseDouble(txtCosto.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Costo inválido.");
            return false;
        }
        if (txtDescripcion.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "La descripción es obligatoria.");
            return false;
        }
        if (getTipoSeleccionado() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un tipo de mantenimiento.");
            return false;
        }
        try {
            Integer.parseInt(txtIdVehiculo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de vehículo inválido.");
            return false;
        }
        return true;
    }
}