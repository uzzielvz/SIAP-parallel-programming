package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.gui.TicketFrame;
import com.siap.tianguistenco.model.CarritoCompra;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de generar tickets de compra
 * Crea un comprobante detallado de la compra realizada
 */
public class GeneradorTicket implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private TicketFrame ticketFrame;

    public GeneradorTicket(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
    }

    @Override
    public void run() {
        try {
            System.out.println("GeneradorTicket iniciado");

            // Esperar a que el usuario se autentique
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Mantener el hilo activo para generar tickets cuando sea necesario
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("GeneradorTicket interrumpido");
        } catch (Exception e) {
            System.err.println("Error en GeneradorTicket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera un ticket de compra
     */
    public void generarTicket(CarritoCompra carritoCompra) {
        try {
            System.out.println("Generando ticket de compra...");
            
            // Crear y mostrar la ventana del ticket
            ticketFrame = new TicketFrame(carritoCompra);
            ticketFrame.setVisible(true);
            
            // Log del ticket generado
            System.out.println("Ticket generado exitosamente:");
            System.out.println("  - Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("  - Items: " + carritoCompra.getCantidadTotalItems());
            System.out.println("  - Total: $" + String.format("%.2f", carritoCompra.getTotalConDescuento()));
            
        } catch (Exception e) {
            System.err.println("Error al generar ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana del ticket
     */
    public void cerrarTicket() {
        if (ticketFrame != null) {
            ticketFrame.dispose();
        }
    }
}
