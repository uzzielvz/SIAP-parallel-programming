package com.siap.tianguistenco.threads;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de mantener la sesión del usuario activa
 * Monitorea el estado de la sesión y registra eventos importantes
 */
public class GestorSesion implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final String usuario;
    private LocalDateTime inicioSesion;
    private LocalDateTime ultimaActividad;
    private boolean sesionActiva;

    public GestorSesion(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, String usuario) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.usuario = usuario;
        this.sesionActiva = true;
        this.inicioSesion = LocalDateTime.now();
        this.ultimaActividad = LocalDateTime.now();
    }

    @Override
    public void run() {
        try {
            System.out.println("GestorSesion iniciado para usuario: " + usuario);
            logEvento("Sesión iniciada");

            // Monitorear la sesión mientras esté activa
            while (aplicacionActiva.get() && usuarioAutenticado.get() && sesionActiva) {
                // Verificar si la sesión sigue activa
                if (!usuarioAutenticado.get()) {
                    cerrarSesion();
                    break;
                }

                // Actualizar última actividad cada 30 segundos
                Thread.sleep(30000);
                ultimaActividad = LocalDateTime.now();
                
                // Log de actividad cada 2 minutos
                if (ultimaActividad.getMinute() % 2 == 0) {
                    logEvento("Sesión activa - Usuario: " + usuario);
                }
            }

        } catch (InterruptedException e) {
            System.out.println("GestorSesion interrumpido para usuario: " + usuario);
            cerrarSesion();
        } catch (Exception e) {
            System.err.println("Error en GestorSesion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    public synchronized void cerrarSesion() {
        if (sesionActiva) {
            sesionActiva = false;
            LocalDateTime finSesion = LocalDateTime.now();
            long duracionMinutos = java.time.Duration.between(inicioSesion, finSesion).toMinutes();
            
            logEvento("Sesión cerrada - Duración: " + duracionMinutos + " minutos");
            System.out.println("Sesión de " + usuario + " cerrada después de " + duracionMinutos + " minutos");
        }
    }

    /**
     * Actualiza la última actividad del usuario
     */
    public synchronized void actualizarActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    /**
     * Registra un evento en la sesión
     */
    private void logEvento(String evento) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] SESIÓN - " + evento);
    }

    /**
     * Obtiene el tiempo transcurrido desde el inicio de la sesión
     */
    public long getTiempoSesionMinutos() {
        return java.time.Duration.between(inicioSesion, LocalDateTime.now()).toMinutes();
    }

    /**
     * Obtiene el tiempo transcurrido desde la última actividad
     */
    public long getTiempoInactividadMinutos() {
        return java.time.Duration.between(ultimaActividad, LocalDateTime.now()).toMinutes();
    }

    /**
     * Verifica si la sesión está activa
     */
    public boolean isSesionActiva() {
        return sesionActiva && usuarioAutenticado.get();
    }

    /**
     * Obtiene el usuario de la sesión
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Obtiene la fecha y hora de inicio de la sesión
     */
    public LocalDateTime getInicioSesion() {
        return inicioSesion;
    }

    /**
     * Obtiene la fecha y hora de la última actividad
     */
    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }
}
