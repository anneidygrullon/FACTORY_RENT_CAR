package com.example.factory_rent_car.Modelo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ReservaObjeto {
        private final SimpleIntegerProperty idRegistro = new SimpleIntegerProperty();
        private final SimpleIntegerProperty idReserva = new SimpleIntegerProperty();
        private final SimpleIntegerProperty idObjeto = new SimpleIntegerProperty();
        private final SimpleStringProperty nombreObjeto = new SimpleStringProperty();
        private final SimpleStringProperty marcaObjeto = new SimpleStringProperty();
        private final SimpleStringProperty tipoObjeto = new SimpleStringProperty();
        private final SimpleDoubleProperty precioUnitario = new SimpleDoubleProperty();
        private final SimpleIntegerProperty cantidad = new SimpleIntegerProperty();
        private final SimpleDoubleProperty subtotal = new SimpleDoubleProperty();

    public ReservaObjeto() {}

    public ReservaObjeto(int idRegistro, int idReserva, int idObjeto,
                         String nombreObjeto, String marcaObjeto, String tipoObjeto,
                         double precioUnitario, int cantidad) {
        this.idRegistro.set(idRegistro);
        this.idReserva.set(idReserva);
        this.idObjeto.set(idObjeto);
        this.nombreObjeto.set(nombreObjeto != null ? nombreObjeto : "");
        this.marcaObjeto.set(marcaObjeto != null ? marcaObjeto : "");
        this.tipoObjeto.set(tipoObjeto != null ? tipoObjeto : "");
        this.precioUnitario.set(precioUnitario);
        this.cantidad.set(cantidad);
        this.subtotal.set(precioUnitario * cantidad);
    }

    public ReservaObjeto(int idReserva, int idObjeto, String nombreObjeto,
                         String marcaObjeto, String tipoObjeto,
                         double precioUnitario, int cantidad) {
        this(0, idReserva, idObjeto, nombreObjeto, marcaObjeto, tipoObjeto, precioUnitario, cantidad);
    }

    public SimpleIntegerProperty idRegistroProperty()   { return idRegistro; }
    public SimpleIntegerProperty idReservaProperty()    { return idReserva; }
    public SimpleIntegerProperty idObjetoProperty()     { return idObjeto; }
    public SimpleStringProperty  nombreObjetoProperty() { return nombreObjeto; }
    public SimpleStringProperty  marcaObjetoProperty()  { return marcaObjeto; }
    public SimpleStringProperty  tipoObjetoProperty()   { return tipoObjeto; }
    public SimpleDoubleProperty  precioUnitarioProperty() { return precioUnitario; }
    public SimpleIntegerProperty cantidadProperty()     { return cantidad; }
    public SimpleDoubleProperty  subtotalProperty()     { return subtotal; }

    public int getIdRegistro()   { return idRegistro.get(); }
    public void setIdRegistro(int v) { idRegistro.set(v); }
    public int getIdReserva()    { return idReserva.get(); }
    public void setIdReserva(int v) { idReserva.set(v); }
    public int getIdObjeto()     { return idObjeto.get(); }
    public void setIdObjeto(int v) { idObjeto.set(v); }
    public String getNombreObjeto() { return nombreObjeto.get(); }
    public void setNombreObjeto(String v) { nombreObjeto.set(v); }
    public String getMarcaObjeto() { return marcaObjeto.get(); }
    public void setMarcaObjeto(String v) { marcaObjeto.set(v); }
    public String getTipoObjeto() { return tipoObjeto.get(); }
    public void setTipoObjeto(String v) { tipoObjeto.set(v); }
    public double getPrecioUnitario() { return precioUnitario.get(); }
    public void setPrecioUnitario(double v) { precioUnitario.set(v); recalcularSubtotal(); }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int v) { cantidad.set(v); recalcularSubtotal(); }
    public double getSubtotal() { return subtotal.get(); }
    private void recalcularSubtotal() { subtotal.set(getPrecioUnitario() * getCantidad()); }
}