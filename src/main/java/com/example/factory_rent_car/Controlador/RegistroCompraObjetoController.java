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

import static com.example.factory_rent_car.Util.MensajeFactory.*;

public class RegistroCompraObjetoController {

    Conexion conexion = Conexion.getInstance();

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
            error("Error cargando suplidores: " + e.getMessage());
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
            int idContrato;
            String sqlNextContrato = "SELECT ISNULL(MAX(pk_id_contrato), 0) + 1 AS next_id FROM TBL_CONTRATO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextContrato);
                 ResultSet rsNext = psNext.executeQuery()) {
                idContrato = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlContrato = "INSERT INTO TBL_CONTRATO (pk_id_contrato, fecha, condicion) VALUES (?, ?, ?)";
            PreparedStatement psContrato = con.prepareStatement(sqlContrato);
            psContrato.setInt(1, idContrato);
            psContrato.setDate(2, Date.valueOf(fecha));
            psContrato.setString(3, "Compra de objeto: " + nombre);
            psContrato.executeUpdate();

            // Crear pedido
            int idCompra;
            String sqlNextPedido = "SELECT ISNULL(MAX(pk_id_compra), 0) + 1 AS next_id FROM TBL_PEDIDO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextPedido);
                 ResultSet rsNext = psNext.executeQuery()) {
                idCompra = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlPedido = "INSERT INTO TBL_PEDIDO (pk_id_compra, cantidad, fecha, monto_total, estado, fk_id_suministrador, fk_pk_id_contrato) " +
                    "VALUES (?, ?, ?, ?, 'Completado', ?, ?)";
            PreparedStatement psPedido = con.prepareStatement(sqlPedido);
            psPedido.setInt(1, idCompra);
            psPedido.setInt(2, cantidad);
            psPedido.setDate(3, Date.valueOf(fecha));
            psPedido.setDouble(4, montoTotal);
            psPedido.setInt(5, idSuplidor);
            psPedido.setInt(6, idContrato);
            psPedido.executeUpdate();

            // Crear objeto
            int idObjeto;
            String sqlNextObj = "SELECT ISNULL(MAX(pk_id_objeto), 0) + 1 AS next_id FROM TBL_OBJETO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextObj);
                 ResultSet rsNext = psNext.executeQuery()) {
                idObjeto = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlObjeto = "INSERT INTO TBL_OBJETO (pk_id_objeto, nombre, marca, precio, stock, tipo, fk_pk_id_compra) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psObjeto = con.prepareStatement(sqlObjeto);
            psObjeto.setInt(1, idObjeto);
            psObjeto.setString(2, nombre);
            psObjeto.setString(3, marca);
            psObjeto.setDouble(4, precio);
            psObjeto.setInt(5, cantidad);
            psObjeto.setString(6, tipo);
            psObjeto.setInt(7, idCompra);
            psObjeto.executeUpdate();

            con.commit();
            informacion("Compra registrada correctamente.\nID del objeto registrado.");
            limpiar(event);
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            error("Error al registrar: " + e.getMessage());
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
            advertencia("El nombre del objeto es obligatorio.");
            return false;
        }
        if (txtPrecio.getText().isBlank()) {
            advertencia("El precio es obligatorio.");
            return false;
        }
        if (txtCantidad.getText().isBlank()) {
            advertencia("La cantidad es obligatoria.");
            return false;
        }
        if (cmbTipo.getValue() == null) {
            advertencia("Seleccione un tipo de objeto.");
            return false;
        }
        if (cmbSuplidor.getValue() == null) {
            advertencia("Seleccione un suplidor.");
            return false;
        }
        if (dpFecha.getValue() == null) {
            advertencia("Seleccione una fecha de compra.");
            return false;
        }
        try {
            Double.parseDouble(txtPrecio.getText().trim());
            Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Precio o cantidad inválidos.");
            return false;
        }
        return true;
    }
}
