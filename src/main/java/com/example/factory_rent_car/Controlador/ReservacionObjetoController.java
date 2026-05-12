package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Modelo.ReservaObjeto;
import com.example.factory_rent_car.Database.Conexion;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReservacionObjetoController {

    @FXML private TextField txtReservaId;
    @FXML private Label lblInfoReserva;
    @FXML private Label lblTotalObjetos;
    @FXML private Label lblTotalReserva;
    @FXML private Label lblTotalGeneral;

    @FXML private TableView<ObjetoDisponible> tablaObjetosDisponibles;
    @FXML private TableColumn<ObjetoDisponible, Integer> colObjId;
    @FXML private TableColumn<ObjetoDisponible, String> colObjNombre;
    @FXML private TableColumn<ObjetoDisponible, String> colObjMarca;
    @FXML private TableColumn<ObjetoDisponible, Double> colObjPrecio;
    @FXML private TableColumn<ObjetoDisponible, Integer> colObjStock;
    @FXML private TableColumn<ObjetoDisponible, String> colObjTipo;
    @FXML private TableColumn<ObjetoDisponible, Void> colObjAccion;

    @FXML private TableView<ReservaObjeto> tablaObjetosReservados;
    @FXML private TableColumn<ReservaObjeto, Integer> colResObjId;
    @FXML private TableColumn<ReservaObjeto, String> colResObjNombre;
    @FXML private TableColumn<ReservaObjeto, String> colResObjMarca;
    @FXML private TableColumn<ReservaObjeto, Double> colResObjPrecio;
    @FXML private TableColumn<ReservaObjeto, Integer> colResObjCantidad;
    @FXML private TableColumn<ReservaObjeto, Void> colResObjAccionEliminar;

    private final ObservableList<ObjetoDisponible> listaObjetosDisponibles = FXCollections.observableArrayList();
    private final ObservableList<ReservaObjeto> listaObjetosReservados = FXCollections.observableArrayList();

    private int reservaIdActual = -1;
    private double montoTotalOriginal = 0.0;
    private long diasReserva = 0;

    private Conexion conexion = new Conexion();

    @FXML
    public void initialize() {
        // Objetos disponibles
        colObjId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colObjMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colObjPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colObjStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colObjTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        agregarBotonAgregar();

        // Objetos reservados
        colResObjId.setCellValueFactory(c -> c.getValue().idObjetoProperty().asObject());
        colResObjNombre.setCellValueFactory(c -> c.getValue().nombreObjetoProperty());
        colResObjMarca.setCellValueFactory(c -> c.getValue().marcaObjetoProperty());
        colResObjPrecio.setCellValueFactory(c -> c.getValue().precioUnitarioProperty().asObject());
        colResObjCantidad.setCellValueFactory(c -> c.getValue().cantidadProperty().asObject());

        // Configuración de edición de cantidad CON VALIDACIÓN
        colResObjCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colResObjCantidad.setOnEditCommit(event -> {
            int newValue = event.getNewValue();
            if (newValue <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
                // recargar la tabla para revertir el valor visual (opcional)
                tablaObjetosReservados.refresh();
                return;
            }
            ReservaObjeto obj = event.getRowValue();
            obj.setCantidad(newValue);
            recalcularTotales();
        });
        agregarBotonEliminar();

        tablaObjetosDisponibles.setItems(listaObjetosDisponibles);
        tablaObjetosReservados.setItems(listaObjetosReservados);
        cargarObjetosDisponibles();
    }

    private void agregarBotonAgregar() {
        colObjAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("+ Agregar");
            { btn.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 12; -fx-padding: 4 8;");
                btn.setOnAction(e -> agregarObjeto(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void agregarBotonEliminar() {
        colResObjAccionEliminar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✖");
            { btn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 4 8;");
                btn.setOnAction(e -> eliminarObjetoReservado(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void cargarObjetosDisponibles() {
        listaObjetosDisponibles.clear();
        String sql = "SELECT pk_id_objeto, nombre, marca, precio, stock, tipo FROM TBL_OBJETO WHERE stock > 0 ORDER BY nombre";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaObjetosDisponibles.add(new ObjetoDisponible(
                        rs.getInt("pk_id_objeto"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando objetos: " + e.getMessage());
        }
    }

    @FXML
    private void buscarReserva() {
        if (txtReservaId.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID de reserva.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtReservaId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }
        String sql = "SELECT r.pk_id_reserva, c.nombre AS cliente, r.monto_total, r.fecha_inicio, r.fech_devolucion " +
                "FROM TBL_RESERVACION r " +
                "JOIN TBL_CLIENTE c ON c.pk_id_cliente = r.fk_pk_id_cliente WHERE r.pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reservaIdActual = id;
                montoTotalOriginal = rs.getDouble("monto_total");
                LocalDate inicio = rs.getDate("fecha_inicio").toLocalDate();
                LocalDate fin = rs.getDate("fech_devolucion").toLocalDate();
                diasReserva = ChronoUnit.DAYS.between(inicio, fin);
                if (diasReserva <= 0) diasReserva = 1;
                lblInfoReserva.setText(String.format("Reserva #%d - Cliente: %s (%.0f días)", id, rs.getString("cliente"), (double)diasReserva));
                lblTotalReserva.setText("RD$ " + String.format("%.2f", montoTotalOriginal));
                cargarObjetosReservados();
                recalcularTotales();
            } else {
                limpiar();
                JOptionPane.showMessageDialog(null, "Reserva no encontrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void cargarObjetosReservados() {
        listaObjetosReservados.clear();
        if (reservaIdActual == -1) return;
        String sql = "SELECT ors.id_obj_res, ors.fk_pk_id_reserva, ors.fk_pk_id_objeto, " +
                "o.nombre, o.marca, o.tipo, o.precio, ors.cantidad " +
                "FROM TBL_OBJ_RESERVA ors " +
                "JOIN TBL_OBJETO o ON o.pk_id_objeto = ors.fk_pk_id_objeto " +
                "WHERE ors.fk_pk_id_reserva = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservaIdActual);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaObjetosReservados.add(new ReservaObjeto(
                        rs.getInt("id_obj_res"),
                        rs.getInt("fk_pk_id_reserva"),
                        rs.getInt("fk_pk_id_objeto"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getString("tipo"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error cargando reservados: " + e.getMessage());
        }
    }

    private void agregarObjeto(ObjetoDisponible objeto) {
        if (reservaIdActual == -1) {
            JOptionPane.showMessageDialog(null, "Primero debe buscar una reserva válida.");
            return;
        }
        Optional<ReservaObjeto> existente = listaObjetosReservados.stream()
                .filter(o -> o.getIdObjeto() == objeto.getId())
                .findFirst();
        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + 1);
        } else {
            listaObjetosReservados.add(new ReservaObjeto(
                    reservaIdActual,
                    objeto.getId(),
                    objeto.getNombre(),
                    objeto.getMarca(),
                    objeto.getTipo(),
                    objeto.getPrecio(),
                    1
            ));
        }
        tablaObjetosReservados.refresh();
        recalcularTotales();
    }

    private void eliminarObjetoReservado(ReservaObjeto obj) {
        listaObjetosReservados.remove(obj);
        recalcularTotales();
    }

    @FXML
    private void eliminarObjetosSeleccionados() {
        ObservableList<ReservaObjeto> seleccionados = tablaObjetosReservados.getSelectionModel().getSelectedItems();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Seleccione al menos un objeto.");
            return;
        }
        listaObjetosReservados.removeAll(seleccionados);
        recalcularTotales();
    }

    private void recalcularTotales() {
        if (reservaIdActual == -1 || diasReserva <= 0) {
            lblTotalObjetos.setText("RD$ 0.00");
            lblTotalGeneral.setText("RD$ 0.00");
            return;
        }
        double totalObjetos = 0;
        for (ReservaObjeto obj : listaObjetosReservados) {
            totalObjetos += obj.getPrecioUnitario() * obj.getCantidad() * diasReserva;
        }
        lblTotalObjetos.setText("RD$ " + String.format("%.2f", totalObjetos));
        double granTotal = montoTotalOriginal + totalObjetos;
        lblTotalGeneral.setText("RD$ " + String.format("%.2f", granTotal));
    }

    @FXML
    private void guardarCambios() {
        if (reservaIdActual == -1) {
            JOptionPane.showMessageDialog(null, "No hay una reserva seleccionada.");
            return;
        }
        for (ReservaObjeto obj : listaObjetosReservados) {
            if (obj.getCantidad() <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad del objeto '" + obj.getNombreObjeto() + "' debe ser mayor a 0.");
                return;
            }
        }

        try (Connection con = conexion.establecerConexion()) {
            con.setAutoCommit(false);
            PreparedStatement psDelete = con.prepareStatement("DELETE FROM TBL_OBJ_RESERVA WHERE fk_pk_id_reserva = ?");
            psDelete.setInt(1, reservaIdActual);
            psDelete.executeUpdate();

            PreparedStatement psInsert = con.prepareStatement(
                    "INSERT INTO TBL_OBJ_RESERVA (cantidad, fk_pk_id_objeto, fk_pk_id_reserva) VALUES (?, ?, ?)"
            );
            for (ReservaObjeto obj : listaObjetosReservados) {
                psInsert.setInt(1, obj.getCantidad());
                psInsert.setInt(2, obj.getIdObjeto());
                psInsert.setInt(3, reservaIdActual);
                psInsert.addBatch();
            }
            psInsert.executeBatch();
            con.commit();
            JOptionPane.showMessageDialog(null, "Objetos guardados correctamente.");
            cargarObjetosReservados();
            recalcularTotales();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error SQL al guardar: " + e.getMessage() + "\nCódigo: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiar() {
        txtReservaId.clear();
        lblInfoReserva.setText("");
        lblTotalReserva.setText("RD$ 0.00");
        lblTotalObjetos.setText("RD$ 0.00");
        lblTotalGeneral.setText("RD$ 0.00");
        reservaIdActual = -1;
        montoTotalOriginal = 0;
        diasReserva = 0;
        listaObjetosReservados.clear();
    }

    // Clase interna para objetos disponibles
    public static class ObjetoDisponible {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty nombre, marca, tipo;
        private final SimpleDoubleProperty precio;
        private final SimpleIntegerProperty stock;
        public ObjetoDisponible(int id, String nombre, String marca, double precio, int stock, String tipo) {
            this.id = new SimpleIntegerProperty(id);
            this.nombre = new SimpleStringProperty(nombre);
            this.marca = new SimpleStringProperty(marca);
            this.precio = new SimpleDoubleProperty(precio);
            this.stock = new SimpleIntegerProperty(stock);
            this.tipo = new SimpleStringProperty(tipo);
        }
        public int getId() { return id.get(); }
        public String getNombre() { return nombre.get(); }
        public String getMarca() { return marca.get(); }
        public double getPrecio() { return precio.get(); }
        public int getStock() { return stock.get(); }
        public String getTipo() { return tipo.get(); }
    }
}