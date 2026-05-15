package com.example.factory_rent_car.Controlador;

import com.example.factory_rent_car.Database.Conexion;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class HomeController {

    private MainLayoutController mainController;

    @FXML private Label lblUsuario;
    @FXML private StackPane chartVehiculos;
    @FXML private StackPane chartReservaciones;

    private final Conexion conexion = Conexion.getInstance();

    private static final Color
            AZUL_PROFUNDO  = new Color(0x1E3A5F),
            AZUL_PRIMARY   = new Color(0x2B6FBC),
            AZUL_CLARO     = new Color(0x5B9BD5),
            VERDE          = new Color(0x27AE60),
            NARANJA        = new Color(0xE67E22),
            ROJO           = new Color(0xC0392B),
            TEAL           = new Color(0x1ABC9C),
            MORADO         = new Color(0x8E44AD),
            GRIS_TEXTO     = new Color(0x5D6D7E),
            GRIS_LINEA     = new Color(0xE8ECF1),
            FONDO          = new Color(255, 255, 255, 0),
            BLANCO_HUESO   = new Color(0xF8F9FA),
            AZUL_GRADIENTE_1 = new Color(0x2B6FBC),
            AZUL_GRADIENTE_2 = new Color(0x5B9BD5);

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            cargarGraficoVehiculos();
            cargarGraficoReservaciones();
        });
    }

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    private void cargarGraficoVehiculos() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String sql = "SELECT ISNULL(estado, 'Sin estado') AS estado, COUNT(*) AS total FROM TBL_VEHICULO GROUP BY estado ORDER BY total DESC";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) dataset.setValue(rs.getString("estado"), rs.getInt("total"));
        } catch (SQLException e) {
            dataset.setValue("Sin datos", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        chart.setBackgroundPaint(FONDO);
        chart.setAntiAlias(true);
        chart.setBorderPaint(null);
        chart.setPadding(new RectangleInsets(2, 2, 2, 2));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(FONDO);
        plot.setOutlinePaint(null);
        plot.setOutlineStroke(null);
        plot.setShadowPaint(null);
        plot.setShadowXOffset(0);
        plot.setShadowYOffset(0);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("#"), new DecimalFormat("0%")));
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.setLabelPaint(GRIS_TEXTO);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 220));
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(new Color(0xE8ECF1));
        plot.setSimpleLabels(true);
        plot.setInteriorGap(0.06);
        plot.setCircular(true);
        plot.setMinimumArcAngleToDraw(0.5);
        plot.setIgnoreNullValues(true);
        plot.setIgnoreZeroValues(true);
        plot.setBackgroundAlpha(0f);
        plot.setForegroundAlpha(0.95f);

        Color[] colores = {AZUL_PROFUNDO, AZUL_PRIMARY, VERDE, NARANJA, ROJO, TEAL, MORADO, AZUL_CLARO};
        int i = 0;
        for (Object k : dataset.getKeys()) {
            plot.setSectionPaint((Comparable<?>) k, colores[i % colores.length]);
            i++;
        }

        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
        chart.getLegend().setBackgroundPaint(FONDO);
        chart.getLegend().setBorder(0, 0, 0, 0);
        chart.removeLegend();

        embed(chart, chartVehiculos);
    }

    private void cargarGraficoReservaciones() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT MONTH(fecha_inicio) AS mes, COUNT(*) AS total FROM TBL_RESERVACION WHERE YEAR(fecha_inicio) = YEAR(GETDATE()) GROUP BY MONTH(fecha_inicio) ORDER BY mes";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("total"), "R", obtenerNombreMes(rs.getInt("mes")));
            }
        } catch (SQLException e) {
            dataset.addValue(0, "R", "Error");
        }

        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(FONDO);
        chart.setAntiAlias(true);
        chart.setBorderPaint(null);
        chart.setPadding(new RectangleInsets(2, 2, 2, 2));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(BLANCO_HUESO);
        plot.setRangeGridlinePaint(GRIS_LINEA);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));
        plot.setDomainGridlinePaint(GRIS_LINEA);
        plot.setDomainGridlineStroke(new BasicStroke(0.5f));
        plot.setOutlinePaint(null);
        plot.setOutlineStroke(null);
        plot.setAxisOffset(new RectangleInsets(6, 6, 6, 6));
        plot.setInsets(new RectangleInsets(0, 0, 0, 0));

        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getDomainAxis().setTickLabelPaint(GRIS_TEXTO);
        plot.getDomainAxis().setAxisLinePaint(GRIS_LINEA);
        plot.getDomainAxis().setAxisLineStroke(new BasicStroke(0.5f));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        plot.getDomainAxis().setLabelPaint(AZUL_PROFUNDO);
        plot.getDomainAxis().setLowerMargin(0.02);
        plot.getDomainAxis().setUpperMargin(0.02);
        plot.getDomainAxis().setCategoryMargin(0.15);

        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getRangeAxis().setTickLabelPaint(GRIS_TEXTO);
        plot.getRangeAxis().setAxisLinePaint(GRIS_LINEA);
        plot.getRangeAxis().setAxisLineStroke(new BasicStroke(0.5f));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        plot.getRangeAxis().setLabelPaint(AZUL_PROFUNDO);
        plot.getRangeAxis().setLowerMargin(0.02);
        plot.getRangeAxis().setUpperMargin(0.05);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.07);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, AZUL_PRIMARY);
        renderer.setDefaultPaint(new GradientPaint(
                0f, 0f, AZUL_GRADIENTE_1, 0f, 0f, AZUL_GRADIENTE_2));
        renderer.setDefaultStroke(new BasicStroke(0.5f));

        embed(chart, chartReservaciones);
    }

    private void embed(JFreeChart chart, StackPane container) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(FONDO);
        panel.setMinimumSize(new Dimension(0, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.setPreferredSize(new Dimension((int) container.getWidth(), (int) container.getHeight()));
        panel.setMouseZoomable(false);
        panel.setPopupMenu(null);

        SwingNode node = new SwingNode();
        node.setContent(panel);

        container.getChildren().setAll(node);
        container.widthProperty().addListener((o, ov, nv) -> {
            panel.setPreferredSize(new Dimension(nv.intValue(), (int) container.getHeight()));
            panel.doLayout();
        });
        container.heightProperty().addListener((o, ov, nv) -> {
            panel.setPreferredSize(new Dimension((int) container.getWidth(), nv.intValue()));
            panel.doLayout();
        });
    }

    private String obtenerNombreMes(int mes) {
        return switch (mes) {
            case 1 -> "Ene"; case 2 -> "Feb"; case 3 -> "Mar";
            case 4 -> "Abr"; case 5 -> "May"; case 6 -> "Jun";
            case 7 -> "Jul"; case 8 -> "Ago"; case 9 -> "Sep";
            case 10 -> "Oct"; case 11 -> "Nov"; case 12 -> "Dic";
            default -> "Mes " + mes;
        };
    }

    @FXML private void irAClientes(MouseEvent e) { if (mainController != null) mainController.navegarA("Clientes", mainController.getMenuClientes(), "Clientes.fxml"); }
    @FXML private void irAReservaVehiculo(MouseEvent e) { if (mainController != null) mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml"); }
    @FXML private void irAReservaObjeto(MouseEvent e) { if (mainController != null) mainController.navegarA("Reservación de Objetos", mainController.getMenuReservacion(), "ReservacionObjeto.fxml"); }
    @FXML private void irAIncidencia(MouseEvent e) { if (mainController != null) mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml"); }
    @FXML private void irAGestionVehiculo(MouseEvent e) { if (mainController != null) mainController.navegarA("Gestión de Vehículo", mainController.getMenuGestionVehiculo(), "EntregaVehiculo.fxml"); }
    @FXML private void irAVehiculos(MouseEvent e) { if (mainController != null) mainController.navegarA("Consulta de Vehículos", mainController.getMenuVehiculos(), "VehiculoConsulta.fxml"); }
    @FXML private void irAReclamos(MouseEvent e) { if (mainController != null) mainController.navegarA("Registro de Reclamo", mainController.getMenuReclamos(), "ReclamoRegistro.fxml"); }

    @FXML private void onCardEnter(MouseEvent e) {
        VBox card = (VBox) e.getSource();
        card.setStyle(card.getStyle() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        card.setCursor(javafx.scene.Cursor.HAND);
    }
    @FXML private void onCardExit(MouseEvent e) {
        VBox card = (VBox) e.getSource();
        card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;", ""));
        card.setCursor(javafx.scene.Cursor.DEFAULT);
    }
}
