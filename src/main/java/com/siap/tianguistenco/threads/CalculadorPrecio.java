package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.model.CarritoCompra;
import com.siap.tianguistenco.model.ItemCarrito;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de calcular el precio total del carrito en tiempo real
 * Utiliza sincronización para evitar condiciones de carrera
 */
public class CalculadorPrecio implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CarritoCompra carritoCompra;
    private final SelectorProductos selectorProductos;
    private double ultimoTotalCalculado;
    private int ultimaCantidadItems;

    public CalculadorPrecio(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, 
                           CarritoCompra carritoCompra, SelectorProductos selectorProductos) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.carritoCompra = carritoCompra;
        this.selectorProductos = selectorProductos;
        this.ultimoTotalCalculado = 0.0;
        this.ultimaCantidadItems = 0;
    }

    @Override
    public void run() {
        try {
            System.out.println("CalculadorPrecio iniciado");

            // Esperar a que el usuario se autentique
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Monitorear cambios en el carrito y recalcular precios
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                synchronized (carritoCompra) {
                    // Verificar si hay cambios en el carrito
                    if (haCambiadoElCarrito()) {
                        recalcularPrecios();
                        actualizarInterfaz();
                    }
                }

                // Esperar antes de la siguiente verificación
                Thread.sleep(500);
            }

        } catch (InterruptedException e) {
            System.out.println("CalculadorPrecio interrumpido");
        } catch (Exception e) {
            System.err.println("Error en CalculadorPrecio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica si el carrito ha cambiado desde la última verificación
     */
    private boolean haCambiadoElCarrito() {
        int cantidadActual = carritoCompra.getCantidadTotalItems();
        double totalActual = carritoCompra.getTotal();
        
        boolean haCambiado = (cantidadActual != ultimaCantidadItems) || 
                           (Math.abs(totalActual - ultimoTotalCalculado) > 0.01);
        
        if (haCambiado) {
            ultimaCantidadItems = cantidadActual;
            ultimoTotalCalculado = totalActual;
        }
        
        return haCambiado;
    }

    /**
     * Recalcula los precios del carrito de manera sincronizada
     */
    private void recalcularPrecios() {
        try {
            // Recalcular el total del carrito
            carritoCompra.calcularTotal();
            
            // Log del cálculo
            System.out.println("Precios recalculados:");
            System.out.println("  - Items en carrito: " + carritoCompra.getCantidadTotalItems());
            System.out.println("  - Subtotal: $" + String.format("%.2f", carritoCompra.getTotal()));
            System.out.println("  - Descuento aplicado: $" + String.format("%.2f", carritoCompra.getDescuento()));
            System.out.println("  - Total final: $" + String.format("%.2f", carritoCompra.getTotalConDescuento()));
            
            // Mostrar detalles de cada item
            if (!carritoCompra.getItems().isEmpty()) {
                System.out.println("  - Detalles del carrito:");
                for (ItemCarrito item : carritoCompra.getItems()) {
                    System.out.println("    * " + item.getProducto().getNombre() + 
                                     " x" + item.getCantidad() + 
                                     " = $" + String.format("%.2f", item.getSubtotal()));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al recalcular precios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la interfaz de usuario con los nuevos precios
     */
    private void actualizarInterfaz() {
        try {
            if (selectorProductos != null) {
                selectorProductos.actualizarInterfaz();
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar interfaz: " + e.getMessage());
        }
    }

    /**
     * Obtiene el último total calculado
     */
    public double getUltimoTotalCalculado() {
        return ultimoTotalCalculado;
    }

    /**
     * Obtiene la última cantidad de items calculada
     */
    public int getUltimaCantidadItems() {
        return ultimaCantidadItems;
    }

    /**
     * Fuerza un recálculo inmediato de los precios
     */
    public void forzarRecalculo() {
        synchronized (carritoCompra) {
            recalcularPrecios();
            actualizarInterfaz();
        }
    }
}
