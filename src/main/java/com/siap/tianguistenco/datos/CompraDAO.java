package com.siap.tianguistenco.datos;

import com.siap.tianguistenco.model.Compra;
import com.siap.tianguistenco.model.CompraItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de compras
 */
public class CompraDAO {
    private final DatabaseManager dbManager;

    public CompraDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Guarda una compra completa con sus items
     */
    public int guardarCompra(Compra compra) {
        String sqlCompra = "INSERT INTO compras (usuario_id, folio, fecha, total, descuento, estado, tipo_envio, direccion_envio, costo_envio) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmtCompra = conn.prepareStatement(sqlCompra, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmtCompra.setInt(1, compra.getUsuarioId());
            stmtCompra.setString(2, compra.getFolio());
            stmtCompra.setString(3, compra.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmtCompra.setDouble(4, compra.getTotal());
            stmtCompra.setDouble(5, compra.getDescuento());
            stmtCompra.setString(6, compra.getEstado());
            stmtCompra.setString(7, compra.getTipoEnvio().name());
            stmtCompra.setString(8, compra.getDireccionEnvio());
            stmtCompra.setDouble(9, compra.getCostoEnvio());

            int rowsAffected = stmtCompra.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmtCompra.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int compraId = generatedKeys.getInt(1);
                        // Guardar items de la compra
                        guardarItemsCompra(compraId, compra.getItems());
                        return compraId;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar compra: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Guarda los items de una compra
     */
    private void guardarItemsCompra(int compraId, List<CompraItem> items) throws SQLException {
        String sql = "INSERT INTO compras_items (compra_id, producto_id, nombre_producto, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CompraItem item : items) {
                stmt.setInt(1, compraId);
                stmt.setString(2, item.getProductoId());
                stmt.setString(3, item.getNombreProducto());
                stmt.setInt(4, item.getCantidad());
                stmt.setDouble(5, item.getPrecioUnitario());
                stmt.setDouble(6, item.getSubtotal());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    /**
     * Obtiene todas las compras de un usuario
     */
    public List<Compra> obtenerComprasPorUsuario(int usuarioId) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT id, usuario_id, folio, fecha, total, descuento, estado, tipo_envio, direccion_envio, costo_envio " +
                     "FROM compras WHERE usuario_id = ? ORDER BY fecha DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Compra compra = new Compra(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("folio"),
                        LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        rs.getDouble("total"),
                        rs.getDouble("descuento"),
                        rs.getString("estado"),
                        Compra.TipoEnvio.valueOf(rs.getString("tipo_envio")),
                        rs.getString("direccion_envio"),
                        rs.getDouble("costo_envio")
                    );
                    // Cargar items
                    compra.setItems(obtenerItemsCompra(compra.getId()));
                    compras.add(compra);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras: " + e.getMessage());
            e.printStackTrace();
        }

        return compras;
    }

    /**
     * Obtiene una compra por su folio
     */
    public Compra obtenerCompraPorFolio(String folio) {
        String sql = "SELECT id, usuario_id, folio, fecha, total, descuento, estado, tipo_envio, direccion_envio, costo_envio " +
                     "FROM compras WHERE folio = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, folio);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Compra compra = new Compra(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("folio"),
                        LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        rs.getDouble("total"),
                        rs.getDouble("descuento"),
                        rs.getString("estado"),
                        Compra.TipoEnvio.valueOf(rs.getString("tipo_envio")),
                        rs.getString("direccion_envio"),
                        rs.getDouble("costo_envio")
                    );
                    // Cargar items
                    compra.setItems(obtenerItemsCompra(compra.getId()));
                    return compra;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compra por folio: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene una compra por su ID
     */
    public Compra obtenerCompraPorId(int compraId) {
        String sql = "SELECT id, usuario_id, folio, fecha, total, descuento, estado, tipo_envio, direccion_envio, costo_envio " +
                     "FROM compras WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Compra compra = new Compra(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("folio"),
                        LocalDateTime.parse(rs.getString("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        rs.getDouble("total"),
                        rs.getDouble("descuento"),
                        rs.getString("estado"),
                        Compra.TipoEnvio.valueOf(rs.getString("tipo_envio")),
                        rs.getString("direccion_envio"),
                        rs.getDouble("costo_envio")
                    );
                    // Cargar items
                    compra.setItems(obtenerItemsCompra(compra.getId()));
                    return compra;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compra por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene los items de una compra
     */
    private List<CompraItem> obtenerItemsCompra(int compraId) {
        List<CompraItem> items = new ArrayList<>();
        String sql = "SELECT id, compra_id, producto_id, nombre_producto, cantidad, precio_unitario, subtotal " +
                     "FROM compras_items WHERE compra_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CompraItem item = new CompraItem(
                        rs.getInt("id"),
                        rs.getInt("compra_id"),
                        rs.getString("producto_id"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal")
                    );
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener items de compra: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Genera un folio único para una compra
     */
    public String generarFolio() {
        return "SIAP-" + System.currentTimeMillis();
    }
}

