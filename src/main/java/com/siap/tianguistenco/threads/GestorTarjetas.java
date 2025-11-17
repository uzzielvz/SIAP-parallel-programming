package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.TarjetaDAO;
import com.siap.tianguistenco.model.Tarjeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de gestionar las tarjetas de pago
 * Maneja el registro, validación y procesamiento de pagos con tarjetas
 */
public class GestorTarjetas implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final TarjetaDAO tarjetaDAO;
    private int usuarioId;

    public GestorTarjetas(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, int usuarioId) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.tarjetaDAO = new TarjetaDAO();
        this.usuarioId = usuarioId;
    }

    @Override
    public void run() {
        try {
            System.out.println("GestorTarjetas iniciado para usuario: " + usuarioId);

            // Esperar a que el usuario esté autenticado
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Mantener el hilo activo para procesar solicitudes de tarjetas
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("GestorTarjetas interrumpido");
        } catch (Exception e) {
            System.err.println("Error en GestorTarjetas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Registra una nueva tarjeta para el usuario
     */
    public boolean registrarTarjeta(String numeroTarjeta, String nombreTitular, 
                                    String fechaVencimiento, Tarjeta.TipoTarjeta tipo) {
        try {
            // Validar formato de tarjeta (básico)
            if (!validarNumeroTarjeta(numeroTarjeta)) {
                System.out.println("Número de tarjeta inválido");
                return false;
            }

            boolean registrada = tarjetaDAO.registrarTarjeta(usuarioId, numeroTarjeta, nombreTitular, 
                                                             fechaVencimiento, tipo);
            if (registrada) {
                System.out.println("Tarjeta registrada exitosamente para usuario: " + usuarioId);
            }
            return registrada;

        } catch (Exception e) {
            System.err.println("Error al registrar tarjeta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las tarjetas activas del usuario
     */
    public List<Tarjeta> obtenerTarjetasUsuario() {
        return tarjetaDAO.obtenerTarjetasPorUsuario(usuarioId);
    }

    /**
     * Procesa un pago con una tarjeta
     */
    public boolean procesarPagoConTarjeta(int tarjetaId, double monto) {
        try {
            Tarjeta tarjeta = tarjetaDAO.obtenerTarjetaPorId(tarjetaId);
            if (tarjeta == null || !tarjeta.isActiva()) {
                System.out.println("Tarjeta no válida o inactiva");
                return false;
            }

            // Simular procesamiento de pago (validación de tarjeta, saldo, etc.)
            System.out.println("Procesando pago con tarjeta: " + tarjeta.getNumeroEnmascarado());
            System.out.println("Monto: $" + String.format("%.2f", monto));

            // Simular delay de procesamiento
            Thread.sleep(1500);

            // Simular éxito del pago (95% de probabilidad)
            boolean exito = Math.random() > 0.05;
            
            if (exito) {
                System.out.println("Pago procesado exitosamente");
            } else {
                System.out.println("Error al procesar el pago");
            }

            return exito;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            System.err.println("Error al procesar pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida el formato de un número de tarjeta (básico)
     */
    private boolean validarNumeroTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.trim().isEmpty()) {
            return false;
        }
        // Remover espacios y guiones
        String numeroLimpio = numeroTarjeta.replaceAll("[\\s-]", "");
        // Debe tener entre 13 y 19 dígitos
        return numeroLimpio.matches("\\d{13,19}");
    }

    /**
     * Actualiza el ID del usuario
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}

