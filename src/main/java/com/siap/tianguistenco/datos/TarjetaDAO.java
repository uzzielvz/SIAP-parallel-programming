package com.siap.tianguistenco.datos;

import com.siap.tianguistenco.model.Tarjeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de tarjetas de pago
 */
public class TarjetaDAO {
    private final DatabaseManager dbManager;

    public TarjetaDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Registra una nueva tarjeta para un usuario
     */
    public boolean registrarTarjeta(int usuarioId, String numeroTarjeta, String nombreTitular,
                                    String fechaVencimiento, Tarjeta.TipoTarjeta tipo) {
        String sql = "INSERT INTO tarjetas (usuario_id, numero_tarjeta, nombre_titular, fecha_vencimiento, tipo, activa) " +
                     "VALUES (?, ?, ?, ?, ?, 1)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, numeroTarjeta);
            stmt.setString(3, nombreTitular);
            stmt.setString(4, fechaVencimiento);
            stmt.setString(5, tipo.name());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar tarjeta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las tarjetas activas de un usuario
     */
    public List<Tarjeta> obtenerTarjetasPorUsuario(int usuarioId) {
        List<Tarjeta> tarjetas = new ArrayList<>();
        String sql = "SELECT id, usuario_id, numero_tarjeta, nombre_titular, fecha_vencimiento, tipo, activa " +
                     "FROM tarjetas WHERE usuario_id = ? AND activa = 1 ORDER BY fecha_creacion DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tarjeta tarjeta = new Tarjeta(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("numero_tarjeta"),
                        rs.getString("nombre_titular"),
                        rs.getString("fecha_vencimiento"),
                        Tarjeta.TipoTarjeta.valueOf(rs.getString("tipo")),
                        rs.getInt("activa") == 1
                    );
                    tarjetas.add(tarjeta);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tarjetas: " + e.getMessage());
            e.printStackTrace();
        }

        return tarjetas;
    }

    /**
     * Obtiene una tarjeta por su ID
     */
    public Tarjeta obtenerTarjetaPorId(int tarjetaId) {
        String sql = "SELECT id, usuario_id, numero_tarjeta, nombre_titular, fecha_vencimiento, tipo, activa " +
                     "FROM tarjetas WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tarjetaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tarjeta(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("numero_tarjeta"),
                        rs.getString("nombre_titular"),
                        rs.getString("fecha_vencimiento"),
                        Tarjeta.TipoTarjeta.valueOf(rs.getString("tipo")),
                        rs.getInt("activa") == 1
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tarjeta por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Desactiva una tarjeta
     */
    public boolean desactivarTarjeta(int tarjetaId) {
        String sql = "UPDATE tarjetas SET activa = 0 WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tarjetaId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar tarjeta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

