package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Incidencia;
import com.example.factory_rent_car.Database.Conexion;
import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class IncidenciaConsultaController {

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Incidencia> tablaIncidencias;
    @FXML private TableColumn<Incidencia, Integer> colId;
    @FXML private TableColumn<Incidencia, LocalDate> colFecha;
    @FXML private TableColumn<Incidencia, String> colTipo;
    @FXML private TableColumn<Incidencia, Double> colMonto;
    @FXML private TableColumn<Incidencia, String> colDescripcion;
    @FXML private TableColumn<Incidencia, String> colReserva;
    @FXML private TableColumn<Incidencia, String> colEmpleado;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private TextField txtIncidenciaInfo;

    private final ObservableList<Incidencia> listaIncidencias = FXCollections.observableArrayList();
    private Incidencia incidenciaSeleccionada;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idIncidenciaProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colMonto.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        colDescripcion.setCellValueFactory(c -> c.getValue().descripcionProperty());
        colReserva.setCellValueFactory(c -> c.getValue().reservaInfoProperty());
        colEmpleado.setCellValueFactory(c -> c.getValue().empleadoNombreProperty());

        tablaIncidencias.setItems(listaIncidencias);
        tablaIncidencias.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        incidenciaSeleccionada = newVal;
                        txtIncidenciaInfo.setText("ID: " + newVal.getIdIncidencia() + " - " + newVal.getTipo());
                    }
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarIncidencias();
    }

    private void cargarIncidencias() {
        listaIncidencias.clear();
        String sql = "SELECT i.pk_id_incidencia, i.tipo, i.monto, i.fecha, i.descripcion, " +
                "i.fk_pk_id_reserva, i.fk_pk_id_hist_incidencia, i.fk_pk_id_empleado, " +
                "r.pk_id_reserva AS reserva_id, " +
                "e.nombre AS empleado_nombre, " +
                "'Pendiente' AS estado_incidencia " +
                "FROM TBL_INCIDENCIA i " +
                "LEFT JOIN TBL_RESERVACION r ON r.pk_id_reserva = i.fk_pk_id_reserva " +
                "LEFT JOIN TBL_EMPLEADO e ON e.pk_id_empleado = i.fk_pk_id_empleado " +
                "LEFT JOIN TBL_HISTORIAL_INCIDENCIA hi ON hi.pk_id_hist_incidencia = i.fk_pk_id_hist_incidencia " +
                "ORDER BY i.pk_id_incidencia DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String reservaInfo = "Reserva #" + rs.getInt("reserva_id");
                Incidencia inc = new Incidencia(
                        rs.getInt("pk_id_incidencia"),
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("descripcion"),
                        rs.getInt("fk_pk_id_reserva"),
                        rs.getInt("fk_pk_id_hist_incidencia"),
                        rs.getInt("fk_pk_id_empleado"),
                        reservaInfo,
                        rs.getString("empleado_nombre"),
                        rs.getString("estado_incidencia")
                );
                listaIncidencias.add(inc);
            }
            tablaIncidencias.refresh();
        } catch (SQLException e) {
            error("Error cargando incidencias: " + e.getMessage());
        }
    }



    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarIncidencias();
            return;
        }
        ObservableList<Incidencia> filtrados = FXCollections.observableArrayList();
        for (Incidencia i : listaIncidencias) {
            if (i.getTipo().toLowerCase().contains(filtro) ||
                    i.getReservaInfo().toLowerCase().contains(filtro) ||
                    i.getEmpleadoNombre().toLowerCase().contains(filtro) ||
                    i.getEstado().toLowerCase().contains(filtro)) {
                filtrados.add(i);
            }
        }
        tablaIncidencias.setItems(filtrados);
        tablaIncidencias.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarIncidencias();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    // ── NAVEGACIÓN RÁPIDA ──────────────────────────────────────────────
    @FXML
    private void irAConsultarIncidencias(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Consulta de Incidencias", mainController.getMenuIncidencias(), "IncidenciaConsulta.fxml");
    }
    @FXML
    private void irARegistrarIncidencia(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml");
    }
    @FXML
    private void irAClientes(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Clientes", mainController.getMenuClientes(), "Clientes.fxml");
    }
    @FXML
    private void irAReservas(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml");
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
