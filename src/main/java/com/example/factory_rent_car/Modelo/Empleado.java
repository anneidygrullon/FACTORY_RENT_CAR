package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Empleado {

    private final SimpleIntegerProperty idEmpleado = new SimpleIntegerProperty();
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleStringProperty telefono = new SimpleStringProperty();
    private final SimpleStringProperty cedula = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDate> fechaNacimiento = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDate> fechaContratacion = new SimpleObjectProperty<>();
    private final SimpleStringProperty nacionalidad = new SimpleStringProperty();
    private final SimpleIntegerProperty edad = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idDireccion = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idPuesto = new SimpleIntegerProperty();

    // Info extra para la tabla (viene de la BD)
    private final SimpleStringProperty nombrePuesto = new SimpleStringProperty();
    private final SimpleStringProperty nombreDepartamento = new SimpleStringProperty();
    private final SimpleDoubleProperty sueldo = new SimpleDoubleProperty();
    private final SimpleStringProperty direccionCompleta = new SimpleStringProperty();

    public Empleado() {}

    public Empleado(int idEmpleado, String nombre, String telefono, String cedula,
                    LocalDate fechaNacimiento, LocalDate fechaContratacion, String nacionalidad,
                    int edad, int idDireccion, int idPuesto,
                    String nombrePuesto, String nombreDepartamento, double sueldo, String direccionCompleta) {
        this.idEmpleado.set(idEmpleado);
        this.nombre.set(nombre != null ? nombre : "");
        this.telefono.set(telefono != null ? telefono : "");
        this.cedula.set(cedula != null ? cedula : "");
        this.fechaNacimiento.set(fechaNacimiento);
        this.fechaContratacion.set(fechaContratacion);
        this.nacionalidad.set(nacionalidad != null ? nacionalidad : "");
        this.edad.set(edad);
        this.idDireccion.set(idDireccion);
        this.idPuesto.set(idPuesto);
        this.nombrePuesto.set(nombrePuesto != null ? nombrePuesto : "");
        this.nombreDepartamento.set(nombreDepartamento != null ? nombreDepartamento : "");
        this.sueldo.set(sueldo);
        this.direccionCompleta.set(direccionCompleta != null ? direccionCompleta : "");
    }

    public SimpleIntegerProperty idEmpleadoProperty() { return idEmpleado; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty telefonoProperty() { return telefono; }
    public SimpleStringProperty cedulaProperty() { return cedula; }
    public SimpleObjectProperty<LocalDate> fechaNacimientoProperty() { return fechaNacimiento; }
    public SimpleObjectProperty<LocalDate> fechaContratacionProperty() { return fechaContratacion; }
    public SimpleStringProperty nacionalidadProperty() { return nacionalidad; }
    public SimpleIntegerProperty edadProperty() { return edad; }
    public SimpleIntegerProperty idDireccionProperty() { return idDireccion; }
    public SimpleIntegerProperty idPuestoProperty() { return idPuesto; }
    public SimpleStringProperty nombrePuestoProperty() { return nombrePuesto; }
    public SimpleStringProperty nombreDepartamentoProperty() { return nombreDepartamento; }
    public SimpleDoubleProperty sueldoProperty() { return sueldo; }
    public SimpleStringProperty direccionCompletaProperty() { return direccionCompleta; }

    public int getIdEmpleado() { return idEmpleado.get(); }
    public void setIdEmpleado(int v) { idEmpleado.set(v); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String v) { nombre.set(v); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String v) { telefono.set(v); }
    public String getCedula() { return cedula.get(); }
    public void setCedula(String v) { cedula.set(v); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento.get(); }
    public void setFechaNacimiento(LocalDate v) { fechaNacimiento.set(v); }
    public LocalDate getFechaContratacion() { return fechaContratacion.get(); }
    public void setFechaContratacion(LocalDate v) { fechaContratacion.set(v); }
    public String getNacionalidad() { return nacionalidad.get(); }
    public void setNacionalidad(String v) { nacionalidad.set(v); }
    public int getEdad() { return edad.get(); }
    public void setEdad(int v) { edad.set(v); }
    public int getIdDireccion() { return idDireccion.get(); }
    public void setIdDireccion(int v) { idDireccion.set(v); }
    public int getIdPuesto() { return idPuesto.get(); }
    public void setIdPuesto(int v) { idPuesto.set(v); }
    public String getNombrePuesto() { return nombrePuesto.get(); }
    public void setNombrePuesto(String v) { nombrePuesto.set(v); }
    public String getNombreDepartamento() { return nombreDepartamento.get(); }
    public void setNombreDepartamento(String v) { nombreDepartamento.set(v); }
    public double getSueldo() { return sueldo.get(); }
    public void setSueldo(double v) { sueldo.set(v); }
    public String getDireccionCompleta() { return direccionCompleta.get(); }
    public void setDireccionCompleta(String v) { direccionCompleta.set(v); }
}