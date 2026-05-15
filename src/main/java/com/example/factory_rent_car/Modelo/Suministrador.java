package com.example.factory_rent_car.Modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Suministrador {

    private final SimpleIntegerProperty idSuministrador = new SimpleIntegerProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleStringProperty correoElectronico = new SimpleStringProperty();
    private final SimpleStringProperty telefono = new SimpleStringProperty();
    private final SimpleStringProperty rnc = new SimpleStringProperty();
    private final SimpleIntegerProperty idDireccion = new SimpleIntegerProperty();
    private final SimpleStringProperty direccionCompleta = new SimpleStringProperty();

    public Suministrador(int idSuministrador, String tipo, String nombre, String correoElectronico,
                         String telefono, String rnc, int idDireccion, String direccionCompleta) {
        this.idSuministrador.set(idSuministrador);
        this.tipo.set(tipo != null ? tipo : "");
        this.nombre.set(nombre != null ? nombre : "");
        this.correoElectronico.set(correoElectronico != null ? correoElectronico : "");
        this.telefono.set(telefono != null ? telefono : "");
        this.rnc.set(rnc != null ? rnc : "");
        this.idDireccion.set(idDireccion);
        this.direccionCompleta.set(direccionCompleta != null ? direccionCompleta : "");
    }

    public SimpleIntegerProperty idSuministradorProperty() { return idSuministrador; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty correoElectronicoProperty() { return correoElectronico; }
    public SimpleStringProperty telefonoProperty() { return telefono; }
    public SimpleStringProperty rncProperty() { return rnc; }
    public SimpleIntegerProperty idDireccionProperty() { return idDireccion; }
    public SimpleStringProperty direccionCompletaProperty() { return direccionCompleta; }

    // Getters para acceso directo a los datos
    public int getIdSuministrador() { return idSuministrador.get(); }
    public String getTipo() { return tipo.get(); }
    public String getNombre() { return nombre.get(); }
    public String getCorreoElectronico() { return correoElectronico.get(); }
    public String getTelefono() { return telefono.get(); }
    public String getRnc() { return rnc.get(); }
    public int getIdDireccion() { return idDireccion.get(); }
    public String getDireccionCompleta() { return direccionCompleta.get(); }
}