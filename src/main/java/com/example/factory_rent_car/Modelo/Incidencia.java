package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Incidencia {

    private final SimpleIntegerProperty idIncidencia = new SimpleIntegerProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleDoubleProperty monto = new SimpleDoubleProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleStringProperty descripcion = new SimpleStringProperty();
    private final SimpleIntegerProperty idReserva = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idHistorial = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idEmpleado = new SimpleIntegerProperty();

    // Datos adicionales para mostrar en tabla (JOIN)
    private final SimpleStringProperty reservaInfo = new SimpleStringProperty();
    private final SimpleStringProperty empleadoNombre = new SimpleStringProperty();
    private final SimpleStringProperty estado = new SimpleStringProperty(); // estado desde historial

    public Incidencia() {}

    public Incidencia(int idIncidencia, String tipo, double monto, LocalDate fecha, String descripcion,
                      int idReserva, int idHistorial, int idEmpleado,
                      String reservaInfo, String empleadoNombre, String estado) {
        this.idIncidencia.set(idIncidencia);
        this.tipo.set(tipo != null ? tipo : "");
        this.monto.set(monto);
        this.fecha.set(fecha);
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.idReserva.set(idReserva);
        this.idHistorial.set(idHistorial);
        this.idEmpleado.set(idEmpleado);
        this.reservaInfo.set(reservaInfo != null ? reservaInfo : "");
        this.empleadoNombre.set(empleadoNombre != null ? empleadoNombre : "");
        this.estado.set(estado != null ? estado : "");
    }

    // Propiedades
    public SimpleIntegerProperty idIncidenciaProperty() { return idIncidencia; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleDoubleProperty montoProperty() { return monto; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleStringProperty descripcionProperty() { return descripcion; }
    public SimpleIntegerProperty idReservaProperty() { return idReserva; }
    public SimpleIntegerProperty idHistorialProperty() { return idHistorial; }
    public SimpleIntegerProperty idEmpleadoProperty() { return idEmpleado; }
    public SimpleStringProperty reservaInfoProperty() { return reservaInfo; }
    public SimpleStringProperty empleadoNombreProperty() { return empleadoNombre; }
    public SimpleStringProperty estadoProperty() { return estado; }

    // Getters y Setters
    public int getIdIncidencia() { return idIncidencia.get(); }
    public void setIdIncidencia(int v) { idIncidencia.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public double getMonto() { return monto.get(); }
    public void setMonto(double v) { monto.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String v) { descripcion.set(v); }
    public int getIdReserva() { return idReserva.get(); }
    public void setIdReserva(int v) { idReserva.set(v); }
    public int getIdHistorial() { return idHistorial.get(); }
    public void setIdHistorial(int v) { idHistorial.set(v); }
    public int getIdEmpleado() { return idEmpleado.get(); }
    public void setIdEmpleado(int v) { idEmpleado.set(v); }
    public String getReservaInfo() { return reservaInfo.get(); }
    public void setReservaInfo(String v) { reservaInfo.set(v); }
    public String getEmpleadoNombre() { return empleadoNombre.get(); }
    public void setEmpleadoNombre(String v) { empleadoNombre.set(v); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }
}