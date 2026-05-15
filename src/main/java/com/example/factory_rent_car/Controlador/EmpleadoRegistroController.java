package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class EmpleadoRegistroController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCedula;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtNacionalidad;
    @FXML private TextField txtEdad;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private ComboBox<String> cmbPuesto;
    @FXML private ComboBox<String> cmbDireccion;

    private Map<String, Integer> mapaPuestos = new HashMap<>();
    private Map<String, Integer> mapaDirecciones = new HashMap<>();

    @FXML
    public void initialize() {
        cargarPuestos();
        cargarDirecciones();

        // Al cambiar la fecha de nacimiento se calcula la edad sola
        dpFechaNacimiento.valueProperty().addListener((obs, old, newDate) -> calcularEdad());
    }

    private void cargarPuestos() {
        String sql = "SELECT pk_id_puesto, nombre FROM TBL_PUESTO ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_puesto");
                String nombre = rs.getString("nombre");
                cmbPuesto.getItems().add(nombre);
                mapaPuestos.put(nombre, id);
            }
        } catch (SQLException e) {
            error("Error cargando puestos: " + e.getMessage());
        }
    }

    private void cargarDirecciones() {
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
            error("Error cargando direcciones: " + e.getMessage());
        }
    }

    private void calcularEdad() {
        if (dpFechaNacimiento.getValue() != null) {
            int edad = Period.between(dpFechaNacimiento.getValue(), LocalDate.now()).getYears();
            txtEdad.setText(String.valueOf(edad));
        } else {
            txtEdad.clear();
        }
    }

    @FXML
    private void registrarEmpleado(ActionEvent event) {
        if (!validarCampos()) return;

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String cedula = txtCedula.getText().trim().replaceAll("[^0-9]", "");
        LocalDate fechaNac = dpFechaNacimiento.getValue();
        String nacionalidad = txtNacionalidad.getText().trim();
        int edad = Integer.parseInt(txtEdad.getText().trim());
        LocalDate fechaCont = dpFechaContratacion.getValue();
        String puestoNombre = cmbPuesto.getValue();
        String direccionStr = cmbDireccion.getValue();

        Integer idPuesto = mapaPuestos.get(puestoNombre);
        Integer idDireccion = mapaDirecciones.get(direccionStr);
        if (idPuesto == null || idDireccion == null) {
            advertencia("Datos inválidos de puesto o dirección.");
            return;
        }

        String sql = "INSERT INTO TBL_EMPLEADO (pk_id_empleado, nombre, telefono, cedula, fecha_nacimiento, nacionalidad, edad, fecha_contratacion, fk_pk_id_puesto, fk_pk_id_direccion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idEmpleado;
            try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_empleado), 0) + 1 AS next_id FROM TBL_EMPLEADO");
                 ResultSet rsNext = psNext.executeQuery()) {
                idEmpleado = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            ps.setInt(1, idEmpleado);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setString(4, cedula);
            ps.setDate(5, Date.valueOf(fechaNac));
            ps.setString(6, nacionalidad);
            ps.setInt(7, edad);
            ps.setDate(8, Date.valueOf(fechaCont));
            ps.setInt(9, idPuesto);
            ps.setInt(10, idDireccion);
            ps.executeUpdate();
            informacion("Empleado registrado con ID: " + idEmpleado);
            limpiar(event);
        } catch (SQLException e) {
            error("Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtNombre.clear();
        txtTelefono.clear();
        txtCedula.clear();
        dpFechaNacimiento.setValue(null);
        txtNacionalidad.clear();
        txtEdad.clear();
        dpFechaContratacion.setValue(null);
        cmbPuesto.setValue(null);
        cmbDireccion.setValue(null);
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isBlank()) { advertencia("Nombre obligatorio."); return false; }
        if (txtTelefono.getText().isBlank()) { advertencia("Teléfono obligatorio."); return false; }
        if (txtCedula.getText().isBlank()) { advertencia("Cédula obligatoria."); return false; }
        if (dpFechaNacimiento.getValue() == null) { advertencia("Fecha nacimiento obligatoria."); return false; }
        if (txtNacionalidad.getText().isBlank()) { advertencia("Nacionalidad obligatoria."); return false; }
        if (dpFechaContratacion.getValue() == null) { advertencia("Fecha contratación obligatoria."); return false; }
        if (cmbPuesto.getValue() == null) { advertencia("Seleccione un puesto."); return false; }
        if (cmbDireccion.getValue() == null) { advertencia("Seleccione una dirección."); return false; }
        int edad = Integer.parseInt(txtEdad.getText());
        if (edad < 21) {
            advertencia("El empleado debe tener al menos 21 años (check de BD).");
            return false;
        }
        return true;
    }
}
