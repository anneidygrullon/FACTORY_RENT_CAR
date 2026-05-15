package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.Objeto;
import com.example.factory_rent_car.Database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static com.example.factory_rent_car.Util.MensajeFactory.*;

import java.sql.*;

public class ConsultaObjetosController {

    Conexion conexion = Conexion.getInstance();

    @FXML private TextField txtBuscar;
    @FXML private TableView<Objeto> tablaObjetos;
    @FXML private TableColumn<Objeto, Integer> colId;
    @FXML private TableColumn<Objeto, String> colNombre;
    @FXML private TableColumn<Objeto, String> colMarca;
    @FXML private TableColumn<Objeto, Double> colPrecio;
    @FXML private TableColumn<Objeto, Integer> colStock;
    @FXML private TableColumn<Objeto, String> colTipo;
    @FXML private TableColumn<Objeto, String> colEstado;

    @FXML private VBox tableContainer;
    @FXML private Button btnToggleTable;
    @FXML private TextField txtObjetoInfo;
    @FXML private ComboBox<String> cmbEstado;

    private final ObservableList<Objeto> listaObjetos = FXCollections.observableArrayList();
    private Objeto objetoSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idObjetoProperty().asObject());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colMarca.setCellValueFactory(c -> c.getValue().marcaProperty());
        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty().asObject());
        colStock.setCellValueFactory(c -> c.getValue().stockProperty().asObject());
        colTipo.setCellValueFactory(c -> c.getValue().tipoProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());

        cmbEstado.getItems().addAll("Disponible", "Agotado", "En uso", "Dañado");

        // Cuando seleccionan un objeto, mostramos su info
        tablaObjetos.setItems(listaObjetos);
        tablaObjetos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        objetoSeleccionado = newVal;
                        txtObjetoInfo.setText(newVal.getNombre() + " - " + newVal.getMarca() + " (Stock: " + newVal.getStock() + ")");
                        cmbEstado.setValue(newVal.getEstado());
                    }
                });

        // La tabla se puede ocultar/mostrar con un botón
        tableContainer.setVisible(true);
        btnToggleTable.setText("📋 Ocultar Tabla");

        cargarObjetos();
    }

    private void cargarObjetos() {
        listaObjetos.clear();
        String sql = "SELECT pk_id_objeto, nombre, marca, precio, stock, tipo, fk_pk_id_compra FROM TBL_OBJETO ORDER BY pk_id_objeto";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Objeto o = new Objeto(
                        rs.getInt("pk_id_objeto"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("tipo"),
                        rs.getInt("fk_pk_id_compra"),
                        rs.getInt("stock") > 0 ? "Disponible" : "Agotado"
                );
                listaObjetos.add(o);
            }
            tablaObjetos.refresh();
        } catch (SQLException e) {
            error("Error cargando objetos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarEstado(ActionEvent event) {
        if (objetoSeleccionado == null) {
            advertencia("Seleccione un objeto de la tabla.");
            return;
        }
        String nuevoEstado = cmbEstado.getValue();
        if (nuevoEstado == null) {
            advertencia("Seleccione un estado.");
            return;
        }

        objetoSeleccionado.setEstado(nuevoEstado);
        informacion("Estado actualizado a: " + nuevoEstado);
        tablaObjetos.refresh();
    }

    @FXML
    private void buscar(ActionEvent event) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarObjetos();
            return;
        }
        ObservableList<Objeto> filtrados = FXCollections.observableArrayList();
        for (Objeto o : listaObjetos) {
            if (o.getNombre().toLowerCase().contains(filtro) ||
                    o.getMarca().toLowerCase().contains(filtro) ||
                    o.getTipo().toLowerCase().contains(filtro)) {
                filtrados.add(o);
            }
        }
        tablaObjetos.setItems(filtrados);
        tablaObjetos.refresh();
    }

    @FXML
    private void limpiarFiltro(ActionEvent event) {
        txtBuscar.clear();
        cargarObjetos();
    }

    @FXML
    private void toggleTableVisibility(ActionEvent event) {
        boolean visible = tableContainer.isVisible();
        tableContainer.setVisible(!visible);
        tableContainer.setManaged(!visible);
        btnToggleTable.setText(visible ? "📋 Mostrar Tabla" : "📋 Ocultar Tabla");
    }
}
