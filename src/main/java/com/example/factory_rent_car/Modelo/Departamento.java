package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

public class Departamento {

    private final SimpleIntegerProperty idDepartamento = new SimpleIntegerProperty();
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleStringProperty telefono = new SimpleStringProperty();

    public Departamento() {}

    public Departamento(int idDepartamento, String nombre, String telefono) {
        this.idDepartamento.set(idDepartamento);
        this.nombre.set(nombre != null ? nombre : "");
        this.telefono.set(telefono != null ? telefono : "");
    }

    public SimpleIntegerProperty idDepartamentoProperty() { return idDepartamento; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty telefonoProperty() { return telefono; }

    public int getIdDepartamento() { return idDepartamento.get(); }
    public void setIdDepartamento(int v) { idDepartamento.set(v); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String v) { nombre.set(v); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String v) { telefono.set(v); }
}