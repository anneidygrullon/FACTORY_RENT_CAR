package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Venta {

    private final SimpleIntegerProperty idVenta = new SimpleIntegerProperty();
    private final SimpleDoubleProperty montoTotal = new SimpleDoubleProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleStringProperty descripcion = new SimpleStringProperty();
    private final SimpleIntegerProperty idSuministrador = new SimpleIntegerProperty();

    private final SimpleStringProperty suministradorNombre = new SimpleStringProperty();

    public Venta() {}

    public Venta(int idVenta, double montoTotal, String tipo, String descripcion,
                 int idSuministrador, String suministradorNombre) {
        this.idVenta.set(idVenta);
        this.montoTotal.set(montoTotal);
        this.tipo.set(tipo != null ? tipo : "");
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.idSuministrador.set(idSuministrador);
        this.suministradorNombre.set(suministradorNombre != null ? suministradorNombre : "");
    }

    public SimpleIntegerProperty idVentaProperty() { return idVenta; }
    public SimpleDoubleProperty montoTotalProperty() { return montoTotal; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty descripcionProperty() { return descripcion; }
    public SimpleIntegerProperty idSuministradorProperty() { return idSuministrador; }
    public SimpleStringProperty suministradorNombreProperty() { return suministradorNombre; }

    public int getIdVenta() { return idVenta.get(); }
    public void setIdVenta(int v) { idVenta.set(v); }
    public double getMontoTotal() { return montoTotal.get(); }
    public void setMontoTotal(double v) { montoTotal.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String v) { descripcion.set(v); }
    public int getIdSuministrador() { return idSuministrador.get(); }
    public void setIdSuministrador(int v) { idSuministrador.set(v); }
    public String getSuministradorNombre() { return suministradorNombre.get(); }
    public void setSuministradorNombre(String v) { suministradorNombre.set(v); }
}
