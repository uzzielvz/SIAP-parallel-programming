package com.siap.tianguistenco.model;

/**
 * Clase que representa un item en el carrito de compras
 */
public class ItemCarrito {
    private Producto producto;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    public void incrementarCantidad() {
        this.cantidad++;
    }

    public void decrementarCantidad() {
        if (this.cantidad > 0) {
            this.cantidad--;
        }
    }

    @Override
    public String toString() {
        return String.format("%s x%d - $%.2f", producto.getNombre(), cantidad, getSubtotal());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemCarrito that = (ItemCarrito) obj;
        return producto != null ? producto.equals(that.producto) : that.producto == null;
    }

    @Override
    public int hashCode() {
        return producto != null ? producto.hashCode() : 0;
    }
}
