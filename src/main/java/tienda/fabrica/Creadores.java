package tienda.fabrica;

import java.util.EnumMap;
import java.util.Map;
import tienda.modelo.Categoria;

/** Registro de creadores por categoría. Una categoría nueva solo requiere un creador nuevo y registrarlo aquí. */
public final class Creadores {
    private static final Map<Categoria, CreadorProducto> REGISTRO = new EnumMap<>(Categoria.class);

    static {
        registrar(new CreadorRopa());
        registrar(new CreadorTecnologia());
        registrar(new CreadorComida());
    }

    private Creadores() {
    }

    public static void registrar(CreadorProducto creador) {
        REGISTRO.put(creador.categoria(), creador);
    }

    public static CreadorProducto para(Categoria categoria) {
        CreadorProducto c = REGISTRO.get(categoria);
        if (c == null) {
            throw new IllegalArgumentException("No hay creador para la categoría " + categoria);
        }
        return c;
    }
}
