package com.example.factory_rent_car.Controlador;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class HomeController {

    private MainLayoutController mainController;

    public void setMainController(MainLayoutController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void irARegistrarCliente(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Registro de Cliente", mainController.getMenuClientes(), "Clientes.fxml");
        }
    }

    @FXML
    private void irARegistrarReserva(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Reservación de Vehículos", mainController.getMenuReservacion(), "Reserva.fxml");
        }
    }

    @FXML
    private void irARegistrarIncidencia(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Registro de Incidencia", mainController.getMenuIncidencias(), "IncidenciaRegistro.fxml");
        }
    }

    @FXML
    private void irARegistrarEntrega(MouseEvent event) {
        if (mainController != null) {
            mainController.navegarA("Registro de Entrega", mainController.getMenuEntregas(), "EntregaVehiculo.fxml");
        }
    }

    // Efectos hover para las tarjetas
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