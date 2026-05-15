package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Mantenimiento {

    private final SimpleIntegerProperty idMantenimiento = new SimpleIntegerProperty();
    private final SimpleDoubleProperty costo = new SimpleDoubleProperty();
    private final SimpleObjectProperty<LocalDate> fechaSalida = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDate> fechaIngreso = new SimpleObjectProperty<>();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleStringProperty descripcion = new SimpleStringProperty();
    private final SimpleIntegerProperty idVehiculo = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idHistMantenimiento = new SimpleIntegerProperty();

    // Info extra para la tabla (viene de la BD)
    private final SimpleStringProperty vehiculoInfo = new SimpleStringProperty();
    private final SimpleIntegerProperty diasDuracion = new SimpleIntegerProperty();

    public Mantenimiento() {}

    public Mantenimiento(int idMantenimiento, double costo, LocalDate fechaSalida, LocalDate fechaIngreso,
                         String tipo, String descripcion, int idVehiculo, int idHistMantenimiento,
                         String vehiculoInfo) {
        this.idMantenimiento.set(idMantenimiento);
        this.costo.set(costo);
        this.fechaSalida.set(fechaSalida);
        this.fechaIngreso.set(fechaIngreso);
        this.tipo.set(tipo != null ? tipo : "");
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.idVehiculo.set(idVehiculo);
        this.idHistMantenimiento.set(idHistMantenimiento);
        this.vehiculoInfo.set(vehiculoInfo != null ? vehiculoInfo : "");
        calcularDiasDuracion();
    }

    private void calcularDiasDuracion() {
        if (fechaSalida.get() != null && fechaIngreso.get() != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaSalida.get(), fechaIngreso.get());
            diasDuracion.set((int) Math.max(0, dias));
        } else {
            diasDuracion.set(0);
        }
    }

    public SimpleIntegerProperty idMantenimientoProperty() { return idMantenimiento; }
    public SimpleDoubleProperty costoProperty() { return costo; }
    public SimpleObjectProperty<LocalDate> fechaSalidaProperty() { return fechaSalida; }
    public SimpleObjectProperty<LocalDate> fechaIngresoProperty() { return fechaIngreso; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty descripcionProperty() { return descripcion; }
    public SimpleIntegerProperty idVehiculoProperty() { return idVehiculo; }
    public SimpleIntegerProperty idHistMantenimientoProperty() { return idHistMantenimiento; }
    public SimpleStringProperty vehiculoInfoProperty() { return vehiculoInfo; }
    public SimpleIntegerProperty diasDuracionProperty() { return diasDuracion; }

    public int getIdMantenimiento() { return idMantenimiento.get(); }
    public void setIdMantenimiento(int v) { idMantenimiento.set(v); }
    public double getCosto() { return costo.get(); }
    public void setCosto(double v) { costo.set(v); }
    public LocalDate getFechaSalida() { return fechaSalida.get(); }
    public void setFechaSalida(LocalDate v) { fechaSalida.set(v); calcularDiasDuracion(); }
    public LocalDate getFechaIngreso() { return fechaIngreso.get(); }
    public void setFechaIngreso(LocalDate v) { fechaIngreso.set(v); calcularDiasDuracion(); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String v) { descripcion.set(v); }
    public int getIdVehiculo() { return idVehiculo.get(); }
    public void setIdVehiculo(int v) { idVehiculo.set(v); }
    public int getIdHistMantenimiento() { return idHistMantenimiento.get(); }
    public void setIdHistMantenimiento(int v) { idHistMantenimiento.set(v); }
    public String getVehiculoInfo() { return vehiculoInfo.get(); }
    public void setVehiculoInfo(String v) { vehiculoInfo.set(v); }
    public int getDiasDuracion() { return diasDuracion.get(); }
}