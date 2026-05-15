package com.example.factory_rent_car.Util;

import java.util.List;

public class AdminPermisoStrategy implements PermisoStrategy {
    @Override
    public List<String> getMenusOcultar() {
        return List.of();
    }
}
