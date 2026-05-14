package com.example.factory_rent_car;

import com.example.factory_rent_car.Controlador.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(loader.load());
        LoginController loginController = loader.getController();
        loginController.setStage(stage);
        stage.setTitle("Factory Rent Car - Iniciar Sesión");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
