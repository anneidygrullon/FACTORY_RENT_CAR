package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Departamento;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;

import javax.swing.*;
import java.sql.*;

public class ConsultaDepartamentoController {

    Conexion conexion = Conexion.getInstance();

    // Componentes de búsqueda y tabla
    @FXML private TextField txtBuscar;
    @FXML private TableView<Departamento> tablaDepartamentos;
    @FXML private TableColumn<Departamento, Integer> colId;
    @FXML private TableColumn<Departamento, String> colNombre;
    @FXML private TableColumn<Departamento, String> colTelefono;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdDepartamento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;

    private final ObservableList<Departamento> listaDepartamentos = FXCollections.observableArrayList();
    private Departamento departamentoSeleccionado;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idDepartamentoProperty().asObject());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colTelefono.setCellValueFactory(c -> c.getValue().telefonoProperty());

        tablaDepartamentos.setItems(listaDepartamentos);
        tablaDepartamentos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
        listaDepartamentos.clear();
        String sql = "SELECT pk_id_dept, nombre, telefono FROM TBL_DEPARTAMENTO ORDER BY pk_id_dept";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Departamento d = new Departamento(
                        rs.getInt("pk_id_dept"),
                        rs.getString("nombre"),
                        rs.getString("telefono")
                );
                listaDepartamentos.add(d);
            }
            tablaDepartamentos.refresh();
            System.out.println("Departamentos cargados: " + listaDepartamentos.size());
        } catch (SQLException e) {
            error("Error cargando departamentos: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(Departamento d) {
        departamentoSeleccionado = d;
        txtIdDepartamento.setText(String.valueOf(d.getIdDepartamento()));
        txtNombre.setText(d.getNombre());
        txtTelefono.setText(d.getTelefono());
        lblTitulo.setText("Editando Departamento #" + d.getIdDepartamento());
    }

    @FXML
    private void guardarDepartamento(ActionEvent event) {
        if (!validarFormulario()) return;

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (departamentoSeleccionado == null) {
            // Insertar nuevo departamento
            String sql = "INSERT INTO TBL_DEPARTAMENTO (nombre, telefono) VALUES (?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    informacion("Departamento registrado con ID: " + rs.getInt(1));
                }
                limpiar(event);
                cargarDepartamentos();
            } catch (SQLException e) {
                error("Error al guardar: " + e.getMessage());
            }
        } else {
            // Actualizar departamento existente
            String sql = "UPDATE TBL_DEPARTAMENTO SET nombre=?, telefono=? WHERE pk_id_dept=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setInt(3, departamentoSeleccionado.getIdDepartamento());
                ps.executeUpdate();
                informacion("Departamento actualizado.");
                limpiar(event);
                cargarDepartamentos();
            } catch (SQLException e) {
                error("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarDepartamento(ActionEvent event) {
        if (departamentoSeleccionado == null) {
            advertencia("Seleccione un departamento de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el departamento #" + departamentoSeleccionado.getIdDepartamento() + "?\n" +
                "Esta acción no se puede deshacer.")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_DEPARTAMENTO WHERE pk_id_dept = ?")) {
            ps.setInt(1, departamentoSeleccionado.getIdDepartamento());
            ps.executeUpdate();
            informacion("Departamento eliminado.");
            limpiar(event);
            cargarDepartamentos();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDepartamentos();
            return;
        }
        ObservableList<Departamento> filtrados = FXCollections.observableArrayList();
        for (Departamento d : listaDepartamentos) {
            if (d.getNombre().toLowerCase().contains(filtro) ||
                    d.getTelefono().toLowerCase().contains(filtro)) {
                filtrados.add(d);
            }
        }
        tablaDepartamentos.setItems(filtrados);
        tablaDepartamentos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarDepartamentos();
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
        departamentoSeleccionado = null;
        txtIdDepartamento.clear();
        txtNombre.clear();
        txtTelefono.clear();
        lblTitulo.setText("Registro de Departamento");
        tablaDepartamentos.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isBlank()) {
            advertencia("El nombre del departamento es obligatorio.");
            return false;
        }
        if (txtTelefono.getText().isBlank()) {
            advertencia("El teléfono es obligatorio.");
            return false;
        }
        return true;
    }
}
