package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import com.example.factory_rent_car.Database.UserRoleManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.Connection;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegistroUsuarioController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmar;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Label lblError;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        cmbRol.getItems().addAll("admin", "gerente", "chofer", "carwasher", "mecanico");
        cmbRol.setValue("chofer");
    }

    @FXML
    private void handleRegistrar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        String confirmar = txtConfirmar.getText();
        String rol = cmbRol.getValue();

        if (usuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            lblError.setText("Todos los campos son obligatorios.");
            return;
        }
        if (!contrasena.equals(confirmar)) {
            lblError.setText("Las contraseñas no coinciden.");
            return;
        }
        if (contrasena.length() < 4) {
            lblError.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        Conexion conexion = Conexion.getInstance();
        try (Connection conn = conexion.establecerConexion()) {
            int idUsuario;
            try (PreparedStatement psNext = conn.prepareStatement("SELECT ISNULL(MAX(id_usuario), 0) + 1 AS next_id FROM TBL_USUARIO");
                 ResultSet rsNext = psNext.executeQuery()) {
                idUsuario = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sql = "INSERT INTO TBL_USUARIO (id_usuario, nombre, contrasena) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, usuario);
            ps.setString(3, contrasena);
            ps.executeUpdate();
            ps.close();

            UserRoleManager.setRole(usuario, rol);
            informacion("Usuario creado correctamente.");
            volverLogin();
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("PK") || e.getMessage().contains("primary key") || e.getMessage().contains("duplicate"))) {
                lblError.setText("El nombre de usuario ya existe.");
            } else {
                lblError.setText("Error al crear usuario: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolver() {
        volverLogin();
    }

    private void volverLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/factory_rent_car/Login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController loginController = loader.getController();
            loginController.setStage(stage);
            stage.setTitle("Factory Rent Car - Iniciar Sesión");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
