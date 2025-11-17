package com.siap.tianguistenco.threads;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de calcular y gestionar envíos a domicilio
 */
public class GestorEnvio implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private static final double COSTO_ENVIO_BASE = 50.00;
    private static final double COSTO_ENVIO_GRATIS_MINIMO = 500.00;

    public GestorEnvio(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
    }

    @Override
    public void run() {
        try {
            System.out.println("GestorEnvio iniciado");

            // Esperar a que el usuario esté autenticado
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Mantener el hilo activo
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("GestorEnvio interrumpido");
        } catch (Exception e) {
            System.err.println("Error en GestorEnvio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcula el costo de envío según el monto total
     * Envío gratis para compras mayores a $500
     */
    public double calcularCostoEnvio(double montoTotal) {
        if (montoTotal >= COSTO_ENVIO_GRATIS_MINIMO) {
            System.out.println("Envío gratis aplicado (compra mayor a $" + COSTO_ENVIO_GRATIS_MINIMO + ")");
            return 0.0;
        }
        return COSTO_ENVIO_BASE;
    }

    /**
     * Valida una dirección de envío
     */
    public boolean validarDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return false;
        }
        // Validación básica: debe tener al menos 20 caracteres (dirección completa)
        return direccion.trim().length() >= 20;
    }
}

