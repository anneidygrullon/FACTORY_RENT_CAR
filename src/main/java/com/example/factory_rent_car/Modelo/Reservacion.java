package com.example.factory_rent_car.Modelo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Reservacion {

        // Propiedades para la tabla
        private final SimpleIntegerProperty idReserva       = new SimpleIntegerProperty();
        private final SimpleStringProperty fechaInicio     = new SimpleStringProperty();
        private final SimpleStringProperty  fechaDevolucion = new SimpleStringProperty();
        private final SimpleDoubleProperty montoTotal      = new SimpleDoubleProperty();
        private final SimpleDoubleProperty  montoApartado   = new SimpleDoubleProperty();
        private final SimpleDoubleProperty  montoPendiente  = new SimpleDoubleProperty();
        private final SimpleDoubleProperty  descuento       = new SimpleDoubleProperty();
        private final SimpleStringProperty  cliente         = new SimpleStringProperty(); // viene de la BD
        private final SimpleStringProperty  vehiculo        = new SimpleStringProperty(); // viene de la BD
        private final SimpleStringProperty  seguro          = new SimpleStringProperty(); // viene de la BD
        private final SimpleStringProperty  estado          = new SimpleStringProperty(); // se calcula automáticamente

        // IDs de la base de datos
        private int idCliente;
        private int idSeguro;
        private int idContrato;
        private int idVehiculo;

        public Reservacion() {}

        public Reservacion(int idReserva, String fechaInicio, String fechaDevolucion,
                           double montoTotal, double montoApartado, double montoPendiente,
                           double descuento, String cliente, String vehiculo, String seguro,
                           int idCliente, int idSeguro, int idContrato, int idVehiculo) {
            this.idReserva.set(idReserva);
            this.fechaInicio.set(fechaInicio != null ? fechaInicio : "");
            this.fechaDevolucion.set(fechaDevolucion != null ? fechaDevolucion : "");
            this.montoTotal.set(montoTotal);
            this.montoApartado.set(montoApartado);
            this.montoPendiente.set(montoPendiente);
            this.descuento.set(descuento);
            this.cliente.set(cliente != null ? cliente : "");
            this.vehiculo.set(vehiculo != null ? vehiculo : "");
            this.seguro.set(seguro != null ? seguro : "");
            this.idCliente  = idCliente;
            this.idSeguro   = idSeguro;
            this.idContrato = idContrato;
            this.idVehiculo = idVehiculo;
            this.estado.set(calcularEstado(fechaDevolucion));
        }

        /**
         * Define si la reserva está Activa, Completada o Vencida
         * según la fecha de devolución y el saldo pendiente
         */
        public String calcularEstadoActual() {
            return calcularEstado(fechaDevolucion.get());
        }

        private String calcularEstado(String fechaDevStr) {
            if (fechaDevStr == null || fechaDevStr.isBlank()) return "Sin fecha";
            try {
                java.time.LocalDate fechaDev = java.time.LocalDate.parse(fechaDevStr.substring(0, 10));
                boolean vencida = fechaDev.isBefore(java.time.LocalDate.now());
                boolean saldada = montoPendiente.get() <= 0.001;
                if (saldada)  return "Completada";
                if (vencida)  return "Vencida";
                return "Activa";
            } catch (Exception e) {
                return "Desconocido";
            }
        }

        public SimpleIntegerProperty idReservaProperty()       { return idReserva; }
        public SimpleStringProperty  fechaInicioProperty()     { return fechaInicio; }
        public SimpleStringProperty  fechaDevolucionProperty() { return fechaDevolucion; }
        public SimpleDoubleProperty  montoTotalProperty()      { return montoTotal; }
        public SimpleDoubleProperty  montoApartadoProperty()   { return montoApartado; }
        public SimpleDoubleProperty  montoPendienteProperty()  { return montoPendiente; }
        public SimpleDoubleProperty  descuentoProperty()       { return descuento; }
        public SimpleStringProperty  clienteProperty()         { return cliente; }
        public SimpleStringProperty  vehiculoProperty()        { return vehiculo; }
        public SimpleStringProperty  seguroProperty()          { return seguro; }
        public SimpleStringProperty  estadoProperty()          { return estado; }

        public int    getIdReserva()                  { return idReserva.get(); }
        public void   setIdReserva(int v)             { idReserva.set(v); }

        public String getFechaInicio()                { return fechaInicio.get(); }
        public void   setFechaInicio(String v)        { fechaInicio.set(v); }

        public String getFechaDevolucion()            { return fechaDevolucion.get(); }
        public void   setFechaDevolucion(String v)    { fechaDevolucion.set(v); estado.set(calcularEstado(v)); }

        public double getMontoTotal()                 { return montoTotal.get(); }
        public void   setMontoTotal(double v)         { montoTotal.set(v); }

        public double getMontoApartado()              { return montoApartado.get(); }
        public void   setMontoApartado(double v)      { montoApartado.set(v); }

        public double getMontoPendiente()             { return montoPendiente.get(); }
        public void   setMontoPendiente(double v)     { montoPendiente.set(v); estado.set(calcularEstado(fechaDevolucion.get())); }

        public double getDescuento()                  { return descuento.get(); }
        public void   setDescuento(double v)          { descuento.set(v); }

        public String getCliente()                    { return cliente.get(); }
        public void   setCliente(String v)            { cliente.set(v); }

        public String getVehiculo()                   { return vehiculo.get(); }
        public void   setVehiculo(String v)           { vehiculo.set(v); }

        public String getSeguro()                     { return seguro.get(); }
        public void   setSeguro(String v)             { seguro.set(v); }

        public String getEstado()                     { return estado.get(); }

        public int    getIdCliente()                  { return idCliente; }
        public void   setIdCliente(int v)             { idCliente = v; }

        public int    getIdSeguro()                   { return idSeguro; }
        public void   setIdSeguro(int v)              { idSeguro = v; }

        public int    getIdContrato()                 { return idContrato; }
        public void   setIdContrato(int v)            { idContrato = v; }

        public int    getIdVehiculo()                 { return idVehiculo; }
        public void   setIdVehiculo(int v)            { idVehiculo = v; }
    }


