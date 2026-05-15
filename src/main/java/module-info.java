module com.example.factory_rent_car {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires javafx.base;
    requires java.desktop;
    requires jakarta.mail;
    requires javafx.swing;
    requires org.jfree.jfreechart;

    opens com.example.factory_rent_car to javafx.fxml;
    exports com.example.factory_rent_car;
    opens com.example.factory_rent_car.Controlador to javafx.fxml;
    exports com.example.factory_rent_car.Controlador;
}