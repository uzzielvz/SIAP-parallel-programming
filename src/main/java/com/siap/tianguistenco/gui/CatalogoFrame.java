package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.datos.ProductoDAO;
import com.siap.tianguistenco.model.*;
import com.siap.tianguistenco.threads.SelectorProductos;

import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Ventana principal del catálogo de productos con carrito de compras
 */
public class CatalogoFrame extends JFrame {
    private final SelectorProductos selectorProductos;
    private final CarritoCompra carritoCompra;
    private final ProductoDAO productoDAO;
    private JTabbedPane tabbedPane;
    private JList<ItemCarrito> listaCarrito;
    private JLabel etiquetaTotal;
    private JLabel etiquetaDescuento;
    private JLabel etiquetaTotalFinal;
    private JButton botonPagar;
    private JButton botonVerTicket;
    private DefaultListModel<ItemCarrito> modeloListaCarrito;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public CatalogoFrame(SelectorProductos selectorProductos, CarritoCompra carritoCompra, ProductoDAO productoDAO) {
        this.selectorProductos = selectorProductos;
        this.carritoCompra = carritoCompra;
        this.productoDAO = productoDAO;
        this.modeloListaCarrito = new DefaultListModel<>();
        
        inicializarComponentes();
        configurarVentana();
        cargarProductos();
        actualizarCarrito();
    }

    private void inicializarComponentes() {
        setTitle("SIAP Tianguistenco - Catálogo de Productos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel principal con división
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.7);

        // Panel izquierdo - Catálogo
        JPanel panelCatalogo = new JPanel(new BorderLayout());
        panelCatalogo.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos"));

        // Crear pestañas por categoría
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        panelCatalogo.add(tabbedPane, BorderLayout.CENTER);

        // Panel derecho - Carrito
        JPanel panelCarrito = crearPanelCarrito();
        panelCarrito.setPreferredSize(new Dimension(350, 0));

        splitPane.setLeftComponent(panelCatalogo);
        splitPane.setRightComponent(panelCarrito);

        add(splitPane, BorderLayout.CENTER);

        // Barra de estado
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstado.setBorder(BorderFactory.createLoweredBevelBorder());
        JLabel estado = new JLabel("Sistema multihilo activo - Carrito: " + carritoCompra.getCantidadTotalItems() + " items");
        panelEstado.add(estado);
        add(panelEstado, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));

        // Lista del carrito
        listaCarrito = new JList<>(modeloListaCarrito);
        listaCarrito.setCellRenderer(new ItemCarritoRenderer());
        listaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollCarrito = new JScrollPane(listaCarrito);
        scrollCarrito.setPreferredSize(new Dimension(300, 300));
        panel.add(scrollCarrito, BorderLayout.CENTER);

        // Panel de totales
        JPanel panelTotales = new JPanel(new GridBagLayout());
        panelTotales.setBorder(BorderFactory.createTitledBorder("Resumen"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        etiquetaTotal = new JLabel("Subtotal: $0.00");
        etiquetaTotal.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelTotales.add(etiquetaTotal, gbc);

        etiquetaDescuento = new JLabel("Descuento: $0.00");
        etiquetaDescuento.setFont(new Font("Arial", Font.PLAIN, 11));
        etiquetaDescuento.setForeground(Color.RED);
        gbc.gridy = 1;
        panelTotales.add(etiquetaDescuento, gbc);

        etiquetaTotalFinal = new JLabel("TOTAL: $0.00");
        etiquetaTotalFinal.setFont(new Font("Arial", Font.BOLD, 14));
        etiquetaTotalFinal.setForeground(new Color(0, 102, 204));
        gbc.gridy = 2;
        panelTotales.add(etiquetaTotalFinal, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        // Botón de pago
        botonPagar = new JButton("Proceder al Pago");
        botonPagar.setFont(new Font("Arial", Font.BOLD, 12));
        botonPagar.setBackground(new Color(40, 167, 69));
        botonPagar.setForeground(Color.WHITE);
        botonPagar.setBorderPainted(false);
        botonPagar.setFocusPainted(false);
        botonPagar.setPreferredSize(new Dimension(150, 35));
        botonPagar.addActionListener(e -> procesarPago());
        
        // Botón ver ticket
        botonVerTicket = new JButton("Ver Ticket");
        botonVerTicket.setFont(new Font("Arial", Font.BOLD, 12));
        botonVerTicket.setBackground(new Color(0, 102, 204));
        botonVerTicket.setForeground(Color.WHITE);
        botonVerTicket.setBorderPainted(false);
        botonVerTicket.setFocusPainted(false);
        botonVerTicket.setPreferredSize(new Dimension(120, 35));
        botonVerTicket.addActionListener(e -> generarTicket());
        botonVerTicket.setVisible(!carritoCompra.estaVacio());
        
        panelBotones.add(botonPagar);
        panelBotones.add(botonVerTicket);
        
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panelTotales.add(panelBotones, gbc);

        panel.add(panelTotales, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarProductos() {
        // Obtener categorías de la base de datos
        List<String> categorias = productoDAO.obtenerCategorias();
        
        for (String categoria : categorias) {
            JPanel panelCategoria = new JPanel(new GridLayout(0, 3, 10, 10));
            panelCategoria.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Obtener productos de esta categoría
            List<Producto> productos = productoDAO.obtenerProductosPorCategoria(categoria);
            
            for (Producto producto : productos) {
                JPanel tarjetaProducto = crearTarjetaProducto(producto);
                panelCategoria.add(tarjetaProducto);
            }

            JScrollPane scrollCategoria = new JScrollPane(panelCategoria);
            scrollCategoria.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollCategoria.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            tabbedPane.addTab(categoria, scrollCategoria);
        }
    }

    private JPanel crearTarjetaProducto(Producto producto) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tarjeta.setPreferredSize(new Dimension(200, 150));
        tarjeta.setBackground(Color.WHITE);

        // Imagen placeholder
        JLabel imagen = new JLabel("📦", JLabel.CENTER);
        imagen.setFont(new Font("Arial", Font.PLAIN, 48));
        imagen.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        imagen.setPreferredSize(new Dimension(80, 80));
        tarjeta.add(imagen, BorderLayout.NORTH);

        // Información del producto
        JPanel panelInfo = new JPanel(new BorderLayout());
        
        JLabel nombre = new JLabel("<html><div style='text-align: center;'>" + 
            producto.getNombre().replaceAll("(.{20})", "$1<br/>") + "</div></html>");
        nombre.setFont(new Font("Arial", Font.BOLD, 10));
        nombre.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(nombre, BorderLayout.NORTH);

        JLabel precio = new JLabel(formatoMoneda.format(producto.getPrecio()));
        precio.setFont(new Font("Arial", Font.BOLD, 12));
        precio.setForeground(new Color(0, 102, 204));
        precio.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(precio, BorderLayout.CENTER);

        // Botón agregar
        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.setFont(new Font("Arial", Font.BOLD, 10));
        botonAgregar.setBackground(new Color(0, 102, 204));
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setBorderPainted(false);
        botonAgregar.setFocusPainted(false);
        botonAgregar.addActionListener(e -> {
            selectorProductos.agregarAlCarrito(producto);
            actualizarCarrito();
        });

        panelInfo.add(botonAgregar, BorderLayout.SOUTH);
        tarjeta.add(panelInfo, BorderLayout.CENTER);

        return tarjeta;
    }

    private void configurarVentana() {
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void actualizarCarrito() {
        SwingUtilities.invokeLater(() -> {
            modeloListaCarrito.clear();
            for (ItemCarrito item : carritoCompra.getItems()) {
                modeloListaCarrito.addElement(item);
            }

            etiquetaTotal.setText("Subtotal: " + formatoMoneda.format(carritoCompra.getTotal()));
            etiquetaDescuento.setText("Descuento: " + formatoMoneda.format(carritoCompra.getDescuento()));
            etiquetaTotalFinal.setText("TOTAL: " + formatoMoneda.format(carritoCompra.getTotalConDescuento()));

            boolean carritoVacio = carritoCompra.estaVacio();
            botonPagar.setEnabled(!carritoVacio);
            botonVerTicket.setVisible(!carritoVacio);
        });
    }

    private void procesarPago() {
        if (carritoCompra.estaVacio()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar la compra
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Desea proceder con el pago?\n\n" +
            "Total: " + formatoMoneda.format(carritoCompra.getTotalConDescuento()),
            "Confirmar Compra",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            // Activar el proceso de pago completo
            activarProcesoPago();
        }
    }
    
    private void activarProcesoPago() {
        // Crear un nuevo hilo para el proceso de pago
        Thread procesoPago = new Thread(() -> {
            try {
                // Paso 1: Mostrar resumen de la compra
                SwingUtilities.invokeLater(() -> mostrarResumenCompra());
                
                // Paso 2: Simular procesamiento de pago
                if (simularProcesamientoPago()) {
                    // Paso 3: Generar ticket
                    SwingUtilities.invokeLater(() -> generarTicket());
                    
                    // Paso 4: Limpiar carrito
                    carritoCompra.limpiar();
                    SwingUtilities.invokeLater(() -> actualizarCarrito());
                    
                    // Paso 5: Mostrar confirmación
                    SwingUtilities.invokeLater(() -> mostrarConfirmacionCompra());
                    
                    // Paso 6: Cerrar sesión después de un delay
                    cerrarSesionDespuesDeCompra();
                } else {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(this, 
                            "El pago no pudo ser procesado", 
                            "Error de Pago", 
                            JOptionPane.ERROR_MESSAGE));
                }
            } catch (Exception e) {
                System.err.println("Error en proceso de pago: " + e.getMessage());
                e.printStackTrace();
            }
        }, "ProcesoPago");
        
        procesoPago.start();
    }
    
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
        
        JOptionPane.showMessageDialog(this, 
            resumen.toString(), 
            "Resumen de Compra", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean simularProcesamientoPago() {
        // Mostrar mensaje de procesamiento simple
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "Procesando su pago...\n\nPor favor espere un momento.", 
                "Procesando Pago", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Simular delay de procesamiento
        try {
            Thread.sleep(2000); // Reducido a 2 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simular éxito del pago (90% de probabilidad)
        return Math.random() > 0.1;
    }
    
    private void generarTicket() {
        // Crear ventana de ticket
        JDialog ticketDialog = new JDialog(this, "Ticket de Compra", true);
        ticketDialog.setSize(600, 500);
        ticketDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Encabezado
        JLabel titulo = new JLabel("SIAP TIANGUISTENCO", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(0, 102, 204));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Detalles de la compra
        JTextArea detalles = new JTextArea();
        detalles.setEditable(false);
        detalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        StringBuilder ticket = new StringBuilder();
        ticket.append("Fecha: ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        ticket.append("Ticket: #").append(System.currentTimeMillis()).append("\n\n");
        
        for (ItemCarrito item : carritoCompra.getItems()) {
            ticket.append(String.format("%-30s x%2d = %8s\n",
                item.getProducto().getNombre().substring(0, Math.min(30, item.getProducto().getNombre().length())),
                item.getCantidad(),
                formatoMoneda.format(item.getSubtotal())));
        }
        
        ticket.append("\n");
        ticket.append("Subtotal: ").append(formatoMoneda.format(carritoCompra.getTotal())).append("\n");
        ticket.append("Descuento: ").append(formatoMoneda.format(carritoCompra.getDescuento())).append("\n");
        ticket.append("TOTAL: ").append(formatoMoneda.format(carritoCompra.getTotalConDescuento())).append("\n\n");
        ticket.append("¡Gracias por su compra!");
        
        detalles.setText(ticket.toString());
        panel.add(new JScrollPane(detalles), BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        // Botón guardar ticket
        JButton guardar = new JButton("Guardar Ticket");
        guardar.setBackground(new Color(0, 102, 204));
        guardar.setForeground(Color.WHITE);
        guardar.setBorderPainted(false);
        guardar.setFocusPainted(false);
        guardar.addActionListener(e -> guardarTicket(ticket.toString()));
        
        // Botón imprimir
        JButton imprimir = new JButton("Imprimir");
        imprimir.setBackground(new Color(40, 167, 69));
        imprimir.setForeground(Color.WHITE);
        imprimir.setBorderPainted(false);
        imprimir.setFocusPainted(false);
        imprimir.addActionListener(e -> imprimirTicket(detalles));
        
        // Botón cerrar
        JButton cerrar = new JButton("Cerrar");
        cerrar.setBackground(new Color(108, 117, 125));
        cerrar.setForeground(Color.WHITE);
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.addActionListener(e -> ticketDialog.dispose());
        
        panelBotones.add(guardar);
        panelBotones.add(imprimir);
        panelBotones.add(cerrar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        ticketDialog.add(panel);
        ticketDialog.setVisible(true);
    }
    
    private void mostrarConfirmacionCompra() {
        JOptionPane.showMessageDialog(this, 
            "¡Compra realizada exitosamente!\n\n" +
            "Su ticket ha sido generado.\n" +
            "Gracias por su compra en SIAP Tianguistenco.", 
            "Compra Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Guarda el ticket en un archivo de texto
     */
    private void guardarTicket(String contenidoTicket) {
        try {
            // Crear nombre de archivo con timestamp
            String nombreArchivo = "ticket_" + 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            
            // Crear directorio de tickets si no existe
            java.io.File directorioTickets = new java.io.File("tickets");
            if (!directorioTickets.exists()) {
                directorioTickets.mkdirs();
            }
            
            // Escribir archivo
            java.io.File archivo = new java.io.File(directorioTickets, nombreArchivo);
            try (java.io.FileWriter writer = new java.io.FileWriter(archivo)) {
                writer.write(contenidoTicket);
            }
            
            JOptionPane.showMessageDialog(this,
                "Ticket guardado exitosamente en:\n" + archivo.getAbsolutePath(),
                "Ticket Guardado",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el ticket:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Imprime el ticket
     */
    private void imprimirTicket(JTextArea detalles) {
        try {
            // Crear un documento imprimible
            javax.print.attribute.PrintRequestAttributeSet pras = 
                new javax.print.attribute.HashPrintRequestAttributeSet();
            
            // Mostrar diálogo de impresión
            javax.print.PrintService[] services = 
                javax.print.PrintServiceLookup.lookupPrintServices(null, pras);
            
            if (services.length > 0) {
                javax.print.PrintService service = 
                    javax.print.PrintServiceLookup.lookupDefaultPrintService();
                
                if (service != null) {
                    javax.print.SimpleDoc doc = new javax.print.SimpleDoc(
                        detalles.getText(), 
                        javax.print.DocFlavor.STRING.TEXT_PLAIN, 
                        null);
                    
                    javax.print.DocPrintJob job = service.createPrintJob();
                    job.print(doc, pras);
                    
                    JOptionPane.showMessageDialog(this,
                        "Ticket enviado a impresión",
                        "Impresión",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se encontró impresora disponible",
                        "Error de Impresión",
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron impresoras disponibles",
                    "Error de Impresión",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al imprimir el ticket:\n" + e.getMessage(),
                "Error de Impresión",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cerrarSesionDespuesDeCompra() {
        // Esperar un poco antes de mostrar opciones
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mostrar opciones al usuario
        SwingUtilities.invokeLater(() -> {
            int opcion = JOptionPane.showOptionDialog(this,
                "¿Qué desea hacer ahora?",
                "Compra Completada",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Nueva Compra", "Ver Ticket", "Salir"},
                "Nueva Compra");
            
            switch (opcion) {
                case 0: // Nueva Compra
                    // Limpiar carrito y continuar
                    carritoCompra.limpiar();
                    actualizarCarrito();
                    break;
                case 1: // Ver Ticket
                    generarTicket();
                    break;
                case 2: // Salir
                    System.exit(0);
                    break;
                default:
                    // Nueva Compra por defecto
                    carritoCompra.limpiar();
                    actualizarCarrito();
                    break;
            }
        });
    }

    /**
     * Renderer personalizado para los items del carrito
     */
    private static class ItemCarritoRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ItemCarrito) {
                ItemCarrito item = (ItemCarrito) value;
                setText(String.format("%s x%d - %s", 
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    new DecimalFormat("$#,##0.00").format(item.getSubtotal())));
            }
            
            return this;
        }
    }
}
