package com.example.factory_rent_car.Database;

import java.sql.SQLException;

public class  Main {
    public static void main(String[] args) {
        Conexion objectoconexion = Conexion.getInstance();
        try {
            objectoconexion.establecerConexion();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
