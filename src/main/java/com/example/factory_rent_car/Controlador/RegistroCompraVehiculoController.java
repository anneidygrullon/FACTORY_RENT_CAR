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

public class RegistroCompraVehiculoController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtSerie;
    @FXML private TextField txtPlaca;
    @FXML private TextField txtColor;
    @FXML private TextField txtKilometraje;
    @FXML private ComboBox<String> cmbCombustible;
    @FXML private TextField txtMaxPasajeros;
    @FXML private TextField txtPrecioPorDia;
    @FXML private TextField txtIdPoliza;
    @FXML private TextField txtPrecioCompra;
    @FXML private ComboBox<String> cmbSuplidor;
    @FXML private DatePicker dpFechaCompra;
    @FXML private TextField txtCantidad;

    private Map<String, Integer> mapaSuplidores = new HashMap<>();

    @FXML
    public void initialize() {
        // Cargar tipos de combustible
        cmbCombustible.getItems().addAll("Gasolina", "Diesel", "Eléctrico", "Híbrido");

        // Cargar suplidores
        cargarSuplidores();
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

    @FXML
    private void registrarCompra(ActionEvent event) {
        if (!validarCampos()) return;

        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String serie = txtSerie.getText().trim().isEmpty() ? null : txtSerie.getText().trim();
        String placa = txtPlaca.getText().trim();
        String color = txtColor.getText().trim().isEmpty() ? null : txtColor.getText().trim();
        double kilometraje = 0;
        try { kilometraje = Double.parseDouble(txtKilometraje.getText().trim()); } catch (NumberFormatException e) {}
        String combustible = cmbCombustible.getValue();
        int maxPasajeros = 5;
        try { maxPasajeros = Integer.parseInt(txtMaxPasajeros.getText().trim()); } catch (NumberFormatException e) {}
        double precioPorDia = Double.parseDouble(txtPrecioPorDia.getText().trim());
        int idPoliza = Integer.parseInt(txtIdPoliza.getText().trim());
        double precioCompra = Double.parseDouble(txtPrecioCompra.getText().trim());
        String suplidorNombre = cmbSuplidor.getValue();
        LocalDate fechaCompra = dpFechaCompra.getValue();
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());

        int idSuplidor = mapaSuplidores.get(suplidorNombre);

        Connection con = null;
        try {
            con = conexion.establecerConexion();
            con.setAutoCommit(false);

            // Crear contrato
            int idContrato = -1;
            String sqlContrato = "INSERT INTO TBL_CONTRATO (fecha, condicion) VALUES (?, ?)";
            PreparedStatement psContrato = con.prepareStatement(sqlContrato, Statement.RETURN_GENERATED_KEYS);
            psContrato.setDate(1, Date.valueOf(fechaCompra));
            psContrato.setString(2, "Compra de vehículo: " + marca + " " + modelo);
            psContrato.executeUpdate();
            ResultSet rsContrato = psContrato.getGeneratedKeys();
            if (rsContrato.next()) idContrato = rsContrato.getInt(1);
            psContrato.close();

            // Crear pedido
            int idCompra = -1;
            double montoTotal = precioCompra * cantidad;
            String sqlPedido = "INSERT INTO TBL_PEDIDO (cantidad, fecha, monto_total, estado, fk_id_suministrador, fk_pk_id_contrato) " +
                    "VALUES (?, ?, ?, 'Completado', ?, ?)";
            PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setInt(1, cantidad);
            psPedido.setDate(2, Date.valueOf(fechaCompra));
            psPedido.setDouble(3, montoTotal);
            psPedido.setInt(4, idSuplidor);
            psPedido.setInt(5, idContrato);
            psPedido.executeUpdate();
            ResultSet rsPedido = psPedido.getGeneratedKeys();
            if (rsPedido.next()) idCompra = rsPedido.getInt(1);
            psPedido.close();

            // Registrar vehículo
            String sqlVehiculo = "INSERT INTO TBL_VEHICULO (marca, modelo, serie, num_placa, color, kilometraje, " +
                    "tp_combustible, max_pasajeros, estado, precio_x_dia, fk_id_poliza, fk_pk_id_compra) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Disponible', ?, ?, ?)";
            PreparedStatement psVehiculo = con.prepareStatement(sqlVehiculo);
            psVehiculo.setString(1, marca);
            psVehiculo.setString(2, modelo);
            psVehiculo.setString(3, serie);
            psVehiculo.setString(4, placa);
            psVehiculo.setString(5, color);
            psVehiculo.setDouble(6, kilometraje);
            psVehiculo.setString(7, combustible);
            psVehiculo.setInt(8, maxPasajeros);
            psVehiculo.setDouble(9, precioPorDia);
            psVehiculo.setInt(10, idPoliza);
            psVehiculo.setInt(11, idCompra);
            psVehiculo.executeUpdate();
            psVehiculo.close();

            con.commit();
            JOptionPane.showMessageDialog(null, "Vehículo registrado correctamente.");
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
        txtMarca.clear();
        txtModelo.clear();
        txtSerie.clear();
        txtPlaca.clear();
        txtColor.clear();
        txtKilometraje.clear();
        cmbCombustible.setValue(null);
        txtMaxPasajeros.clear();
        txtPrecioPorDia.clear();
        txtIdPoliza.clear();
        txtPrecioCompra.clear();
        cmbSuplidor.setValue(null);
        dpFechaCompra.setValue(null);
        txtCantidad.clear();
    }

    private boolean validarCampos() {
        if (txtMarca.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Marca obligatoria."); return false; }
        if (txtModelo.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Modelo obligatorio."); return false; }
        if (txtPlaca.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Placa obligatoria."); return false; }
        if (cmbCombustible.getValue() == null) { JOptionPane.showMessageDialog(null, "Seleccione combustible."); return false; }
        if (txtMaxPasajeros.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Máx. pasajeros obligatorio."); return false; }
        if (txtPrecioPorDia.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Precio por día obligatorio."); return false; }
        if (txtIdPoliza.getText().isBlank()) { JOptionPane.showMessageDialog(null, "ID de póliza obligatorio."); return false; }
        if (txtPrecioCompra.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Precio de compra obligatorio."); return false; }
        if (cmbSuplidor.getValue() == null) { JOptionPane.showMessageDialog(null, "Seleccione suplidor."); return false; }
        if (dpFechaCompra.getValue() == null) { JOptionPane.showMessageDialog(null, "Fecha de compra obligatoria."); return false; }
        if (txtCantidad.getText().isBlank()) { JOptionPane.showMessageDialog(null, "Cantidad obligatoria."); return false; }
        try {
            Integer.parseInt(txtCantidad.getText().trim());
            Double.parseDouble(txtPrecioCompra.getText().trim());
            Double.parseDouble(txtPrecioPorDia.getText().trim());
            Integer.parseInt(txtIdPoliza.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Cantidad o precios inválidos.");
            return false;
        }
        return true;
    }
}