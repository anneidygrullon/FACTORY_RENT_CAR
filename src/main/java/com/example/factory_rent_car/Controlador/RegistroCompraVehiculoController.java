package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.example.factory_rent_car.Util.MensajeFactory.*;

public class RegistroCompraVehiculoController {

    Conexion conexion = Conexion.getInstance();

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
        cmbCombustible.getItems().addAll("Gasolina", "Diesel", "Eléctrico", "Híbrido");

        // Traer los suplidores de la base
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
            error("Error cargando suplidores: " + e.getMessage());
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

            int idContrato;
            String sqlNextContrato = "SELECT ISNULL(MAX(pk_id_contrato), 0) + 1 AS next_id FROM TBL_CONTRATO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextContrato);
                 ResultSet rsNext = psNext.executeQuery()) {
                idContrato = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlContrato = "INSERT INTO TBL_CONTRATO (pk_id_contrato, fecha, condicion) VALUES (?, ?, ?)";
            PreparedStatement psContrato = con.prepareStatement(sqlContrato);
            psContrato.setInt(1, idContrato);
            psContrato.setDate(2, Date.valueOf(fechaCompra));
            psContrato.setString(3, "Compra de vehículo: " + marca + " " + modelo);
            psContrato.executeUpdate();

            int idCompra;
            double montoTotal = precioCompra * cantidad;
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
            psPedido.setDate(3, Date.valueOf(fechaCompra));
            psPedido.setDouble(4, montoTotal);
            psPedido.setInt(5, idSuplidor);
            psPedido.setInt(6, idContrato);
            psPedido.executeUpdate();

            int idVehiculo;
            String sqlNextVehiculo = "SELECT ISNULL(MAX(id_vehiculo), 0) + 1 AS next_id FROM TBL_VEHICULO";
            try (PreparedStatement psNext = con.prepareStatement(sqlNextVehiculo);
                 ResultSet rsNext = psNext.executeQuery()) {
                idVehiculo = rsNext.next() ? rsNext.getInt("next_id") : 1;
            }
            String sqlVehiculo = "INSERT INTO TBL_VEHICULO (id_vehiculo, marca, modelo, serie, num_placa, color, kilometraje, " +
                    "tp_combustible, max_pasajeros, estado, precio_x_dia, fk_id_poliza, fk_pk_id_compra) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Disponible', ?, ?, ?)";
            PreparedStatement psVehiculo = con.prepareStatement(sqlVehiculo);
            psVehiculo.setInt(1, idVehiculo);
            psVehiculo.setString(2, marca);
            psVehiculo.setString(3, modelo);
            psVehiculo.setString(4, serie);
            psVehiculo.setString(5, placa);
            psVehiculo.setString(6, color);
            psVehiculo.setDouble(7, kilometraje);
            psVehiculo.setString(8, combustible);
            psVehiculo.setInt(9, maxPasajeros);
            psVehiculo.setDouble(10, precioPorDia);
            psVehiculo.setInt(11, idPoliza);
            psVehiculo.setInt(12, idCompra);
            psVehiculo.executeUpdate();

            con.commit();
            informacion("Vehículo registrado correctamente.");
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
        if (txtMarca.getText().isBlank()) { advertencia("Marca obligatoria."); return false; }
        if (txtModelo.getText().isBlank()) { advertencia("Modelo obligatorio."); return false; }
        if (txtPlaca.getText().isBlank()) { advertencia("Placa obligatoria."); return false; }
        if (cmbCombustible.getValue() == null) { advertencia("Seleccione combustible."); return false; }
        if (txtMaxPasajeros.getText().isBlank()) { advertencia("Máx. pasajeros obligatorio."); return false; }
        if (txtPrecioPorDia.getText().isBlank()) { advertencia("Precio por día obligatorio."); return false; }
        if (txtIdPoliza.getText().isBlank()) { advertencia("ID de póliza obligatorio."); return false; }
        if (txtPrecioCompra.getText().isBlank()) { advertencia("Precio de compra obligatorio."); return false; }
        if (cmbSuplidor.getValue() == null) { advertencia("Seleccione suplidor."); return false; }
        if (dpFechaCompra.getValue() == null) { advertencia("Fecha de compra obligatoria."); return false; }
        if (txtCantidad.getText().isBlank()) { advertencia("Cantidad obligatoria."); return false; }
        try {
            Integer.parseInt(txtCantidad.getText().trim());
            Double.parseDouble(txtPrecioCompra.getText().trim());
            Double.parseDouble(txtPrecioPorDia.getText().trim());
            Integer.parseInt(txtIdPoliza.getText().trim());
        } catch (NumberFormatException e) {
            advertencia("Cantidad o precios inválidos.");
            return false;
        }
        return true;
    }
}
