package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Reclamo {

    private final SimpleIntegerProperty idReclamo = new SimpleIntegerProperty();
    private final SimpleStringProperty estado = new SimpleStringProperty();
    private final SimpleStringProperty motivo = new SimpleStringProperty();
    private final SimpleStringProperty descripcion = new SimpleStringProperty();
    private final SimpleIntegerProperty idCliente = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idHistorial = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idEmpleado = new SimpleIntegerProperty();

    // Info extra para la tabla (viene de la BD)
    private final SimpleStringProperty clienteNombre = new SimpleStringProperty();
    private final SimpleStringProperty empleadoNombre = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();

    public Reclamo() {}

    public Reclamo(int idReclamo, String estado, String motivo, String descripcion,
                   int idCliente, int idHistorial, int idEmpleado,
                   String clienteNombre, String empleadoNombre, LocalDate fecha) {
        this.idReclamo.set(idReclamo);
        this.estado.set(estado != null ? estado : "");
        this.motivo.set(motivo != null ? motivo : "");
        this.descripcion.set(descripcion != null ? descripcion : "");
        this.idCliente.set(idCliente);
        this.idHistorial.set(idHistorial);
        this.idEmpleado.set(idEmpleado);
        this.clienteNombre.set(clienteNombre != null ? clienteNombre : "");
        this.empleadoNombre.set(empleadoNombre != null ? empleadoNombre : "");
        this.fecha.set(fecha);
    }

    public SimpleIntegerProperty idReclamoProperty() { return idReclamo; }
    public SimpleStringProperty estadoProperty() { return estado; }
    public SimpleStringProperty motivoProperty() { return motivo; }
    public SimpleStringProperty descripcionProperty() { return descripcion; }
    public SimpleIntegerProperty idClienteProperty() { return idCliente; }
    public SimpleIntegerProperty idHistorialProperty() { return idHistorial; }
    public SimpleIntegerProperty idEmpleadoProperty() { return idEmpleado; }
    public SimpleStringProperty clienteNombreProperty() { return clienteNombre; }
    public SimpleStringProperty empleadoNombreProperty() { return empleadoNombre; }
    public SimpleObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public int getIdReclamo() { return idReclamo.get(); }
    public void setIdReclamo(int v) { idReclamo.set(v); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }
    public String getMotivo() { return motivo.get(); }
    public void setMotivo(String v) { motivo.set(v); }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String v) { descripcion.set(v); }
    public int getIdCliente() { return idCliente.get(); }
    public void setIdCliente(int v) { idCliente.set(v); }
    public int getIdHistorial() { return idHistorial.get(); }
    public void setIdHistorial(int v) { idHistorial.set(v); }
    public int getIdEmpleado() { return idEmpleado.get(); }
    public void setIdEmpleado(int v) { idEmpleado.set(v); }
    public String getClienteNombre() { return clienteNombre.get(); }
    public void setClienteNombre(String v) { clienteNombre.set(v); }
    public String getEmpleadoNombre() { return empleadoNombre.get(); }
    public void setEmpleadoNombre(String v) { empleadoNombre.set(v); }
    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate v) { fecha.set(v); }
}