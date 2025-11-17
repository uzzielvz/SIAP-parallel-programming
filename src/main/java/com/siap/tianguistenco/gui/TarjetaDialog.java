package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Tarjeta;
import com.siap.tianguistenco.threads.GestorTarjetas;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Diálogo para seleccionar o registrar una tarjeta de pago
 */
public class TarjetaDialog extends JDialog {
    private GestorTarjetas gestorTarjetas;
    private Tarjeta tarjetaSeleccionada;
    private JComboBox<Tarjeta> comboBoxTarjetas;
    private JButton botonRegistrar;
    private JButton botonSeleccionar;
    private JButton botonCancelar;

    public TarjetaDialog(Frame parent, GestorTarjetas gestorTarjetas) {
        super(parent, "Seleccionar Tarjeta de Pago", true);
        this.gestorTarjetas = gestorTarjetas;
        this.tarjetaSeleccionada = null;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(500, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Seleccione una tarjeta de pago", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Combo box de tarjetas
        JLabel etiquetaTarjeta = new JLabel("Tarjeta:");
        etiquetaTarjeta.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelCentral.add(etiquetaTarjeta, gbc);

        List<Tarjeta> tarjetas = gestorTarjetas.obtenerTarjetasUsuario();
        comboBoxTarjetas = new JComboBox<>();
        DefaultComboBoxModel<Tarjeta> model = new DefaultComboBoxModel<>();
        for (Tarjeta tarjeta : tarjetas) {
            model.addElement(tarjeta);
        }
        comboBoxTarjetas.setModel(model);
        comboBoxTarjetas.setRenderer(new TarjetaRenderer());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panelCentral.add(comboBoxTarjetas, gbc);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonRegistrar = new JButton("Registrar Nueva Tarjeta");
        botonRegistrar.setBackground(new Color(0, 102, 204));
        botonRegistrar.setForeground(Color.WHITE);
        botonRegistrar.setBorderPainted(false);
        botonRegistrar.setFocusPainted(false);
        botonRegistrar.addActionListener(e -> mostrarDialogoRegistro());

        botonSeleccionar = new JButton("Seleccionar");
        botonSeleccionar.setBackground(new Color(40, 167, 69));
        botonSeleccionar.setForeground(Color.WHITE);
        botonSeleccionar.setBorderPainted(false);
        botonSeleccionar.setFocusPainted(false);
        botonSeleccionar.addActionListener(e -> {
            if (comboBoxTarjetas.getSelectedItem() != null) {
                tarjetaSeleccionada = (Tarjeta) comboBoxTarjetas.getSelectedItem();
                System.out.println("Tarjeta seleccionada: " + tarjetaSeleccionada.toString());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor seleccione una tarjeta", 
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonCancelar = new JButton("Cancelar");
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.setFocusPainted(false);
        botonCancelar.addActionListener(e -> {
            tarjetaSeleccionada = null; // Asegurar que sea null si se cancela
            dispose();
        });

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonSeleccionar);
        panelBotones.add(botonCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void mostrarDialogoRegistro() {
        JDialog dialogRegistro = new JDialog(this, "Registrar Nueva Tarjeta", true);
        dialogRegistro.setSize(450, 400);
        dialogRegistro.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Número de tarjeta
        JLabel etiquetaNumero = new JLabel("Número de Tarjeta:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(etiquetaNumero, gbc);

        JTextField campoNumero = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campoNumero, gbc);

        // Nombre del titular
        JLabel etiquetaNombre = new JLabel("Nombre del Titular:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(etiquetaNombre, gbc);

        JTextField campoNombre = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campoNombre, gbc);

        // Fecha de vencimiento
        JLabel etiquetaFecha = new JLabel("Fecha Vencimiento (MM/YY):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(etiquetaFecha, gbc);

        JTextField campoFecha = new JTextField(10);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campoFecha, gbc);

        // Tipo de tarjeta
        JLabel etiquetaTipo = new JLabel("Tipo de Tarjeta:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(etiquetaTipo, gbc);

        JComboBox<Tarjeta.TipoTarjeta> comboTipo = new JComboBox<>(Tarjeta.TipoTarjeta.values());
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comboTipo, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.setBackground(new Color(40, 167, 69));
        botonGuardar.setForeground(Color.WHITE);
        botonGuardar.setBorderPainted(false);
        botonGuardar.addActionListener(e -> {
            String numero = campoNumero.getText().trim();
            String nombre = campoNombre.getText().trim();
            String fecha = campoFecha.getText().trim();
            Tarjeta.TipoTarjeta tipo = (Tarjeta.TipoTarjeta) comboTipo.getSelectedItem();

            if (numero.isEmpty() || nombre.isEmpty() || fecha.isEmpty()) {
                JOptionPane.showMessageDialog(dialogRegistro, "Por favor complete todos los campos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (gestorTarjetas.registrarTarjeta(numero, nombre, fecha, tipo)) {
                JOptionPane.showMessageDialog(dialogRegistro, "Tarjeta registrada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogRegistro.dispose();
                // Actualizar combo box
                List<Tarjeta> tarjetas = gestorTarjetas.obtenerTarjetasUsuario();
                DefaultComboBoxModel<Tarjeta> model = new DefaultComboBoxModel<>();
                for (Tarjeta tarjeta : tarjetas) {
                    model.addElement(tarjeta);
                }
                comboBoxTarjetas.setModel(model);
            } else {
                JOptionPane.showMessageDialog(dialogRegistro, "Error al registrar la tarjeta", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.addActionListener(e -> dialogRegistro.dispose());

        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(panelBotones, gbc);

        dialogRegistro.add(panel);
        dialogRegistro.setVisible(true);
    }

    public Tarjeta getTarjetaSeleccionada() {
        return tarjetaSeleccionada;
    }

    // Renderer personalizado para mostrar las tarjetas
    private static class TarjetaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tarjeta) {
                Tarjeta tarjeta = (Tarjeta) value;
                setText(tarjeta.toString());
            }
            return this;
        }
    }
}

