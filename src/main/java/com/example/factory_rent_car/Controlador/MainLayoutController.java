package com.example.factory_rent_car.Controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import com.example.factory_rent_car.Util.PermisoStrategy;
import com.example.factory_rent_car.Util.PermisoStrategyFactory;

public class MainLayoutController implements Initializable {

    private static final String PROFILES_DIR = "profiles";

    // Top bar
    @FXML private Label sectionTitle;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private ImageView profileImageView;
    @FXML private HBox userArea;

    // Sidebar — menú items (principales)
    @FXML private HBox menuInicio;
    @FXML private HBox menuReservacion;
    @FXML private HBox menuGestionVehiculo;
    @FXML private HBox menuPagos;
    @FXML private HBox menuVehiculos;
    @FXML private HBox menuMantenimiento;
    @FXML private HBox menuLimpieza;
    @FXML private HBox menuIncidencias;
    @FXML private HBox menuCompras;
    @FXML private HBox menuReclamos;
    @FXML private HBox menuRegistros;
    @FXML private HBox menuClientes;
    @FXML private HBox menuSuplidores;
    @FXML private HBox menuVenta;

    // Submenú reservación
    @FXML private VBox subMenuReservacion;
    @FXML private Label reservacionArrow;
    @FXML private HBox subMenuReservaVehiculo;
    @FXML private HBox subMenuReservaObjeto;

    // Submenú vehículos
    @FXML private VBox subMenuVehiculos;
    @FXML private Label vehiculosArrow;
    @FXML private HBox subMenuVehiculoConsulta;

    // Submenú registros
    @FXML private VBox subMenuRegistros;
    @FXML private Label registrosArrow;
    @FXML private HBox subMenuEmpleadoConsulta;
    @FXML private HBox subMenuEmpleadoRegistro;
    @FXML private HBox subMenuConsultaDepartamento;
    @FXML private HBox subMenuConsultaUsuario;

    // Submenú gestión de vehículo
    @FXML private VBox subMenuGestionVehiculo;
    @FXML private Label gestionVehiculoArrow;
    @FXML private HBox subMenuGestionVehiculoItem;
    @FXML private HBox subMenuRegistroDireccion;

    // Submenú compras
    @FXML private VBox subMenuCompras;
    @FXML private Label comprasArrow;
    @FXML private HBox subMenuCompraObjeto;
    @FXML private HBox subMenuConsultaObjetos;
    @FXML private HBox subMenuCompraVehiculo;

    // Submenú reclamos
    @FXML private VBox subMenuReclamos;
    @FXML private Label reclamosArrow;
    @FXML private HBox subMenuReclamoConsulta;
    @FXML private HBox subMenuReclamoRegistro;

    // Submenú incidencias
    @FXML private VBox subMenuIncidencias;
    @FXML private Label incidenciasArrow;
    @FXML private HBox subMenuIncidenciaConsulta;
    @FXML private HBox subMenuIncidenciaRegistro;

    // Submenú venta
    @FXML private VBox subMenuVenta;
    @FXML private Label ventaArrow;
    @FXML private HBox subMenuVentaConsulta;
    @FXML private HBox subMenuVentaRegistro;

    // Submenú pagos
    @FXML private VBox subMenuPagos;
    @FXML private Label pagosArrow;
    @FXML private HBox subMenuPagoConsulta;
    @FXML private HBox subMenuPagoRegistro;
    @FXML private HBox subMenuNotaCredito;

    // Contenido principal
    @FXML private StackPane contentArea;

    // Estado
    private HBox menuActivo;
    private boolean subMenuReservacionVisible = false;
    private boolean subMenuVehiculosVisible = false;
    private boolean subMenuRegistrosVisible = false;
    private boolean subMenuGestionVehiculoVisible = false;
    private boolean subMenuComprasVisible = false;
    private boolean subMenuReclamosVisible = false;
    private boolean subMenuIncidenciasVisible = false;
    private boolean subMenuPagosVisible = false;
    private boolean subMenuVentaVisible = false;

    private static final String BASE = "/com/example/factory_rent_car/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        subMenuReservacion.setVisible(false);
        subMenuReservacion.setManaged(false);
        reservacionArrow.setText("❯");

        subMenuVehiculos.setVisible(false);
        subMenuVehiculos.setManaged(false);
        vehiculosArrow.setText("❯");

        subMenuRegistros.setVisible(false);
        subMenuRegistros.setManaged(false);
        registrosArrow.setText("❯");

        subMenuGestionVehiculo.setVisible(false);
        subMenuGestionVehiculo.setManaged(false);
        gestionVehiculoArrow.setText("❯");

        subMenuCompras.setVisible(false);
        subMenuCompras.setManaged(false);
        comprasArrow.setText("❯");

        subMenuReclamos.setVisible(false);
        subMenuReclamos.setManaged(false);
        reclamosArrow.setText("❯");

        subMenuIncidencias.setVisible(false);
        subMenuIncidencias.setManaged(false);
        incidenciasArrow.setText("❯");

        subMenuPagos.setVisible(false);
        subMenuPagos.setManaged(false);
        pagosArrow.setText("❯");

        subMenuVenta.setVisible(false);
        subMenuVenta.setManaged(false);
        ventaArrow.setText("❯");

        activarMenu(menuInicio);
        mostrarHome();
    }

    // Handlers del menú principal
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

    // ================= Submenú Gestión de Vehículo =================
    @FXML private void toggleSubMenuGestionVehiculo(MouseEvent e) {
        subMenuGestionVehiculoVisible = !subMenuGestionVehiculoVisible;
        subMenuGestionVehiculo.setVisible(subMenuGestionVehiculoVisible);
        subMenuGestionVehiculo.setManaged(subMenuGestionVehiculoVisible);
        gestionVehiculoArrow.setText(subMenuGestionVehiculoVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuGestionVehiculo(MouseEvent e) {
        activarMenu(menuGestionVehiculo);
        sectionTitle.setText("Gestión de Vehículo");
        cargarFxml("EntregaVehiculo.fxml");
        cerrarSubMenuGestionVehiculo();
    }

    @FXML private void handleSubMenuRegistroDireccion(MouseEvent e) {
        activarMenu(menuGestionVehiculo);
        sectionTitle.setText("Dirección");
        cargarFxml("Direccion.fxml");
        cerrarSubMenuGestionVehiculo();
    }

    private void cerrarSubMenuGestionVehiculo() {
        subMenuGestionVehiculoVisible = false;
        subMenuGestionVehiculo.setVisible(false);
        subMenuGestionVehiculo.setManaged(false);
        gestionVehiculoArrow.setText("❯");
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

    @FXML private void handleSubMenuConsultaUsuario(MouseEvent e) {
        activarMenu(menuRegistros);
        sectionTitle.setText("Usuarios del Sistema");
        cargarFxml("UsuarioConsulta.fxml");
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

    // ================= Submenú Incidencias =================
    @FXML private void toggleSubMenuIncidencias(MouseEvent e) {
        subMenuIncidenciasVisible = !subMenuIncidenciasVisible;
        subMenuIncidencias.setVisible(subMenuIncidenciasVisible);
        subMenuIncidencias.setManaged(subMenuIncidenciasVisible);
        incidenciasArrow.setText(subMenuIncidenciasVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuIncidenciaConsulta(MouseEvent e) {
        activarMenu(menuIncidencias);
        sectionTitle.setText("Consulta de Incidencias");
        cargarFxml("IncidenciaConsulta.fxml");
        cerrarSubMenuIncidencias();
    }

    @FXML private void handleSubMenuIncidenciaRegistro(MouseEvent e) {
        activarMenu(menuIncidencias);
        sectionTitle.setText("Registro de Incidencia");
        cargarFxml("IncidenciaRegistro.fxml");
        cerrarSubMenuIncidencias();
    }

    private void cerrarSubMenuIncidencias() {
        subMenuIncidenciasVisible = false;
        subMenuIncidencias.setVisible(false);
        subMenuIncidencias.setManaged(false);
        incidenciasArrow.setText("❯");
    }

    // ================= Submenú Pagos =================
    @FXML private void toggleSubMenuPagos(MouseEvent e) {
        subMenuPagosVisible = !subMenuPagosVisible;
        subMenuPagos.setVisible(subMenuPagosVisible);
        subMenuPagos.setManaged(subMenuPagosVisible);
        pagosArrow.setText(subMenuPagosVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuPagoConsulta(MouseEvent e) {
        activarMenu(menuPagos);
        sectionTitle.setText("Consulta de Pagos");
        cargarFxml("PagoConsulta.fxml");
        cerrarSubMenuPagos();
    }

    @FXML private void handleSubMenuPagoRegistro(MouseEvent e) {
        activarMenu(menuPagos);
        sectionTitle.setText("Registro de Pago");
        cargarFxml("PagoRegistro.fxml");
        cerrarSubMenuPagos();
    }

    @FXML private void handleSubMenuNotaCredito(MouseEvent e) {
        activarMenu(menuPagos);
        sectionTitle.setText("Notas de Crédito");
        cargarFxml("NotaCredito.fxml");
        cerrarSubMenuPagos();
    }

    private void cerrarSubMenuPagos() {
        subMenuPagosVisible = false;
        subMenuPagos.setVisible(false);
        subMenuPagos.setManaged(false);
        pagosArrow.setText("❯");
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

    // ================= Submenú Venta =================
    @FXML private void toggleSubMenuVenta(MouseEvent e) {
        subMenuVentaVisible = !subMenuVentaVisible;
        subMenuVenta.setVisible(subMenuVentaVisible);
        subMenuVenta.setManaged(subMenuVentaVisible);
        ventaArrow.setText(subMenuVentaVisible ? "❯" : "❮");
    }

    @FXML private void handleSubMenuVentaConsulta(MouseEvent e) {
        activarMenu(menuVenta);
        sectionTitle.setText("Consulta de Ventas");
        cargarFxml("VentaConsulta.fxml");
        cerrarSubMenuVenta();
    }

    @FXML private void handleSubMenuVentaRegistro(MouseEvent e) {
        activarMenu(menuVenta);
        sectionTitle.setText("Registro de Venta");
        cargarFxml("VentaRegistro.fxml");
        cerrarSubMenuVenta();
    }

    private void cerrarSubMenuVenta() {
        subMenuVentaVisible = false;
        subMenuVenta.setVisible(false);
        subMenuVenta.setManaged(false);
        ventaArrow.setText("❯");
    }

    // Hover
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

    // Hover top bar
    @FXML private void onNotificationEnter(MouseEvent e) {
        StackPane s = (StackPane) e.getSource();
        s.setStyle("-fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 8; -fx-background-color: rgba(255,255,255,0.1);");
    }
    @FXML private void onNotificationExit(MouseEvent e) {
        StackPane s = (StackPane) e.getSource();
        s.setStyle("-fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 8; -fx-background-color: rgba(255,255,255,0.05);");
    }
    @FXML private void onUserAreaEnter(MouseEvent e) {
        userArea.setStyle("-fx-cursor: hand; -fx-padding: 4 8 4 4; -fx-background-radius: 10; -fx-background-color: rgba(255,255,255,0.08);");
    }
    @FXML private void onUserAreaExit(MouseEvent e) {
        userArea.setStyle("-fx-cursor: hand; -fx-padding: 4 8 4 4; -fx-background-radius: 10; -fx-background-color: rgba(255,255,255,0.04);");
    }
    @FXML private void onLogoutEnter(MouseEvent e) {
        StackPane s = (StackPane) e.getSource();
        s.setStyle("-fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 8; -fx-background-color: rgba(255,70,70,0.15);");
    }
    @FXML private void onLogoutExit(MouseEvent e) {
        StackPane s = (StackPane) e.getSource();
        s.setStyle("-fx-cursor: hand; -fx-padding: 6; -fx-background-radius: 8; -fx-background-color: rgba(255,255,255,0.05);");
    }

    // Inyecta el MainLayoutController en los controladores
    private void injectMainController(Object controller) {
        try {
            Method method = controller.getClass().getMethod("setMainController", MainLayoutController.class);
            method.invoke(controller, this);
        } catch (NoSuchMethodException e) {
            // El controlador no requiere inyección
        } catch (Exception e) {
            System.err.println("Error inyectando MainController: " + e.getMessage());
        }
    }

    // Carga de vistas (con inyección automática del controlador principal)
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
            injectMainController(loader.getController());
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
        try {
            URL resource = getClass().getResource(BASE + "Home.fxml");
            if (resource == null) {
                mostrarPlaceholder("No se encontró Home.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Node home = loader.load();

            // Le pasamos la referencia del MainLayoutController al HomeController
            HomeController homeController = loader.getController();
            homeController.setMainController(this);

            contentArea.getChildren().setAll(home);
            if (home instanceof Region region) {
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }
            System.out.println("Home cargado correctamente");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarPlaceholder("Error cargando Home");
        }
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

    // API pública para navegación externa
    public void navegarA(String titulo, HBox menuItem, String fxmlName) {
        sectionTitle.setText(titulo);
        activarMenu(menuItem);
        cargarFxml(fxmlName);
    }

    // Activar menú (estilo)
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

    // Getters para los menús (necesarios para navegación desde Home)
    public HBox getMenuInicio()        { return menuInicio; }
    public HBox getMenuReservacion()   { return menuReservacion; }
    public HBox getMenuGestionVehiculo() { return menuGestionVehiculo; }
    public HBox getMenuPagos()           { return menuPagos; }
    public HBox getMenuVehiculos()     { return menuVehiculos; }
    public HBox getMenuMantenimiento() { return menuMantenimiento; }
    public HBox getMenuLimpieza()      { return menuLimpieza; }
    public HBox getMenuIncidencias()   { return menuIncidencias; }
    public HBox getMenuCompras()       { return menuCompras; }
    public HBox getMenuReclamos()      { return menuReclamos; }
    public HBox getMenuRegistros()     { return menuRegistros; }
    public HBox getMenuClientes()      { return menuClientes; }
    public HBox getMenuSuplidores()    { return menuSuplidores; }
    public HBox getMenuVenta()         { return menuVenta; }
    public HBox getSubMenuPagoConsulta() { return subMenuPagoConsulta; }
    public HBox getSubMenuPagoRegistro() { return subMenuPagoRegistro; }
    public HBox getSubMenuNotaCredito()  { return subMenuNotaCredito; }

    private String nombreUsuario;
    private String rolUsuario;

    public void setUsuarioActual(String usuario, String rol) {
        this.nombreUsuario = usuario;
        this.rolUsuario = rol;
        lblRol.setText(usuario);
        lblUsuario.setText(usuario);
        cargarFotoPerfil();
        aplicarPermisos(rol);
    }

    private void ocultar(Node... nodos) {
        for (Node n : nodos) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(false);
            }
        }
    }

    private void aplicarPermisos(String rol) {
        PermisoStrategy strategy = PermisoStrategyFactory.getStrategy(rol);
        Map<String, Node> menuMap = new HashMap<>();
        menuMap.put("menuReservacion", menuReservacion);
        menuMap.put("menuGestionVehiculo", menuGestionVehiculo);
        menuMap.put("menuPagos", menuPagos);
        menuMap.put("menuMantenimiento", menuMantenimiento);
        menuMap.put("menuLimpieza", menuLimpieza);
        menuMap.put("menuIncidencias", menuIncidencias);
        menuMap.put("menuCompras", menuCompras);
        menuMap.put("menuReclamos", menuReclamos);
        menuMap.put("menuRegistros", menuRegistros);
        menuMap.put("menuClientes", menuClientes);
        menuMap.put("menuSuplidores", menuSuplidores);
        menuMap.put("menuVenta", menuVenta);
        for (String nombre : strategy.getMenusOcultar()) {
            Node n = menuMap.get(nombre);
            if (n != null) ocultar(n);
        }
    }

    @FXML
    private void handleUserMenu(MouseEvent e) {
        ContextMenu menu = new ContextMenu();

        MenuItem cambiarFoto = new MenuItem("Cambiar foto de perfil");
        cambiarFoto.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 13px;");
        cambiarFoto.setOnAction(ev -> cambiarFotoPerfil());

        MenuItem cerrarSesion = new MenuItem("Cerrar sesión");
        cerrarSesion.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 13px;");
        cerrarSesion.setOnAction(ev -> handleLogout(null));

        menu.getItems().addAll(cambiarFoto, new SeparatorMenuItem(), cerrarSesion);
        menu.show(userArea, e.getScreenX(), e.getScreenY());
    }

    @FXML
    private void handleLogout(MouseEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/factory_rent_car/Login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController loginController = loader.getController();
            Stage stage = (Stage) userArea.getScene().getWindow();
            loginController.setStage(stage);
            stage.setTitle("Factory Rent Car - Iniciar Sesión");
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cambiarFotoPerfil() {
        Stage stage = (Stage) userArea.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            try {
                File dir = new File(PROFILES_DIR);
                if (!dir.exists()) dir.mkdirs();
                String extension = archivo.getName().substring(archivo.getName().lastIndexOf('.'));
                File destino = new File(PROFILES_DIR, nombreUsuario + extension);
                Files.copy(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                cargarFotoPerfil();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void cargarFotoPerfil() {
        if (nombreUsuario == null) return;
        File dir = new File(PROFILES_DIR);
        if (!dir.exists()) return;
        File[] matching = dir.listFiles((d, name) -> name.startsWith(nombreUsuario + "."));
        if (matching != null && matching.length > 0) {
            Image img = new Image(matching[0].toURI().toString(), 36, 36, true, true);
            profileImageView.setImage(img);
            Circle clip = new Circle(18, 18, 18);
            profileImageView.setClip(clip);
        }
    }
}