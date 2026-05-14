package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegistroCompraObjetoController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private ComboBox<String> cmbSuplidor;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtMontoTotal;

    private Map<String, Integer> mapaSuplidores = new HashMap<>();

    @FXML
    public void initialize() {
        // Cargar tipos de objeto
        cmbTipo.getItems().addAll("Mantenimiento", "Reserva", "Limpieza");

        // Cargar suplidores desde la base de datos
        cargarSuplidores();

        // Calcular monto total automáticamente
        txtPrecio.textProperty().addListener((obs, old, newVal) -> calcularMontoTotal());
        txtCantidad.textProperty().addListener((obs, old, newVal) -> calcularMontoTotal());
    }

    private void cargarSuplidores() {
        cmbSuplidor.getItems().clear();
        mapaSuplidores.clear();
        String sql = "SELECT id_suministrador, nombre FROM TBL_SUMINISTRADOR ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_suministrador");
                String nombre = rs.getString("nombre");
                cmbSuplidor.getItems().add(nombre);
                mapaSuplidores.put(nombre, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando suplidores: " + e.getMessage());
        }
    }

    private void calcularMontoTotal() {
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double total = precio * cantidad;
            txtMontoTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            txtMontoTotal.setText("0.00");
        }
    }

    @FXML
    private void registrarCompra(ActionEvent event) {
        if (!validarCampos()) return;

        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim().isEmpty() ? null : txtMarca.getText().trim();
        double precio = Double.parseDouble(txtPrecio.getText().trim());
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        String tipo = cmbTipo.getValue();
        String suplidorNombre = cmbSuplidor.getValue();
        LocalDate fecha = dpFecha.getValue();
        double montoTotal = Double.parseDouble(txtMontoTotal.getText().trim());

        int idSuplidor = mapaSuplidores.get(suplidorNombre);

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            // Crear contrato
            int idContrato = -1;
            String sqlContrato = "INSERT INTO TBL_CONTRATO (fecha, condicion) VALUES (?, ?)";
            PreparedStatement psContrato = con.prepareStatement(sqlContrato, Statement.RETURN_GENERATED_KEYS);
            psContrato.setDate(1, Date.valueOf(fecha));
            psContrato.setString(2, "Compra de objeto: " + nombre);
            psContrato.executeUpdate();
            ResultSet rsContrato = psContrato.getGeneratedKeys();
            if (rsContrato.next()) idContrato = rsContrato.getInt(1);
            psContrato.close();

            // Crear pedido
            int idCompra = -1;
            String sqlPedido = "INSERT INTO TBL_PEDIDO (cantidad, fecha, monto_total, estado, fk_id_suministrador, fk_pk_id_contrato) " +
                    "VALUES (?, ?, ?, 'Completado', ?, ?)";
            PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setInt(1, cantidad);
            psPedido.setDate(2, Date.valueOf(fecha));
            psPedido.setDouble(3, montoTotal);
            psPedido.setInt(4, idSuplidor);
            psPedido.setInt(5, idContrato);
            psPedido.executeUpdate();
            ResultSet rsPedido = psPedido.getGeneratedKeys();
            if (rsPedido.next()) idCompra = rsPedido.getInt(1);
            psPedido.close();

            // Crear objeto
            String sqlObjeto = "INSERT INTO TBL_OBJETO (nombre, marca, precio, stock, tipo, fk_pk_id_compra) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psObjeto = con.prepareStatement(sqlObjeto);
            psObjeto.setString(1, nombre);
            psObjeto.setString(2, marca);
            psObjeto.setDouble(3, precio);
            psObjeto.setInt(4, cantidad);
            psObjeto.setString(5, tipo);
            psObjeto.setInt(6, idCompra);
            psObjeto.executeUpdate();
            psObjeto.close();

            con.commit();
            JOptionPane.showMessageDialog(null, "Compra registrada correctamente.\nID del objeto registrado.");
            limpiar(event);
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(null, "Error al registrar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @FXML
    private void limpiar(ActionEvent event) {
        txtNombre.clear();
        txtMarca.clear();
        txtPrecio.clear();
        txtCantidad.clear();
        cmbTipo.setValue(null);
        cmbSuplidor.setValue(null);
        dpFecha.setValue(null);
        txtMontoTotal.clear();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El nombre del objeto es obligatorio.");
            return false;
        }
        if (txtPrecio.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "El precio es obligatorio.");
            return false;
        }
        if (txtCantidad.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "La cantidad es obligatoria.");
            return false;
        }
        if (cmbTipo.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un tipo de objeto.");
            return false;
        }
        if (cmbSuplidor.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un suplidor.");
            return false;
        }
        if (dpFecha.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha de compra.");
            return false;
        }
        try {
            Double.parseDouble(txtPrecio.getText().trim());
            Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Precio o cantidad inválidos.");
            return false;
        }
        return true;
    }
}