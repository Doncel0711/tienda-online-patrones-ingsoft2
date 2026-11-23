package tienda.fabrica;

import java.util.List;
import java.util.Map;
import tienda.modelo.Categoria;
import tienda.modelo.Producto;
import tienda.modelo.Tecnologia;

public class CreadorTecnologia extends CreadorProducto {
    @Override
    protected Producto crearProducto(String id, String nombre, long precio, int stock, Map<String, String> a) {
        int meses;
        try {
            meses = Integer.parseInt(a.get("mesesGarantia").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("mesesGarantia debe ser un número entero");
        }
        return new Tecnologia(id, nombre, precio, stock, a.get("marca"), meses);
    }

    @Override
    public Categoria categoria() {
        return Categoria.TECNOLOGIA;
    }

    @Override
    public List<String> atributosRequeridos() {
        return List.of("marca", "mesesGarantia");
    }
}
