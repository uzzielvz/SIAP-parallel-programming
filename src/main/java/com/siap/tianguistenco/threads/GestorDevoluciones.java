package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.DevolucionDAO;
import com.siap.tianguistenco.model.Devolucion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de procesar devoluciones de compras
 */
public class GestorDevoluciones implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final DevolucionDAO devolucionDAO;
    private int usuarioId;

    public GestorDevoluciones(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, int usuarioId) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.devolucionDAO = new DevolucionDAO();
        this.usuarioId = usuarioId;
    }

    @Override
    public void run() {
        try {
            System.out.println("GestorDevoluciones iniciado para usuario: " + usuarioId);

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
            System.out.println("GestorDevoluciones interrumpido");
        } catch (Exception e) {
            System.err.println("Error en GestorDevoluciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Procesa una devolución
     */
    public int procesarDevolucion(int compraId, String folioCompra, Devolucion.MotivoDevolucion motivo, 
                                  double montoDevolucion, String observaciones) {
        try {
            Devolucion devolucion = new Devolucion();
            devolucion.setCompraId(compraId);
            devolucion.setFolioCompra(folioCompra);
            devolucion.setFecha(LocalDateTime.now());
            devolucion.setMotivo(motivo);
            devolucion.setEstado("PENDIENTE");
            devolucion.setMontoDevolucion(montoDevolucion);
            devolucion.setObservaciones(observaciones);

            int devolucionId = devolucionDAO.registrarDevolucion(devolucion);
            
            if (devolucionId > 0) {
                System.out.println("Devolución registrada: ID=" + devolucionId + ", Folio=" + folioCompra);
                
                // Simular procesamiento de devolución
                Thread.sleep(1000);
                
                // Actualizar estado a PROCESADA
                devolucionDAO.actualizarEstadoDevolucion(devolucionId, "PROCESADA");
                System.out.println("Devolución procesada exitosamente");
            }

            return devolucionId;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (Exception e) {
            System.err.println("Error al procesar devolución: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Obtiene todas las devoluciones del usuario
     */
    public List<Devolucion> obtenerDevolucionesUsuario() {
        return devolucionDAO.obtenerDevolucionesPorUsuario(usuarioId);
    }

    /**
     * Obtiene una devolución por su ID
     */
    public Devolucion obtenerDevolucionPorId(int devolucionId) {
        return devolucionDAO.obtenerDevolucionPorId(devolucionId);
    }

    /**
     * Actualiza el ID del usuario
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}

