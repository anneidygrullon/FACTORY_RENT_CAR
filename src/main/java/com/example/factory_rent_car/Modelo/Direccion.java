package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

public class Direccion {

    private final SimpleIntegerProperty idDireccion = new SimpleIntegerProperty();
    private final SimpleStringProperty calleAvenida = new SimpleStringProperty();
    private final SimpleStringProperty numEdificioCasa = new SimpleStringProperty();
    private final SimpleStringProperty codigoPostal = new SimpleStringProperty();
    private final SimpleStringProperty referencia = new SimpleStringProperty();
    private final SimpleIntegerProperty idCiudad = new SimpleIntegerProperty();

    // Datos adicionales para mostrar en tabla
    private final SimpleStringProperty nombreCiudad = new SimpleStringProperty();

    public Direccion() {}

    public Direccion(int idDireccion, String calleAvenida, String numEdificioCasa,
                     String codigoPostal, String referencia, int idCiudad, String nombreCiudad) {
        this.idDireccion.set(idDireccion);
        this.calleAvenida.set(calleAvenida != null ? calleAvenida : "");
        this.numEdificioCasa.set(numEdificioCasa != null ? numEdificioCasa : "");
        this.codigoPostal.set(codigoPostal != null ? codigoPostal : "");
        this.referencia.set(referencia != null ? referencia : "");
        this.idCiudad.set(idCiudad);
        this.nombreCiudad.set(nombreCiudad != null ? nombreCiudad : "");
    }

    // Propiedades
    public SimpleIntegerProperty idDireccionProperty() { return idDireccion; }
    public SimpleStringProperty calleAvenidaProperty() { return calleAvenida; }
    public SimpleStringProperty numEdificioCasaProperty() { return numEdificioCasa; }
    public SimpleStringProperty codigoPostalProperty() { return codigoPostal; }
    public SimpleStringProperty referenciaProperty() { return referencia; }
    public SimpleIntegerProperty idCiudadProperty() { return idCiudad; }
    public SimpleStringProperty nombreCiudadProperty() { return nombreCiudad; }

    // Getters y Setters
    public int getIdDireccion() { return idDireccion.get(); }
    public void setIdDireccion(int v) { idDireccion.set(v); }
    public String getCalleAvenida() { return calleAvenida.get(); }
    public void setCalleAvenida(String v) { calleAvenida.set(v); }
    public String getNumEdificioCasa() { return numEdificioCasa.get(); }
    public void setNumEdificioCasa(String v) { numEdificioCasa.set(v); }
    public String getCodigoPostal() { return codigoPostal.get(); }
    public void setCodigoPostal(String v) { codigoPostal.set(v); }
    public String getReferencia() { return referencia.get(); }
    public void setReferencia(String v) { referencia.set(v); }
    public int getIdCiudad() { return idCiudad.get(); }
    public void setIdCiudad(int v) { idCiudad.set(v); }
    public String getNombreCiudad() { return nombreCiudad.get(); }
    public void setNombreCiudad(String v) { nombreCiudad.set(v); }

    // Método para obtener dirección completa como String
    public String getDireccionCompleta() {
        String direccion = calleAvenida.get();
        if (numEdificioCasa.get() != null && !numEdificioCasa.get().isEmpty()) {
            direccion += " " + numEdificioCasa.get();
        }
        if (nombreCiudad.get() != null && !nombreCiudad.get().isEmpty()) {
            direccion += ", " + nombreCiudad.get();
        }
        return direccion;
    }
}