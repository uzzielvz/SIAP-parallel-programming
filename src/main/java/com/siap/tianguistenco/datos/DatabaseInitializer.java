package com.siap.tianguistenco.datos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase responsable de inicializar la base de datos
 * Crea las tablas y las puebla con datos iniciales
 */
public class DatabaseInitializer {
    private final DatabaseManager dbManager;
    
    public DatabaseInitializer() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Inicializa la base de datos completa
     * Crea tablas y las puebla con datos iniciales
     */
    public void inicializar() {
        try {
            System.out.println("Inicializando base de datos...");
            
            crearTablas();
            poblarUsuarios();
            poblarProductos();
            
            System.out.println("Base de datos inicializada correctamente");
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea las tablas necesarias en la base de datos
     * @throws SQLException si hay error al crear las tablas
     */
    public void crearTablas() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla usuarios
            String crearUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
            
            // Crear tabla productos
            String crearProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                "id TEXT PRIMARY KEY, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "precio REAL NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "imagen TEXT, " +
                "stock INTEGER DEFAULT 0, " +
                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
            
            stmt.execute(crearUsuarios);
            stmt.execute(crearProductos);
            
            System.out.println("Tablas creadas correctamente");
        }
    }
    
    /**
     * Puebla la tabla usuarios con datos iniciales
     * @throws SQLException si hay error al insertar datos
     */
    public void poblarUsuarios() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verificar si ya hay usuarios
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            int count = rs.getInt(1);
            rs.close();
            
            if (count == 0) {
                // Insertar usuario por defecto
                String insertUsuario = "INSERT INTO usuarios (username, password) " +
                    "VALUES ('admin', 'admin')";
                
                stmt.execute(insertUsuario);
                System.out.println("Usuario por defecto creado: admin/admin");
            } else {
                System.out.println("Usuarios ya existen en la base de datos");
            }
        }
    }
    
    /**
     * Puebla la tabla productos con todos los productos de SIAP
     * @throws SQLException si hay error al insertar datos
     */
    public void poblarProductos() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Verificar si ya hay productos
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM productos");
            int count = rs.getInt(1);
            rs.close();
            
            if (count == 0) {
                System.out.println("Poblando productos en la base de datos...");
                
                // Insertar todos los productos
                insertarProductosLeche(stmt);
                insertarProductosYogurt(stmt);
                insertarProductosMantequilla(stmt);
                insertarProductosSnacks(stmt);
                insertarProductosLimpieza(stmt);
                insertarProductosBebidas(stmt);
                
                System.out.println("Productos insertados correctamente");
            } else {
                System.out.println("Productos ya existen en la base de datos");
            }
        }
    }
    
    private void insertarProductosLeche(Statement stmt) throws SQLException {
        String[] productosLeche = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC001', 'Leche Lala Entera', 'Leche entera Lala 1L', 28.50, 'Leche', 'leche_lala.png', 50)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC002', 'Leche Santa Clara Entera', 'Leche entera Santa Clara 1L (6 piezas)', 230.00, 'Leche', 'leche_santa_clara.png', 30)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC003', 'Leche Alpura Entera', 'Leche entera Alpura 1L (6 piezas)', 180.00, 'Leche', 'leche_alpura.png', 25)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC004', 'Leche Nutrileche', 'Leche entera Nutrileche 1L', 25.00, 'Leche', 'leche_nutrileche.png', 40)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC005', 'Leche Lala Deslactosada', 'Leche deslactosada Lala 1L (6 piezas)', 159.00, 'Leche', 'leche_lala_deslac.png', 20)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC006', 'Leche Alpura Deslactosada', 'Leche deslactosada Alpura 1L', 30.00, 'Leche', 'leche_alpura_deslac.png', 35)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC007', 'Leche Santa Clara Deslactosada', 'Leche deslactosada Santa Clara 1L', 40.00, 'Leche', 'leche_santa_clara_deslac.png', 15)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC008', 'Leche Lala Yomi Vainilla', 'Leche saborizada Lala Yomi vainilla 180ml', 10.00, 'Leche', 'leche_yomi_vainilla.png', 100)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC009', 'Leche Lala Yomi Chocolate', 'Leche saborizada Lala Yomi chocolate 180ml', 10.00, 'Leche', 'leche_yomi_chocolate.png', 100)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC010', 'Leche Lala Yomi Fresa', 'Leche saborizada Lala Yomi fresa 180ml', 10.00, 'Leche', 'leche_yomi_fresa.png', 100)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC011', 'Leche Alpura Vainilla', 'Leche saborizada Alpura vainilla 180ml', 11.00, 'Leche', 'leche_alpura_vainilla.png', 80)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC012', 'Leche Alpura Fresa', 'Leche saborizada Alpura fresa 180ml', 11.00, 'Leche', 'leche_alpura_fresa.png', 80)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC013', 'Leche Alpura Chocolate', 'Leche saborizada Alpura chocolate 180ml', 11.00, 'Leche', 'leche_alpura_chocolate.png', 80)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC014', 'Leche Santa Clara Vainilla', 'Leche saborizada Santa Clara vainilla 180ml', 13.00, 'Leche', 'leche_santa_clara_vainilla.png', 60)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC015', 'Leche Santa Clara Chocolate', 'Leche saborizada Santa Clara chocolate 180ml', 13.00, 'Leche', 'leche_santa_clara_chocolate.png', 60)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LEC016', 'Leche Santa Clara Fresa', 'Leche saborizada Santa Clara fresa 180ml', 13.00, 'Leche', 'leche_santa_clara_fresa.png', 60)"
        };
        
        for (String sql : productosLeche) {
            stmt.execute(sql);
        }
    }
    
    private void insertarProductosYogurt(Statement stmt) throws SQLException {
        String[] productosYogurt = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('YOG001', 'Yogurt Lala Fresa', 'Yogurt bebible Lala fresa 220g (8 piezas)', 70.00, 'Yogurt', 'yogurt_lala_fresa.png', 25)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('YOG002', 'Yogurt Alpura Natural', 'Yogurt natural Alpura 1kg', 42.00, 'Yogurt', 'yogurt_alpura_natural.png', 30)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('YOG003', 'Yogurt Danone Griego', 'Yogurt griego Danone 150g', 18.00, 'Yogurt', 'yogurt_danone_griego.png', 40)"
        };
        
        for (String sql : productosYogurt) {
            stmt.execute(sql);
        }
    }
    
    private void insertarProductosMantequilla(Statement stmt) throws SQLException {
        String[] productosMantequilla = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('MAN001', 'Mantequilla Lala Sin Sal', 'Mantequilla Lala sin sal 90g', 24.00, 'Mantequilla y Margarina', 'mantequilla_lala.png', 20)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('MAN002', 'Margarina Primavera', 'Margarina Primavera 225g', 18.00, 'Mantequilla y Margarina', 'margarina_primavera.png', 25)"
        };
        
        for (String sql : productosMantequilla) {
            stmt.execute(sql);
        }
    }
    
    private void insertarProductosSnacks(Statement stmt) throws SQLException {
        String[] productosSnacks = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK001', 'Canelitas Marinela', 'Galletas Canelitas Marinela 300g', 37.90, 'Snacks', 'canelitas.png', 15)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK002', 'Chokis Marinela', 'Galletas Chokis Marinela 300g', 107.00, 'Snacks', 'chokis.png', 10)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK003', 'Sponch Marinela', 'Galletas Sponch Marinela 700g (4 paquetes)', 79.50, 'Snacks', 'sponch.png', 8)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK004', 'Pasticetas Marinela', 'Galletas Pasticetas Marinela 400g', 65.90, 'Snacks', 'pasticetas.png', 12)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK005', 'Surtido Marinela', 'Surtido de galletas Marinela 450g', 73.50, 'Snacks', 'surtido_marinela.png', 10)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK006', 'Sabritas Flamin'' Hot', 'Papitas Sabritas Flamin'' Hot 20g', 18.00, 'Snacks', 'sabritas_flamin_hot.png', 50)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK007', 'Sabritas Adobadas', 'Papitas Sabritas Adobadas 20g', 18.00, 'Snacks', 'sabritas_adobadas.png', 50)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK008', 'Sabritas Original', 'Papitas Sabritas Original 20g', 18.00, 'Snacks', 'sabritas_original.png', 50)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK009', 'Doritos Rojos', 'Doritos Rojos 75g', 18.00, 'Snacks', 'doritos_rojos.png', 40)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK010', 'Doritos Verdes', 'Doritos Verdes 35g', 18.00, 'Snacks', 'doritos_verdes.png', 40)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK011', 'Cheetos Torciditos', 'Cheetos Torciditos 80g', 15.00, 'Snacks', 'cheetos_torciditos.png', 35)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK012', 'Cheetos Puffs', 'Cheetos Puffs 80g', 15.00, 'Snacks', 'cheetos_puffs.png', 35)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK013', 'Cheetos Flamin'' Hot', 'Cheetos Flamin'' Hot 80g', 15.00, 'Snacks', 'cheetos_flamin_hot.png', 35)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK014', 'Cacahuates', 'Cacahuates 70g', 20.00, 'Snacks', 'cacahuates.png', 30)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK015', 'Gansito Marinela', 'Pastelito Gansito Marinela 50g', 20.90, 'Snacks', 'gansito.png', 25)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK016', 'Pingüinos Marinela', 'Pastelito Pingüinos Marinela 80g', 27.90, 'Snacks', 'pinguinos.png', 20)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK017', 'Choco Roles Marinela', 'Pastelito Choco Roles Marinela 122g (2 piezas)', 27.90, 'Snacks', 'choco_roles.png', 18)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('SNK018', 'Gansito Marinela 3 Piezas', 'Pastelito Gansito Marinela 3 piezas', 50.90, 'Snacks', 'gansito_3piezas.png', 15)"
        };
        
        for (String sql : productosSnacks) {
            stmt.execute(sql);
        }
    }
    
    private void insertarProductosLimpieza(Statement stmt) throws SQLException {
        String[] productosLimpieza = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM001', 'Pinol El Original', 'Desinfectante Pinol El Original 5.1L', 179.00, 'Productos de Limpieza', 'pinol.png', 10)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM002', 'Fabuloso', 'Desinfectante Fabuloso 6L', 199.00, 'Productos de Limpieza', 'fabuloso.png', 8)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM003', 'Cloralex', 'Desinfectante Cloralex 1L', 68.00, 'Productos de Limpieza', 'cloralex.png', 15)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM004', 'Clorox', 'Desinfectante Clorox 1L', 65.00, 'Productos de Limpieza', 'clorox.png', 15)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM005', 'Vanish', 'Desinfectante Vanish 1L', 90.00, 'Productos de Limpieza', 'vanish.png', 12)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM006', 'Ariel Líquido Poder y Cuidado', 'Detergente Ariel Líquido Poder y Cuidado 8.5L', 374.25, 'Productos de Limpieza', 'ariel_liquido.png', 5)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM007', 'Persil Polvo Color', 'Detergente Persil en Polvo para Ropa de Color 9kg', 439.00, 'Productos de Limpieza', 'persil_polvo.png', 4)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM008', 'Ariel Líquido Color', 'Detergente Ariel Líquido Color 2.8L (45 lavadas)', 149.00, 'Productos de Limpieza', 'ariel_color.png', 8)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM009', 'Ariel Polvo Downy', 'Detergente Ariel en Polvo con Downy 750g', 35.00, 'Productos de Limpieza', 'ariel_polvo.png', 20)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM010', 'Ariel Expert Líquido', 'Detergente Ariel Expert Líquido 5L (80 lavadas)', 194.90, 'Productos de Limpieza', 'ariel_expert.png', 6)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM011', 'Salvo Limón Líquido', 'Lavatrastes Salvo Limón Líquido 1.4L', 69.00, 'Productos de Limpieza', 'salvo_liquido.png', 12)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM012', 'Salvo Polvo', 'Lavatrastes Salvo polvo 1Kg', 39.00, 'Productos de Limpieza', 'salvo_polvo.png', 15)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM013', 'Salvo Lavatrastes Limón 900ml', 'Lavatrastes Salvo Limón 900ml', 55.00, 'Productos de Limpieza', 'salvo_900ml.png', 18)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('LIM014', 'Salvo Lavatrastes Limón 500ml', 'Lavatrastes Salvo Limón 500ml', 32.90, 'Productos de Limpieza', 'salvo_500ml.png', 25)"
        };
        
        for (String sql : productosLimpieza) {
            stmt.execute(sql);
        }
    }
    
    private void insertarProductosBebidas(Statement stmt) throws SQLException {
        String[] productosBebidas = {
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('BEB001', 'Coca-Cola 600ml', 'Refresco Coca-Cola 600ml', 18.00, 'Bebidas', 'coca_cola_600ml.png', 100)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('BEB002', 'Coca-Cola 3L', 'Refresco Coca-Cola 3L', 45.00, 'Bebidas', 'coca_cola_3l.png', 30)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('BEB003', 'Agua Ciel 1L', 'Agua purificada Ciel 1L', 15.00, 'Bebidas', 'agua_ciel.png', 80)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('BEB004', 'Jugo del Valle Naranja', 'Jugo del Valle sabor naranja 1L', 28.00, 'Bebidas', 'jugo_del_valle.png', 40)",
            "INSERT INTO productos (id, nombre, descripcion, precio, categoria, imagen, stock) VALUES ('BEB005', 'Powerade Morado', 'Bebida deportiva Powerade sabor morado 500ml', 22.00, 'Bebidas', 'powerade_morado.png', 35)"
        };
        
        for (String sql : productosBebidas) {
            stmt.execute(sql);
        }
    }
}
