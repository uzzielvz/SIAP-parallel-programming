package com.siap.tianguistenco;

import com.formdev.flatlaf.FlatLightLaf;
import com.siap.tianguistenco.datos.DatabaseInitializer;
import com.siap.tianguistenco.model.CarritoCompra;
import com.siap.tianguistenco.threads.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Aplicación principal de SIAP Tianguistenco
 * Coordina todos los hilos del sistema multihilo de e-commerce
 */
public class SIAPApplication {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CarritoCompra carritoCompra;
    
    // Hilos del sistema
    private Thread hiloAutenticacion;
    private Thread hiloSesion;
    private Thread hiloSelectorProductos;
    private Thread hiloCalculadorPrecio;
    private Thread hiloAplicadorDescuentos;
    private Thread hiloFinalizadorCompra;
    private Thread hiloGeneradorTicket;
    
    // Gestores de hilos
    private GestorAutenticacion gestorAutenticacion;
    private GestorSesion gestorSesion;
    private SelectorProductos selectorProductos;
    private CalculadorPrecio calculadorPrecio;
    private AplicadorDescuentos aplicadorDescuentos;
    private FinalizadorCompra finalizadorCompra;
    private GeneradorTicket generadorTicket;

    public SIAPApplication() {
        this.usuarioAutenticado = new AtomicBoolean(false);
        this.aplicacionActiva = new AtomicBoolean(true);
        this.carritoCompra = new CarritoCompra();
        
        inicializarHilos();
    }

    /**
     * Inicializa todos los hilos del sistema
     */
    private void inicializarHilos() {
        // Hilo 1: GestorAutenticacion
        gestorAutenticacion = new GestorAutenticacion(usuarioAutenticado, aplicacionActiva);
        hiloAutenticacion = new Thread(gestorAutenticacion, "GestorAutenticacion");

        // Hilo 2: GestorSesion (se inicializará después del login)
        // Hilo 3: SelectorProductos
        selectorProductos = new SelectorProductos(usuarioAutenticado, aplicacionActiva, carritoCompra);
        hiloSelectorProductos = new Thread(selectorProductos, "SelectorProductos");

        // Hilo 4: CalculadorPrecio
        calculadorPrecio = new CalculadorPrecio(usuarioAutenticado, aplicacionActiva, carritoCompra, selectorProductos);
        hiloCalculadorPrecio = new Thread(calculadorPrecio, "CalculadorPrecio");

        // Hilo 5: AplicadorDescuentos
        aplicadorDescuentos = new AplicadorDescuentos(usuarioAutenticado, aplicacionActiva, carritoCompra, calculadorPrecio);
        hiloAplicadorDescuentos = new Thread(aplicadorDescuentos, "AplicadorDescuentos");

        // Hilo 6: FinalizadorCompra (se inicializará después del login)
        // Hilo 7: GeneradorTicket
        generadorTicket = new GeneradorTicket(usuarioAutenticado, aplicacionActiva);
        hiloGeneradorTicket = new Thread(generadorTicket, "GeneradorTicket");
    }

    /**
     * Inicia la aplicación
     */
    public void iniciar() {
        try {
            // Configurar Look and Feel moderno
            FlatLightLaf.setup();
            
            System.out.println("=== SIAP TIANGUISTENCO E-COMMERCE MULTIHILO ===");
            System.out.println("Iniciando aplicación...");
            
            // Inicializar base de datos
            System.out.println("Inicializando base de datos...");
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.inicializar();
            
            // Iniciar hilos principales
            hiloAutenticacion.start();
            hiloSelectorProductos.start();
            hiloCalculadorPrecio.start();
            hiloAplicadorDescuentos.start();
            hiloGeneradorTicket.start();
            
            // Monitorear el estado de autenticación
            monitorearAutenticacion();
            
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Monitorea el estado de autenticación para inicializar hilos dependientes
     */
    private void monitorearAutenticacion() {
        new Thread(() -> {
            try {
                // Esperar a que el usuario se autentique
                while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                    Thread.sleep(100);
                }

                if (aplicacionActiva.get() && usuarioAutenticado.get()) {
                    // Inicializar hilos que requieren autenticación
                    inicializarHilosPostAutenticacion();
                }

            } catch (InterruptedException e) {
                System.out.println("Monitoreo de autenticación interrumpido");
            }
        }, "MonitoreoAutenticacion").start();
    }

    /**
     * Inicializa los hilos que requieren que el usuario esté autenticado
     */
    private void inicializarHilosPostAutenticacion() {
        try {
            String usuario = gestorAutenticacion.getUsuarioActual();
            
            // Hilo 2: GestorSesion
            gestorSesion = new GestorSesion(usuarioAutenticado, aplicacionActiva, usuario);
            hiloSesion = new Thread(gestorSesion, "GestorSesion");
            hiloSesion.start();

            // Hilo 6: FinalizadorCompra
            finalizadorCompra = new FinalizadorCompra(usuarioAutenticado, aplicacionActiva, 
                carritoCompra, gestorSesion, generadorTicket);
            hiloFinalizadorCompra = new Thread(finalizadorCompra, "FinalizadorCompra");
            hiloFinalizadorCompra.start();

            System.out.println("Hilos post-autenticación iniciados para usuario: " + usuario);
            
        } catch (Exception e) {
            System.err.println("Error al inicializar hilos post-autenticación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Detiene la aplicación y cierra todos los hilos
     */
    public void detener() {
        try {
            System.out.println("Deteniendo aplicación...");
            
            aplicacionActiva.set(false);
            usuarioAutenticado.set(false);
            
            // Cerrar ventanas
            if (gestorAutenticacion != null) {
                gestorAutenticacion.cerrarLogin();
            }
            if (selectorProductos != null) {
                selectorProductos.cerrarCatalogo();
            }
            if (generadorTicket != null) {
                generadorTicket.cerrarTicket();
            }
            
            // Esperar a que los hilos terminen
            esperarHilos();
            
            System.out.println("Aplicación detenida correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al detener la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Espera a que todos los hilos terminen
     */
    private void esperarHilos() {
        Thread[] hilos = {
            hiloAutenticacion, hiloSesion, hiloSelectorProductos, 
            hiloCalculadorPrecio, hiloAplicadorDescuentos, 
            hiloFinalizadorCompra, hiloGeneradorTicket
        };
        
        for (Thread hilo : hilos) {
            if (hilo != null && hilo.isAlive()) {
                try {
                    hilo.join(5000); // Esperar máximo 5 segundos
                    if (hilo.isAlive()) {
                        System.out.println("Hilo " + hilo.getName() + " no terminó en el tiempo esperado");
                    }
                } catch (InterruptedException e) {
                    System.out.println("Interrumpido mientras se esperaba el hilo " + hilo.getName());
                }
            }
        }
    }

    /**
     * Método principal de la aplicación
     */
    public static void main(String[] args) {
        // Configurar el manejo de excepciones no capturadas
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Excepción no capturada en hilo " + thread.getName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        });

        // Crear e iniciar la aplicación
        SIAPApplication app = new SIAPApplication();
        
        // Configurar shutdown hook para limpiar recursos
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando aplicación...");
            app.detener();
        }));

        // Iniciar la aplicación
        app.iniciar();
    }
}
