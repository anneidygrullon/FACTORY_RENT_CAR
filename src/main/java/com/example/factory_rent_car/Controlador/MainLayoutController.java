package com.example.factory_rent_car.Controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    // ── TOP BAR ──────────────────────────────────────────────
    @FXML private Label sectionTitle;

    // ── SIDEBAR — menú items ──────────────────────────────────
    @FXML private HBox menuInicio;
    @FXML private HBox menuReservacion;
    @FXML private HBox menuEntregas;
    @FXML private HBox menuDevolucion;
    @FXML private HBox menuPagos;
    @FXML private HBox menuVehiculos;
    @FXML private HBox menuMantenimiento;
    @FXML private HBox menuLimpieza;
    @FXML private HBox menuIncidencias;
    @FXML private HBox menuCompras;
    @FXML private HBox menuGps;
    @FXML private HBox menuRegistros;
    @FXML private HBox menuClientes;
    @FXML private HBox menuSuplidores;   // nuevo

    // ── SUBMENÚ RESERVACIÓN ──────────────────────────────────
    @FXML private VBox subMenuReservacion;
    @FXML private Label reservacionArrow;
    @FXML private HBox subMenuReservaVehiculo;
    @FXML private HBox subMenuReservaObjeto;

    // ── BUSCADOR ─────────────────────────────────────────────
    @FXML private TextField searchField;

    // ── CONTENIDO PRINCIPAL ──────────────────────────────────
    @FXML private StackPane contentArea;

    private void activarMenu(HBox nuevoActivo) {
        if (menuActivo != null && menuActivo != nuevoActivo) {
            menuActivo.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-padding: 11 14 11 16;" +
                            "-fx-cursor: hand;"
            );
        }
        menuActivo = nuevoActivo;
        nuevoActivo.setStyle(
                "-fx-background-color: #0a1540;" +
                        "-fx-border-color: transparent transparent transparent #4a9eff;" +
                        "-fx-border-width: 0 0 0 3;" +
                        "-fx-padding: 13 14 13 13;" +
                        "-fx-cursor: hand;"
        );
    }

    // ── Estado ───────────────────────────────────────────────
    private HBox menuActivo;
    private boolean subMenuReservacionVisible = false;

    private static final String BASE = "/com/example/factory_rent_car/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        subMenuReservacion.setVisible(false);
        subMenuReservacion.setManaged(false);
        reservacionArrow.setText("❯");
        activarMenu(menuInicio);
        mostrarHome();
    }

    // ── HANDLERS DEL MENÚ PRINCIPAL ──────────────────────────
    @FXML private void handleMenuInicio(MouseEvent e) {
        activarMenu(menuInicio);
        sectionTitle.setText("Inicio");
        mostrarHome();
    }

    @FXML private void toggleSubMenuReservacion(MouseEvent e) {
        subMenuReservacionVisible = !subMenuReservacionVisible;
        subMenuReservacion.setVisible(subMenuReservacionVisible);
        subMenuReservacion.setManaged(subMenuReservacionVisible);
        reservacionArrow.setText(subMenuReservacionVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuReservaVehiculo(MouseEvent e) {
        activarMenu(menuReservacion);
        sectionTitle.setText("Reservación de Vehículos");
        cargarFxml("Reserva.fxml");
        cerrarSubMenu();
    }

    @FXML private void handleSubMenuReservaObjeto(MouseEvent e) {
        activarMenu(menuReservacion);
        sectionTitle.setText("Reservación de Objetos");
        cargarFxml("ReservacionObjeto.fxml");
        cerrarSubMenu();
    }

    private void cerrarSubMenu() {
        subMenuReservacionVisible = false;
        subMenuReservacion.setVisible(false);
        subMenuReservacion.setManaged(false);
        reservacionArrow.setText("❯");
    }

    @FXML private void handleMenuEntregas(MouseEvent e) {
        activarMenu(menuEntregas);
        sectionTitle.setText("Entregas");
        cargarFxml("Entregas.fxml");
    }
    @FXML private void handleMenuDevolucion(MouseEvent e) {
        activarMenu(menuDevolucion);
        sectionTitle.setText("Devolución");
        cargarFxml("Devolucion.fxml");
    }
    @FXML private void handleMenuPagos(MouseEvent e) {
        activarMenu(menuPagos);
        sectionTitle.setText("Pagos");
        cargarFxml("Pagos.fxml");
    }
    @FXML private void handleMenuVehiculos(MouseEvent e) {
        activarMenu(menuVehiculos);
        sectionTitle.setText("Vehículos");
        cargarFxml("Vehiculos.fxml");
    }
    @FXML private void handleMenuMantenimiento(MouseEvent e) {
        activarMenu(menuMantenimiento);
        sectionTitle.setText("Mantenimiento");
        cargarFxml("Mantenimiento.fxml");
    }
    @FXML private void handleMenuLimpieza(MouseEvent e) {
        activarMenu(menuLimpieza);
        sectionTitle.setText("Limpieza");
        cargarFxml("Limpieza.fxml");
    }
    @FXML private void handleMenuIncidencias(MouseEvent e) {
        activarMenu(menuIncidencias);
        sectionTitle.setText("Incidencias");
        cargarFxml("Incidencias.fxml");
    }
    @FXML private void handleMenuCompras(MouseEvent e) {
        activarMenu(menuCompras);
        sectionTitle.setText("Compras");
        cargarFxml("Compras.fxml");
    }
    @FXML private void handleMenuGps(MouseEvent e) {
        activarMenu(menuGps);
        sectionTitle.setText("GPS");
        cargarFxml("Gps.fxml");
    }
    @FXML private void handleMenuRegistros(MouseEvent e) {
        activarMenu(menuRegistros);
        sectionTitle.setText("Registros");
        cargarFxml("Registros.fxml");
    }
    @FXML private void handleMenuClientes(MouseEvent e) {
        activarMenu(menuClientes);
        sectionTitle.setText("Clientes");
        cargarFxml("Clientes.fxml");
    }

    // NUEVO HANDLER PARA SUPLIDORES
    @FXML private void handleMenuSuplidores(MouseEvent e) {
        activarMenu(menuSuplidores);
        sectionTitle.setText("Suplidores");
        cargarFxml("Suministrador.fxml");
    }

    // ── HOVER ────────────────────────────────────────────────
    @FXML private void onMenuEnter(MouseEvent e) {
        HBox item = (HBox) e.getSource();
        if (item == menuActivo) return;
        String estilo = item.getStyle();
        if (!estilo.contains("rgba(255,255,255,0.06)")) {
            item.setStyle(estilo + " -fx-background-color: rgba(255,255,255,0.06);");
        }
    }
    @FXML private void onMenuExit(MouseEvent e) {
        HBox item = (HBox) e.getSource();
        if (item == menuActivo) return;
        item.setStyle(item.getStyle().replace(" -fx-background-color: rgba(255,255,255,0.06);", ""));
    }
    @FXML private void onSubMenuEnter(MouseEvent e) {
        HBox item = (HBox) e.getSource();
        item.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 8 14 8 16; -fx-cursor: hand;");
    }
    @FXML private void onSubMenuExit(MouseEvent e) {
        HBox item = (HBox) e.getSource();
        item.setStyle("-fx-padding: 8 14 8 16; -fx-cursor: hand;");
    }

    // ── BUSCADOR ─────────────────────────────────────────────
    @FXML private void handleClearSearch(MouseEvent e) {
        searchField.clear();
        searchField.requestFocus();
    }

    // ── CARGA DE VISTAS ──────────────────────────────────────
    private void cargarFxml(String fxmlName) {
        try {
            URL resource = getClass().getResource(BASE + fxmlName);
            if (resource == null) {
                System.err.println("ERROR: No se encontró " + BASE + fxmlName);
                mostrarPlaceholder("No se encontró: " + fxmlName);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Node vista = loader.load();
            contentArea.getChildren().setAll(vista);
            if (vista instanceof Region region) {
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }
            System.out.println("Cargado correctamente: " + fxmlName);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarPlaceholder("Error: " + ex.getMessage());
        }
    }

    private void mostrarHome() {
        cargarFxml("Home.fxml");
    }

    private void mostrarPlaceholder(String mensaje) {
        VBox ph = new VBox(14);
        ph.setAlignment(javafx.geometry.Pos.CENTER);
        ph.setStyle("-fx-background-color: #eef2f8;");
        Label ico = new Label("🚧");
        ico.setStyle("-fx-font-size: 50px;");
        Label lbl = new Label(mensaje);
        lbl.setStyle("-fx-text-fill: #0d0d1a; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label sub = new Label("Módulo en construcción o archivo no encontrado");
        sub.setStyle("-fx-text-fill: #8aa0cc; -fx-font-size: 13px;");
        ph.getChildren().addAll(ico, lbl, sub);
        contentArea.getChildren().setAll(ph);
    }

    // ── API PÚBLICA para navegación externa ──────────────────
    public void navegarA(String titulo, HBox menuItem, String fxmlName) {
        sectionTitle.setText(titulo);
        activarMenu(menuItem);
        cargarFxml(fxmlName);
    }

    // Getters para los menús (opcional)
    public HBox getMenuInicio()        { return menuInicio; }
    public HBox getMenuReservacion()   { return menuReservacion; }
    public HBox getMenuEntregas()      { return menuEntregas; }
    public HBox getMenuDevolucion()    { return menuDevolucion; }
    public HBox getMenuPagos()         { return menuPagos; }
    public HBox getMenuVehiculos()     { return menuVehiculos; }
    public HBox getMenuMantenimiento() { return menuMantenimiento; }
    public HBox getMenuLimpieza()      { return menuLimpieza; }
    public HBox getMenuIncidencias()   { return menuIncidencias; }
    public HBox getMenuCompras()       { return menuCompras; }
    public HBox getMenuGps()           { return menuGps; }
    public HBox getMenuRegistros()     { return menuRegistros; }
    public HBox getMenuClientes()      { return menuClientes; }
    public HBox getMenuSuplidores()    { return menuSuplidores; }
}