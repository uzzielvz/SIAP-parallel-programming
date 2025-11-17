package com.siap.tianguistenco.threads;

import com.siap.tianguistenco.datos.ProductoDAO;
import com.siap.tianguistenco.gui.CatalogoFrame;
import com.siap.tianguistenco.model.CarritoCompra;
import com.siap.tianguistenco.model.Producto;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo responsable de gestionar la selección de productos y el catálogo
 * Maneja la interfaz del catálogo y las operaciones del carrito
 */
public class SelectorProductos implements Runnable {
    private final AtomicBoolean usuarioAutenticado;
    private final AtomicBoolean aplicacionActiva;
    private final CarritoCompra carritoCompra;
    private final ProductoDAO productoDAO;
    private CatalogoFrame catalogoFrame;

    public SelectorProductos(AtomicBoolean usuarioAutenticado, AtomicBoolean aplicacionActiva, CarritoCompra carritoCompra) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.aplicacionActiva = aplicacionActiva;
        this.carritoCompra = carritoCompra;
        this.productoDAO = new ProductoDAO();
    }

    @Override
    public void run() {
        try {
            // Esperar a que el usuario se autentique
            while (aplicacionActiva.get() && !usuarioAutenticado.get()) {
                Thread.sleep(100);
            }

            if (!aplicacionActiva.get()) {
                return;
            }

            System.out.println("SelectorProductos iniciado - Mostrando catálogo");

            // Crear y mostrar la ventana del catálogo
            catalogoFrame = new CatalogoFrame(this, carritoCompra, productoDAO);
            catalogoFrame.setVisible(true);

            // Mantener el hilo activo mientras la aplicación esté corriendo
            while (aplicacionActiva.get() && usuarioAutenticado.get()) {
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("SelectorProductos interrumpido");
        } catch (Exception e) {
            System.err.println("Error en SelectorProductos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (catalogoFrame != null) {
                catalogoFrame.dispose();
            }
        }
    }

    /**
     * Agrega un producto al carrito de compras
     */
    public synchronized void agregarAlCarrito(Producto producto) {
        if (producto != null) {
            carritoCompra.agregarProducto(producto);
            System.out.println("Producto agregado al carrito: " + producto.getNombre());
            
            // Notificar a otros hilos sobre el cambio
            notifyAll();
        }
    }

    /**
     * Elimina un producto del carrito de compras
     */
    public synchronized void eliminarDelCarrito(Producto producto) {
        if (producto != null) {
            carritoCompra.eliminarProducto(producto);
            System.out.println("Producto eliminado del carrito: " + producto.getNombre());
            
            // Notificar a otros hilos sobre el cambio
            notifyAll();
        }
    }

    /**
     * Obtiene el carrito de compras
     */
    public CarritoCompra getCarritoCompra() {
        return carritoCompra;
    }

    /**
     * Obtiene el DAO de productos
     */
    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }
    
    /**
     * Obtiene todos los productos de la base de datos
     */
    public List<Producto> obtenerTodosLosProductos() {
        return productoDAO.obtenerTodosLosProductos();
    }
    
    /**
     * Obtiene productos por categoría
     */
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoDAO.obtenerProductosPorCategoria(categoria);
    }
    
    /**
     * Obtiene todas las categorías disponibles
     */
    public List<String> obtenerCategorias() {
        return productoDAO.obtenerCategorias();
    }

    /**
     * Cierra la ventana del catálogo
     */
    public void cerrarCatalogo() {
        if (catalogoFrame != null) {
            catalogoFrame.dispose();
        }
    }

    /**
     * Actualiza la interfaz del catálogo
     */
    public void actualizarInterfaz() {
        if (catalogoFrame != null) {
            catalogoFrame.actualizarCarrito();
        }
    }

    /**
     * Procesa el pago de la compra
     */
    public void procesarPago() {
        // Este método será llamado desde la interfaz para activar el finalizador
        // La lógica real del pago se maneja en FinalizadorCompra
        System.out.println("Solicitud de pago recibida en SelectorProductos");
    }

    /**
     * Obtiene la ventana del catálogo
     */
    public CatalogoFrame getCatalogoFrame() {
        return catalogoFrame;
    }
}
