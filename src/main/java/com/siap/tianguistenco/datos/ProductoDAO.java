package com.siap.tianguistenco.datos;

import com.siap.tianguistenco.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de productos
 * Maneja las operaciones CRUD de la tabla productos
 */
public class ProductoDAO {
    private final DatabaseManager dbManager;
    
    public ProductoDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Obtiene todos los productos de la base de datos
     * @return lista de productos
     */
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, precio, categoria, imagen, stock FROM productos ORDER BY categoria, nombre";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getString("imagen"),
                    rs.getInt("stock")
                );
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    /**
     * Obtiene productos por categoría
     * @param categoria nombre de la categoría
     * @return lista de productos de la categoría
     */
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, precio, categoria, imagen, stock FROM productos WHERE categoria = ? ORDER BY nombre";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getString("imagen"),
                        rs.getInt("stock")
                    );
                    productos.add(producto);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productos;
    }
    
    /**
     * Busca un producto por su ID
     * @param id ID del producto
     * @return producto encontrado o null si no existe
     */
    public Producto buscarProductoPorId(String id) {
        String sql = "SELECT id, nombre, descripcion, precio, categoria, imagen, stock FROM productos WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getString("imagen"),
                        rs.getInt("stock")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las categorías disponibles
     * @return lista de categorías únicas
     */
    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM productos ORDER BY categoria";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categorias;
    }
    
    /**
     * Actualiza el stock de un producto
     * @param id ID del producto
     * @param nuevoStock nuevo valor de stock
     * @return true si se actualizó correctamente
     */
    public boolean actualizarStock(String id, int nuevoStock) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nuevoStock);
            stmt.setString(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
