package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Pago {

    private final SimpleIntegerProperty idPago = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleDoubleProperty monto = new SimpleDoubleProperty();
    private final SimpleIntegerProperty idFactura = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idMetodoPago = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idCuenta = new SimpleIntegerProperty();

    // Datos adicionales para mostrar en tabla
    private final SimpleStringProperty metodoPagoNombre = new SimpleStringProperty();
    private final SimpleStringProperty cuentaInfo = new SimpleStringProperty();
    private final SimpleStringProperty reservaInfo = new SimpleStringProperty();
    private final SimpleStringProperty clienteNombre = new SimpleStringProperty();

    public Pago() {}

    public Pago(int idPago, LocalDate fecha, String tipo, double monto, int idFactura,
                int idMetodoPago, int idCuenta, String metodoPagoNombre,
                String cuentaInfo, String reservaInfo, String clienteNombre) {
        this.idPago.set(idPago);
        this.fecha.set(fecha);
        this.tipo.set(tipo != null ? tipo : "");
        this.monto.set(monto);
        this.idFactura.set(idFactura);
        this.idMetodoPago.set(idMetodoPago);
        this.idCuenta.set(idCuenta);
        this.metodoPagoNombre.set(metodoPagoNombre != null ? metodoPagoNombre : "");
        this.cuentaInfo.set(cuentaInfo != null ? cuentaInfo : "");
        this.reservaInfo.set(reservaInfo != null ? reservaInfo : "");
        this.clienteNombre.set(clienteNombre != null ? clienteNombre : "");
    }

    // Propiedades
    public SimpleIntegerProperty idPagoProperty() { return idPago; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleDoubleProperty montoProperty() { return monto; }
    public SimpleIntegerProperty idFacturaProperty() { return idFactura; }
    public SimpleIntegerProperty idMetodoPagoProperty() { return idMetodoPago; }
    public SimpleIntegerProperty idCuentaProperty() { return idCuenta; }
    public SimpleStringProperty metodoPagoNombreProperty() { return metodoPagoNombre; }
    public SimpleStringProperty cuentaInfoProperty() { return cuentaInfo; }
    public SimpleStringProperty reservaInfoProperty() { return reservaInfo; }
    public SimpleStringProperty clienteNombreProperty() { return clienteNombre; }

    // Getters y Setters
    public int getIdPago() { return idPago.get(); }
    public void setIdPago(int v) { idPago.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public double getMonto() { return monto.get(); }
    public void setMonto(double v) { monto.set(v); }
    public int getIdFactura() { return idFactura.get(); }
    public void setIdFactura(int v) { idFactura.set(v); }
    public int getIdMetodoPago() { return idMetodoPago.get(); }
    public void setIdMetodoPago(int v) { idMetodoPago.set(v); }
    public int getIdCuenta() { return idCuenta.get(); }
    public void setIdCuenta(int v) { idCuenta.set(v); }
    public String getMetodoPagoNombre() { return metodoPagoNombre.get(); }
    public void setMetodoPagoNombre(String v) { metodoPagoNombre.set(v); }
    public String getCuentaInfo() { return cuentaInfo.get(); }
    public void setCuentaInfo(String v) { cuentaInfo.set(v); }
    public String getReservaInfo() { return reservaInfo.get(); }
    public void setReservaInfo(String v) { reservaInfo.set(v); }
    public String getClienteNombre() { return clienteNombre.get(); }
    public void setClienteNombre(String v) { clienteNombre.set(v); }
}