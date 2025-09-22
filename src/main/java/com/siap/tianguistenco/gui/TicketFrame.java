package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.CarritoCompra;
import com.siap.tianguistenco.model.ItemCarrito;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

/**
 * Ventana que muestra el ticket de compra generado
 */
public class TicketFrame extends JFrame {
    private final CarritoCompra carritoCompra;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public TicketFrame(CarritoCompra carritoCompra) {
        this.carritoCompra = carritoCompra;
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setTitle("SIAP Tianguistenco - Ticket de Compra");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(Color.WHITE);

        // Encabezado del ticket
        JPanel panelEncabezado = crearEncabezado();
        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);

        // Detalles de la compra
        JPanel panelDetalles = crearDetallesCompra();
        panelPrincipal.add(panelDetalles, BorderLayout.CENTER);

        // Pie del ticket
        JPanel panelPie = crearPieTicket();
        panelPrincipal.add(panelPie, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Botón de cerrar
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        botonCerrar.setBackground(new Color(0, 102, 204));
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setFocusPainted(false);
        botonCerrar.setPreferredSize(new Dimension(100, 35));
        botonCerrar.addActionListener(e -> dispose());
        panelBotones.add(botonCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Título principal
        JLabel titulo = new JLabel("SIAP TIANGUISTENCO", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 102, 204));
        panel.add(titulo, BorderLayout.NORTH);

        // Subtítulo
        JLabel subtitulo = new JLabel("E-commerce Multihilo", JLabel.CENTER);
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitulo.setForeground(new Color(100, 100, 100));
        panel.add(subtitulo, BorderLayout.CENTER);

        // Información de la compra
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        panelInfo.setBackground(Color.WHITE);

        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        JLabel etiquetaFecha = new JLabel("Fecha: " + fechaHora);
        etiquetaFecha.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfo.add(etiquetaFecha);

        JLabel etiquetaTicket = new JLabel("Ticket: #" + System.currentTimeMillis());
        etiquetaTicket.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfo.add(etiquetaTicket);

        JLabel etiquetaItems = new JLabel("Items: " + carritoCompra.getCantidadTotalItems());
        etiquetaItems.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfo.add(etiquetaItems);

        JLabel etiquetaCajero = new JLabel("Cajero: Sistema Automático");
        etiquetaCajero.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfo.add(etiquetaCajero);

        panel.add(panelInfo, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearDetallesCompra() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Detalles de la Compra"));

        // Tabla de productos
        String[] columnas = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        Object[][] datos = new Object[carritoCompra.getItems().size()][4];

        int fila = 0;
        for (ItemCarrito item : carritoCompra.getItems()) {
            datos[fila][0] = item.getProducto().getNombre();
            datos[fila][1] = item.getCantidad();
            datos[fila][2] = formatoMoneda.format(item.getProducto().getPrecio());
            datos[fila][3] = formatoMoneda.format(item.getSubtotal());
            fila++;
        }

        JTable tabla = new JTable(datos, columnas);
        tabla.setFont(new Font("Arial", Font.PLAIN, 11));
        tabla.setRowHeight(25);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setShowGrid(true);
        tabla.setEnabled(false); // Solo lectura

        // Configurar columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPieTicket() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;

        // Subtotal
        JLabel etiquetaSubtotal = new JLabel("Subtotal:");
        etiquetaSubtotal.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(etiquetaSubtotal, gbc);

        JLabel valorSubtotal = new JLabel(formatoMoneda.format(carritoCompra.getTotal()));
        valorSubtotal.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(valorSubtotal, gbc);

        // Descuento
        if (carritoCompra.getDescuento() > 0) {
            JLabel etiquetaDescuento = new JLabel("Descuento:");
            etiquetaDescuento.setFont(new Font("Arial", Font.PLAIN, 12));
            etiquetaDescuento.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(etiquetaDescuento, gbc);

            JLabel valorDescuento = new JLabel("-" + formatoMoneda.format(carritoCompra.getDescuento()));
            valorDescuento.setFont(new Font("Arial", Font.PLAIN, 12));
            valorDescuento.setForeground(Color.RED);
            gbc.gridx = 1;
            panel.add(valorDescuento, gbc);
        }

        // Total
        JLabel etiquetaTotal = new JLabel("TOTAL:");
        etiquetaTotal.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(etiquetaTotal, gbc);

        JLabel valorTotal = new JLabel(formatoMoneda.format(carritoCompra.getTotalConDescuento()));
        valorTotal.setFont(new Font("Arial", Font.BOLD, 14));
        valorTotal.setForeground(new Color(0, 102, 204));
        gbc.gridx = 1;
        panel.add(valorTotal, gbc);

        // Línea separadora
        JSeparator separador = new JSeparator();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separador, gbc);

        // Mensaje de agradecimiento
        JLabel mensaje = new JLabel("¡Gracias por su compra!", JLabel.CENTER);
        mensaje.setFont(new Font("Arial", Font.BOLD, 12));
        mensaje.setForeground(new Color(0, 150, 0));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(mensaje, gbc);

        return panel;
    }

    private void configurarVentana() {
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
}
