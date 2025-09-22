package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.model.CarritoCompra;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de aplicar promociones y descuentos al carrito
 * Se coordina con el CalculadorPrecio para aplicar descuentos automáticamente
 */
public class AplicadorDescuentos implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CarritoCompra carritoCompra;
    private final CalculadorPrecio calculadorPrecio;
    private double ultimoDescuentoAplicado;
    private double ultimoTotalVerificado;

    // Reglas de descuento
    private static final double DESCUENTO_500 = 0.10; // 10% de descuento para compras > $500
    private static final double DESCUENTO_1000 = 0.15; // 15% de descuento para compras > $1000
    private static final double DESCUENTO_2000 = 0.20; // 20% de descuento para compras > $2000
    private static final double UMBRAL_500 = 500.0;
    private static final double UMBRAL_1000 = 1000.0;
    private static final double UMBRAL_2000 = 2000.0;

    public AplicadorDescuentos(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, 
                              CarritoCompra carritoCompra, CalculadorPrecio calculadorPrecio) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.carritoCompra = carritoCompra;
        this.calculadorPrecio = calculadorPrecio;
        this.ultimoDescuentoAplicado = 0.0;
        this.ultimoTotalVerificado = 0.0;
    }

    @Override
    public void run() {
        try {
            System.out.println("AplicadorDescuentos iniciado");

            // Esperar a que el usuario se autentique
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Monitorear cambios en el carrito y aplicar descuentos
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                synchronized (carritoCompra) {
                    // Verificar si el total ha cambiado
                    if (haCambiadoElTotal()) {
                        aplicarDescuentosApropiados();
                    }
                }

                // Esperar antes de la siguiente verificación
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("AplicadorDescuentos interrumpido");
        } catch (Exception e) {
            System.err.println("Error en AplicadorDescuentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica si el total del carrito ha cambiado desde la última verificación
     */
    private boolean haCambiadoElTotal() {
        double totalActual = carritoCompra.getTotal();
        boolean haCambiado = Math.abs(totalActual - ultimoTotalVerificado) > 0.01;
        
        if (haCambiado) {
            ultimoTotalVerificado = totalActual;
        }
        
        return haCambiado;
    }

    /**
     * Aplica los descuentos apropiados según el total del carrito
     */
    private void aplicarDescuentosApropiados() {
        double total = carritoCompra.getTotal();
        double descuentoCalculado = 0.0;
        String tipoDescuento = "";

        // Determinar el descuento apropiado
        if (total >= UMBRAL_2000) {
            descuentoCalculado = total * DESCUENTO_2000;
            tipoDescuento = "20% (Compra mayor a $2,000)";
        } else if (total >= UMBRAL_1000) {
            descuentoCalculado = total * DESCUENTO_1000;
            tipoDescuento = "15% (Compra mayor a $1,000)";
        } else if (total >= UMBRAL_500) {
            descuentoCalculado = total * DESCUENTO_500;
            tipoDescuento = "10% (Compra mayor a $500)";
        }

        // Aplicar el descuento si es diferente al anterior
        if (Math.abs(descuentoCalculado - ultimoDescuentoAplicado) > 0.01) {
            carritoCompra.aplicarDescuento(descuentoCalculado);
            ultimoDescuentoAplicado = descuentoCalculado;

            // Log del descuento aplicado
            if (descuentoCalculado > 0) {
                System.out.println("Descuento aplicado:");
                System.out.println("  - Tipo: " + tipoDescuento);
                System.out.println("  - Monto: $" + String.format("%.2f", descuentoCalculado));
                System.out.println("  - Total original: $" + String.format("%.2f", total));
                System.out.println("  - Total con descuento: $" + String.format("%.2f", carritoCompra.getTotalConDescuento()));
            } else {
                System.out.println("Sin descuento aplicable - Total: $" + String.format("%.2f", total));
            }

            // Notificar al calculador de precios para actualizar la interfaz
            if (calculadorPrecio != null) {
                calculadorPrecio.forzarRecalculo();
            }
        }
    }

    /**
     * Obtiene el último descuento aplicado
     */
    public double getUltimoDescuentoAplicado() {
        return ultimoDescuentoAplicado;
    }

    /**
     * Obtiene el último total verificado
     */
    public double getUltimoTotalVerificado() {
        return ultimoTotalVerificado;
    }

    /**
     * Aplica un descuento personalizado (para promociones especiales)
     */
    public void aplicarDescuentoPersonalizado(double porcentaje, String descripcion) {
        synchronized (carritoCompra) {
            double total = carritoCompra.getTotal();
            double descuento = total * (porcentaje / 100.0);
            
            carritoCompra.aplicarDescuento(descuento);
            ultimoDescuentoAplicado = descuento;
            
            System.out.println("Descuento personalizado aplicado:");
            System.out.println("  - Descripción: " + descripcion);
            System.out.println("  - Porcentaje: " + porcentaje + "%");
            System.out.println("  - Monto: $" + String.format("%.2f", descuento));
            
            if (calculadorPrecio != null) {
                calculadorPrecio.forzarRecalculo();
            }
        }
    }

    /**
     * Remueve todos los descuentos aplicados
     */
    public void removerDescuentos() {
        synchronized (carritoCompra) {
            carritoCompra.aplicarDescuento(0.0);
            ultimoDescuentoAplicado = 0.0;
            
            System.out.println("Todos los descuentos han sido removidos");
            
            if (calculadorPrecio != null) {
                calculadorPrecio.forzarRecalculo();
            }
        }
    }
}
