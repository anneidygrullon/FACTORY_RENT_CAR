package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Suministrador;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import java.sql.*;

public class SuministradorController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Suministrador> tablaSuministradores;
    @FXML private TableColumn<Suministrador, Integer> colId;
    @FXML private TableColumn<Suministrador, String> colTipo;
    @FXML private TableColumn<Suministrador, String> colNombre;
    @FXML private TableColumn<Suministrador, String> colCorreo;
    @FXML private TableColumn<Suministrador, String> colTelefono;
    @FXML private TableColumn<Suministrador, String> colRnc;
    @FXML private TableColumn<Suministrador, String> colDireccion;

    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtRnc;
    @FXML private TextField txtIdDireccion;
    @FXML private TextField txtDireccionVista;

    private final ObservableList<Suministrador> listaSuministradores = FXCollections.observableArrayList();
    private int suministradorSeleccionadoId = -1;

    @FXML
    public void initialize() {
        // Configuración directa de columnas usando lambda
        colId.setCellValueFactory(cellData -> cellData.getValue().idSuministradorProperty().asObject());
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colCorreo.setCellValueFactory(cellData -> cellData.getValue().correoElectronicoProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colRnc.setCellValueFactory(cellData -> cellData.getValue().rncProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionCompletaProperty());

        tablaSuministradores.setItems(listaSuministradores);

        // Cuando seleccionan una fila, cargamos los datos en el formulario
        tablaSuministradores.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        cargarSuministradores();
    }

    private void cargarSuministradores() {
        listaSuministradores.clear();

        String sql = "SELECT s.id_suministrador, s.tipo, s.nombre, s.correo_electronico, s.telefono, " +
                "ISNULL(s.rnc, '') AS rnc, s.fk_pk_id_direccion, " +
                "ISNULL(d.calle_avenida + ' ' + ISNULL(d.num_edificio_casa, '') + ', ' + c.nombre, 'Sin dirección') AS direccion_completa " +
                "FROM TBL_SUMINISTRADOR s " +
                "LEFT JOIN TBL_DIRECCION d ON d.pk_id_direccion = s.fk_pk_id_direccion " +
                "LEFT JOIN TBL_CIUDAD c ON c.pk_id_ciudad = d.fk_pk_id_ciudad " +
                "ORDER BY s.id_suministrador";

        System.out.println("Ejecutando consulta SQL...");

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int contador = 0;
            while (rs.next()) {
                contador++;
                Suministrador sup = new Suministrador(
                        rs.getInt("id_suministrador"),
                        rs.getString("tipo"),
                        rs.getString("nombre"),
                        rs.getString("correo_electronico"),
                        rs.getString("telefono"),
                        rs.getString("rnc"),
                        rs.getInt("fk_pk_id_direccion"),
                        rs.getString("direccion_completa")
                );
                listaSuministradores.add(sup);
            }
            System.out.println("Suplidores cargados: " + contador);

            // Refrescamos la tabla para que se vean los cambios
            tablaSuministradores.refresh();

            if (contador == 0) {
                System.out.println("No se encontraron suplidores. Verifica la tabla TBL_SUMINISTRADOR.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            e.printStackTrace();
            error("Error al cargar suplidores: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarSuministradores();
            return;
        }
        ObservableList<Suministrador> filtrados = FXCollections.observableArrayList();
        for (Suministrador s : listaSuministradores) {
            if (s.getNombre().toLowerCase().contains(filtro) ||
                    s.getRnc().toLowerCase().contains(filtro) ||
                    s.getCorreoElectronico().toLowerCase().contains(filtro)) {
                filtrados.add(s);
            }
        }
        tablaSuministradores.setItems(filtrados);
        tablaSuministradores.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarSuministradores();
    }

    @FXML
    private void verificarDireccion(ActionEvent event) {
        if (txtIdDireccion.getText().isBlank()) {
            advertencia("Ingrese un ID de dirección.");
            return;
        }
        int idDir;
        try {
            idDir = Integer.parseInt(txtIdDireccion.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("ID inválido.");
            return;
        }
        String sql = "SELECT d.calle_avenida, d.num_edificio_casa, c.nombre AS ciudad " +
                "FROM TBL_DIRECCION d JOIN TBL_CIUDAD c ON c.pk_id_ciudad = d.fk_pk_id_ciudad " +
                "WHERE d.pk_id_direccion = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDir);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String direccion = rs.getString("calle_avenida") +
                        (rs.getString("num_edificio_casa") != null ? " " + rs.getString("num_edificio_casa") : "") +
                        ", " + rs.getString("ciudad");
                txtDireccionVista.setText(direccion);
                informacion("Dirección válida.");
            } else {
                txtDireccionVista.clear();
                advertencia("Dirección no encontrada.");
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void guardarSuministrador(ActionEvent event) {
        if (!validarFormulario()) return;

        String tipo = txtTipo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String rnc = txtRnc.getText().trim().isEmpty() ? null : txtRnc.getText().trim();
        int idDireccion;
        try {
            idDireccion = Integer.parseInt(txtIdDireccion.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("ID de dirección debe ser numérico.");
            return;
        }

        if (suministradorSeleccionadoId == -1) {
            String sql = "INSERT INTO TBL_SUMINISTRADOR (id_suministrador, tipo, nombre, correo_electronico, telefono, rnc, fk_pk_id_direccion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                int idS;
                try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(id_suministrador), 0) + 1 AS next_id FROM TBL_SUMINISTRADOR");
                     ResultSet rsNext = psNext.executeQuery()) {
                    idS = rsNext.next() ? rsNext.getInt("next_id") : 1;
                }
                ps.setInt(1, idS);
                ps.setString(2, tipo);
                ps.setString(3, nombre);
                ps.setString(4, correo);
                ps.setString(5, telefono);
                ps.setString(6, rnc);
                ps.setInt(7, idDireccion);
                ps.executeUpdate();
                informacion("Suplidor agregado con ID: " + idS);
                limpiarFormulario(event);
                cargarSuministradores();
            } catch (SQLException e) {
                error("Error al guardar: " + e.getMessage());
            }
        } else {
            String sql = "UPDATE TBL_SUMINISTRADOR SET tipo=?, nombre=?, correo_electronico=?, telefono=?, rnc=?, fk_pk_id_direccion=? " +
                    "WHERE id_suministrador = ?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tipo);
                ps.setString(2, nombre);
                ps.setString(3, correo);
                ps.setString(4, telefono);
                ps.setString(5, rnc);
                ps.setInt(6, idDireccion);
                ps.setInt(7, suministradorSeleccionadoId);
                ps.executeUpdate();
                informacion("Suplidor actualizado.");
                limpiarFormulario(event);
                cargarSuministradores();
            } catch (SQLException e) {
                error("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarSuministrador(ActionEvent event) {
        if (suministradorSeleccionadoId == -1) {
            advertencia("Seleccione un suplidor de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el suplidor #" + suministradorSeleccionadoId + "?")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_SUMINISTRADOR WHERE id_suministrador = ?")) {
            ps.setInt(1, suministradorSeleccionadoId);
            ps.executeUpdate();
            informacion("Suplidor eliminado.");
            limpiarFormulario(event);
            cargarSuministradores();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        txtTipo.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtRnc.clear();
        txtIdDireccion.clear();
        txtDireccionVista.clear();
        lblTituloFormulario.setText("Nuevo Suplidor");
        suministradorSeleccionadoId = -1;
        tablaSuministradores.getSelectionModel().clearSelection();
    }

    private void cargarEnFormulario(Suministrador s) {
        suministradorSeleccionadoId = s.getIdSuministrador();
        lblTituloFormulario.setText("Editando Suplidor #" + suministradorSeleccionadoId);
        txtTipo.setText(s.getTipo());
        txtNombre.setText(s.getNombre());
        txtCorreo.setText(s.getCorreoElectronico());
        txtTelefono.setText(s.getTelefono());
        txtRnc.setText(s.getRnc() != null ? s.getRnc() : "");
        txtIdDireccion.setText(String.valueOf(s.getIdDireccion()));
        txtDireccionVista.setText(s.getDireccionCompleta());
    }

    private boolean validarFormulario() {
        if (txtTipo.getText().isBlank()) {
            advertencia("El tipo es obligatorio.");
            return false;
        }
        if (txtNombre.getText().isBlank()) {
            advertencia("El nombre es obligatorio.");
            return false;
        }
        if (txtCorreo.getText().isBlank()) {
            advertencia("El correo electrónico es obligatorio.");
            return false;
        }
        if (txtTelefono.getText().isBlank()) {
            advertencia("El teléfono es obligatorio.");
            return false;
        }
        if (txtIdDireccion.getText().isBlank()) {
            advertencia("El ID de dirección es obligatorio.");
            return false;
        }
        try {
            Integer.parseInt(txtIdDireccion.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("El ID de dirección debe ser numérico.");
            return false;
        }
        return true;
    }
}
