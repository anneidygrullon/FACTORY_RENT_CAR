package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Reclamo;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.sql.*;

public class ReclamoConsultaController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Reclamo> tablaReclamos;
    @FXML private TableColumn<Reclamo, Integer> colId;
    @FXML private TableColumn<Reclamo, java.time.LocalDate> colFecha;
    @FXML private TableColumn<Reclamo, String> colCliente;
    @FXML private TableColumn<Reclamo, String> colMotivo;
    @FXML private TableColumn<Reclamo, String> colDescripcion;
    @FXML private TableColumn<Reclamo, String> colEstado;
    @FXML private TableColumn<Reclamo, String> colEmpleado;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private TextField txtReclamoInfo;
    @FXML private ComboBox<String> cmbEstado;

    private final ObservableList<Reclamo> listaReclamos = FXCollections.observableArrayList();
    private Reclamo reclamoSeleccionado;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idReclamoProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colMotivo.setCellValueFactory(c -> c.getValue().motivoProperty());
        colDescripcion.setCellValueFactory(c -> c.getValue().descripcionProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colEmpleado.setCellValueFactory(c -> c.getValue().empleadoNombreProperty());

        // Cargar opciones de estado
        cmbEstado.getItems().addAll("Pendiente", "En revisión", "Resuelto", "Rechazado");

        tablaReclamos.setItems(listaReclamos);
        tablaReclamos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        reclamoSeleccionado = newVal;
                        txtReclamoInfo.setText("ID: " + newVal.getIdReclamo() + " - " + newVal.getMotivo());
                        cmbEstado.setValue(newVal.getEstado());
                    }
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarReclamos();
    }

    private void cargarReclamos() {
        listaReclamos.clear();
        String sql = "SELECT r.pk_id_reclamo, r.estado, r.motivo, r.descripcion, " +
                "r.fk_pk_id_cliente, r.fk_pk_id_hist_reclamo, r.fk_pk_id_empleado, " +
                "c.nombre AS cliente_nombre, e.nombre AS empleado_nombre, h.fecha " +
                "FROM TBL_RECLAMACION r " +
                "LEFT JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente " +
                "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = r.fk_pk_id_empleado " +
                "LEFT JOIN TBL_HISTORIAL_RECLAMOS h ON h.pk_id_hist_reclamo = r.fk_pk_id_hist_reclamo " +
                "ORDER BY r.pk_id_reclamo DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reclamo r = new Reclamo(
                        rs.getInt("pk_id_reclamo"),
                        rs.getString("estado"),
                        rs.getString("motivo"),
                        rs.getString("descripcion"),
                        rs.getInt("fk_pk_id_cliente"),
                        rs.getInt("fk_pk_id_hist_reclamo"),
                        rs.getInt("fk_pk_id_empleado"),
                        rs.getString("cliente_nombre"),
                        rs.getString("empleado_nombre"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null
                );
                listaReclamos.add(r);
            }
            tablaReclamos.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando reclamos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarEstado(ActionEvent event) {
        if (reclamoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un reclamo de la tabla.");
            return;
        }
        String nuevoEstado = cmbEstado.getValue();
        if (nuevoEstado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un estado.");
            return;
        }

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("UPDATE TBL_RECLAMACION SET estado = ? WHERE pk_id_reclamo = ?")) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, reclamoSeleccionado.getIdReclamo());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Estado actualizado a: " + nuevoEstado);
            reclamoSeleccionado.setEstado(nuevoEstado);
            tablaReclamos.refresh();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarReclamos();
            return;
        }
        ObservableList<Reclamo> filtrados = FXCollections.observableArrayList();
        for (Reclamo r : listaReclamos) {
            if (r.getClienteNombre().toLowerCase().contains(filtro) ||
                    r.getMotivo().toLowerCase().contains(filtro) ||
                    r.getEstado().toLowerCase().contains(filtro)) {
                filtrados.add(r);
            }
        }
        tablaReclamos.setItems(filtrados);
        tablaReclamos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarReclamos();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }
}