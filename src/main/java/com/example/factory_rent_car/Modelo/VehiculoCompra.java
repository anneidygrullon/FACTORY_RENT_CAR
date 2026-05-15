package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class VehiculoCompra {

    private final SimpleIntegerProperty idVehiculo = new SimpleIntegerProperty();
    private final SimpleStringProperty marca = new SimpleStringProperty();
    private final SimpleStringProperty modelo = new SimpleStringProperty();
    private final SimpleStringProperty serie = new SimpleStringProperty();
    private final SimpleStringProperty numPlaca = new SimpleStringProperty();
    private final SimpleStringProperty color = new SimpleStringProperty();
    private final SimpleDoubleProperty kilometraje = new SimpleDoubleProperty();
    private final SimpleStringProperty tipoCombustible = new SimpleStringProperty();
    private final SimpleIntegerProperty maxPasajeros = new SimpleIntegerProperty();
    private final SimpleStringProperty estado = new SimpleStringProperty();
    private final SimpleDoubleProperty precioPorDia = new SimpleDoubleProperty();
    private final SimpleIntegerProperty idPoliza = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idCompra = new SimpleIntegerProperty();

    // Info de la compra del vehículo
    private final SimpleIntegerProperty cantidad = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fechaCompra = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty precioCompra = new SimpleDoubleProperty();
    private final SimpleStringProperty suministradorNombre = new SimpleStringProperty();

    public VehiculoCompra() {}

    public VehiculoCompra(int idVehiculo, String marca, String modelo, String serie, String numPlaca,
                          String color, double kilometraje, String tipoCombustible, int maxPasajeros,
                          String estado, double precioPorDia, int idPoliza, int idCompra,
                          int cantidad, LocalDate fechaCompra, double precioCompra, String suministradorNombre) {
        this.idVehiculo.set(idVehiculo);
        this.marca.set(marca != null ? marca : "");
        this.modelo.set(modelo != null ? modelo : "");
        this.serie.set(serie != null ? serie : "");
        this.numPlaca.set(numPlaca != null ? numPlaca : "");
        this.color.set(color != null ? color : "");
        this.kilometraje.set(kilometraje);
        this.tipoCombustible.set(tipoCombustible != null ? tipoCombustible : "");
        this.maxPasajeros.set(maxPasajeros);
        this.estado.set(estado != null ? estado : "Disponible");
        this.precioPorDia.set(precioPorDia);
        this.idPoliza.set(idPoliza);
        this.idCompra.set(idCompra);
        this.cantidad.set(cantidad);
        this.fechaCompra.set(fechaCompra);
        this.precioCompra.set(precioCompra);
        this.suministradorNombre.set(suministradorNombre != null ? suministradorNombre : "");
    }

    public SimpleIntegerProperty idVehiculoProperty() { return idVehiculo; }
    public SimpleStringProperty marcaProperty() { return marca; }
    public SimpleStringProperty modeloProperty() { return modelo; }
    public SimpleStringProperty serieProperty() { return serie; }
    public SimpleStringProperty numPlacaProperty() { return numPlaca; }
    public SimpleStringProperty colorProperty() { return color; }
    public SimpleDoubleProperty kilometrajeProperty() { return kilometraje; }
    public SimpleStringProperty tipoCombustibleProperty() { return tipoCombustible; }
    public SimpleIntegerProperty maxPasajerosProperty() { return maxPasajeros; }
    public SimpleStringProperty estadoProperty() { return estado; }
    public SimpleDoubleProperty precioPorDiaProperty() { return precioPorDia; }
    public SimpleIntegerProperty idPolizaProperty() { return idPoliza; }
    public SimpleIntegerProperty idCompraProperty() { return idCompra; }
    public SimpleIntegerProperty cantidadProperty() { return cantidad; }
    public SimpleObjectProperty<LocalDate> fechaCompraProperty() { return fechaCompra; }
    public SimpleDoubleProperty precioCompraProperty() { return precioCompra; }
    public SimpleStringProperty suministradorNombreProperty() { return suministradorNombre; }

    public int getIdVehiculo() { return idVehiculo.get(); }
    public void setIdVehiculo(int v) { idVehiculo.set(v); }
    public String getMarca() { return marca.get(); }
    public void setMarca(String v) { marca.set(v); }
    public String getModelo() { return modelo.get(); }
    public void setModelo(String v) { modelo.set(v); }
    public String getSerie() { return serie.get(); }
    public void setSerie(String v) { serie.set(v); }
    public String getNumPlaca() { return numPlaca.get(); }
    public void setNumPlaca(String v) { numPlaca.set(v); }
    public String getColor() { return color.get(); }
    public void setColor(String v) { color.set(v); }
    public double getKilometraje() { return kilometraje.get(); }
    public void setKilometraje(double v) { kilometraje.set(v); }
    public String getTipoCombustible() { return tipoCombustible.get(); }
    public void setTipoCombustible(String v) { tipoCombustible.set(v); }
    public int getMaxPasajeros() { return maxPasajeros.get(); }
    public void setMaxPasajeros(int v) { maxPasajeros.set(v); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }
    public double getPrecioPorDia() { return precioPorDia.get(); }
    public void setPrecioPorDia(double v) { precioPorDia.set(v); }
    public int getIdPoliza() { return idPoliza.get(); }
    public void setIdPoliza(int v) { idPoliza.set(v); }
    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int v) { idCompra.set(v); }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int v) { cantidad.set(v); }
    public LocalDate getFechaCompra() { return fechaCompra.get(); }
    public void setFechaCompra(LocalDate v) { fechaCompra.set(v); }
    public double getPrecioCompra() { return precioCompra.get(); }
    public void setPrecioCompra(double v) { precioCompra.set(v); }
    public String getSuministradorNombre() { return suministradorNombre.get(); }
    public void setSuministradorNombre(String v) { suministradorNombre.set(v); }
}