package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class ControlVehi {

    private final SimpleIntegerProperty idControl = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty nivelCombustible = new SimpleDoubleProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleIntegerProperty idDireccion = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idReserva = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idEmpleado = new SimpleIntegerProperty();

    // Info extra para la tabla (viene de la BD)
    private final SimpleStringProperty clienteNombre = new SimpleStringProperty();
    private final SimpleStringProperty vehiculoInfo = new SimpleStringProperty();
    private final SimpleStringProperty empleadoNombre = new SimpleStringProperty();
    private final SimpleStringProperty direccionCompleta = new SimpleStringProperty();

    public ControlVehi() {}

    public ControlVehi(int idControl, LocalDate fecha, double nivelCombustible, String tipo,
                       int idDireccion, int idReserva, int idEmpleado,
                       String clienteNombre, String vehiculoInfo, String empleadoNombre, String direccionCompleta) {
        this.idControl.set(idControl);
        this.fecha.set(fecha);
        this.nivelCombustible.set(nivelCombustible);
        this.tipo.set(tipo != null ? tipo : "");
        this.idDireccion.set(idDireccion);
        this.idReserva.set(idReserva);
        this.idEmpleado.set(idEmpleado);
        this.clienteNombre.set(clienteNombre != null ? clienteNombre : "");
        this.vehiculoInfo.set(vehiculoInfo != null ? vehiculoInfo : "");
        this.empleadoNombre.set(empleadoNombre != null ? empleadoNombre : "");
        this.direccionCompleta.set(direccionCompleta != null ? direccionCompleta : "");
    }

    public SimpleIntegerProperty idControlProperty() { return idControl; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }
    public SimpleDoubleProperty nivelCombustibleProperty() { return nivelCombustible; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleIntegerProperty idDireccionProperty() { return idDireccion; }
    public SimpleIntegerProperty idReservaProperty() { return idReserva; }
    public SimpleIntegerProperty idEmpleadoProperty() { return idEmpleado; }
    public SimpleStringProperty clienteNombreProperty() { return clienteNombre; }
    public SimpleStringProperty vehiculoInfoProperty() { return vehiculoInfo; }
    public SimpleStringProperty empleadoNombreProperty() { return empleadoNombre; }
    public SimpleStringProperty direccionCompletaProperty() { return direccionCompleta; }

    public int getIdControl() { return idControl.get(); }
    public void setIdControl(int v) { idControl.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
    public double getNivelCombustible() { return nivelCombustible.get(); }
    public void setNivelCombustible(double v) { nivelCombustible.set(v); }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public int getIdDireccion() { return idDireccion.get(); }
    public void setIdDireccion(int v) { idDireccion.set(v); }
    public int getIdReserva() { return idReserva.get(); }
    public void setIdReserva(int v) { idReserva.set(v); }
    public int getIdEmpleado() { return idEmpleado.get(); }
    public void setIdEmpleado(int v) { idEmpleado.set(v); }
    public String getClienteNombre() { return clienteNombre.get(); }
    public void setClienteNombre(String v) { clienteNombre.set(v); }
    public String getVehiculoInfo() { return vehiculoInfo.get(); }
    public void setVehiculoInfo(String v) { vehiculoInfo.set(v); }
    public String getEmpleadoNombre() { return empleadoNombre.get(); }
    public void setEmpleadoNombre(String v) { empleadoNombre.set(v); }
    public String getDireccionCompleta() { return direccionCompleta.get(); }
    public void setDireccionCompleta(String v) { direccionCompleta.set(v); }
}