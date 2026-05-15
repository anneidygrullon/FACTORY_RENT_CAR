package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import com.example.factory_rent_car.Database.UserRoleManager;
import com.example.factory_rent_car.Modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

import static com.example.factory_rent_car.Util.MensajeFactory.*;

public class UsuarioConsultaController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private ComboBox<String> cmbRol;

    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colRol.setCellValueFactory(c -> c.getValue().rolProperty());

        cmbRol.getItems().addAll("admin", "gerente", "chofer", "carwasher", "mecanico");

        tablaUsuarios.setItems(listaUsuarios);
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    usuarioSeleccionado = newVal;
                    if (newVal != null) cmbRol.setValue(newVal.getRol());
                });

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        String sql = "SELECT nombre FROM TBL_USUARIO ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String rol = UserRoleManager.getRole(nombre);
                listaUsuarios.add(new Usuario(nombre, rol));
            }
            tablaUsuarios.refresh();
        } catch (SQLException e) {
            error("Error cargando usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarUsuarios();
            return;
        }
        ObservableList<Usuario> filtrados = FXCollections.observableArrayList();
        for (Usuario u : listaUsuarios) {
            if (u.getNombre().toLowerCase().contains(filtro) ||
                    u.getRol().toLowerCase().contains(filtro)) {
                filtrados.add(u);
            }
        }
        tablaUsuarios.setItems(filtrados);
        tablaUsuarios.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarUsuarios();
    }

    @FXML
    private void actualizarRol(ActionEvent event) {
        if (usuarioSeleccionado == null) {
            advertencia("Seleccione un usuario de la tabla.");
            return;
        }
        String nuevoRol = cmbRol.getValue();
        if (nuevoRol == null || nuevoRol.isEmpty()) {
            advertencia("Seleccione un rol válido.");
            return;
        }
        UserRoleManager.setRole(usuarioSeleccionado.getNombre(), nuevoRol);
        usuarioSeleccionado.setRol(nuevoRol);
        tablaUsuarios.refresh();
        informacion("Rol actualizado correctamente.");
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        if (usuarioSeleccionado == null) {
            advertencia("Seleccione un usuario de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el usuario '" + usuarioSeleccionado.getNombre() + "'?")) return;

        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_USUARIO WHERE nombre = ?")) {
            ps.setString(1, usuarioSeleccionado.getNombre());
            ps.executeUpdate();
            UserRoleManager.removeRole(usuarioSeleccionado.getNombre());
            informacion("Usuario eliminado.");
            listaUsuarios.remove(usuarioSeleccionado);
            usuarioSeleccionado = null;
            tablaUsuarios.refresh();
        } catch (SQLException e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }
}
