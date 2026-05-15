package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class NotaCredito {

    private final SimpleIntegerProperty idNota = new SimpleIntegerProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleStringProperty motivo = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleStringProperty descripcion = new SimpleStringProperty();
    private final SimpleIntegerProperty idDevolucion = new SimpleIntegerProperty();

    // Info extra para la tabla
    private final SimpleDoubleProperty monto = new SimpleDoubleProperty();
    private final SimpleStringProperty clienteNombre = new SimpleStringProperty();
    private final SimpleStringProperty reservaInfo = new SimpleStringProperty();
    private final SimpleBooleanProperty usado = new SimpleBooleanProperty();

    public NotaCredito() {}

    public NotaCredito(int idNota, String tipo, String motivo, LocalDate fecha, String descripcion,
                       int idDevolucion, double monto, String clienteNombre, String reservaInfo, boolean usado) {
        this.idNota.set(idNota);
        this.tipo.set(tipo != null ? tipo : "");
        this.motivo.set(motivo != null ? motivo : "");
        this.fecha.set(fecha);
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.idDevolucion.set(idDevolucion);
        this.monto.set(monto);
        this.clienteNombre.set(clienteNombre != null ? clienteNombre : "");
        this.reservaInfo.set(reservaInfo != null ? reservaInfo : "");
        this.usado.set(usado);
    }

    public SimpleIntegerProperty idNotaProperty() { return idNota; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty motivoProperty() { return motivo; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleStringProperty descripcionProperty() { return descripcion; }
    public SimpleIntegerProperty idDevolucionProperty() { return idDevolucion; }
    public SimpleDoubleProperty montoProperty() { return monto; }
    public SimpleStringProperty clienteNombreProperty() { return clienteNombre; }
    public SimpleStringProperty reservaInfoProperty() { return reservaInfo; }
    public SimpleBooleanProperty usadoProperty() { return usado; }

    public int getIdNota() { return idNota.get(); }
    public void setIdNota(int v) { idNota.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public String getMotivo() { return motivo.get(); }
    public void setMotivo(String v) { motivo.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String v) { descripcion.set(v); }
    public int getIdDevolucion() { return idDevolucion.get(); }
    public void setIdDevolucion(int v) { idDevolucion.set(v); }
    public double getMonto() { return monto.get(); }
    public void setMonto(double v) { monto.set(v); }
    public String getClienteNombre() { return clienteNombre.get(); }
    public void setClienteNombre(String v) { clienteNombre.set(v); }
    public String getReservaInfo() { return reservaInfo.get(); }
    public void setReservaInfo(String v) { reservaInfo.set(v); }
    public boolean isUsado() { return usado.get(); }
    public void setUsado(boolean v) { usado.set(v); }
}