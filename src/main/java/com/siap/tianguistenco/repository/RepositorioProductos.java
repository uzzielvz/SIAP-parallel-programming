package com.siap.tianguistenco.repository;

import com.siap.tianguistenco.model.Categoria;
import com.siap.tianguistenco.model.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio singleton que contiene todos los productos de SIAP Tianguistenco
 */
public class RepositorioProductos {
    private static RepositorioProductos instancia;
    private final List<Categoria> categorias;
    private final List<Producto> todosLosProductos;

    private RepositorioProductos() {
        this.categorias = new ArrayList<>();
        this.todosLosProductos = new ArrayList<>();
        inicializarProductos();
    }

    public static synchronized RepositorioProductos getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioProductos();
        }
        return instancia;
    }

    private void inicializarProductos() {
        // Categoría: Leche
        Categoria leche = new Categoria("Leche", "Productos lácteos frescos");
        
        // Leche entera
        leche.agregarProducto(new Producto("LEC001", "Leche Lala Entera", "Leche entera Lala 1L", 28.50, "Leche", "leche_lala.png", 50));
        leche.agregarProducto(new Producto("LEC002", "Leche Santa Clara Entera", "Leche entera Santa Clara 1L (6 piezas)", 230.00, "Leche", "leche_santa_clara.png", 30));
        leche.agregarProducto(new Producto("LEC003", "Leche Alpura Entera", "Leche entera Alpura 1L (6 piezas)", 180.00, "Leche", "leche_alpura.png", 25));
        leche.agregarProducto(new Producto("LEC004", "Leche Nutrileche", "Leche entera Nutrileche 1L", 25.00, "Leche", "leche_nutrileche.png", 40));
        
        // Leche deslactosada
        leche.agregarProducto(new Producto("LEC005", "Leche Lala Deslactosada", "Leche deslactosada Lala 1L (6 piezas)", 159.00, "Leche", "leche_lala_deslac.png", 20));
        leche.agregarProducto(new Producto("LEC006", "Leche Alpura Deslactosada", "Leche deslactosada Alpura 1L", 30.00, "Leche", "leche_alpura_deslac.png", 35));
        leche.agregarProducto(new Producto("LEC007", "Leche Santa Clara Deslactosada", "Leche deslactosada Santa Clara 1L", 40.00, "Leche", "leche_santa_clara_deslac.png", 15));
        
        // Leche saborizada
        leche.agregarProducto(new Producto("LEC008", "Leche Lala Yomi Vainilla", "Leche saborizada Lala Yomi vainilla 180ml", 10.00, "Leche", "leche_yomi_vainilla.png", 100));
        leche.agregarProducto(new Producto("LEC009", "Leche Lala Yomi Chocolate", "Leche saborizada Lala Yomi chocolate 180ml", 10.00, "Leche", "leche_yomi_chocolate.png", 100));
        leche.agregarProducto(new Producto("LEC010", "Leche Lala Yomi Fresa", "Leche saborizada Lala Yomi fresa 180ml", 10.00, "Leche", "leche_yomi_fresa.png", 100));
        leche.agregarProducto(new Producto("LEC011", "Leche Alpura Vainilla", "Leche saborizada Alpura vainilla 180ml", 11.00, "Leche", "leche_alpura_vainilla.png", 80));
        leche.agregarProducto(new Producto("LEC012", "Leche Alpura Fresa", "Leche saborizada Alpura fresa 180ml", 11.00, "Leche", "leche_alpura_fresa.png", 80));
        leche.agregarProducto(new Producto("LEC013", "Leche Alpura Chocolate", "Leche saborizada Alpura chocolate 180ml", 11.00, "Leche", "leche_alpura_chocolate.png", 80));
        leche.agregarProducto(new Producto("LEC014", "Leche Santa Clara Vainilla", "Leche saborizada Santa Clara vainilla 180ml", 13.00, "Leche", "leche_santa_clara_vainilla.png", 60));
        leche.agregarProducto(new Producto("LEC015", "Leche Santa Clara Chocolate", "Leche saborizada Santa Clara chocolate 180ml", 13.00, "Leche", "leche_santa_clara_chocolate.png", 60));
        leche.agregarProducto(new Producto("LEC016", "Leche Santa Clara Fresa", "Leche saborizada Santa Clara fresa 180ml", 13.00, "Leche", "leche_santa_clara_fresa.png", 60));
        
        categorias.add(leche);
        todosLosProductos.addAll(leche.getProductos());

        // Categoría: Yogurt
        Categoria yogurt = new Categoria("Yogurt", "Productos de yogurt y lácteos fermentados");
        
        yogurt.agregarProducto(new Producto("YOG001", "Yogurt Lala Fresa", "Yogurt bebible Lala fresa 220g (8 piezas)", 70.00, "Yogurt", "yogurt_lala_fresa.png", 25));
        yogurt.agregarProducto(new Producto("YOG002", "Yogurt Alpura Natural", "Yogurt natural Alpura 1kg", 42.00, "Yogurt", "yogurt_alpura_natural.png", 30));
        yogurt.agregarProducto(new Producto("YOG003", "Yogurt Danone Griego", "Yogurt griego Danone 150g", 18.00, "Yogurt", "yogurt_danone_griego.png", 40));
        
        categorias.add(yogurt);
        todosLosProductos.addAll(yogurt.getProductos());

        // Categoría: Mantequilla y Margarina
        Categoria mantequilla = new Categoria("Mantequilla y Margarina", "Productos para untar");
        
        mantequilla.agregarProducto(new Producto("MAN001", "Mantequilla Lala Sin Sal", "Mantequilla Lala sin sal 90g", 24.00, "Mantequilla y Margarina", "mantequilla_lala.png", 20));
        mantequilla.agregarProducto(new Producto("MAN002", "Margarina Primavera", "Margarina Primavera 225g", 18.00, "Mantequilla y Margarina", "margarina_primavera.png", 25));
        
        categorias.add(mantequilla);
        todosLosProductos.addAll(mantequilla.getProductos());

        // Categoría: Snacks
        Categoria snacks = new Categoria("Snacks", "Galletas, botanas y pastelitos");
        
        // Galletas
        snacks.agregarProducto(new Producto("SNK001", "Canelitas Marinela", "Galletas Canelitas Marinela 300g", 37.90, "Snacks", "canelitas.png", 15));
        snacks.agregarProducto(new Producto("SNK002", "Chokis Marinela", "Galletas Chokis Marinela 300g", 107.00, "Snacks", "chokis.png", 10));
        snacks.agregarProducto(new Producto("SNK003", "Sponch Marinela", "Galletas Sponch Marinela 700g (4 paquetes)", 79.50, "Snacks", "sponch.png", 8));
        snacks.agregarProducto(new Producto("SNK004", "Pasticetas Marinela", "Galletas Pasticetas Marinela 400g", 65.90, "Snacks", "pasticetas.png", 12));
        snacks.agregarProducto(new Producto("SNK005", "Surtido Marinela", "Surtido de galletas Marinela 450g", 73.50, "Snacks", "surtido_marinela.png", 10));
        
        // Botanas - Papitas
        snacks.agregarProducto(new Producto("SNK006", "Sabritas Flamin' Hot", "Papitas Sabritas Flamin' Hot 20g", 18.00, "Snacks", "sabritas_flamin_hot.png", 50));
        snacks.agregarProducto(new Producto("SNK007", "Sabritas Adobadas", "Papitas Sabritas Adobadas 20g", 18.00, "Snacks", "sabritas_adobadas.png", 50));
        snacks.agregarProducto(new Producto("SNK008", "Sabritas Original", "Papitas Sabritas Original 20g", 18.00, "Snacks", "sabritas_original.png", 50));
        snacks.agregarProducto(new Producto("SNK009", "Doritos Rojos", "Doritos Rojos 75g", 18.00, "Snacks", "doritos_rojos.png", 40));
        snacks.agregarProducto(new Producto("SNK010", "Doritos Verdes", "Doritos Verdes 35g", 18.00, "Snacks", "doritos_verdes.png", 40));
        snacks.agregarProducto(new Producto("SNK011", "Cheetos Torciditos", "Cheetos Torciditos 80g", 15.00, "Snacks", "cheetos_torciditos.png", 35));
        snacks.agregarProducto(new Producto("SNK012", "Cheetos Puffs", "Cheetos Puffs 80g", 15.00, "Snacks", "cheetos_puffs.png", 35));
        snacks.agregarProducto(new Producto("SNK013", "Cheetos Flamin' Hot", "Cheetos Flamin' Hot 80g", 15.00, "Snacks", "cheetos_flamin_hot.png", 35));
        snacks.agregarProducto(new Producto("SNK014", "Cacahuates", "Cacahuates 70g", 20.00, "Snacks", "cacahuates.png", 30));
        
        // Pastelitos
        snacks.agregarProducto(new Producto("SNK015", "Gansito Marinela", "Pastelito Gansito Marinela 50g", 20.90, "Snacks", "gansito.png", 25));
        snacks.agregarProducto(new Producto("SNK016", "Pingüinos Marinela", "Pastelito Pingüinos Marinela 80g", 27.90, "Snacks", "pinguinos.png", 20));
        snacks.agregarProducto(new Producto("SNK017", "Choco Roles Marinela", "Pastelito Choco Roles Marinela 122g (2 piezas)", 27.90, "Snacks", "choco_roles.png", 18));
        snacks.agregarProducto(new Producto("SNK018", "Gansito Marinela 3 Piezas", "Pastelito Gansito Marinela 3 piezas", 50.90, "Snacks", "gansito_3piezas.png", 15));
        
        categorias.add(snacks);
        todosLosProductos.addAll(snacks.getProductos());

        // Categoría: Productos de Limpieza
        Categoria limpieza = new Categoria("Productos de Limpieza", "Detergentes, desinfectantes y productos de limpieza");
        
        // Desinfectantes
        limpieza.agregarProducto(new Producto("LIM001", "Pinol El Original", "Desinfectante Pinol El Original 5.1L", 179.00, "Productos de Limpieza", "pinol.png", 10));
        limpieza.agregarProducto(new Producto("LIM002", "Fabuloso", "Desinfectante Fabuloso 6L", 199.00, "Productos de Limpieza", "fabuloso.png", 8));
        limpieza.agregarProducto(new Producto("LIM003", "Cloralex", "Desinfectante Cloralex 1L", 68.00, "Productos de Limpieza", "cloralex.png", 15));
        limpieza.agregarProducto(new Producto("LIM004", "Clorox", "Desinfectante Clorox 1L", 65.00, "Productos de Limpieza", "clorox.png", 15));
        limpieza.agregarProducto(new Producto("LIM005", "Vanish", "Desinfectante Vanish 1L", 90.00, "Productos de Limpieza", "vanish.png", 12));
        
        // Detergentes
        limpieza.agregarProducto(new Producto("LIM006", "Ariel Líquido Poder y Cuidado", "Detergente Ariel Líquido Poder y Cuidado 8.5L", 374.25, "Productos de Limpieza", "ariel_liquido.png", 5));
        limpieza.agregarProducto(new Producto("LIM007", "Persil Polvo Color", "Detergente Persil en Polvo para Ropa de Color 9kg", 439.00, "Productos de Limpieza", "persil_polvo.png", 4));
        limpieza.agregarProducto(new Producto("LIM008", "Ariel Líquido Color", "Detergente Ariel Líquido Color 2.8L (45 lavadas)", 149.00, "Productos de Limpieza", "ariel_color.png", 8));
        limpieza.agregarProducto(new Producto("LIM009", "Ariel Polvo Downy", "Detergente Ariel en Polvo con Downy 750g", 35.00, "Productos de Limpieza", "ariel_polvo.png", 20));
        limpieza.agregarProducto(new Producto("LIM010", "Ariel Expert Líquido", "Detergente Ariel Expert Líquido 5L (80 lavadas)", 194.90, "Productos de Limpieza", "ariel_expert.png", 6));
        
        // Lavatrastes
        limpieza.agregarProducto(new Producto("LIM011", "Salvo Limón Líquido", "Lavatrastes Salvo Limón Líquido 1.4L", 69.00, "Productos de Limpieza", "salvo_liquido.png", 12));
        limpieza.agregarProducto(new Producto("LIM012", "Salvo Polvo", "Lavatrastes Salvo polvo 1Kg", 39.00, "Productos de Limpieza", "salvo_polvo.png", 15));
        limpieza.agregarProducto(new Producto("LIM013", "Salvo Lavatrastes Limón 900ml", "Lavatrastes Salvo Limón 900ml", 55.00, "Productos de Limpieza", "salvo_900ml.png", 18));
        limpieza.agregarProducto(new Producto("LIM014", "Salvo Lavatrastes Limón 500ml", "Lavatrastes Salvo Limón 500ml", 32.90, "Productos de Limpieza", "salvo_500ml.png", 25));
        
        categorias.add(limpieza);
        todosLosProductos.addAll(limpieza.getProductos());

        // Categoría: Bebidas
        Categoria bebidas = new Categoria("Bebidas", "Refrescos, agua y jugos");
        
        bebidas.agregarProducto(new Producto("BEB001", "Coca-Cola 600ml", "Refresco Coca-Cola 600ml", 18.00, "Bebidas", "coca_cola_600ml.png", 100));
        bebidas.agregarProducto(new Producto("BEB002", "Coca-Cola 3L", "Refresco Coca-Cola 3L", 45.00, "Bebidas", "coca_cola_3l.png", 30));
        bebidas.agregarProducto(new Producto("BEB003", "Agua Ciel 1L", "Agua purificada Ciel 1L", 15.00, "Bebidas", "agua_ciel.png", 80));
        bebidas.agregarProducto(new Producto("BEB004", "Jugo del Valle Naranja", "Jugo del Valle sabor naranja 1L", 28.00, "Bebidas", "jugo_del_valle.png", 40));
        bebidas.agregarProducto(new Producto("BEB005", "Powerade Morado", "Bebida deportiva Powerade sabor morado 500ml", 22.00, "Bebidas", "powerade_morado.png", 35));
        
        categorias.add(bebidas);
        todosLosProductos.addAll(bebidas.getProductos());
    }

    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    public List<Producto> getTodosLosProductos() {
        return new ArrayList<>(todosLosProductos);
    }

    public List<Producto> getProductosPorCategoria(String categoria) {
        return todosLosProductos.stream()
                .filter(producto -> producto.getCategoria().equals(categoria))
                .toList();
    }

    public Producto buscarProductoPorId(String id) {
        return todosLosProductos.stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
