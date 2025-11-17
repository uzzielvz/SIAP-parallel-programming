package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.model.CompraItem;
import com.siap.tianguistenco.threads.GestorHistorial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana para ver el historial de compras del usuario
 */
public class HistorialComprasFrame extends JFrame {
    private GestorHistorial gestorHistorial;
    private JTable tablaCompras;
    private DefaultTableModel modeloTabla;
    private JTextArea areaDetalles;
    private JButton botonVerDetalles;
    private JButton botonDevolver;
    private JButton botonCerrar;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public HistorialComprasFrame(GestorHistorial gestorHistorial) {
        this.gestorHistorial = gestorHistorial;
        inicializarComponentes();
        configurarVentana();
        cargarCompras();
    }

    private void inicializarComponentes() {
        setTitle("Historial de Compras - SIAP Tianguistenco");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con tabla
        String[] columnas = {"Folio", "Fecha", "Total", "Estado", "Tipo Envío"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCompras = new JTable(modeloTabla);
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollTabla = new JScrollPane(tablaCompras);

        // Panel inferior con detalles
        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBorder(BorderFactory.createTitledBorder("Detalles de la Compra"));

        areaDetalles = new JTextArea(10, 50);
        areaDetalles.setEditable(false);
        areaDetalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollDetalles = new JScrollPane(areaDetalles);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonVerDetalles = new JButton("Ver Detalles");
        botonVerDetalles.setBackground(new Color(0, 102, 204));
        botonVerDetalles.setForeground(Color.WHITE);
        botonVerDetalles.setBorderPainted(false);
        botonVerDetalles.addActionListener(e -> mostrarDetalles());

        botonDevolver = new JButton("Devolver");
        botonDevolver.setBackground(new Color(220, 53, 69));
        botonDevolver.setForeground(Color.WHITE);
        botonDevolver.setBorderPainted(false);
        botonDevolver.addActionListener(e -> iniciarDevolucion());

        botonCerrar = new JButton("Cerrar");
        botonCerrar.setBackground(new Color(108, 117, 125));
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setBorderPainted(false);
        botonCerrar.addActionListener(e -> dispose());

        panelBotones.add(botonVerDetalles);
        panelBotones.add(botonDevolver);
        panelBotones.add(botonCerrar);

        panelDetalles.add(scrollDetalles, BorderLayout.CENTER);
        panelDetalles.add(panelBotones, BorderLayout.SOUTH);

        // Layout principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabla, panelDetalles);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    private void configurarVentana() {
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void cargarCompras() {
        modeloTabla.setRowCount(0);
        List<Compra> compras = gestorHistorial.obtenerHistorialCompras();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Compra compra : compras) {
            Object[] fila = {
                compra.getFolio(),
                compra.getFecha().format(formatter),
                formatoMoneda.format(compra.getTotalConDescuento()),
                compra.getEstado(),
                compra.getTipoEnvio().name()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void mostrarDetalles() {
        int filaSeleccionada = tablaCompras.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una compra", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String folio = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Compra compra = gestorHistorial.obtenerCompraPorFolio(folio);

        if (compra != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("=== DETALLES DE COMPRA ===\n\n");
            detalles.append("Folio: ").append(compra.getFolio()).append("\n");
            detalles.append("Fecha: ").append(compra.getFecha().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
            detalles.append("Estado: ").append(compra.getEstado()).append("\n");
            detalles.append("Tipo de Envío: ").append(compra.getTipoEnvio().name()).append("\n");
            if (compra.getDireccionEnvio() != null) {
                detalles.append("Dirección: ").append(compra.getDireccionEnvio()).append("\n");
            }
            detalles.append("\n=== PRODUCTOS ===\n\n");

            for (CompraItem item : compra.getItems()) {
                detalles.append(String.format("%-40s x%2d = %10s\n",
                    item.getNombreProducto(),
                    item.getCantidad(),
                    formatoMoneda.format(item.getSubtotal())));
            }

            detalles.append("\n=== TOTALES ===\n");
            detalles.append("Subtotal: ").append(formatoMoneda.format(compra.getTotal())).append("\n");
            if (compra.getDescuento() > 0) {
                detalles.append("Descuento: -").append(formatoMoneda.format(compra.getDescuento())).append("\n");
            }
            if (compra.getCostoEnvio() > 0) {
                detalles.append("Envío: ").append(formatoMoneda.format(compra.getCostoEnvio())).append("\n");
            }
            detalles.append("TOTAL: ").append(formatoMoneda.format(compra.getTotalConDescuento())).append("\n");

            areaDetalles.setText(detalles.toString());
        }
    }

    private void iniciarDevolucion() {
        int filaSeleccionada = tablaCompras.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una compra", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String folio = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Compra compra = gestorHistorial.obtenerCompraPorFolio(folio);

        if (compra != null) {
            // Abrir ventana de devolución
            // Esto se implementará cuando se cree DevolucionFrame
            JOptionPane.showMessageDialog(this, 
                "Funcionalidad de devolución se abrirá en una nueva ventana", 
                "Devolución", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarHistorial() {
        cargarCompras();
    }
}

