package com.example.factory_rent_car.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
        Connection connection = null;

        String usuario = "Anneidy3";
        String contrase = "Anthony";
        String db = "FACTORYY";
        String server = "localhost";
        String puerto = "1433";

        public Connection establecerConexion() {
            try {
                String cadena = "jdbc:sqlserver://" + server + ":" + puerto + ";"
                        + "databaseName=" + db + ";"
                        + "encrypt=true;"
                        + "trustServerCertificate=true";
                connection = DriverManager.getConnection(cadena, usuario, contrase);
            } catch (Exception e) {
                System.err.println("Error en la conexion a la BD: " + e.getMessage());
            }
            return connection;
        }
    }

