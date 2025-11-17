package com.siap.tianguistenco.model;

/**
 * Clase que representa una tarjeta de pago registrada por un usuario
 */
public class Tarjeta {
    private int id;
    private int usuarioId;
    private String numeroTarjeta; // Enmascarado (últimos 4 dígitos)
    private String nombreTitular;
    private String fechaVencimiento; // Formato MM/YY
    private TipoTarjeta tipo;
    private boolean activa;

    public enum TipoTarjeta {
        VISA, MASTERCARD
    }

    public Tarjeta() {
    }

    public Tarjeta(int id, int usuarioId, String numeroTarjeta, String nombreTitular, 
                   String fechaVencimiento, TipoTarjeta tipo, boolean activa) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.numeroTarjeta = numeroTarjeta;
        this.nombreTitular = nombreTitular;
        this.fechaVencimiento = fechaVencimiento;
        this.tipo = tipo;
        this.activa = activa;
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

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public TipoTarjeta getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarjeta tipo) {
        this.tipo = tipo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    /**
     * Obtiene el número de tarjeta enmascarado para mostrar
     * @return número enmascarado (ej: **** **** **** 1234)
     */
    public String getNumeroEnmascarado() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "**** **** **** ****";
        }
        String ultimos4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        return "**** **** **** " + ultimos4;
    }

    @Override
    public String toString() {
        return tipo + " " + getNumeroEnmascarado() + " - " + nombreTitular;
    }
}

