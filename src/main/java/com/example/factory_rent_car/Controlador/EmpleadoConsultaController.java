package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Empleado;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EmpleadoConsultaController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colCedula;
    @FXML private TableColumn<Empleado, String> colNacionalidad;
    @FXML private TableColumn<Empleado, Integer> colEdad;
    @FXML private TableColumn<Empleado, String> colPuesto;
    @FXML private TableColumn<Empleado, String> colDepartamento;
    @FXML private TableColumn<Empleado, Double> colSueldo;
    @FXML private TableColumn<Empleado, LocalDate> colFecContratacion;
    @FXML private TableColumn<Empleado, String> colDireccion;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private TextField txtIdEmpleado;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbPuesto;
    @FXML private ComboBox<String> cmbDepartamento;
    @FXML private TextField txtSueldo;

    private final ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();
    private Empleado empleadoSeleccionado;

    private Map<String, Integer> mapaPuestos = new HashMap<>();
    private Map<String, Integer> mapaDepartamentos = new HashMap<>();
    private Map<String, Double> mapaSueldosPorPuesto = new HashMap<>();

    @FXML
    public void initialize() {
        cargarPuestosYDepartamentos();

        colId.setCellValueFactory(c -> c.getValue().idEmpleadoProperty().asObject());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colTelefono.setCellValueFactory(c -> c.getValue().telefonoProperty());
        colCedula.setCellValueFactory(c -> c.getValue().cedulaProperty());
        colNacionalidad.setCellValueFactory(c -> c.getValue().nacionalidadProperty());
        colEdad.setCellValueFactory(c -> c.getValue().edadProperty().asObject());
        colPuesto.setCellValueFactory(c -> c.getValue().nombrePuestoProperty());
        colDepartamento.setCellValueFactory(c -> c.getValue().nombreDepartamentoProperty());
        colSueldo.setCellValueFactory(c -> c.getValue().sueldoProperty().asObject());
        colFecContratacion.setCellValueFactory(c -> c.getValue().fechaContratacionProperty());
        colDireccion.setCellValueFactory(c -> c.getValue().direccionCompletaProperty());

        tablaEmpleados.setItems(listaEmpleados);
        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) cargarEnFormulario(newVal);
                });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarEmpleados();
    }

    private void cargarPuestosYDepartamentos() {
        // Cargar puestos
        String sqlPuestos = "SELECT pk_id_puesto, nombre, sueldo FROM TBL_PUESTO";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlPuestos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_puesto");
                String nombre = rs.getString("nombre");
                double sueldo = rs.getDouble("sueldo");
                cmbPuesto.getItems().add(nombre);
                mapaPuestos.put(nombre, id);
                mapaSueldosPorPuesto.put(nombre, sueldo);
            }
        } catch (SQLException e) {
            error("Error cargando puestos: " + e.getMessage());
        }

        // Cargar departamentos
        String sqlDeptos = "SELECT pk_id_dept, nombre FROM TBL_DEPARTAMENTO";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlDeptos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pk_id_dept");
                String nombre = rs.getString("nombre");
                cmbDepartamento.getItems().add(nombre);
                mapaDepartamentos.put(nombre, id);
            }
        } catch (SQLException e) {
            error("Error cargando departamentos: " + e.getMessage());
        }

        // Listener para cuando se seleccione un puesto: actualizar el sueldo automáticamente
        cmbPuesto.valueProperty().addListener((obs, old, newPuesto) -> {
            if (newPuesto != null && mapaSueldosPorPuesto.containsKey(newPuesto)) {
                txtSueldo.setText(String.valueOf(mapaSueldosPorPuesto.get(newPuesto)));
            } else {
                txtSueldo.clear();
            }
        });
    }

    private void cargarEmpleados() {
        listaEmpleados.clear();
        String sql = "SELECT e.pk_id_empleado, e.nombre, e.telefono, e.cedula, e.fecha_nacimiento, " +
                "e.fecha_contratacion, e.nacionalidad, e.edad, e.fk_pk_id_direccion, e.fk_pk_id_puesto, " +
                "p.nombre AS nombre_puesto, d.nombre AS nombre_depto, p.sueldo, " +
                "ISNULL(dir.calle_avenida + ' ' + ISNULL(dir.num_edificio_casa, '') + ', ' + c.nombre, 'Sin dirección') AS direccion_completa " +
                "FROM TBL_EMPLEADO e " +
                "LEFT JOIN TBL_PUESTO p ON p.pk_id_puesto = e.fk_pk_id_puesto " +
                "LEFT JOIN TBL_DEPARTAMENTO d ON d.pk_id_dept = p.fk_pk_id_dept " +
                "LEFT JOIN TBL_DIRECCION dir ON dir.pk_id_direccion = e.fk_pk_id_direccion " +
                "LEFT JOIN TBL_CIUDAD c ON c.pk_id_ciudad = dir.fk_pk_id_ciudad " +
                "ORDER BY e.pk_id_empleado";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Empleado emp = new Empleado(
                        rs.getInt("pk_id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("cedula"),
                        rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null,
                        rs.getDate("fecha_contratacion") != null ? rs.getDate("fecha_contratacion").toLocalDate() : null,
                        rs.getString("nacionalidad"),
                        rs.getInt("edad"),
                        rs.getInt("fk_pk_id_direccion"),
                        rs.getInt("fk_pk_id_puesto"),
                        rs.getString("nombre_puesto"),
                        rs.getString("nombre_depto"),
                        rs.getDouble("sueldo"),
                        rs.getString("direccion_completa")
                );
                listaEmpleados.add(emp);
            }
            tablaEmpleados.refresh();
        } catch (SQLException e) {
            error("Error cargando empleados: " + e.getMessage());
        }
    }

    private void cargarEnFormulario(Empleado emp) {
        empleadoSeleccionado = emp;
        txtIdEmpleado.setText(String.valueOf(emp.getIdEmpleado()));
        txtNombre.setText(emp.getNombre());
        cmbPuesto.setValue(emp.getNombrePuesto());
        cmbDepartamento.setValue(emp.getNombreDepartamento());
        txtSueldo.setText(String.valueOf(emp.getSueldo()));
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarEmpleados();
            return;
        }
        ObservableList<Empleado> filtrados = FXCollections.observableArrayList();
        for (Empleado e : listaEmpleados) {
            if (e.getNombre().toLowerCase().contains(filtro) ||
                    e.getCedula().toLowerCase().contains(filtro) ||
                    e.getNombrePuesto().toLowerCase().contains(filtro)) {
                filtrados.add(e);
            }
        }
        tablaEmpleados.setItems(filtrados);
        tablaEmpleados.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarEmpleados();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void actualizarEmpleado(ActionEvent event) {
        if (empleadoSeleccionado == null) {
            advertencia("Seleccione un empleado de la tabla.");
            return;
        }
        String nuevoPuestoNombre = cmbPuesto.getValue();
        String nuevoDeptoNombre = cmbDepartamento.getValue();
        double nuevoSueldo;
        try {
            nuevoSueldo = Double.parseDouble(txtSueldo.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Sueldo inválido.");
            return;
        }

        Integer idPuesto = mapaPuestos.get(nuevoPuestoNombre);
        Integer idDepto = mapaDepartamentos.get(nuevoDeptoNombre);
        if (idPuesto == null || idDepto == null) {
            advertencia("Datos inválidos de puesto o departamento.");
            return;
        }

        // Primero, necesitamos actualizar el puesto (si cambia) y también el departamento asociado al puesto.
        // Pero la tabla EMPLEADO solo tiene FK a PUESTO. El departamento pertenece al puesto.
        // Por lo tanto, al cambiar el puesto, automáticamente cambiamos el departamento y el sueldo.
        // Si el usuario selecciona un departamento diferente, debemos buscar un puesto que pertenezca a ese departamento.
        // Para simplificar, vamos a actualizar directamente el puesto (que ya incluye departamento y sueldo).
        // Si el usuario quiere cambiar departamento, debe seleccionar un puesto que pertenezca a ese departamento.
        // Para ello, al seleccionar departamento, filtramos los puestos posibles.
        // Pero ya que tenemos los datos cargados, podemos actualizar el registro directamente.

        String sqlUpdate = "UPDATE TBL_EMPLEADO SET fk_pk_id_puesto = ? WHERE pk_id_empleado = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, idPuesto);
            ps.setInt(2, empleadoSeleccionado.getIdEmpleado());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                informacion("Empleado actualizado correctamente.");
                cargarEmpleados(); // recargar tabla
            } else {
                advertencia("No se pudo actualizar.");
            }
        } catch (SQLException e) {
            error("Error: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        empleadoSeleccionado = null;
        txtIdEmpleado.clear();
        txtNombre.clear();
        cmbPuesto.setValue(null);
        cmbDepartamento.setValue(null);
        txtSueldo.clear();
        tablaEmpleados.getSelectionModel().clearSelection();
    }
}
