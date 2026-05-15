package com.example.factory_rent_car.Util;

import java.util.List;

public class MecanicoPermisoStrategy implements PermisoStrategy {
    @Override
    public List<String> getMenusOcultar() {
        return List.of("menuReservacion", "menuGestionVehiculo", "menuPagos",
                "menuLimpieza", "menuIncidencias", "menuCompras",
                "menuReclamos", "menuRegistros", "menuClientes", "menuSuplidores", "menuVenta");
    }
}
