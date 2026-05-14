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

    // ── SIDEBAR — menú items (principales) ───────────────────
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
    @FXML private HBox menuReclamos;        // Reemplaza a GPS
    @FXML private HBox menuRegistros;
    @FXML private HBox menuClientes;
    @FXML private HBox menuSuplidores;

    // ── SUBMENÚ RESERVACIÓN ──────────────────────────────────
    @FXML private VBox subMenuReservacion;
    @FXML private Label reservacionArrow;
    @FXML private HBox subMenuReservaVehiculo;
    @FXML private HBox subMenuReservaObjeto;

    // ── SUBMENÚ VEHÍCULOS ────────────────────────────────────
    @FXML private VBox subMenuVehiculos;
    @FXML private Label vehiculosArrow;
    @FXML private HBox subMenuVehiculoConsulta;
    @FXML private HBox subMenuVehiculoRegistro;

    // ── SUBMENÚ REGISTROS ────────────────────────────────────
    @FXML private VBox subMenuRegistros;
    @FXML private Label registrosArrow;
    @FXML private HBox subMenuEmpleadoConsulta;
    @FXML private HBox subMenuEmpleadoRegistro;
    @FXML private HBox subMenuConsultaDepartamento;

    // ── SUBMENÚ ENTREGAS ─────────────────────────────────────
    @FXML private VBox subMenuEntregas;
    @FXML private Label entregasArrow;
    @FXML private HBox subMenuRegistroEntrega;
    @FXML private HBox subMenuRegistroDireccion;

    // ── SUBMENÚ COMPRAS ──────────────────────────────────────
    @FXML private VBox subMenuCompras;
    @FXML private Label comprasArrow;
    @FXML private HBox subMenuCompraObjeto;
    @FXML private HBox subMenuConsultaObjetos;
    @FXML private HBox subMenuCompraVehiculo;

    // ── SUBMENÚ RECLAMOS ─────────────────────────────────────
    @FXML private VBox subMenuReclamos;
    @FXML private Label reclamosArrow;
    @FXML private HBox subMenuReclamoConsulta;
    @FXML private HBox subMenuReclamoRegistro;

    // ── BUSCADOR ─────────────────────────────────────────────
    @FXML private TextField searchField;

    // ── CONTENIDO PRINCIPAL ──────────────────────────────────
    @FXML private StackPane contentArea;

    // ── Estado ───────────────────────────────────────────────
    private HBox menuActivo;
    private boolean subMenuReservacionVisible = false;
    private boolean subMenuVehiculosVisible = false;
    private boolean subMenuRegistrosVisible = false;
    private boolean subMenuEntregasVisible = false;
    private boolean subMenuComprasVisible = false;
    private boolean subMenuReclamosVisible = false;

    private static final String BASE = "/com/example/factory_rent_car/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar submenús cerrados
        subMenuReservacion.setVisible(false);
        subMenuReservacion.setManaged(false);
        reservacionArrow.setText("❯");

        subMenuVehiculos.setVisible(false);
        subMenuVehiculos.setManaged(false);
        vehiculosArrow.setText("❯");

        subMenuRegistros.setVisible(false);
        subMenuRegistros.setManaged(false);
        registrosArrow.setText("❯");

        subMenuEntregas.setVisible(false);
        subMenuEntregas.setManaged(false);
        entregasArrow.setText("❯");

        subMenuCompras.setVisible(false);
        subMenuCompras.setManaged(false);
        comprasArrow.setText("❯");

        subMenuReclamos.setVisible(false);
        subMenuReclamos.setManaged(false);
        reclamosArrow.setText("❯");

        activarMenu(menuInicio);
        mostrarHome();
    }

    // ── HANDLERS DEL MENÚ PRINCIPAL ──────────────────────────
    @FXML private void handleMenuInicio(MouseEvent e) {
        activarMenu(menuInicio);
        sectionTitle.setText("Inicio");
        mostrarHome();
    }

    // ================= Submenú Reservación =================
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
        cerrarSubMenuReservacion();
    }

    @FXML private void handleSubMenuReservaObjeto(MouseEvent e) {
        activarMenu(menuReservacion);
        sectionTitle.setText("Reservación de Objetos");
        cargarFxml("ReservacionObjeto.fxml");
        cerrarSubMenuReservacion();
    }

    private void cerrarSubMenuReservacion() {
        subMenuReservacionVisible = false;
        subMenuReservacion.setVisible(false);
        subMenuReservacion.setManaged(false);
        reservacionArrow.setText("❯");
    }

    // ================= Submenú Entregas =================
    @FXML private void toggleSubMenuEntregas(MouseEvent e) {
        subMenuEntregasVisible = !subMenuEntregasVisible;
        subMenuEntregas.setVisible(subMenuEntregasVisible);
        subMenuEntregas.setManaged(subMenuEntregasVisible);
        entregasArrow.setText(subMenuEntregasVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuRegistroEntrega(MouseEvent e) {
        activarMenu(menuEntregas);
        sectionTitle.setText("Registro de Entrega");
        cargarFxml("EntregaVehiculo.fxml");
        cerrarSubMenuEntregas();
    }

    @FXML private void handleSubMenuRegistroDireccion(MouseEvent e) {
        activarMenu(menuEntregas);
        sectionTitle.setText("Registro de Dirección");
        cargarFxml("Direccion.fxml");
        cerrarSubMenuEntregas();
    }

    private void cerrarSubMenuEntregas() {
        subMenuEntregasVisible = false;
        subMenuEntregas.setVisible(false);
        subMenuEntregas.setManaged(false);
        entregasArrow.setText("❯");
    }

    // ================= Submenú Vehículos =================
    @FXML private void toggleSubMenuVehiculos(MouseEvent e) {
        subMenuVehiculosVisible = !subMenuVehiculosVisible;
        subMenuVehiculos.setVisible(subMenuVehiculosVisible);
        subMenuVehiculos.setManaged(subMenuVehiculosVisible);
        vehiculosArrow.setText(subMenuVehiculosVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuVehiculoConsulta(MouseEvent e) {
        activarMenu(menuVehiculos);
        sectionTitle.setText("Consulta de Vehículos");
        cargarFxml("VehiculoConsulta.fxml");
        cerrarSubMenuVehiculos();
    }

    @FXML private void handleSubMenuVehiculoRegistro(MouseEvent e) {
        activarMenu(menuVehiculos);
        sectionTitle.setText("Registro de Vehículos");
        cargarFxml("VehiculoRegistro.fxml");
        cerrarSubMenuVehiculos();
    }

    private void cerrarSubMenuVehiculos() {
        subMenuVehiculosVisible = false;
        subMenuVehiculos.setVisible(false);
        subMenuVehiculos.setManaged(false);
        vehiculosArrow.setText("❯");
    }

    // ================= Submenú Registros =================
    @FXML private void toggleSubMenuRegistros(MouseEvent e) {
        subMenuRegistrosVisible = !subMenuRegistrosVisible;
        subMenuRegistros.setVisible(subMenuRegistrosVisible);
        subMenuRegistros.setManaged(subMenuRegistrosVisible);
        registrosArrow.setText(subMenuRegistrosVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuEmpleadoConsulta(MouseEvent e) {
        activarMenu(menuRegistros);
        sectionTitle.setText("Consulta de Empleados");
        cargarFxml("EmpleadoConsulta.fxml");
        cerrarSubMenuRegistros();
    }

    @FXML private void handleSubMenuEmpleadoRegistro(MouseEvent e) {
        activarMenu(menuRegistros);
        sectionTitle.setText("Registro de Empleados");
        cargarFxml("EmpleadoRegistro.fxml");
        cerrarSubMenuRegistros();
    }

    @FXML private void handleSubMenuConsultaDepartamento(MouseEvent e) {
        activarMenu(menuRegistros);
        sectionTitle.setText("Consulta de Departamentos");
        cargarFxml("ConsultaDepartamento.fxml");
        cerrarSubMenuRegistros();
    }

    private void cerrarSubMenuRegistros() {
        subMenuRegistrosVisible = false;
        subMenuRegistros.setVisible(false);
        subMenuRegistros.setManaged(false);
        registrosArrow.setText("❯");
    }

    // ================= Submenú Compras =================
    @FXML private void toggleSubMenuCompras(MouseEvent e) {
        subMenuComprasVisible = !subMenuComprasVisible;
        subMenuCompras.setVisible(subMenuComprasVisible);
        subMenuCompras.setManaged(subMenuComprasVisible);
        comprasArrow.setText(subMenuComprasVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuCompraObjeto(MouseEvent e) {
        activarMenu(menuCompras);
        sectionTitle.setText("Registro de Compra - Objetos");
        cargarFxml("RegistroCompraObjeto.fxml");
        cerrarSubMenuCompras();
    }

    @FXML private void handleSubMenuConsultaObjetos(MouseEvent e) {
        activarMenu(menuCompras);
        sectionTitle.setText("Consulta de Objetos");
        cargarFxml("ConsultaObjetos.fxml");
        cerrarSubMenuCompras();
    }

    @FXML private void handleSubMenuCompraVehiculo(MouseEvent e) {
        activarMenu(menuCompras);
        sectionTitle.setText("Registro de Compra - Vehículos");
        cargarFxml("RegistroCompraVehiculo.fxml");
        cerrarSubMenuCompras();
    }

    private void cerrarSubMenuCompras() {
        subMenuComprasVisible = false;
        subMenuCompras.setVisible(false);
        subMenuCompras.setManaged(false);
        comprasArrow.setText("❯");
    }

    // ================= Submenú Reclamos =================
    @FXML private void toggleSubMenuReclamos(MouseEvent e) {
        subMenuReclamosVisible = !subMenuReclamosVisible;
        subMenuReclamos.setVisible(subMenuReclamosVisible);
        subMenuReclamos.setManaged(subMenuReclamosVisible);
        reclamosArrow.setText(subMenuReclamosVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuReclamoConsulta(MouseEvent e) {
        activarMenu(menuReclamos);
        sectionTitle.setText("Consulta de Reclamos");
        cargarFxml("ReclamoConsulta.fxml");
        cerrarSubMenuReclamos();
    }

    @FXML private void handleSubMenuReclamoRegistro(MouseEvent e) {
        activarMenu(menuReclamos);
        sectionTitle.setText("Registro de Reclamo");
        cargarFxml("ReclamoRegistro.fxml");
        cerrarSubMenuReclamos();
    }

    private void cerrarSubMenuReclamos() {
        subMenuReclamosVisible = false;
        subMenuReclamos.setVisible(false);
        subMenuReclamos.setManaged(false);
        reclamosArrow.setText("❯");
    }

    // ================= Otros menús (sin submenú) =================
    @FXML private void handleMenuDevolucion(MouseEvent e) {
        activarMenu(menuDevolucion);
        sectionTitle.setText("Devolución de Vehículos");
        cargarFxml("DevolucionVehiculo.fxml");
    }

    @FXML private void handleMenuPagos(MouseEvent e) {
        activarMenu(menuPagos);
        sectionTitle.setText("Pagos");
        cargarFxml("Pago.fxml");
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

    @FXML private void handleMenuClientes(MouseEvent e) {
        activarMenu(menuClientes);
        sectionTitle.setText("Clientes");
        cargarFxml("Clientes.fxml");
    }

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

    // ── ACTIVAR MENÚ (estilo) ────────────────────────────────
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

    // ── GETTERS PARA LOS MENÚS (opcional) ────────────────────
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
    public HBox getMenuReclamos()      { return menuReclamos; }
    public HBox getMenuRegistros()     { return menuRegistros; }
    public HBox getMenuClientes()      { return menuClientes; }
    public HBox getMenuSuplidores()    { return menuSuplidores; }
}