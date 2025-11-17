package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.CompraDAO;
import com.siap.tianguistenco.datos.MetodoPagoDAO;
import com.siap.tianguistenco.model.*;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de procesar el pago y finalizar la compra
 * Simula una pasarela de pago y cierra la sesión del usuario
 */
public class FinalizadorCompra implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CarritoCompra carritoCompra;
    private final GestorSesion gestorSesion;
    private final GeneradorTicket generadorTicket;
    private final CompraDAO compraDAO;
    private final MetodoPagoDAO metodoPagoDAO;
    private int usuarioId;
    private boolean compraEnProceso;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public FinalizadorCompra(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, 
                            CarritoCompra carritoCompra, GestorSesion gestorSesion, 
                            GeneradorTicket generadorTicket) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.carritoCompra = carritoCompra;
        this.gestorSesion = gestorSesion;
        this.generadorTicket = generadorTicket;
        this.compraDAO = new CompraDAO();
        this.metodoPagoDAO = new MetodoPagoDAO();
        this.compraEnProceso = false;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public void run() {
        try {
            System.out.println("FinalizadorCompra iniciado");

            // Esperar a que el usuario se autentique
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            // Monitorear solicitudes de pago
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("FinalizadorCompra interrumpido");
        } catch (Exception e) {
            System.err.println("Error en FinalizadorCompra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicia el proceso de finalización de compra
     */
    public void procesarCompra() {
        if (compraEnProceso) {
            JOptionPane.showMessageDialog(null, 
                "Ya hay una compra en proceso", 
                "Compra en Proceso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (carritoCompra.estaVacio()) {
            JOptionPane.showMessageDialog(null, 
                "El carrito está vacío", 
                "Carrito Vacío", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        compraEnProceso = true;
        
        try {
            // Mostrar resumen de la compra
            mostrarResumenCompra();
            
            // Simular procesamiento de pago
            if (simularProcesamientoPago()) {
                // Generar ticket
                if (generadorTicket != null) {
                    generadorTicket.generarTicket(carritoCompra);
                }
                
                // Limpiar carrito
                carritoCompra.limpiar();
                
                // Mostrar confirmación
                mostrarConfirmacionCompra();
                
                // Cerrar sesión después de un delay
                cerrarSesionDespuesDeCompra();
                
            } else {
                JOptionPane.showMessageDialog(null, 
                    "El pago no pudo ser procesado", 
                    "Error de Pago", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Error al procesar compra: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al procesar la compra: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            compraEnProceso = false;
        }
    }

    /**
     * Muestra el resumen de la compra antes del pago
     */
    private void mostrarResumenCompra() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESUMEN DE COMPRA ===\n\n");
        
        for (ItemCarrito item : carritoCompra.getItems()) {
            resumen.append(String.format("%s x%d = %s\n", 
                item.getProducto().getNombre(),
                item.getCantidad(),
                formatoMoneda.format(item.getSubtotal())));
        }
        
        resumen.append("\n");
        resumen.append("Subtotal: ").append(formatoMoneda.format(carritoCompra.getTotal())).append("\n");
        resumen.append("Descuento: ").append(formatoMoneda.format(carritoCompra.getDescuento())).append("\n");
        resumen.append("TOTAL: ").append(formatoMoneda.format(carritoCompra.getTotalConDescuento())).append("\n");
        
        JOptionPane.showMessageDialog(null, 
            resumen.toString(), 
            "Resumen de Compra", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Simula el procesamiento del pago
     */
    private boolean simularProcesamientoPago() {
        // Mostrar diálogo de procesamiento
        JDialog dialog = new JDialog((Frame) null, "Procesando Pago", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel mensaje = new JLabel("Procesando su pago...", JLabel.CENTER);
        mensaje.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(mensaje, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        // Simular delay de procesamiento
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        dialog.dispose();
        
        // Simular éxito del pago (90% de probabilidad)
        return Math.random() > 0.1;
    }

    /**
     * Muestra la confirmación de compra exitosa
     */
    private void mostrarConfirmacionCompra() {
        JOptionPane.showMessageDialog(null, 
            "¡Compra realizada exitosamente!\n\n" +
            "Su ticket ha sido generado.\n" +
            "Gracias por su compra en SIAP Tianguistenco.", 
            "Compra Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cierra la sesión después de completar la compra
     */
    private void cerrarSesionDespuesDeCompra() {
        // Esperar un poco antes de cerrar la sesión
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (gestorSesion != null) {
            gestorSesion.cerrarSesion();
        }
        
        usuarioAutenticado.set(false);
        
        System.out.println("Sesión cerrada después de compra exitosa");
    }

    /**
     * Verifica si hay una compra en proceso
     */
    public boolean isCompraEnProceso() {
        return compraEnProceso;
    }

    /**
     * Cancela la compra en proceso
     */
    public void cancelarCompra() {
        if (compraEnProceso) {
            compraEnProceso = false;
            System.out.println("Compra cancelada por el usuario");
        }
    }

    /**
     * Guarda una compra en la base de datos
     */
    public Compra guardarCompra(String folio, Compra.TipoEnvio tipoEnvio, String direccionEnvio, 
                                double costoEnvio, Integer tarjetaId) {
        try {
            System.out.println("=== INICIANDO GUARDADO DE COMPRA ===");
            System.out.println("Usuario ID: " + usuarioId);
            System.out.println("Folio: " + folio);
            System.out.println("Total carrito: " + carritoCompra.getTotal());
            System.out.println("Descuento: " + carritoCompra.getDescuento());
            System.out.println("Items en carrito: " + carritoCompra.getItems().size());
            
            Compra compra = new Compra();
            compra.setUsuarioId(usuarioId);
            compra.setFolio(folio);
            compra.setFecha(LocalDateTime.now());
            compra.setTotal(carritoCompra.getTotal());
            compra.setDescuento(carritoCompra.getDescuento());
            compra.setEstado("COMPLETADA");
            compra.setTipoEnvio(tipoEnvio);
            compra.setDireccionEnvio(direccionEnvio);
            compra.setCostoEnvio(costoEnvio);

            // Convertir items del carrito a items de compra
            List<CompraItem> itemsCompra = new ArrayList<>();
            for (ItemCarrito itemCarrito : carritoCompra.getItems()) {
                CompraItem item = new CompraItem();
                item.setProductoId(itemCarrito.getProducto().getId());
                item.setNombreProducto(itemCarrito.getProducto().getNombre());
                item.setCantidad(itemCarrito.getCantidad());
                item.setPrecioUnitario(itemCarrito.getProducto().getPrecio());
                item.setSubtotal(itemCarrito.getSubtotal());
                itemsCompra.add(item);
                System.out.println("  - Item: " + item.getNombreProducto() + " x" + item.getCantidad());
            }
            compra.setItems(itemsCompra);

            // Guardar compra en BD
            System.out.println("Guardando compra en base de datos...");
            int compraId = compraDAO.guardarCompra(compra);
            if (compraId > 0) {
                compra.setId(compraId);
                System.out.println("Compra guardada con ID: " + compraId);

                // Guardar método de pago
                double montoTotal = compra.getTotalConDescuento();
                System.out.println("Guardando método de pago. Monto: " + montoTotal);
                boolean metodoPagoGuardado = false;
                if (tarjetaId != null) {
                    metodoPagoGuardado = metodoPagoDAO.registrarMetodoPago(compraId, "TARJETA", tarjetaId, montoTotal);
                    System.out.println("Método de pago TARJETA guardado: " + metodoPagoGuardado);
                } else {
                    metodoPagoGuardado = metodoPagoDAO.registrarMetodoPago(compraId, "EFECTIVO", null, montoTotal);
                    System.out.println("Método de pago EFECTIVO guardado: " + metodoPagoGuardado);
                }

                System.out.println("=== COMPRA GUARDADA EXITOSAMENTE ===");
                System.out.println("Folio: " + folio);
                System.out.println("ID: " + compraId);
                System.out.println("Total: $" + String.format("%.2f", montoTotal));
                return compra;
            } else {
                System.err.println("ERROR: No se pudo guardar la compra. compraId = " + compraId);
            }

        } catch (Exception e) {
            System.err.println("ERROR al guardar compra: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
