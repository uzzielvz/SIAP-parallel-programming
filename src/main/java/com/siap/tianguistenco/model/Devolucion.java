package com.siap.tianguistenco.model;

import java.time.LocalDateTime;

/**
 * Clase que representa una devolución de compra
 */
public class Devolucion {
    private int id;
    private int compraId;
    private String folioCompra;
    private LocalDateTime fecha;
    private MotivoDevolucion motivo;
    private String estado; // PENDIENTE, PROCESADA, RECHAZADA
    private double montoDevolucion;
    private String observaciones;

    public enum MotivoDevolucion {
        COBRO_ERRONEO("Cobro Erróneo"),
        PRODUCTO_DANADO("Producto Dañado"),
        PRODUCTO_CADUCADO("Producto Caducado");

        private final String descripcion;

        MotivoDevolucion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Devolucion() {
    }

    public Devolucion(int id, int compraId, String folioCompra, LocalDateTime fecha, 
                      MotivoDevolucion motivo, String estado, double montoDevolucion, 
                      String observaciones) {
        this.id = id;
        this.compraId = compraId;
        this.folioCompra = folioCompra;
        this.fecha = fecha;
        this.motivo = motivo;
        this.estado = estado;
        this.montoDevolucion = montoDevolucion;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompraId() {
        return compraId;
    }

    public void setCompraId(int compraId) {
        this.compraId = compraId;
    }

    public String getFolioCompra() {
        return folioCompra;
    }

    public void setFolioCompra(String folioCompra) {
        this.folioCompra = folioCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public MotivoDevolucion getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoDevolucion motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getMontoDevolucion() {
        return montoDevolucion;
    }

    public void setMontoDevolucion(double montoDevolucion) {
        this.montoDevolucion = montoDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Devolución #" + id + " - Folio: " + folioCompra + " - " + motivo.getDescripcion() + 
               " - $" + String.format("%.2f", montoDevolucion);
    }
}

