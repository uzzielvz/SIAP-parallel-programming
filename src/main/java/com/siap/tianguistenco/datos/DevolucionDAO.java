package com.siap.tianguistenco.datos;

import com.siap.tianguistenco.model.Devolucion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de devoluciones
 */
public class DevolucionDAO {
    private final DatabaseManager dbManager;

    public DevolucionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Registra una nueva devolución
     */
    public int registrarDevolucion(Devolucion devolucion) {
        String sql = "INSERT INTO devoluciones (compra_id, folio_compra, fecha, motivo, estado, monto_devolucion, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, devolucion.getCompraId());
            stmt.setString(2, devolucion.getFolioCompra());
            stmt.setString(3, devolucion.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(4, devolucion.getMotivo().name());
            stmt.setString(5, devolucion.getEstado());
            stmt.setDouble(6, devolucion.getMontoDevolucion());
            stmt.setString(7, devolucion.getObservaciones());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar devolución: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Obtiene todas las devoluciones de un usuario (a través de sus compras)
     */
    public List<Devolucion> obtenerDevolucionesPorUsuario(int usuarioId) {
        List<Devolucion> devoluciones = new ArrayList<>();
        String sql = "SELECT d.id, d.compra_id, d.folio_compra, d.fecha, d.motivo, d.estado, d.monto_devolucion, d.observaciones " +
                     "FROM devoluciones d " +
                     "INNER JOIN compras c ON d.compra_id = c.id " +
                     "WHERE c.usuario_id = ? ORDER BY d.fecha DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Devolucion devolucion = new Devolucion(
                        rs.getInt("id"),
                        rs.getInt("compra_id"),
                        rs.getString("folio_compra"),
                        LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Devolucion.MotivoDevolucion.valueOf(rs.getString("motivo")),
                        rs.getString("estado"),
                        rs.getDouble("monto_devolucion"),
                        rs.getString("observaciones")
                    );
                    devoluciones.add(devolucion);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener devoluciones: " + e.getMessage());
            e.printStackTrace();
        }

        return devoluciones;
    }

    /**
     * Obtiene una devolución por su ID
     */
    public Devolucion obtenerDevolucionPorId(int devolucionId) {
        String sql = "SELECT id, compra_id, folio_compra, fecha, motivo, estado, monto_devolucion, observaciones " +
                     "FROM devoluciones WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, devolucionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Devolucion(
                        rs.getInt("id"),
                        rs.getInt("compra_id"),
                        rs.getString("folio_compra"),
                        LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Devolucion.MotivoDevolucion.valueOf(rs.getString("motivo")),
                        rs.getString("estado"),
                        rs.getDouble("monto_devolucion"),
                        rs.getString("observaciones")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener devolución por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Actualiza el estado de una devolución
     */
    public boolean actualizarEstadoDevolucion(int devolucionId, String nuevoEstado) {
        String sql = "UPDATE devoluciones SET estado = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, devolucionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de devolución: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

