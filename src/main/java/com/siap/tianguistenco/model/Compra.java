package com.siap.tianguistenco.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una compra realizada por un usuario
 */
public class Compra {
    private int id;
    private int usuarioId;
    private String folio;
    private LocalDateTime fecha;
    private double total;
    private double descuento;
    private String estado; // PENDIENTE, COMPLETADA, CANCELADA
    private TipoEnvio tipoEnvio;
    private String direccionEnvio;
    private double costoEnvio;
    private List<CompraItem> items;

    public enum TipoEnvio {
        TIENDA, DOMICILIO
    }

    public Compra() {
        this.items = new ArrayList<>();
    }

    public Compra(int id, int usuarioId, String folio, LocalDateTime fecha, double total, 
                  double descuento, String estado, TipoEnvio tipoEnvio, String direccionEnvio, 
                  double costoEnvio) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.folio = folio;
        this.fecha = fecha;
        this.total = total;
        this.descuento = descuento;
        this.estado = estado;
        this.tipoEnvio = tipoEnvio;
        this.direccionEnvio = direccionEnvio;
        this.costoEnvio = costoEnvio;
        this.items = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public TipoEnvio getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(TipoEnvio tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(double costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public List<CompraItem> getItems() {
        return items;
    }

    public void setItems(List<CompraItem> items) {
        this.items = items;
    }

    public void agregarItem(CompraItem item) {
        this.items.add(item);
    }

    public double getTotalConDescuento() {
        return total - descuento + costoEnvio;
    }

    @Override
    public String toString() {
        return "Compra #" + folio + " - " + fecha + " - $" + String.format("%.2f", getTotalConDescuento());
    }
}

