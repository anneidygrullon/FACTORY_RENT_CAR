package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private Stage stage;
    private final Properties roles = new Properties();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private String obtenerRol(String username) {
        if (!roles.isEmpty()) {
            return roles.getProperty(username, "admin");
        }
        try (InputStream is = getClass().getResourceAsStream("/com/example/factory_rent_car/roles.properties")) {
            if (is != null) {
                roles.load(is);
                return roles.getProperty(username, "admin");
            }
        } catch (Exception ignored) {}
        return "admin";
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblError.setText("Ingrese usuario y contraseña");
            return;
        }

        Conexion conexion = new Conexion();
        try (Connection conn = conexion.establecerConexion()) {
            if (conn == null) {
                lblError.setText("Error de conexión con la base de datos");
                return;
            }

            String sql = "SELECT nombre FROM TBL_USUARIO WHERE nombre = ? AND contrasena = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nombreUsuario = rs.getString("nombre");
                String rol = obtenerRol(nombreUsuario);
                lblError.setText("");
                cargarMainLayout(nombreUsuario, rol);
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            lblError.setText("Error al validar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarMainLayout(String nombreUsuario, String rol) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/factory_rent_car/MainLayout.fxml"));
            Scene scene = new Scene(loader.load());
            MainLayoutController mainController = loader.getController();
            mainController.setUsuarioActual(nombreUsuario, rol);
            stage.setTitle("Sistema de gestión Factory Rent Car");
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            lblError.setText("Error al cargar el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
