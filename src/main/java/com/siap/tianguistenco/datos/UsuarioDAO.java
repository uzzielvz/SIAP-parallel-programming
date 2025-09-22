package com.siap.tianguistenco.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object para la gestión de usuarios
 * Maneja las operaciones CRUD de la tabla usuarios
 */
public class UsuarioDAO {
    private final DatabaseManager dbManager;
    
    public UsuarioDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Valida las credenciales de un usuario
     * @param username nombre de usuario
     * @param password contraseña
     * @return true si las credenciales son válidas
     */
    public boolean validarUsuario(String username, String password) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     * @param username nombre de usuario
     * @param password contraseña
     * @return true si el usuario se registró exitosamente
     */
    public boolean registrarUsuario(String username, String password) {
        // Verificar si el usuario ya existe
        if (usuarioExiste(username)) {
            System.out.println("El usuario '" + username + "' ya existe");
            return false;
        }
        
        String sql = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si un usuario ya existe
     * @param username nombre de usuario
     * @return true si el usuario existe
     */
    public boolean usuarioExiste(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene el ID de un usuario por su nombre
     * @param username nombre de usuario
     * @return ID del usuario o -1 si no existe
     */
    public int obtenerIdUsuario(String username) {
        String sql = "SELECT id FROM usuarios WHERE username = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
}
