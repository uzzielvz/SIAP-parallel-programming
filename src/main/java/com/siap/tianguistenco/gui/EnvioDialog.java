package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.threads.GestorEnvio;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo para seleccionar tipo de envío y dirección con formulario completo
 */
public class EnvioDialog extends JDialog {
    private Compra.TipoEnvio tipoEnvioSeleccionado;
    private String direccionEnvio;
    private double costoEnvio;
    private GestorEnvio gestorEnvio;
    private double montoTotal;

    private JRadioButton radioTienda;
    private JRadioButton radioDomicilio;
    
    // Campos del formulario de dirección
    private JTextField campoCalle;
    private JTextField campoNumero;
    private JTextField campoColonia;
    private JTextField campoCiudad;
    private JTextField campoCodigoPostal;
    private JTextArea campoReferencias;
    
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
        setSize(550, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Seleccione el tipo de envío", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Panel central con scroll
        JPanel panelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Radio buttons para tipo de envío
        ButtonGroup grupoEnvio = new ButtonGroup();
        radioTienda = new JRadioButton("Recoger en Tienda", true);
        radioDomicilio = new JRadioButton("Envío a Domicilio", false);
        grupoEnvio.add(radioTienda);
        grupoEnvio.add(radioDomicilio);

        radioTienda.addActionListener(e -> {
            habilitarCamposDireccion(false);
            costoEnvio = 0.0;
            actualizarCostoEnvio();
        });

        radioDomicilio.addActionListener(e -> {
            habilitarCamposDireccion(true);
            costoEnvio = gestorEnvio.calcularCostoEnvio(montoTotal);
            actualizarCostoEnvio();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelCentral.add(radioTienda, gbc);

        gbc.gridy = 1;
        panelCentral.add(radioDomicilio, gbc);

        // Separador
        JSeparator separador = new JSeparator();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCentral.add(separador, gbc);

        // Título del formulario de dirección
        JLabel tituloDireccion = new JLabel("Datos de Envío:");
        tituloDireccion.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        panelCentral.add(tituloDireccion, gbc);

        // Calle
        JLabel etiquetaCalle = new JLabel("Calle:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaCalle, gbc);

        campoCalle = new JTextField(25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelCentral.add(campoCalle, gbc);

        // Número
        JLabel etiquetaNumero = new JLabel("Número:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaNumero, gbc);

        campoNumero = new JTextField(25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelCentral.add(campoNumero, gbc);

        // Colonia
        JLabel etiquetaColonia = new JLabel("Colonia:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaColonia, gbc);

        campoColonia = new JTextField(25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelCentral.add(campoColonia, gbc);

        // Ciudad
        JLabel etiquetaCiudad = new JLabel("Ciudad:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaCiudad, gbc);

        campoCiudad = new JTextField(25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelCentral.add(campoCiudad, gbc);

        // Código Postal
        JLabel etiquetaCP = new JLabel("Código Postal:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaCP, gbc);

        campoCodigoPostal = new JTextField(10);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelCentral.add(campoCodigoPostal, gbc);

        // Referencias
        JLabel etiquetaReferencias = new JLabel("Referencias:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        panelCentral.add(etiquetaReferencias, gbc);

        campoReferencias = new JTextArea(3, 25);
        campoReferencias.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollReferencias = new JScrollPane(campoReferencias);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelCentral.add(scrollReferencias, gbc);

        // Costo de envío
        etiquetaCostoEnvio = new JLabel("Costo de Envío: $0.00");
        etiquetaCostoEnvio.setFont(new Font("Arial", Font.BOLD, 14));
        etiquetaCostoEnvio.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 8, 8);
        panelCentral.add(etiquetaCostoEnvio, gbc);

        // Inicialmente deshabilitar campos
        habilitarCamposDireccion(false);

        JScrollPane scrollPanel = new JScrollPane(panelCentral);
        scrollPanel.setBorder(null);
        panelPrincipal.add(scrollPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonAceptar = new JButton("PAGAR");
        botonAceptar.setFont(new Font("Arial", Font.BOLD, 16));
        botonAceptar.setBackground(new Color(40, 167, 69));
        botonAceptar.setForeground(Color.WHITE);
        botonAceptar.setBorderPainted(false);
        botonAceptar.setFocusPainted(false);
        botonAceptar.setPreferredSize(new Dimension(150, 45));
        botonAceptar.addActionListener(e -> {
            if (radioDomicilio.isSelected()) {
                if (!validarFormulario()) {
                    return;
                }
                direccionEnvio = construirDireccionCompleta();
                tipoEnvioSeleccionado = Compra.TipoEnvio.DOMICILIO;
            } else {
                tipoEnvioSeleccionado = Compra.TipoEnvio.TIENDA;
                direccionEnvio = null;
            }
            System.out.println("Botón PAGAR presionado en diálogo de envío");
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

    private void habilitarCamposDireccion(boolean habilitar) {
        campoCalle.setEnabled(habilitar);
        campoNumero.setEnabled(habilitar);
        campoColonia.setEnabled(habilitar);
        campoCiudad.setEnabled(habilitar);
        campoCodigoPostal.setEnabled(habilitar);
        campoReferencias.setEnabled(habilitar);
    }

    private boolean validarFormulario() {
        if (campoCalle.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la calle");
            campoCalle.requestFocus();
            return false;
        }
        if (campoNumero.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese el número");
            campoNumero.requestFocus();
            return false;
        }
        if (campoColonia.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la colonia");
            campoColonia.requestFocus();
            return false;
        }
        if (campoCiudad.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese la ciudad");
            campoCiudad.requestFocus();
            return false;
        }
        if (campoCodigoPostal.getText().trim().isEmpty()) {
            mostrarError("Por favor ingrese el código postal");
            campoCodigoPostal.requestFocus();
            return false;
        }
        if (!campoCodigoPostal.getText().trim().matches("\\d{5}")) {
            mostrarError("El código postal debe tener 5 dígitos");
            campoCodigoPostal.requestFocus();
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String construirDireccionCompleta() {
        StringBuilder direccion = new StringBuilder();
        direccion.append("Calle: ").append(campoCalle.getText().trim());
        direccion.append(", Número: ").append(campoNumero.getText().trim());
        direccion.append(", Colonia: ").append(campoColonia.getText().trim());
        direccion.append(", Ciudad: ").append(campoCiudad.getText().trim());
        direccion.append(", CP: ").append(campoCodigoPostal.getText().trim());
        if (!campoReferencias.getText().trim().isEmpty()) {
            direccion.append("\nReferencias: ").append(campoReferencias.getText().trim());
        }
        return direccion.toString();
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
