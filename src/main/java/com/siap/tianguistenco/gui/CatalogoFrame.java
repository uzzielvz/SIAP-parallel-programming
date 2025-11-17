package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.datos.ProductoDAO;
import com.siap.tianguistenco.model.*;
import com.siap.tianguistenco.threads.*;

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
    private GestorTarjetas gestorTarjetas;
    private GestorEnvio gestorEnvio;
    private FinalizadorCompra finalizadorCompra;
    private GestorHistorial gestorHistorial;
    private GestorDevoluciones gestorDevoluciones;
    private JTabbedPane tabbedPane;
    private JList<ItemCarrito> listaCarrito;
    private JLabel etiquetaTotal;
    private JLabel etiquetaDescuento;
    private JLabel etiquetaTotalFinal;
    private JButton botonPagar;
    private JButton botonVerTicket;
    private JButton botonHistorial;
    private JButton botonDevoluciones;
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

    public void setGestores(GestorTarjetas gestorTarjetas, GestorEnvio gestorEnvio, 
                           FinalizadorCompra finalizadorCompra, GestorHistorial gestorHistorial,
                           GestorDevoluciones gestorDevoluciones) {
        this.gestorTarjetas = gestorTarjetas;
        this.gestorEnvio = gestorEnvio;
        this.finalizadorCompra = finalizadorCompra;
        this.gestorHistorial = gestorHistorial;
        this.gestorDevoluciones = gestorDevoluciones;
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

        // Botón historial
        botonHistorial = new JButton("Historial");
        botonHistorial.setFont(new Font("Arial", Font.BOLD, 12));
        botonHistorial.setBackground(new Color(108, 117, 125));
        botonHistorial.setForeground(Color.WHITE);
        botonHistorial.setBorderPainted(false);
        botonHistorial.setFocusPainted(false);
        botonHistorial.setPreferredSize(new Dimension(100, 35));
        botonHistorial.addActionListener(e -> abrirHistorial());

        // Botón devoluciones
        botonDevoluciones = new JButton("Devoluciones");
        botonDevoluciones.setFont(new Font("Arial", Font.BOLD, 12));
        botonDevoluciones.setBackground(new Color(220, 53, 69));
        botonDevoluciones.setForeground(Color.WHITE);
        botonDevoluciones.setBorderPainted(false);
        botonDevoluciones.setFocusPainted(false);
        botonDevoluciones.setPreferredSize(new Dimension(120, 35));
        botonDevoluciones.addActionListener(e -> abrirDevoluciones());
        
        panelBotones.add(botonPagar);
        panelBotones.add(botonVerTicket);
        panelBotones.add(botonHistorial);
        panelBotones.add(botonDevoluciones);
        
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
            // Usar GridBagLayout para 4 columnas con tamaño fijo
            JPanel panelCategoria = new JPanel(new GridBagLayout());
            panelCategoria.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panelCategoria.setBackground(new Color(245, 245, 245));

            // Obtener productos de esta categoría
            List<Producto> productos = productoDAO.obtenerProductosPorCategoria(categoria);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE; // No expandir para mantener tamaño fijo
            
            int columna = 0;
            int fila = 0;
            
            for (Producto producto : productos) {
                JPanel tarjetaProducto = crearTarjetaProducto(producto);
                
                gbc.gridx = columna;
                gbc.gridy = fila;
                panelCategoria.add(tarjetaProducto, gbc);
                
                columna++;
                if (columna >= 4) {
                    columna = 0;
                    fila++;
                }
            }

            JScrollPane scrollCategoria = new JScrollPane(panelCategoria);
            scrollCategoria.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollCategoria.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            tabbedPane.addTab(categoria, scrollCategoria);
        }
    }

    private JPanel crearTarjetaProducto(Producto producto) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        // Borde muy sutil o sin borde para un look más limpio
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Tamaño fijo y moderado independiente del número de productos
        Dimension tamanoFijo = new Dimension(220, 140);
        tarjeta.setPreferredSize(tamanoFijo);
        tarjeta.setMaximumSize(tamanoFijo);
        tarjeta.setMinimumSize(tamanoFijo);
        tarjeta.setBackground(Color.WHITE);

        // Información del producto (sin espacio para imagen)
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInfo.setBackground(Color.WHITE);
        
        // Nombre del producto (más grande y claro)
        JLabel nombre = new JLabel("<html><div style='text-align: center;'>" + 
            producto.getNombre().replaceAll("(.{30})", "$1<br/>") + "</div></html>");
        nombre.setFont(new Font("Arial", Font.BOLD, 13));
        nombre.setHorizontalAlignment(JLabel.CENTER);
        nombre.setForeground(new Color(30, 30, 30));
        panelInfo.add(nombre, BorderLayout.NORTH);

        // Precio (más prominente)
        JLabel precio = new JLabel(formatoMoneda.format(producto.getPrecio()));
        precio.setFont(new Font("Arial", Font.BOLD, 18));
        precio.setForeground(new Color(0, 102, 204));
        precio.setHorizontalAlignment(JLabel.CENTER);
        precio.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panelInfo.add(precio, BorderLayout.CENTER);

        // Botón agregar
        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.setFont(new Font("Arial", Font.BOLD, 13));
        botonAgregar.setBackground(new Color(40, 167, 69));
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setBorderPainted(false);
        botonAgregar.setFocusPainted(false);
        botonAgregar.setPreferredSize(new Dimension(180, 40));
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
                // Paso 1: Seleccionar tipo de envío y PAGAR
                System.out.println("=== PASO 1: Seleccionando tipo de envío ===");
                final Compra.TipoEnvio[] tipoEnvio = {null};
                final String[] direccionEnvio = {null};
                final double[] costoEnvio = {0.0};
                
                if (gestorEnvio != null) {
                    EnvioDialog envioDialog = new EnvioDialog(this, gestorEnvio, 
                        carritoCompra.getTotalConDescuento());
                    SwingUtilities.invokeLater(() -> envioDialog.setVisible(true));
                    
                    // Esperar a que el usuario seleccione el tipo de envío y haga clic en PAGAR
                    System.out.println("Esperando selección de envío y clic en PAGAR...");
                    while (envioDialog.isVisible()) {
                        Thread.sleep(100);
                    }
                    
                    tipoEnvio[0] = envioDialog.getTipoEnvio();
                    if (tipoEnvio[0] == null) {
                        System.out.println("Usuario canceló la selección de envío");
                        // Usuario canceló
                        return;
                    }
                    direccionEnvio[0] = envioDialog.getDireccionEnvio();
                    costoEnvio[0] = envioDialog.getCostoEnvio();
                    System.out.println("Tipo de envío seleccionado: " + tipoEnvio[0] + ", Costo: " + costoEnvio[0]);
                } else {
                    tipoEnvio[0] = Compra.TipoEnvio.TIENDA;
                }
                
                // Paso 2: Seleccionar/Registrar tarjeta
                System.out.println("=== PASO 2: Seleccionando tarjeta ===");
                final Tarjeta[] tarjetaSeleccionada = {null};
                if (gestorTarjetas != null) {
                    System.out.println("GestorTarjetas disponible, mostrando diálogo...");
                    final TarjetaDialog[] tarjetaDialog = {new TarjetaDialog(this, gestorTarjetas)};
                    
                    // Mostrar diálogo de forma síncrona
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            tarjetaDialog[0].setVisible(true);
                            System.out.println("Diálogo de tarjeta visible");
                        });
                    } catch (Exception ex) {
                        System.err.println("Error al mostrar diálogo de tarjeta: " + ex.getMessage());
                        SwingUtilities.invokeLater(() -> tarjetaDialog[0].setVisible(true));
                    }
                    
                    // Esperar a que el usuario seleccione una tarjeta
                    System.out.println("Esperando selección de tarjeta...");
                    while (tarjetaDialog[0].isVisible()) {
                        Thread.sleep(100);
                    }
                    
                    tarjetaSeleccionada[0] = tarjetaDialog[0].getTarjetaSeleccionada();
                    System.out.println("Tarjeta seleccionada: " + (tarjetaSeleccionada[0] != null ? tarjetaSeleccionada[0].toString() : "null"));
                    if (tarjetaSeleccionada[0] == null) {
                        System.out.println("Usuario canceló la selección de tarjeta - ABORTANDO PAGO");
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(this, 
                                "Pago cancelado. Debe seleccionar una tarjeta para continuar.", 
                                "Pago Cancelado", 
                                JOptionPane.WARNING_MESSAGE));
                        return;
                    }
                } else {
                    System.out.println("GestorTarjetas no disponible, continuando sin tarjeta");
                }
                
                System.out.println("=== PROCESANDO PAGO ===");
                System.out.println("Estado actual:");
                System.out.println("  - gestorTarjetas: " + (gestorTarjetas != null ? "disponible" : "null"));
                System.out.println("  - tarjetaSeleccionada: " + (tarjetaSeleccionada[0] != null ? tarjetaSeleccionada[0].toString() : "null"));
                
                // Paso 3: Procesar pago con tarjeta
                double montoTotal = carritoCompra.getTotalConDescuento() + costoEnvio[0];
                System.out.println("=== PASO 3: Procesando pago ===");
                System.out.println("Monto total: " + montoTotal);
                boolean pagoExitoso = false;
                if (gestorTarjetas != null && tarjetaSeleccionada[0] != null) {
                    System.out.println("Procesando pago con tarjeta ID: " + tarjetaSeleccionada[0].getId());
                    pagoExitoso = gestorTarjetas.procesarPagoConTarjeta(
                        tarjetaSeleccionada[0].getId(), montoTotal);
                    System.out.println("Resultado del pago: " + pagoExitoso);
                } else {
                    System.out.println("Procesando pago en efectivo (gestorTarjetas o tarjeta no disponible)");
                    // Simular pago en efectivo
                    pagoExitoso = simularProcesamientoPago();
                    System.out.println("Resultado del pago: " + pagoExitoso);
                }
                
                System.out.println("Pago procesado. Resultado: " + pagoExitoso);
                
                if (pagoExitoso) {
                    System.out.println("=== PAGO EXITOSO - GUARDANDO COMPRA ===");
                    // Paso 4: Generar folio y guardar compra
                    final String[] folio = {null};
                    if (gestorHistorial != null) {
                        folio[0] = gestorHistorial.generarFolio();
                    } else {
                        folio[0] = "SIAP-" + System.currentTimeMillis();
                    }
                    
                    Compra compraGuardada = null;
                    if (finalizadorCompra != null) {
                        Integer tarjetaId = tarjetaSeleccionada[0] != null ? tarjetaSeleccionada[0].getId() : null;
                        System.out.println("Llamando a guardarCompra con:");
                        System.out.println("  - Folio: " + folio[0]);
                        System.out.println("  - Tipo Envío: " + tipoEnvio[0]);
                        System.out.println("  - Dirección: " + (direccionEnvio[0] != null ? direccionEnvio[0] : "N/A"));
                        System.out.println("  - Costo Envío: " + costoEnvio[0]);
                        System.out.println("  - Tarjeta ID: " + tarjetaId);
                        compraGuardada = finalizadorCompra.guardarCompra(
                            folio[0], tipoEnvio[0], direccionEnvio[0], costoEnvio[0], tarjetaId);
                        if (compraGuardada == null) {
                            System.err.println("ERROR: No se pudo guardar la compra. compraGuardada es null");
                            SwingUtilities.invokeLater(() -> 
                                JOptionPane.showMessageDialog(this, 
                                    "Error al guardar la compra en el historial.\n" +
                                    "La compra se completó pero no se guardó en la base de datos.", 
                                    "Advertencia", 
                                    JOptionPane.WARNING_MESSAGE));
                        } else {
                            System.out.println("Compra guardada exitosamente en el historial");
                        }
                    } else {
                        System.err.println("ERROR: finalizadorCompra es null, no se puede guardar la compra");
                    }
                    
                    // Paso 6: Mostrar mensaje de pago exitoso
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "¡Pago procesado exitosamente!\n\n" +
                            "Su compra ha sido confirmada y guardada.",
                            "Pago Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                    // Paso 7: Generar ticket (con folio y datos de envío)
                    final Compra.TipoEnvio tipoEnvioFinal = tipoEnvio[0];
                    final String direccionEnvioFinal = direccionEnvio[0];
                    final double costoEnvioFinal = costoEnvio[0];
                    SwingUtilities.invokeLater(() -> generarTicket(folio[0], tipoEnvioFinal, direccionEnvioFinal, costoEnvioFinal));
                    
                    // Paso 8: Limpiar carrito
                    carritoCompra.limpiar();
                    SwingUtilities.invokeLater(() -> actualizarCarrito());
                    
                    // Paso 9: Mostrar confirmación final
                    final String folioFinal = folio[0];
                    SwingUtilities.invokeLater(() -> mostrarConfirmacionCompra(folioFinal));
                    
                    // Paso 10: Cerrar sesión después de un delay
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
        generarTicket(null, Compra.TipoEnvio.TIENDA, null, 0.0);
    }

    private void generarTicket(String folio, Compra.TipoEnvio tipoEnvio, String direccionEnvio, double costoEnvio) {
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
        if (folio != null) {
            ticket.append("Folio: ").append(folio).append("\n");
        } else {
            ticket.append("Ticket: #").append(System.currentTimeMillis()).append("\n");
        }
        ticket.append("Tipo de Envío: ").append(tipoEnvio.name()).append("\n");
        if (direccionEnvio != null && !direccionEnvio.isEmpty()) {
            ticket.append("Dirección: ").append(direccionEnvio).append("\n");
        }
        ticket.append("\n");
        
        for (ItemCarrito item : carritoCompra.getItems()) {
            ticket.append(String.format("%-30s x%2d = %8s\n",
                item.getProducto().getNombre().substring(0, Math.min(30, item.getProducto().getNombre().length())),
                item.getCantidad(),
                formatoMoneda.format(item.getSubtotal())));
        }
        
        ticket.append("\n");
        ticket.append("Subtotal: ").append(formatoMoneda.format(carritoCompra.getTotal())).append("\n");
        ticket.append("Descuento: ").append(formatoMoneda.format(carritoCompra.getDescuento())).append("\n");
        if (costoEnvio > 0) {
            ticket.append("Costo de Envío: ").append(formatoMoneda.format(costoEnvio)).append("\n");
        }
        double totalFinal = carritoCompra.getTotalConDescuento() + costoEnvio;
        ticket.append("TOTAL: ").append(formatoMoneda.format(totalFinal)).append("\n\n");
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
        mostrarConfirmacionCompra(null);
    }

    private void mostrarConfirmacionCompra(String folio) {
        String mensaje = "¡Compra realizada exitosamente!\n\n";
        if (folio != null) {
            mensaje += "Folio: " + folio + "\n";
        }
        mensaje += "Su ticket ha sido generado.\n";
        mensaje += "Gracias por su compra en SIAP Tianguistenco.";
        
        JOptionPane.showMessageDialog(this, mensaje, 
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
     * Abre la ventana de historial de compras
     */
    private void abrirHistorial() {
        if (gestorHistorial == null) {
            JOptionPane.showMessageDialog(this, 
                "El gestor de historial no está disponible", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        HistorialComprasFrame historialFrame = new HistorialComprasFrame(gestorHistorial);
        historialFrame.setVisible(true);
    }

    /**
     * Abre la ventana de devoluciones
     */
    private void abrirDevoluciones() {
        if (gestorHistorial == null || gestorDevoluciones == null) {
            JOptionPane.showMessageDialog(this, 
                "Los gestores necesarios no están disponibles", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DevolucionFrame devolucionFrame = new DevolucionFrame(gestorDevoluciones, gestorHistorial);
        devolucionFrame.setVisible(true);
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
