package com.siap.tianguistenco;

import com.formdev.flatlaf.FlatLightLaf;
import com.siap.tianguistenco.datos.DatabaseInitializer;
import com.siap.tianguistenco.datos.UsuarioDAO;
import com.siap.tianguistenco.gui.CatalogoFrame;
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
    private Thread hiloGestorTarjetas;
    private Thread hiloGestorHistorial;
    private Thread hiloGestorDevoluciones;
    private Thread hiloGestorEnvio;
    
    // Gestores de hilos
    private GestorAutenticacion gestorAutenticacion;
    private GestorSesion gestorSesion;
    private SelectorProductos selectorProductos;
    private CalculadorPrecio calculadorPrecio;
    private AplicadorDescuentos aplicadorDescuentos;
    private FinalizadorCompra finalizadorCompra;
    private GeneradorTicket generadorTicket;
    private GestorTarjetas gestorTarjetas;
    private GestorHistorial gestorHistorial;
    private GestorDevoluciones gestorDevoluciones;
    private GestorEnvio gestorEnvio;

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
            
            // Obtener ID del usuario
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            int usuarioId = usuarioDAO.obtenerIdUsuario(usuario);
            
            if (usuarioId < 0) {
                System.err.println("Error: No se pudo obtener el ID del usuario");
                return;
            }
            
            // Hilo 2: GestorSesion
            gestorSesion = new GestorSesion(usuarioAutenticado, aplicacionActiva, usuario);
            hiloSesion = new Thread(gestorSesion, "GestorSesion");
            hiloSesion.start();

            // Hilo 6: FinalizadorCompra
            finalizadorCompra = new FinalizadorCompra(usuarioAutenticado, aplicacionActiva, 
                carritoCompra, gestorSesion, generadorTicket);
            finalizadorCompra.setUsuarioId(usuarioId);
            hiloFinalizadorCompra = new Thread(finalizadorCompra, "FinalizadorCompra");
            hiloFinalizadorCompra.start();

            // Hilo 8: GestorTarjetas
            gestorTarjetas = new GestorTarjetas(usuarioAutenticado, aplicacionActiva, usuarioId);
            hiloGestorTarjetas = new Thread(gestorTarjetas, "GestorTarjetas");
            hiloGestorTarjetas.start();

            // Hilo 9: GestorHistorial
            gestorHistorial = new GestorHistorial(usuarioAutenticado, aplicacionActiva, usuarioId);
            hiloGestorHistorial = new Thread(gestorHistorial, "GestorHistorial");
            hiloGestorHistorial.start();

            // Hilo 10: GestorDevoluciones
            gestorDevoluciones = new GestorDevoluciones(usuarioAutenticado, aplicacionActiva, usuarioId);
            hiloGestorDevoluciones = new Thread(gestorDevoluciones, "GestorDevoluciones");
            hiloGestorDevoluciones.start();

            // Hilo 11: GestorEnvio
            gestorEnvio = new GestorEnvio(usuarioAutenticado, aplicacionActiva);
            hiloGestorEnvio = new Thread(gestorEnvio, "GestorEnvio");
            hiloGestorEnvio.start();

            // Conectar gestores con CatalogoFrame
            conectarGestoresConCatalogo();

            System.out.println("Hilos post-autenticación iniciados para usuario: " + usuario + " (ID: " + usuarioId + ")");
            
        } catch (Exception e) {
            System.err.println("Error al inicializar hilos post-autenticación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Conecta los gestores con la ventana del catálogo
     */
    private void conectarGestoresConCatalogo() {
        try {
            // Esperar un poco para que el catálogo se cree
            Thread.sleep(500);
            
            if (selectorProductos != null) {
                CatalogoFrame catalogoFrame = selectorProductos.getCatalogoFrame();
                if (catalogoFrame != null) {
                    catalogoFrame.setGestores(gestorTarjetas, gestorEnvio, finalizadorCompra, 
                                             gestorHistorial, gestorDevoluciones);
                    System.out.println("Gestores conectados con CatalogoFrame");
                } else {
                    System.out.println("CatalogoFrame aún no está disponible, reintentando...");
                    // Reintentar después de un delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            conectarGestoresConCatalogo();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al conectar gestores con catálogo: " + e.getMessage());
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
            hiloFinalizadorCompra, hiloGeneradorTicket,
            hiloGestorTarjetas, hiloGestorHistorial, 
            hiloGestorDevoluciones, hiloGestorEnvio
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
