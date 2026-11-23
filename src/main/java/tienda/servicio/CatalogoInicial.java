package tienda.servicio;

import java.util.Map;
import tienda.modelo.Categoria;
import tienda.modelo.Cliente;

/** Datos de demostración (todo vive en memoria, sin base de datos). */
public final class CatalogoInicial {
    private CatalogoInicial() {
    }

    public static Tienda cargar(Tienda t) {
        t.crearProducto(Categoria.ROPA, "R1", "Camiseta básica", 45_000, 20, Map.of("talla", "M", "color", "Blanco"));
        t.crearProducto(Categoria.ROPA, "R2", "Jean clásico", 120_000, 10, Map.of("talla", "32", "color", "Azul"));
        t.crearProducto(Categoria.ROPA, "R3", "Chaqueta impermeable", 210_000, 5, Map.of("talla", "L", "color", "Negro"));
        t.crearProducto(Categoria.TECNOLOGIA, "T1", "Audífonos inalámbricos", 180_000, 0, Map.of("marca", "SoundMax", "mesesGarantia", "12"));
        t.crearProducto(Categoria.TECNOLOGIA, "T2", "Teclado mecánico", 250_000, 8, Map.of("marca", "KeyPro", "mesesGarantia", "12"));
        t.crearProducto(Categoria.TECNOLOGIA, "T3", "Portátil 15\"", 2_800_000, 3, Map.of("marca", "TechBook", "mesesGarantia", "24"));
        t.crearProducto(Categoria.COMIDA, "C1", "Café molido 500 g", 28_000, 30, Map.of("pesoGramos", "500", "vence", "2027-06-30"));
        t.crearProducto(Categoria.COMIDA, "C2", "Chocolate oscuro 100 g", 9_500, 50, Map.of("pesoGramos", "100", "vence", "2027-03-15"));

        t.registrarCliente(new Cliente("C1", "Laura Doncel"));
        t.registrarCliente(new Cliente("C2", "Jhonatan Forigua", 3)); // cliente frecuente
        return t;
    }
}
