package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Cliente;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class ClienteController {

    Conexion conexion = Conexion.getInstance();

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, Integer> colEdad;
    @FXML private TableColumn<Cliente, LocalDate> colFechaNac;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colIdentificacion;
    @FXML private TableColumn<Cliente, String> colLicencia;
    @FXML private TableColumn<Cliente, String> colNacionalidad;
    @FXML private TableColumn<Cliente, String> colPasaporte;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtLicencia;
    @FXML private TextField txtNacionalidad;
    @FXML private TextField txtPasaporte;

    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private int clienteSeleccionadoId = -1;

    @FXML
    public void initialize() {
        // Configuración directa de columnas
        colId.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colEdad.setCellValueFactory(cellData -> cellData.getValue().edadProperty().asObject());
        colFechaNac.setCellValueFactory(cellData -> cellData.getValue().fechaNacimientoProperty());
        colCorreo.setCellValueFactory(cellData -> cellData.getValue().correoElectronicoProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colIdentificacion.setCellValueFactory(cellData -> cellData.getValue().identificacionProperty());
        colLicencia.setCellValueFactory(cellData -> cellData.getValue().licenciaProperty());
        colNacionalidad.setCellValueFactory(cellData -> cellData.getValue().nacionalidadProperty());
        colPasaporte.setCellValueFactory(cellData -> cellData.getValue().numPasaporteProperty());

        tablaClientes.setItems(listaClientes);
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> { if (newVal != null) cargarEnFormulario(newVal); });

        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        dpFechaNacimiento.valueProperty().addListener((obs, old, newDate) -> calcularEdad());

        cargarClientes();
    }

    private void calcularEdad() {
        if (dpFechaNacimiento.getValue() != null) {
            int edad = Period.between(dpFechaNacimiento.getValue(), LocalDate.now()).getYears();
            txtEdad.setText(String.valueOf(edad));
        } else {
            txtEdad.clear();
        }
    }

    private void cargarClientes() {
        listaClientes.clear();
        String sql = "SELECT pk_id_cliente, nombre, edad, fecha_nacimiento, correo_electronico, " +
                "telefono, identificacion, licencia, nacionalidad, num_pasaporte " +
                "FROM TBL_CLIENTE ORDER BY pk_id_cliente";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int contador = 0;
            while (rs.next()) {
                contador++;
                Cliente c = new Cliente(
                        rs.getInt("pk_id_cliente"),
                        rs.getString("nombre"),
                        rs.getInt("edad"),
                        rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null,
                        rs.getString("correo_electronico"),
                        rs.getString("telefono"),
                        rs.getString("identificacion"),
                        rs.getString("licencia"),
                        rs.getString("nacionalidad"),
                        rs.getString("num_pasaporte")
                );
                listaClientes.add(c);
            }
            System.out.println("Clientes cargados: " + contador);
            tablaClientes.refresh();
        } catch (SQLException e) {
            error("Error al cargar clientes: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarClientes();
            return;
        }
        ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
        for (Cliente c : listaClientes) {
            if (c.getNombre().toLowerCase().contains(filtro) ||
                    (c.getIdentificacion() != null && c.getIdentificacion().toLowerCase().contains(filtro)) ||
                    (c.getLicencia() != null && c.getLicencia().toLowerCase().contains(filtro))) {
                filtrados.add(c);
            }
        }
        tablaClientes.setItems(filtrados);
        tablaClientes.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarClientes();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void guardarCliente(ActionEvent event) {
        if (!validarFormulario()) return;

        String nombre = txtNombre.getText().trim();
        int edad;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Edad inválida.");
            return;
        }
        LocalDate fechaNac = dpFechaNacimiento.getValue();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String identificacion = txtIdentificacion.getText().trim().isEmpty() ? null : txtIdentificacion.getText().trim();
        String licencia = txtLicencia.getText().trim();
        String nacionalidad = txtNacionalidad.getText().trim();
        String pasaporte = txtPasaporte.getText().trim().isEmpty() ? null : txtPasaporte.getText().trim();

        if (clienteSeleccionadoId == -1) {
            String sql = "INSERT INTO TBL_CLIENTE (pk_id_cliente, nombre, edad, fecha_nacimiento, correo_electronico, telefono, " +
                    "identificacion, licencia, nacionalidad, num_pasaporte) VALUES (?,?,?,?,?,?,?,?,?,?)";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                int idCliente;
                try (PreparedStatement psNext = con.prepareStatement("SELECT ISNULL(MAX(pk_id_cliente), 0) + 1 AS next_id FROM TBL_CLIENTE");
                     ResultSet rsNext = psNext.executeQuery()) {
                    idCliente = rsNext.next() ? rsNext.getInt("next_id") : 1;
                }
                ps.setInt(1, idCliente);
                ps.setString(2, nombre);
                ps.setInt(3, edad);
                ps.setDate(4, Date.valueOf(fechaNac));
                ps.setString(5, correo);
                ps.setString(6, telefono);
                ps.setString(7, identificacion);
                ps.setString(8, licencia);
                ps.setString(9, nacionalidad);
                ps.setString(10, pasaporte);
                ps.executeUpdate();
                informacion("Cliente agregado con ID: " + idCliente);
                limpiarFormulario(event);
                cargarClientes();
            } catch (SQLException e) {
                error("Error al guardar: " + e.getMessage());
            }
        } else {
            String sql = "UPDATE TBL_CLIENTE SET nombre=?, edad=?, fecha_nacimiento=?, correo_electronico=?, telefono=?, " +
                    "identificacion=?, licencia=?, nacionalidad=?, num_pasaporte=? WHERE pk_id_cliente=?";
            try (Connection con = conexion.establecerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setInt(2, edad);
                ps.setDate(3, Date.valueOf(fechaNac));
                ps.setString(4, correo);
                ps.setString(5, telefono);
                ps.setString(6, identificacion);
                ps.setString(7, licencia);
                ps.setString(8, nacionalidad);
                ps.setString(9, pasaporte);
                ps.setInt(10, clienteSeleccionadoId);
                if (ps.executeUpdate() > 0)
                    informacion("Cliente actualizado.");
                else
                    advertencia("No se pudo actualizar.");
                limpiarFormulario(event);
                cargarClientes();
            } catch (SQLException e) {
                error("Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarCliente(ActionEvent event) {
        if (clienteSeleccionadoId == -1) {
            advertencia("Seleccione un cliente de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el cliente #" + clienteSeleccionadoId + "?")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
            ps.setInt(1, clienteSeleccionadoId);
            ps.executeUpdate();
            informacion("Cliente eliminado.");
            limpiarFormulario(event);
            cargarClientes();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        txtNombre.clear();
        txtEdad.clear();
        dpFechaNacimiento.setValue(null);
        txtCorreo.clear();
        txtTelefono.clear();
        txtIdentificacion.clear();
        txtLicencia.clear();
        txtNacionalidad.clear();
        txtPasaporte.clear();
        lblTituloFormulario.setText("Nuevo Cliente");
        clienteSeleccionadoId = -1;
        tablaClientes.getSelectionModel().clearSelection();
    }

    private void cargarEnFormulario(Cliente c) {
        clienteSeleccionadoId = c.getIdCliente();
        lblTituloFormulario.setText("Editando Cliente #" + clienteSeleccionadoId);
        txtNombre.setText(c.getNombre());
        txtEdad.setText(String.valueOf(c.getEdad()));
        dpFechaNacimiento.setValue(c.getFechaNacimiento());
        txtCorreo.setText(c.getCorreoElectronico());
        txtTelefono.setText(c.getTelefono());
        txtIdentificacion.setText(c.getIdentificacion() != null ? c.getIdentificacion() : "");
        txtLicencia.setText(c.getLicencia());
        txtNacionalidad.setText(c.getNacionalidad());
        txtPasaporte.setText(c.getNumPasaporte() != null ? c.getNumPasaporte() : "");
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isBlank()) {
            advertencia("El nombre es obligatorio.");
            return false;
        }
        if (dpFechaNacimiento.getValue() == null) {
            advertencia("La fecha de nacimiento es obligatoria.");
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
        if (txtLicencia.getText().isBlank()) {
            advertencia("El número de licencia es obligatorio.");
            return false;
        }
        if (txtNacionalidad.getText().isBlank()) {
            advertencia("La nacionalidad es obligatoria.");
            return false;
        }
        LocalDate nac = dpFechaNacimiento.getValue();
        int edad = Period.between(nac, LocalDate.now()).getYears();
        txtEdad.setText(String.valueOf(edad));
        if (edad < 22) {
            advertencia("El cliente debe tener al menos 22 años (edad calculada: " + edad + ").");
            return false;
        }
        return true;
    }

    // Navegación rápida
    @FXML
    private void irAClientes(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Clientes", mainController.getMenuClientes(), "Clientes.fxml");
    }
    @FXML
    private void irAReserva(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml");
    }
    @FXML
    private void irAIncidencia(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml");
    }
    @FXML
    private void irAGestionVehiculo(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Gestión de Vehículo", mainController.getMenuGestionVehiculo(), "EntregaVehiculo.fxml");
    }

    // Efectos hover para tarjetas
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
