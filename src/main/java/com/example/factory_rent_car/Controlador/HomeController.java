package com.example.factory_rent_car.Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class HomeController {

    private MainLayoutController mainController;

    @FXML private Label lblUsuario;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML private void irAClientes(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Clientes", mainController.getMenuClientes(), "Clientes.fxml");
    }
    @FXML private void irAReservaVehiculo(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml");
    }
    @FXML private void irAReservaObjeto(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Reservación de Objetos", mainController.getMenuReservacion(), "ReservacionObjeto.fxml");
    }
    @FXML private void irAIncidencia(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml");
    }
    @FXML private void irAGestionVehiculo(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Gestión de Vehículo", mainController.getMenuGestionVehiculo(), "EntregaVehiculo.fxml");
    }
    @FXML private void irAVehiculos(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Consulta de Vehículos", mainController.getMenuVehiculos(), "VehiculoConsulta.fxml");
    }
    @FXML private void irAReclamos(MouseEvent event) {
        if (mainController != null) mainController.navegarA("Registro de Reclamo", mainController.getMenuReclamos(), "ReclamoRegistro.fxml");
    }

    @FXML
    private void onCardEnter(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        card.setStyle(card.getStyle() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        card.setCursor(javafx.scene.Cursor.HAND);
    }

    @FXML
    private void onCardExit(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); -fx-scale-x: 1.02; -fx-scale-y: 1.02;", ""));
        card.setCursor(javafx.scene.Cursor.DEFAULT);
    }
}
