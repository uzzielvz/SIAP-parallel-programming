package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.model.ItemCarrito;
import com.siap.tianguistenco.model.Tarjeta;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Diálogo de confirmación de pago con resumen y botón PAGAR
 */
public class ConfirmacionPagoDialog extends JDialog {
    private boolean pagoConfirmado = false;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    
    public ConfirmacionPagoDialog(Frame parent, java.util.List<ItemCarrito> items, 
                                  double total, double descuento, double costoEnvio,
                                  Tarjeta tarjeta, Compra.TipoEnvio tipoEnvio, String direccionEnvio) {
        super(parent, "Confirmar Pago", true);
        System.out.println("ConfirmacionPagoDialog creado");
        inicializarComponentes(items, total, descuento, costoEnvio, tarjeta, tipoEnvio, direccionEnvio);
        System.out.println("ConfirmacionPagoDialog inicializado");
    }
    
    private void inicializarComponentes(java.util.List<ItemCarrito> items, double total, 
                                       double descuento, double costoEnvio, Tarjeta tarjeta,
                                       Compra.TipoEnvio tipoEnvio, String direccionEnvio) {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titulo = new JLabel("Confirmar Pago", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 102, 204));
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        
        // Panel central con scroll
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Resumen de productos
        JTextArea areaResumen = new JTextArea();
        areaResumen.setEditable(false);
        areaResumen.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaResumen.setBackground(Color.WHITE);
        
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESUMEN DE COMPRA ===\n\n");
        
        for (ItemCarrito item : items) {
            resumen.append(String.format("%-35s x%2d = %10s\n",
                item.getProducto().getNombre().substring(0, Math.min(35, item.getProducto().getNombre().length())),
                item.getCantidad(),
                formatoMoneda.format(item.getSubtotal())));
        }
        
        resumen.append("\n");
        resumen.append("Subtotal: ").append(formatoMoneda.format(total)).append("\n");
        if (descuento > 0) {
            resumen.append("Descuento: -").append(formatoMoneda.format(descuento)).append("\n");
        }
        if (costoEnvio > 0) {
            resumen.append("Costo de Envío: ").append(formatoMoneda.format(costoEnvio)).append("\n");
        } else if (tipoEnvio == Compra.TipoEnvio.DOMICILIO) {
            resumen.append("Costo de Envío: GRATIS\n");
        }
        double totalFinal = total - descuento + costoEnvio;
        resumen.append("─────────────────────────────\n");
        resumen.append("TOTAL: ").append(formatoMoneda.format(totalFinal)).append("\n\n");
        
        resumen.append("=== MÉTODO DE PAGO ===\n");
        if (tarjeta != null) {
            resumen.append("Tarjeta: ").append(tarjeta.toString()).append("\n");
        } else {
            resumen.append("Método: Efectivo\n");
        }
        
        resumen.append("\n=== TIPO DE ENVÍO ===\n");
        resumen.append("Tipo: ").append(tipoEnvio.name()).append("\n");
        if (direccionEnvio != null && !direccionEnvio.isEmpty()) {
            resumen.append("Dirección:\n").append(direccionEnvio).append("\n");
        }
        
        areaResumen.setText(resumen.toString());
        JScrollPane scrollResumen = new JScrollPane(areaResumen);
        scrollResumen.setPreferredSize(new Dimension(550, 350));
        
        panelCentral.add(scrollResumen, BorderLayout.CENTER);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton botonPagar = new JButton("PAGAR");
        botonPagar.setFont(new Font("Arial", Font.BOLD, 16));
        botonPagar.setBackground(new Color(40, 167, 69));
        botonPagar.setForeground(Color.WHITE);
        botonPagar.setBorderPainted(false);
        botonPagar.setFocusPainted(false);
        botonPagar.setPreferredSize(new Dimension(150, 45));
        botonPagar.addActionListener(e -> {
            System.out.println("Botón PAGAR presionado");
            pagoConfirmado = true;
            System.out.println("pagoConfirmado establecido a: " + pagoConfirmado);
            dispose();
            System.out.println("Diálogo cerrado");
        });
        
        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.setFocusPainted(false);
        botonCancelar.setPreferredSize(new Dimension(120, 45));
        botonCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(botonPagar);
        panelBotones.add(botonCancelar);
        
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    public boolean isPagoConfirmado() {
        return pagoConfirmado;
    }
}

