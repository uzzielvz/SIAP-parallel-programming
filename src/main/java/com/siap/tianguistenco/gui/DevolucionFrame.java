package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.model.CompraItem;
import com.siap.tianguistenco.model.Devolucion;
import com.siap.tianguistenco.threads.GestorDevoluciones;
import com.siap.tianguistenco.threads.GestorHistorial;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para procesar devoluciones de compras
 */
public class DevolucionFrame extends JFrame {
    private GestorDevoluciones gestorDevoluciones;
    private GestorHistorial gestorHistorial;
    private Compra compraSeleccionada;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JTable tablaItems;
    private DefaultTableModel modeloTabla;
    private JComboBox<Devolucion.MotivoDevolucion> comboMotivo;
    private JTextArea campoObservaciones;
    private JLabel etiquetaMontoDevolucion;
    private JButton botonProcesar;
    private JButton botonCancelar;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private List<CompraItem> itemsSeleccionados;

    public DevolucionFrame(GestorDevoluciones gestorDevoluciones, GestorHistorial gestorHistorial) {
        this.gestorDevoluciones = gestorDevoluciones;
        this.gestorHistorial = gestorHistorial;
        this.itemsSeleccionados = new ArrayList<>();
        inicializarComponentes();
        configurarVentana();
        cargarHistorialCompras();
    }

    private void inicializarComponentes() {
        setTitle("Devolución de Compra - SIAP Tianguistenco");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior - Historial de compras
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Compras - Seleccione una compra"));

        String[] columnasHistorial = {"Folio", "Fecha", "Total", "Estado", "Envío"};
        modeloHistorial = new DefaultTableModel(columnasHistorial, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaHistorial.getSelectedRow() != -1) {
                seleccionarCompra();
            }
        });
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setPreferredSize(new Dimension(0, 200));
        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);

        // Panel central - Tabla de items de la compra seleccionada
        JPanel panelItems = new JPanel(new BorderLayout());
        panelItems.setBorder(BorderFactory.createTitledBorder("Items de la Compra Seleccionada"));

        String[] columnas = {"Seleccionar", "Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Solo la columna de selección es editable
            }
        };
        tablaItems = new JTable(modeloTabla);
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(300);
        JScrollPane scrollTabla = new JScrollPane(tablaItems);
        panelItems.add(scrollTabla, BorderLayout.CENTER);

        // Panel de motivo y observaciones
        JPanel panelMotivo = new JPanel(new GridBagLayout());
        panelMotivo.setBorder(BorderFactory.createTitledBorder("Información de Devolución"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel etiquetaMotivo = new JLabel("Motivo de Devolución:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelMotivo.add(etiquetaMotivo, gbc);

        comboMotivo = new JComboBox<>(Devolucion.MotivoDevolucion.values());
        comboMotivo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Devolucion.MotivoDevolucion) {
                    setText(((Devolucion.MotivoDevolucion) value).getDescripcion());
                }
                return this;
            }
        });
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelMotivo.add(comboMotivo, gbc);

        JLabel etiquetaObs = new JLabel("Observaciones:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelMotivo.add(etiquetaObs, gbc);

        campoObservaciones = new JTextArea(3, 30);
        campoObservaciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollObs = new JScrollPane(campoObservaciones);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        panelMotivo.add(scrollObs, gbc);

        etiquetaMontoDevolucion = new JLabel("Monto a Devolver: $0.00");
        etiquetaMontoDevolucion.setFont(new Font("Arial", Font.BOLD, 16));
        etiquetaMontoDevolucion.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelMotivo.add(etiquetaMontoDevolucion, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonProcesar = new JButton("Procesar Devolución");
        botonProcesar.setBackground(new Color(220, 53, 69));
        botonProcesar.setForeground(Color.WHITE);
        botonProcesar.setBorderPainted(false);
        botonProcesar.setEnabled(false);
        botonProcesar.addActionListener(e -> procesarDevolucion());

        botonCancelar = new JButton("Cancelar");
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.addActionListener(e -> dispose());

        panelBotones.add(botonProcesar);
        panelBotones.add(botonCancelar);

        // Layout principal
        JSplitPane splitPanePrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelHistorial, panelItems);
        splitPanePrincipal.setDividerLocation(200);
        splitPanePrincipal.setResizeWeight(0.3);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelMotivo, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        JSplitPane splitPaneSecundario = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPanePrincipal, panelInferior);
        splitPaneSecundario.setDividerLocation(400);
        splitPaneSecundario.setResizeWeight(0.6);

        add(splitPaneSecundario, BorderLayout.CENTER);

        // Listener para actualizar monto cuando se seleccionan items
        tablaItems.getModel().addTableModelListener(e -> actualizarMontoDevolucion());
    }

    private void configurarVentana() {
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void cargarHistorialCompras() {
        modeloHistorial.setRowCount(0);
        List<Compra> compras = gestorHistorial.obtenerHistorialCompras();
        for (Compra compra : compras) {
            modeloHistorial.addRow(new Object[]{
                compra.getFolio(),
                compra.getFecha().format(formatoFecha),
                formatoMoneda.format(compra.getTotalConDescuento()),
                compra.getEstado(),
                compra.getTipoEnvio().name()
            });
        }
    }

    private void seleccionarCompra() {
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada < 0) {
            return;
        }

        String folio = (String) modeloHistorial.getValueAt(filaSeleccionada, 0);
        compraSeleccionada = gestorHistorial.obtenerCompraPorFolio(folio);

        if (compraSeleccionada != null) {
            cargarItemsCompra();
            botonProcesar.setEnabled(true);
        }
    }

    private void cargarItemsCompra() {
        modeloTabla.setRowCount(0);
        itemsSeleccionados.clear();

        if (compraSeleccionada != null) {
            for (CompraItem item : compraSeleccionada.getItems()) {
                Object[] fila = {
                    false, // Seleccionado
                    item.getNombreProducto(),
                    item.getCantidad(),
                    formatoMoneda.format(item.getPrecioUnitario()),
                    formatoMoneda.format(item.getSubtotal())
                };
                modeloTabla.addRow(fila);
            }
        }
    }

    private void actualizarMontoDevolucion() {
        double montoTotal = 0.0;
        itemsSeleccionados.clear();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Boolean seleccionado = (Boolean) modeloTabla.getValueAt(i, 0);
            if (seleccionado != null && seleccionado) {
                if (compraSeleccionada != null && i < compraSeleccionada.getItems().size()) {
                    CompraItem item = compraSeleccionada.getItems().get(i);
                    montoTotal += item.getSubtotal();
                    itemsSeleccionados.add(item);
                }
            }
        }

        etiquetaMontoDevolucion.setText("Monto a Devolver: " + formatoMoneda.format(montoTotal));
    }

    private void procesarDevolucion() {
        if (compraSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una compra primero", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (itemsSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione al menos un producto para devolver", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Devolucion.MotivoDevolucion motivo = (Devolucion.MotivoDevolucion) comboMotivo.getSelectedItem();
        String observaciones = campoObservaciones.getText().trim();

        double montoDevolucion = itemsSeleccionados.stream()
            .mapToDouble(CompraItem::getSubtotal)
            .sum();

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de procesar la devolución?\n\n" +
            "Folio: " + compraSeleccionada.getFolio() + "\n" +
            "Motivo: " + motivo.getDescripcion() + "\n" +
            "Monto: " + formatoMoneda.format(montoDevolucion),
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Procesar en un hilo separado
            new Thread(() -> {
                int devolucionId = gestorDevoluciones.procesarDevolucion(
                    compraSeleccionada.getId(),
                    compraSeleccionada.getFolio(),
                    motivo,
                    montoDevolucion,
                    observaciones
                );

                SwingUtilities.invokeLater(() -> {
                    if (devolucionId > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Devolución procesada exitosamente\n\n" +
                            "ID de Devolución: " + devolucionId + "\n" +
                            "Monto: " + formatoMoneda.format(montoDevolucion),
                            "Devolución Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Error al procesar la devolución",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        }
    }
}
