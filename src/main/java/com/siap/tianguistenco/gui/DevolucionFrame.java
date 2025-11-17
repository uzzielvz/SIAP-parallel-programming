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
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para procesar devoluciones de compras
 */
public class DevolucionFrame extends JFrame {
    private GestorDevoluciones gestorDevoluciones;
    private GestorHistorial gestorHistorial;
    private Compra compraSeleccionada;
    private JTextField campoFolio;
    private JButton botonBuscar;
    private JTable tablaItems;
    private DefaultTableModel modeloTabla;
    private JComboBox<Devolucion.MotivoDevolucion> comboMotivo;
    private JTextArea campoObservaciones;
    private JLabel etiquetaMontoDevolucion;
    private JButton botonProcesar;
    private JButton botonCancelar;
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    private List<CompraItem> itemsSeleccionados;

    public DevolucionFrame(GestorDevoluciones gestorDevoluciones, GestorHistorial gestorHistorial) {
        this.gestorDevoluciones = gestorDevoluciones;
        this.gestorHistorial = gestorHistorial;
        this.itemsSeleccionados = new ArrayList<>();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setTitle("Devolución de Compra - SIAP Tianguistenco");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior - Búsqueda por folio
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Compra"));

        JLabel etiquetaFolio = new JLabel("Folio de Compra:");
        campoFolio = new JTextField(20);
        botonBuscar = new JButton("Buscar");
        botonBuscar.setBackground(new Color(0, 102, 204));
        botonBuscar.setForeground(Color.WHITE);
        botonBuscar.setBorderPainted(false);
        botonBuscar.addActionListener(e -> buscarCompra());

        panelBusqueda.add(etiquetaFolio);
        panelBusqueda.add(campoFolio);
        panelBusqueda.add(botonBuscar);

        // Panel central - Tabla de items
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
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        panelCentral.add(panelMotivo, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelBusqueda, panelCentral);
        splitPane.setDividerLocation(80);
        splitPane.setResizeWeight(0.1);

        add(splitPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Listener para actualizar monto cuando se seleccionan items
        tablaItems.getModel().addTableModelListener(e -> actualizarMontoDevolucion());
    }

    private void configurarVentana() {
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void buscarCompra() {
        String folio = campoFolio.getText().trim();
        if (folio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un folio", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        compraSeleccionada = gestorHistorial.obtenerCompraPorFolio(folio);
        if (compraSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "No se encontró una compra con ese folio", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cargarItemsCompra();
        botonProcesar.setEnabled(true);
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
            JOptionPane.showMessageDialog(this, "Por favor busque una compra primero", 
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

