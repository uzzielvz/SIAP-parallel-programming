package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.CompraDAO;
import com.siap.tianguistenco.model.Compra;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de gestionar el historial de compras
 */
public class GestorHistorial implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CompraDAO compraDAO;
    private int usuarioId;

    public GestorHistorial(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, int usuarioId) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.compraDAO = new CompraDAO();
        this.usuarioId = usuarioId;
    }

    @Override
    public void run() {
        try {
            System.out.println("GestorHistorial iniciado para usuario: " + usuarioId);

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
            System.out.println("GestorHistorial interrumpido");
        } catch (Exception e) {
            System.err.println("Error en GestorHistorial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene todas las compras del usuario
     */
    public List<Compra> obtenerHistorialCompras() {
        return compraDAO.obtenerComprasPorUsuario(usuarioId);
    }

    /**
     * Obtiene una compra por su folio
     */
    public Compra obtenerCompraPorFolio(String folio) {
        Compra compra = compraDAO.obtenerCompraPorFolio(folio);
        // Verificar que la compra pertenece al usuario
        if (compra != null && compra.getUsuarioId() == usuarioId) {
            return compra;
        }
        return null;
    }

    /**
     * Genera un nuevo folio para una compra
     */
    public String generarFolio() {
        return compraDAO.generarFolio();
    }

    /**
     * Actualiza el ID del usuario
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}

