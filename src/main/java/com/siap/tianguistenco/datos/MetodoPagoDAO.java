package com.siap.tianguistenco.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object para la gestión de métodos de pago
 */
public class MetodoPagoDAO {
    private final DatabaseManager dbManager;

    public MetodoPagoDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Registra un método de pago usado en una compra
     */
    public boolean registrarMetodoPago(int compraId, String tipoPago, Integer tarjetaId, double monto) {
        String sql = "INSERT INTO metodos_pago (compra_id, tipo_pago, tarjeta_id, monto) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, compraId);
            stmt.setString(2, tipoPago);
            if (tarjetaId != null) {
                stmt.setInt(3, tarjetaId);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setDouble(4, monto);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar método de pago: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

