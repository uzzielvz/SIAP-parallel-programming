package com.siap.tianguistenco.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que gestiona la conexión a la base de datos SQLite
 * Implementa el patrón Singleton para asegurar una única instancia
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:siap_tienda.db";
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        // Constructor privado para implementar Singleton
    }
    
    /**
     * Obtiene la instancia única del DatabaseManager
     * @return instancia del DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     * @return objeto Connection
     * @throws SQLException si hay error al conectar
     */
    public Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Crear conexión a la base de datos
            Connection connection = DriverManager.getConnection(DB_URL);
            
            // Habilitar foreign keys
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de SQLite no encontrado", e);
        }
    }
    
    /**
     * Verifica si la base de datos existe
     * @return true si la base de datos existe
     */
    public boolean databaseExists() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Cierra una conexión de manera segura
     * @param connection conexión a cerrar
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
