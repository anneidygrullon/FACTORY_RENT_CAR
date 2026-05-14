package com.example.factory_rent_car.Modelo;

import javafx.beans.property.*;

public class Objeto {

    private final SimpleIntegerProperty idObjeto = new SimpleIntegerProperty();
    private final SimpleStringProperty nombre = new SimpleStringProperty();
    private final SimpleStringProperty marca = new SimpleStringProperty();
    private final SimpleDoubleProperty precio = new SimpleDoubleProperty();
    private final SimpleIntegerProperty stock = new SimpleIntegerProperty();
    private final SimpleStringProperty tipo = new SimpleStringProperty();
    private final SimpleIntegerProperty idCompra = new SimpleIntegerProperty();
    private final SimpleStringProperty estado = new SimpleStringProperty();

    public Objeto() {}

    public Objeto(int idObjeto, String nombre, String marca, double precio, int stock,
                  String tipo, int idCompra, String estado) {
        this.idObjeto.set(idObjeto);
        this.nombre.set(nombre != null ? nombre : "");
        this.marca.set(marca != null ? marca : "");
        this.precio.set(precio);
        this.stock.set(stock);
        this.tipo.set(tipo != null ? tipo : "");
        this.idCompra.set(idCompra);
        this.estado.set(estado != null ? estado : (stock > 0 ? "Disponible" : "Agotado"));
    }

    // Propiedades
    public SimpleIntegerProperty idObjetoProperty() { return idObjeto; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty marcaProperty() { return marca; }
    public SimpleDoubleProperty precioProperty() { return precio; }
    public SimpleIntegerProperty stockProperty() { return stock; }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleIntegerProperty idCompraProperty() { return idCompra; }
    public SimpleStringProperty estadoProperty() { return estado; }

    // Getters y Setters
    public int getIdObjeto() { return idObjeto.get(); }
    public void setIdObjeto(int v) { idObjeto.set(v); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String v) { nombre.set(v); }
    public String getMarca() { return marca.get(); }
    public void setMarca(String v) { marca.set(v); }
    public double getPrecio() { return precio.get(); }
    public void setPrecio(double v) { precio.set(v); }
    public int getStock() { return stock.get(); }
    public void setStock(int v) {
        stock.set(v);
        if (v <= 0) estado.set("Agotado");
        else if (v > 0 && !estado.get().equals("En uso")) estado.set("Disponible");
    }
    public String getTipo() { return tipo.get(); }
    public void setTipo(String v) { tipo.set(v); }
    public int getIdCompra() { return idCompra.get(); }
    public void setIdCompra(int v) { idCompra.set(v); }
    public String getEstado() { return estado.get(); }
    public void setEstado(String v) { estado.set(v); }
}