package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.UsuarioDAO;
import com.siap.tianguistenco.gui.LoginFrame;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de gestionar la autenticación de usuarios
 * Maneja el login y registro de usuarios en la aplicación
 */
public class GestorAutenticacion implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final UsuarioDAO usuarioDAO;
    private LoginFrame loginFrame;
    private String usuarioActual;

    public GestorAutenticacion(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public void run() {
        try {
            // Crear y mostrar ventana de login
            SwingUtilities.invokeLater(() -> {
                loginFrame = new LoginFrame(this);
                loginFrame.setVisible(true);
            });

            // Esperar hasta que el usuario se autentique o la aplicación se cierre
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

        } catch (Exception e) {
            System.err.println("Error en GestorAutenticacion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida las credenciales del usuario
     * @param usuario nombre de usuario
     * @param contrasena contraseña
     * @return true si las credenciales son válidas
     */
    public boolean validarCredenciales(String usuario, String contrasena) {
        // Validar credenciales usando la base de datos
        boolean esValido = usuarioDAO.validarUsuario(usuario, contrasena);
        
        if (esValido) {
            this.usuarioActual = usuario;
            usuarioAutenticado.set(true);
            System.out.println("Usuario autenticado exitosamente: " + usuario);
        } else {
            System.out.println("Credenciales inválidas para usuario: " + usuario);
        }
        
        return esValido;
    }

    /**
     * Cierra la sesión del usuario
     */
    public void cerrarSesion() {
        usuarioAutenticado.set(false);
        usuarioActual = null;
        if (loginFrame != null) {
            SwingUtilities.invokeLater(() -> loginFrame.setVisible(false));
        }
    }

    /**
     * Obtiene el usuario actual autenticado
     */
    public String getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean isUsuarioAutenticado() {
        return usuarioAutenticado.get();
    }

    /**
     * Registra un nuevo usuario
     * @param usuario nombre de usuario
     * @param contrasena contraseña
     * @return true si el usuario se registró exitosamente
     */
    public boolean registrarUsuario(String usuario, String contrasena) {
        boolean registrado = usuarioDAO.registrarUsuario(usuario, contrasena);
        
        if (registrado) {
            System.out.println("Usuario registrado exitosamente: " + usuario);
        } else {
            System.out.println("Error al registrar usuario: " + usuario);
        }
        
        return registrado;
    }

    /**
     * Cierra la ventana de login
     */
    public void cerrarLogin() {
        if (loginFrame != null) {
            SwingUtilities.invokeLater(() -> loginFrame.dispose());
        }
    }
}
