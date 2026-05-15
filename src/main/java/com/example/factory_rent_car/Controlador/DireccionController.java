package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Direccion;
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
import java.util.HashMap;
import java.util.Map;

public class DireccionController {

    Conexion conexion = Conexion.getInstance();

    // Componentes de búsqueda y tabla
    @FXML private TextField txtBuscar;
    @FXML private TableView<Direccion> tablaDirecciones;
    @FXML private TableColumn<Direccion, Integer> colId;
    @FXML private TableColumn<Direccion, String> colCalle;
    @FXML private TableColumn<Direccion, String> colNumero;
    @FXML private TableColumn<Direccion, String> colCiudad;
    @FXML private TableColumn<Direccion, String> colCodigoPostal;
    @FXML private TableColumn<Direccion, String> colReferencia;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTitulo;
    @FXML private TextField txtIdDireccion;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private ComboBox<String> cmbCiudad;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextArea txtReferencia;

    private final ObservableList<Direccion> listaDirecciones = FXCollections.observableArrayList();
    private Map<String, Integer> mapaCiudades = new HashMap<>();
    private Direccion direccionSeleccionada;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> c.getValue().idDireccionProperty().asObject());
        colCalle.setCellValueFactory(c -> c.getValue().calleAvenidaProperty());
        colNumero.setCellValueFactory(c -> c.getValue().numEdificioCasaProperty());
        colCiudad.setCellValueFactory(c -> c.getValue().nombreCiudadProperty());
        colCodigoPostal.setCellValueFactory(c -> c.getValue().codigoPostalProperty());
        colReferencia.setCellValueFactory(c -> c.getValue().referenciaProperty());

        tablaDirecciones.setItems(listaDirecciones);
        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarCiudades();
        cargarDirecciones();
    }

    private void cargarCiudades() {
        String sql = "SELECT pk_id_ciudad, nombre FROM TBL_CIUDAD ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_ciudad");
                String nombre = rs.getString("nombre");
                cmbCiudad.getItems().add(nombre);
                mapaCiudades.put(nombre, id);
            }
        } catch (SQLException e) {
            error("Error cargando ciudades: " + e.getMessage());
        }
    }

    private void cargarDirecciones() {
        listaDirecciones.clear();
        String sql = "SELECT d.pk_id_direccion, d.calle_avenida, d.num_edificio_casa, " +
                "d.codigo_postal, d.referencia, d.fk_pk_id_ciudad, c.nombre AS ciudad_nombre " +
                "FROM TBL_DIRECCION d " +
                "LEFT JOIN TBL_CIUDAD c ON c.pk_id_ciudad = d.fk_pk_id_ciudad " +
                "ORDER BY d.pk_id_direccion";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Direccion dir = new Direccion(
                        rs.getInt("pk_id_direccion"),
                        rs.getString("calle_avenida"),
                        rs.getString("num_edificio_casa"),
                        rs.getString("codigo_postal"),
                        rs.getString("referencia"),
                        rs.getInt("fk_pk_id_ciudad"),
                        rs.getString("ciudad_nombre")
                );
                listaDirecciones.add(dir);
            }
            tablaDirecciones.refresh();
            System.out.println("Direcciones cargadas: " + listaDirecciones.size());
        } catch (SQLException e) {
            error("Error cargando direcciones: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(Direccion dir) {
        direccionSeleccionada = dir;
        txtIdDireccion.setText(String.valueOf(dir.getIdDireccion()));
        txtCalle.setText(dir.getCalleAvenida());
        txtNumero.setText(dir.getNumEdificioCasa() != null ? dir.getNumEdificioCasa() : "");
        txtCodigoPostal.setText(dir.getCodigoPostal() != null ? dir.getCodigoPostal() : "");
        txtReferencia.setText(dir.getReferencia() != null ? dir.getReferencia() : "");
        cmbCiudad.setValue(dir.getNombreCiudad());
        lblTitulo.setText("Editando Dirección #" + dir.getIdDireccion());
    }

    @FXML
    private void guardarDireccion(ActionEvent event) {
        if (!validarFormulario()) return;

        String calle = txtCalle.getText().trim();
        String numero = txtNumero.getText().trim().isEmpty() ? null : txtNumero.getText().trim();
        String ciudadNombre = cmbCiudad.getValue();
        String codigoPostal = txtCodigoPostal.getText().trim().isEmpty() ? null : txtCodigoPostal.getText().trim();
        String referencia = txtReferencia.getText().trim().isEmpty() ? null : txtReferencia.getText().trim();

        Integer idCiudad = mapaCiudades.get(ciudadNombre);
        if (idCiudad == null) {
            advertencia("Seleccione una ciudad válida.");
            return;
        }

        if (direccionSeleccionada == null) {
            // Insertar nueva dirección (pk no es IDENTITY)
            String sqlNextId = "SELECT ISNULL(MAX(pk_id_direccion), 0) + 1 AS next_id FROM TBL_DIRECCION";
            String sqlInsert = "INSERT INTO TBL_DIRECCION (pk_id_direccion, calle_avenida, num_edificio_casa, codigo_postal, referencia, fk_pk_id_ciudad) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement psNext = con.prepareStatement(sqlNextId);
                 ResultSet rsNext = psNext.executeQuery()) {
                int nextId = rsNext.next() ? rsNext.getInt("next_id") : 1;
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, nextId);
                    ps.setString(2, calle);
                    ps.setString(3, numero);
                    ps.setString(4, codigoPostal);
                    ps.setString(5, referencia);
                    ps.setInt(6, idCiudad);
                    ps.executeUpdate();
                    informacion("Dirección registrada con ID: " + nextId);
                }
                limpiar(event);
                cargarDirecciones();
            } catch (SQLException e) {
                error("Error al guardar: " + e.getMessage());
            }
        } else {
            // Actualizar dirección existente
            String sql = "UPDATE TBL_DIRECCION SET calle_avenida=?, num_edificio_casa=?, codigo_postal=?, referencia=?, fk_pk_id_ciudad=? " +
                    "WHERE pk_id_direccion=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, calle);
                ps.setString(2, numero);
                ps.setString(3, codigoPostal);
                ps.setString(4, referencia);
                ps.setInt(5, idCiudad);
                ps.setInt(6, direccionSeleccionada.getIdDireccion());
                ps.executeUpdate();
                informacion("Dirección actualizada.");
                limpiar(event);
                cargarDirecciones();
            } catch (SQLException e) {
                error("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarDireccion(ActionEvent event) {
        if (direccionSeleccionada == null) {
            advertencia("Seleccione una dirección de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar la dirección #" + direccionSeleccionada.getIdDireccion() + "?\n" +
                "Esta acción no se puede deshacer.")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_DIRECCION WHERE pk_id_direccion = ?")) {
            ps.setInt(1, direccionSeleccionada.getIdDireccion());
            ps.executeUpdate();
            informacion("Dirección eliminada.");
            limpiar(event);
            cargarDirecciones();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDirecciones();
            return;
        }
        ObservableList<Direccion> filtrados = FXCollections.observableArrayList();
        for (Direccion d : listaDirecciones) {
            if (d.getCalleAvenida().toLowerCase().contains(filtro) ||
                    (d.getNombreCiudad() != null && d.getNombreCiudad().toLowerCase().contains(filtro)) ||
                    (d.getCodigoPostal() != null && d.getCodigoPostal().toLowerCase().contains(filtro))) {
                filtrados.add(d);
            }
        }
        tablaDirecciones.setItems(filtrados);
        tablaDirecciones.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarDirecciones();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void nuevo(ActionEvent event) {
        limpiar(event);
        txtCalle.requestFocus();
    }

    @FXML
    private void limpiar(ActionEvent event) {
        direccionSeleccionada = null;
        txtIdDireccion.clear();
        txtCalle.clear();
        txtNumero.clear();
        cmbCiudad.setValue(null);
        txtCodigoPostal.clear();
        txtReferencia.clear();
        lblTitulo.setText("Registro de Dirección");
        tablaDirecciones.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        if (txtCalle.getText().isBlank()) {
            advertencia("La calle/avenida es obligatoria.");
            return false;
        }
        if (cmbCiudad.getValue() == null) {
            advertencia("Seleccione una ciudad.");
            return false;
        }
        return true;
    }
}
