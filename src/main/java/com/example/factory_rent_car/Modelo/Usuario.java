package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

public class Usuario {
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty rol = new SimpleStringProperty();

    public Usuario(String nombre, String rol) {
        this.nombre.set(nombre);
        this.rol.set(rol);
    }

    public StringProperty nombreProperty() { return nombre; }
    public String getNombre() { return nombre.get(); }

    public StringProperty rolProperty() { return rol; }
    public String getRol() { return rol.get(); }
    public void setRol(String v) { rol.set(v); }
}
