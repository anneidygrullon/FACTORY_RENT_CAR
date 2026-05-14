package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Limpieza {

    private final SimpleIntegerProperty idLimpieza = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleStringProperty observaciones = new SimpleStringProperty();
    private final SimpleIntegerProperty idVehiculo = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idEmpleado = new SimpleIntegerProperty();

    // Datos adicionales para mostrar en tabla (JOIN)
    private final SimpleStringProperty vehiculoInfo = new SimpleStringProperty();
    private final SimpleStringProperty empleadoNombre = new SimpleStringProperty();

    public Limpieza() {}

    public Limpieza(int idLimpieza, LocalDate fecha, String tipo, String observaciones,
                    int idVehiculo, int idEmpleado, String vehiculoInfo, String empleadoNombre) {
        this.idLimpieza.set(idLimpieza);
        this.fecha.set(fecha);
        this.tipo.set(tipo != null ? tipo : "");
        this.observaciones.set(observaciones != null ? observaciones : "");
        this.idVehiculo.set(idVehiculo);
        this.idEmpleado.set(idEmpleado);
        this.vehiculoInfo.set(vehiculoInfo != null ? vehiculoInfo : "");
        this.empleadoNombre.set(empleadoNombre != null ? empleadoNombre : "");
    }

    // Propiedades
    public SimpleIntegerProperty idLimpiezaProperty() { return idLimpieza; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty observacionesProperty() { return observaciones; }
    public SimpleIntegerProperty idVehiculoProperty() { return idVehiculo; }
    public SimpleIntegerProperty idEmpleadoProperty() { return idEmpleado; }
    public SimpleStringProperty vehiculoInfoProperty() { return vehiculoInfo; }
    public SimpleStringProperty empleadoNombreProperty() { return empleadoNombre; }

    // Getters y Setters
    public int getIdLimpieza() { return idLimpieza.get(); }
    public void setIdLimpieza(int v) { idLimpieza.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public String getObservaciones() { return observaciones.get(); }
    public void setObservaciones(String v) { observaciones.set(v); }
    public int getIdVehiculo() { return idVehiculo.get(); }
    public void setIdVehiculo(int v) { idVehiculo.set(v); }
    public int getIdEmpleado() { return idEmpleado.get(); }
    public void setIdEmpleado(int v) { idEmpleado.set(v); }
    public String getVehiculoInfo() { return vehiculoInfo.get(); }
    public void setVehiculoInfo(String v) { vehiculoInfo.set(v); }
    public String getEmpleadoNombre() { return empleadoNombre.get(); }
    public void setEmpleadoNombre(String v) { empleadoNombre.set(v); }
}