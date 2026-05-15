package com.example.factory_rent_car.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection connection = null;

    private final String usuario = "Anneidy3";
    private final String contrase = "Anthony";
    private final String db = "FACTORYY";
    private final String server = "26.32.251.97";
    private final String puerto = "1433";

    private Conexion() {}

    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection establecerConexion() throws SQLException {
        String cadena = "jdbc:sqlserver://" + server + ":" + puerto + ";"
                + "databaseName=" + db + ";"
                + "encrypt=false;"
                                    + "loginTimeout=5;";

        System.out.println("Conectando a: " + server);
        connection = DriverManager.getConnection(cadena, usuario, contrase);
        System.out.println("Conexión exitosa");
        return connection;
    }

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada correctamente");
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
