package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Pedido {

    private final SimpleIntegerProperty idCompra = new SimpleIntegerProperty();
    private final SimpleIntegerProperty cantidad = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty montoTotal = new SimpleDoubleProperty();
    private final SimpleStringProperty estado = new SimpleStringProperty();
    private final SimpleIntegerProperty idSuministrador = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idContrato = new SimpleIntegerProperty();

    // Datos adicionales
    private final SimpleStringProperty suministradorNombre = new SimpleStringProperty();

    public Pedido() {}

    public Pedido(int idCompra, int cantidad, LocalDate fecha, double montoTotal, String estado,
                  int idSuministrador, int idContrato, String suministradorNombre) {
        this.idCompra.set(idCompra);
        this.cantidad.set(cantidad);
        this.fecha.set(fecha);
        this.montoTotal.set(montoTotal);
        this.estado.set(estado != null ? estado : "");
        this.idSuministrador.set(idSuministrador);
        this.idContrato.set(idContrato);
        this.suministradorNombre.set(suministradorNombre != null ? suministradorNombre : "");
    }

    // Propiedades
    public SimpleIntegerProperty idCompraProperty() { return idCompra; }
    public SimpleIntegerProperty cantidadProperty() { return cantidad; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleDoubleProperty montoTotalProperty() { return montoTotal; }
    public SimpleStringProperty estadoProperty() { return estado; }
    public SimpleIntegerProperty idSuministradorProperty() { return idSuministrador; }
    public SimpleIntegerProperty idContratoProperty() { return idContrato; }
    public SimpleStringProperty suministradorNombreProperty() { return suministradorNombre; }

    // Getters y Setters
    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int v) { idCompra.set(v); }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int v) { cantidad.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public double getMontoTotal() { return montoTotal.get(); }
    public void setMontoTotal(double v) { montoTotal.set(v); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }
    public int getIdSuministrador() { return idSuministrador.get(); }
    public void setIdSuministrador(int v) { idSuministrador.set(v); }
    public int getIdContrato() { return idContrato.get(); }
    public void setIdContrato(int v) { idContrato.set(v); }
    public String getSuministradorNombre() { return suministradorNombre.get(); }
    public void setSuministradorNombre(String v) { suministradorNombre.set(v); }
}