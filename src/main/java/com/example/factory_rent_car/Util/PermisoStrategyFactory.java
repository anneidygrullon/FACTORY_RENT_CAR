package com.example.factory_rent_car.Util;

public class PermisoStrategyFactory {
    public static PermisoStrategy getStrategy(String rol) {
        if (rol == null) rol = "admin";
        return switch (rol) {
            case "chofer" -> new ChoferPermisoStrategy();
            case "carwasher" -> new CarwasherPermisoStrategy();
            case "mecanico" -> new MecanicoPermisoStrategy();
            default -> new AdminPermisoStrategy();
        };
    }
}
