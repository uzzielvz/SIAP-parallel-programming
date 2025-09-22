package com.siap.tianguistenco.gui;

import com.siap.tianguistenco.threads.GestorAutenticacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana de login para la autenticación de usuarios
 */
public class LoginFrame extends JFrame {
    private final GestorAutenticacion gestorAutenticacion;
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JButton botonIngresar;
    private JButton botonRegistrarse;
    private JLabel etiquetaError;

    public LoginFrame(GestorAutenticacion gestorAutenticacion) {
        this.gestorAutenticacion = gestorAutenticacion;
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setTitle("SIAP Tianguistenco - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(245, 245, 245));

        // Panel del logo/título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(245, 245, 245));
        JLabel titulo = new JLabel("SIAP TIANGUISTENCO");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 102, 204));
        JLabel subtitulo = new JLabel("E-commerce Multihilo");
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitulo.setForeground(new Color(100, 100, 100));
        
        panelTitulo.add(titulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(subtitulo);

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Campo de usuario
        JLabel etiquetaUsuario = new JLabel("Usuario:");
        etiquetaUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(etiquetaUsuario, gbc);

        campoUsuario = new JTextField(20);
        campoUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        campoUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(campoUsuario, gbc);

        // Campo de contraseña
        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        etiquetaContrasena.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(etiquetaContrasena, gbc);

        campoContrasena = new JPasswordField(20);
        campoContrasena.setFont(new Font("Arial", Font.PLAIN, 12));
        campoContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(campoContrasena, gbc);

        // Etiqueta de error
        etiquetaError = new JLabel(" ");
        etiquetaError.setForeground(Color.RED);
        etiquetaError.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(etiquetaError, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(Color.WHITE);

        botonIngresar = new JButton("Ingresar");
        botonIngresar.setFont(new Font("Arial", Font.BOLD, 12));
        botonIngresar.setBackground(new Color(0, 102, 204));
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setBorderPainted(false);
        botonIngresar.setFocusPainted(false);
        botonIngresar.setPreferredSize(new Dimension(100, 35));
        botonIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarLogin();
            }
        });

        botonRegistrarse = new JButton("Registrarse");
        botonRegistrarse.setFont(new Font("Arial", Font.BOLD, 12));
        botonRegistrarse.setBackground(new Color(108, 117, 125));
        botonRegistrarse.setForeground(Color.WHITE);
        botonRegistrarse.setBorderPainted(false);
        botonRegistrarse.setFocusPainted(false);
        botonRegistrarse.setPreferredSize(new Dimension(100, 35));
        botonRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMensajeRegistro();
            }
        });

        panelBotones.add(botonIngresar);
        panelBotones.add(Box.createHorizontalStrut(10));
        panelBotones.add(botonRegistrarse);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(panelBotones, gbc);

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setBackground(new Color(245, 245, 245));
        JLabel info = new JLabel("Credenciales de prueba: admin / admin");
        info.setFont(new Font("Arial", Font.ITALIC, 10));
        info.setForeground(new Color(100, 100, 100));
        panelInfo.add(info);

        // Agregar componentes al panel principal
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Configurar Enter para login
        getRootPane().setDefaultButton(botonIngresar);
        campoContrasena.addActionListener(e -> intentarLogin());
    }

    private void configurarVentana() {
        pack();
        setLocationRelativeTo(null);
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }

        if (gestorAutenticacion.validarCredenciales(usuario, contrasena)) {
            etiquetaError.setText("Login exitoso!");
            etiquetaError.setForeground(new Color(0, 150, 0));
            
            // Cerrar la ventana de login después de un breve delay
            Timer timer = new Timer(1000, e -> {
                setVisible(false);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            mostrarError("Credenciales incorrectas");
        }
    }

    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
        etiquetaError.setForeground(Color.RED);
    }

    private void mostrarMensajeRegistro() {
        // Crear diálogo de registro
        JDialog dialogRegistro = new JDialog(this, "Registro de Usuario", true);
        dialogRegistro.setSize(400, 200);
        dialogRegistro.setLocationRelativeTo(this);
        
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Campo de usuario
        JLabel etiquetaUsuarioReg = new JLabel("Usuario:");
        etiquetaUsuarioReg.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelRegistro.add(etiquetaUsuarioReg, gbc);
        
        JTextField campoUsuarioReg = new JTextField(15);
        campoUsuarioReg.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRegistro.add(campoUsuarioReg, gbc);
        
        // Campo de contraseña
        JLabel etiquetaContrasenaReg = new JLabel("Contraseña:");
        etiquetaContrasenaReg.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelRegistro.add(etiquetaContrasenaReg, gbc);
        
        JPasswordField campoContrasenaReg = new JPasswordField(15);
        campoContrasenaReg.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRegistro.add(campoContrasenaReg, gbc);
        
        // Botones
        JPanel panelBotonesReg = new JPanel(new FlowLayout());
        
        JButton botonRegistrar = new JButton("Registrar");
        botonRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        botonRegistrar.setBackground(new Color(0, 102, 204));
        botonRegistrar.setForeground(Color.WHITE);
        botonRegistrar.setBorderPainted(false);
        botonRegistrar.setFocusPainted(false);
        botonRegistrar.addActionListener(e -> {
            String usuario = campoUsuarioReg.getText().trim();
            String contrasena = new String(campoContrasenaReg.getPassword());
            
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(dialogRegistro, 
                    "Por favor, complete todos los campos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (gestorAutenticacion.registrarUsuario(usuario, contrasena)) {
                JOptionPane.showMessageDialog(dialogRegistro, 
                    "Usuario registrado exitosamente", 
                    "Registro Exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                dialogRegistro.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogRegistro, 
                    "Error al registrar usuario. El usuario ya existe.", 
                    "Error de Registro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        botonCancelar.setBackground(new Color(108, 117, 125));
        botonCancelar.setForeground(Color.WHITE);
        botonCancelar.setBorderPainted(false);
        botonCancelar.setFocusPainted(false);
        botonCancelar.addActionListener(e -> dialogRegistro.dispose());
        
        panelBotonesReg.add(botonRegistrar);
        panelBotonesReg.add(botonCancelar);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelRegistro.add(panelBotonesReg, gbc);
        
        dialogRegistro.add(panelRegistro);
        dialogRegistro.setVisible(true);
    }
}
