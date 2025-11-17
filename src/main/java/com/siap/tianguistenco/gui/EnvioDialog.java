package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.threads.GestorEnvio;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo para seleccionar tipo de envío y dirección
 */
public class EnvioDialog extends JDialog {
    private Compra.TipoEnvio tipoEnvioSeleccionado;
    private String direccionEnvio;
    private double costoEnvio;
    private GestorEnvio gestorEnvio;
    private double montoTotal;

    private JRadioButton radioTienda;
    private JRadioButton radioDomicilio;
    private JTextArea campoDireccion;
    private JLabel etiquetaCostoEnvio;
    private JButton botonAceptar;
    private JButton botonCancelar;

    public EnvioDialog(Frame parent, GestorEnvio gestorEnvio, double montoTotal) {
        super(parent, "Tipo de Envío", true);
        this.gestorEnvio = gestorEnvio;
        this.montoTotal = montoTotal;
        this.tipoEnvioSeleccionado = Compra.TipoEnvio.TIENDA;
        this.direccionEnvio = null;
        this.costoEnvio = 0.0;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Seleccione el tipo de envío", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Radio buttons para tipo de envío
        ButtonGroup grupoEnvio = new ButtonGroup();
        radioTienda = new JRadioButton("Recoger en Tienda", true);
        radioDomicilio = new JRadioButton("Envío a Domicilio", false);
        grupoEnvio.add(radioTienda);
        grupoEnvio.add(radioDomicilio);

        radioTienda.addActionListener(e -> {
            campoDireccion.setEnabled(false);
            costoEnvio = 0.0;
            actualizarCostoEnvio();
        });

        radioDomicilio.addActionListener(e -> {
            campoDireccion.setEnabled(true);
            costoEnvio = gestorEnvio.calcularCostoEnvio(montoTotal);
            actualizarCostoEnvio();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelCentral.add(radioTienda, gbc);

        gbc.gridy = 1;
        panelCentral.add(radioDomicilio, gbc);

        // Campo de dirección
        JLabel etiquetaDireccion = new JLabel("Dirección de Envío:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panelCentral.add(etiquetaDireccion, gbc);

        campoDireccion = new JTextArea(4, 30);
        campoDireccion.setEnabled(false);
        campoDireccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollDireccion = new JScrollPane(campoDireccion);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelCentral.add(scrollDireccion, gbc);

        // Costo de envío
        etiquetaCostoEnvio = new JLabel("Costo de Envío: $0.00");
        etiquetaCostoEnvio.setFont(new Font("Arial", Font.BOLD, 14));
        etiquetaCostoEnvio.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelCentral.add(etiquetaCostoEnvio, gbc);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonAceptar = new JButton("Aceptar");
        botonAceptar.setBackground(new Color(40, 167, 69));
        botonAceptar.setForeground(Color.WHITE);
        botonAceptar.setBorderPainted(false);
        botonAceptar.setFocusPainted(false);
        botonAceptar.addActionListener(e -> {
            if (radioDomicilio.isSelected()) {
                String direccion = campoDireccion.getText().trim();
                if (!gestorEnvio.validarDireccion(direccion)) {
                    JOptionPane.showMessageDialog(this, 
                        "Por favor ingrese una dirección válida (mínimo 10 caracteres)", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                direccionEnvio = direccion;
                tipoEnvioSeleccionado = Compra.TipoEnvio.DOMICILIO;
            } else {
                tipoEnvioSeleccionado = Compra.TipoEnvio.TIENDA;
                direccionEnvio = null;
            }
            dispose();
        });

        botonCancelar = new JButton("Cancelar");
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.setFocusPainted(false);
        botonCancelar.addActionListener(e -> {
            tipoEnvioSeleccionado = null;
            dispose();
        });

        panelBotones.add(botonAceptar);
        panelBotones.add(botonCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void actualizarCostoEnvio() {
        java.text.DecimalFormat formato = new java.text.DecimalFormat("$#,##0.00");
        etiquetaCostoEnvio.setText("Costo de Envío: " + formato.format(costoEnvio));
        if (costoEnvio == 0.0 && radioDomicilio.isSelected()) {
            etiquetaCostoEnvio.setText("Costo de Envío: GRATIS (Compra mayor a $500)");
        }
    }

    public Compra.TipoEnvio getTipoEnvio() {
        return tipoEnvioSeleccionado;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }
}

