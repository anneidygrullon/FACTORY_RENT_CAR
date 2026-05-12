package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class EmpleadoRegistroController {

    Conexion conexion = new Conexion();

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

        // Calcular edad automáticamente al seleccionar fecha de nacimiento
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
            JOptionPane.showMessageDialog(null, "Error cargando puestos: " + e.getMessage());
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
            JOptionPane.showMessageDialog(null, "Error cargando direcciones: " + e.getMessage());
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
        String cedula = txtCedula.getText().trim();
        LocalDate fechaNac = dpFechaNacimiento.getValue();
        String nacionalidad = txtNacionalidad.getText().trim();
        int edad = Integer.parseInt(txtEdad.getText().trim());
        LocalDate fechaCont = dpFechaContratacion.getValue();
        String puestoNombre = cmbPuesto.getValue();
        String direccionStr = cmbDireccion.getValue();

        Integer idPuesto = mapaPuestos.get(puestoNombre);
        Integer idDireccion = mapaDirecciones.get(direccionStr);
        if (idPuesto == null || idDireccion == null) {
            JOptionPane.showMessageDialog(null, "Datos inválidos de puesto o dirección.");
            return;
        }

        String sql = "INSERT INTO TBL_EMPLEADO (nombre, telefono, cedula, fecha_nacimiento, nacionalidad, edad, fecha_contratacion, fk_pk_id_puesto, fk_pk_id_direccion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, cedula);
            ps.setDate(4, Date.valueOf(fechaNac));
            ps.setString(5, nacionalidad);
            ps.setInt(6, edad);
            ps.setDate(7, Date.valueOf(fechaCont));
            ps.setInt(8, idPuesto);
            ps.setInt(9, idDireccion);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Empleado registrado con ID: " + rs.getInt(1));
            } else {
                JOptionPane.showMessageDialog(null, "Empleado registrado.");
            }
            limpiar(event);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.getMessage());
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
        if (txtNombre.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Nombre obligatorio."); return false; }
        if (txtTelefono.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Teléfono obligatorio."); return false; }
        if (txtCedula.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Cédula obligatoria."); return false; }
        if (dpFechaNacimiento.getValue() == null) { JOptionPane.showMessageDialog(null, "Fecha nacimiento obligatoria."); return false; }
        if (txtNacionalidad.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Nacionalidad obligatoria."); return false; }
        if (dpFechaContratacion.getValue() == null) { JOptionPane.showMessageDialog(null, "Fecha contratación obligatoria."); return false; }
        if (cmbPuesto.getValue() == null) { JOptionPane.showMessageDialog(null, "Seleccione un puesto."); return false; }
        if (cmbDireccion.getValue() == null) { JOptionPane.showMessageDialog(null, "Seleccione una dirección."); return false; }
        int edad = Integer.parseInt(txtEdad.getText());
        if (edad < 21) {
            JOptionPane.showMessageDialog(null, "El empleado debe tener al menos 21 años (check de BD).");
            return false;
        }
        return true;
    }
}