package com.siap.tianguistenco.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase que representa el carrito de compras con sincronización para multihilo
 */
public class CarritoCompra {
    private final List<ItemCarrito> items;
    private double total;
    private double descuento;
    private double totalConDescuento;

    public CarritoCompra() {
        this.items = new CopyOnWriteArrayList<>();
        this.total = 0.0;
        this.descuento = 0.0;
        this.totalConDescuento = 0.0;
    }

    /**
     * Agrega un producto al carrito de manera sincronizada
     */
    public synchronized void agregarProducto(Producto producto) {
        ItemCarrito itemExistente = buscarItem(producto);
        if (itemExistente != null) {
            itemExistente.incrementarCantidad();
        } else {
            items.add(new ItemCarrito(producto, 1));
        }
        calcularTotal();
    }

    /**
     * Elimina un producto del carrito de manera sincronizada
     */
    public synchronized void eliminarProducto(Producto producto) {
        ItemCarrito item = buscarItem(producto);
        if (item != null) {
            item.decrementarCantidad();
            if (item.getCantidad() <= 0) {
                items.remove(item);
            }
            calcularTotal();
        }
    }

    /**
     * Busca un item en el carrito por producto
     */
    private ItemCarrito buscarItem(Producto producto) {
        return items.stream()
                .filter(item -> item.getProducto().equals(producto))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula el total del carrito de manera sincronizada
     */
    public synchronized void calcularTotal() {
        total = items.stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
        totalConDescuento = total - descuento;
    }

    /**
     * Aplica un descuento al carrito
     */
    public synchronized void aplicarDescuento(double descuento) {
        this.descuento = descuento;
        totalConDescuento = total - descuento;
    }

    /**
     * Limpia el carrito
     */
    public synchronized void limpiar() {
        items.clear();
        total = 0.0;
        descuento = 0.0;
        totalConDescuento = 0.0;
    }

    // Getters
    public List<ItemCarrito> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotal() {
        return total;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotalConDescuento() {
        return totalConDescuento;
    }

    public int getCantidadTotalItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CARRITO DE COMPRAS ===\n");
        for (ItemCarrito item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("Subtotal: $").append(String.format("%.2f", total)).append("\n");
        if (descuento > 0) {
            sb.append("Descuento: -$").append(String.format("%.2f", descuento)).append("\n");
        }
        sb.append("TOTAL: $").append(String.format("%.2f", totalConDescuento));
        return sb.toString();
    }
}
