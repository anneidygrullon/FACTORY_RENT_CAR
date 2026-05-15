package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Pago;
import com.example.factory_rent_car.Database.Conexion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoConsultaController {

    private MainLayoutController mainController;
    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, Integer> colIdPago;
    @FXML private TableColumn<Pago, LocalDate> colFechaPago;
    @FXML private TableColumn<Pago, String> colClientePago;
    @FXML private TableColumn<Pago, String> colReservaPago;
    @FXML private TableColumn<Pago, String> colMetodoPago;
    @FXML private TableColumn<Pago, String> colCuentaPago;
    @FXML private TableColumn<Pago, Double> colMontoPago;
    @FXML private TableColumn<Pago, String> colTipoPago;
    @FXML private VBox tableContainerPagos;
    @FXML private Button btnToggleTablePagos;

    private final ObservableList<Pago> listaPagos = FXCollections.observableArrayList();

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colIdPago.setCellValueFactory(c -> c.getValue().idPagoProperty().asObject());
        colFechaPago.setCellValueFactory(c -> c.getValue().fechaProperty());
        colClientePago.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colReservaPago.setCellValueFactory(c -> c.getValue().reservaInfoProperty());
        colMetodoPago.setCellValueFactory(c -> c.getValue().metodoPagoNombreProperty());
        colCuentaPago.setCellValueFactory(c -> c.getValue().cuentaInfoProperty());
        colMontoPago.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        colTipoPago.setCellValueFactory(c -> c.getValue().tipoProperty());

        tablaPagos.setItems(listaPagos);

        tableContainerPagos.setVisible(true);
        btnToggleTablePagos.setText("📋 Ocultar Tabla");

        cargarPagos();
    }

    private void cargarPagos() {
        listaPagos.clear();
        String sql = "SELECT p.id_pago, p.fecha, p.tipo, p.monto, p.fk_id_factura, " +
                "p.fk_id_metodo_pago, p.fk_id_cuenta, mp.tipo AS metodo_nombre, " +
                "c.numero + ' - ' + c.banco AS cuenta_info " +
                "FROM TBL_PAGO p " +
                "LEFT JOIN TBL_METODO_PAGO mp ON mp.id_metodo_pago = p.fk_id_metodo_pago " +
                "LEFT JOIN TBL_CUENTA c ON c.id_cuenta = p.fk_id_cuenta " +
                "ORDER BY p.id_pago DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaPagos.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null,
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        rs.getInt("fk_id_factura"),
                        rs.getInt("fk_id_metodo_pago"),
                        rs.getInt("fk_id_cuenta"),
                        rs.getString("metodo_nombre"),
                        rs.getString("cuenta_info"),
                        "", ""
                ));
            }
            tablaPagos.refresh();
        } catch (SQLException e) {
            error("Error cargando pagos: " + e.getMessage());
        }
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarPagos();
            return;
        }
        ObservableList<Pago> filtrados = FXCollections.observableArrayList();
        for (Pago p : listaPagos) {
            if (p.getClienteNombre().toLowerCase().contains(filtro) ||
                    p.getReservaInfo().toLowerCase().contains(filtro) ||
                    p.getMetodoPagoNombre().toLowerCase().contains(filtro)) {
                filtrados.add(p);
            }
        }
        tablaPagos.setItems(filtrados);
        tablaPagos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarPagos();
    }

    @FXML
    private void toggleTablePagosVisibility(ActionEvent event) {
        boolean visible = tableContainerPagos.isVisible();
        tableContainerPagos.setVisible(!visible);
        tableContainerPagos.setManaged(!visible);
        btnToggleTablePagos.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }

    @FXML
    private void irARegistroPago(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Registro de Pago", mainController.getSubMenuPagoRegistro(), "PagoRegistro.fxml");
        }
    }

    @FXML
    private void irANotaCredito(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Notas de Crédito", mainController.getSubMenuNotaCredito(), "NotaCredito.fxml");
        }
    }
}
