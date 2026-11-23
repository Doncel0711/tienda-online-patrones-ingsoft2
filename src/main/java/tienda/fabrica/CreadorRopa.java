package tienda.fabrica;

import java.util.List;
import java.util.Map;
import tienda.modelo.Categoria;
import tienda.modelo.Producto;
import tienda.modelo.Ropa;

public class CreadorRopa extends CreadorProducto {
    @Override
    protected Producto crearProducto(String id, String nombre, long precio, int stock, Map<String, String> a) {
        return new Ropa(id, nombre, precio, stock, a.get("talla"), a.get("color"));
    }

    @Override
    public Categoria categoria() {
        return Categoria.ROPA;
    }

    @Override
    public List<String> atributosRequeridos() {
        return List.of("talla", "color");
    }
}
