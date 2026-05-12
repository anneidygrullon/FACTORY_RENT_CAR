package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Cliente {

    private final SimpleIntegerProperty idCliente = new SimpleIntegerProperty();
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleIntegerProperty edad = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fechaNacimiento = new SimpleObjectProperty<>();
    private final SimpleStringProperty correoElectronico = new SimpleStringProperty();
    private final SimpleStringProperty telefono = new SimpleStringProperty();
    private final SimpleStringProperty identificacion = new SimpleStringProperty();
    private final SimpleStringProperty licencia = new SimpleStringProperty();
    private final SimpleStringProperty nacionalidad = new SimpleStringProperty();
    private final SimpleStringProperty numPasaporte = new SimpleStringProperty();

    public Cliente() {}

    public Cliente(int idCliente, String nombre, int edad, LocalDate fechaNacimiento,
                   String correoElectronico, String telefono, String identificacion,
                   String licencia, String nacionalidad, String numPasaporte) {
        this.idCliente.set(idCliente);
        this.nombre.set(nombre != null ? nombre : "");
        this.edad.set(edad);
        this.fechaNacimiento.set(fechaNacimiento);
        this.correoElectronico.set(correoElectronico != null ? correoElectronico : "");
        this.telefono.set(telefono != null ? telefono : "");
        this.identificacion.set(identificacion != null ? identificacion : "");
        this.licencia.set(licencia != null ? licencia : "");
        this.nacionalidad.set(nacionalidad != null ? nacionalidad : "");
        this.numPasaporte.set(numPasaporte != null ? numPasaporte : "");
    }

    // Propiedades para TableView
    public SimpleIntegerProperty idClienteProperty() { return idCliente; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleIntegerProperty edadProperty() { return edad; }
    public SimpleObjectProperty<LocalDate> fechaNacimientoProperty() { return fechaNacimiento; }
    public SimpleStringProperty correoElectronicoProperty() { return correoElectronico; }
    public SimpleStringProperty telefonoProperty() { return telefono; }
    public SimpleStringProperty identificacionProperty() { return identificacion; }
    public SimpleStringProperty licenciaProperty() { return licencia; }
    public SimpleStringProperty nacionalidadProperty() { return nacionalidad; }
    public SimpleStringProperty numPasaporteProperty() { return numPasaporte; }

    // Getters y Setters
    public int getIdCliente() { return idCliente.get(); }
    public void setIdCliente(int v) { idCliente.set(v); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String v) { nombre.set(v); }
    public int getEdad() { return edad.get(); }
    public void setEdad(int v) { edad.set(v); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento.get(); }
    public void setFechaNacimiento(LocalDate v) { fechaNacimiento.set(v); }
    public String getCorreoElectronico() { return correoElectronico.get(); }
    public void setCorreoElectronico(String v) { correoElectronico.set(v); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String v) { telefono.set(v); }
    public String getIdentificacion() { return identificacion.get(); }
    public void setIdentificacion(String v) { identificacion.set(v); }
    public String getLicencia() { return licencia.get(); }
    public void setLicencia(String v) { licencia.set(v); }
    public String getNacionalidad() { return nacionalidad.get(); }
    public void setNacionalidad(String v) { nacionalidad.set(v); }
    public String getNumPasaporte() { return numPasaporte.get(); }
    public void setNumPasaporte(String v) { numPasaporte.set(v); }
}