package com.example.factory_rent_car.Util;

import java.util.List;

public class ChoferPermisoStrategy implements PermisoStrategy {
    @Override
    public List<String> getMenusOcultar() {
        return List.of("menuPagos", "menuMantenimiento", "menuLimpieza",
                "menuCompras", "menuRegistros", "menuSuplidores", "menuVenta");
    }
}
