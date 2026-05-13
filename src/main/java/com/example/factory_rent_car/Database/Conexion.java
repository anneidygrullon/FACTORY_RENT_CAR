package com.example.factory_rent_car.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    private Connection connection = null;

    private String usuario = "Anneidy3";
    private String contrase = "Anthony";
    private String db = "FACTORYY";
    private String server = "26.32.251.97";
    private String puerto = "1433";

    public Connection establecerConexion() {
        try {
            String cadena = "jdbc:sqlserver://" + server + ":" + puerto + ";"
                    + "databaseName=" + db + ";"
                    + "encrypt=false;"
                    + "loginTimeout=30;";

            System.out.println("Conectando a: " + server);
            connection = DriverManager.getConnection(cadena, usuario, contrase);
            System.out.println("Conexión exitosa");
            return connection;
        } catch (Exception e) {
            System.err.println("Error en la conexion: " + e.getMessage());
            return null;
        }
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