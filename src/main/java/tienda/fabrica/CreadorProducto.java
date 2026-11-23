package tienda.fabrica;

import java.util.List;
import java.util.Map;
import tienda.modelo.Categoria;
import tienda.modelo.Producto;

/**
 * Patrón Factory Method - rol Creator.
 * El método plantilla crear() valida lo común a todos los productos y delega en
 * crearProducto() (el factory method) la construcción del producto concreto.
 */
public abstract class CreadorProducto {

    public final Producto crear(String id, String nombre, long precio, int stock, Map<String, String> atributos) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        for (String requerido : atributosRequeridos()) {
            if (atributos == null || atributos.get(requerido) == null || atributos.get(requerido).isBlank()) {
                throw new IllegalArgumentException("Falta el atributo: " + requerido);
            }
        }
        return crearProducto(id, nombre, precio, stock, atributos);
    }

    /** Factory method: cada creador concreto decide qué subclase de Producto instanciar. */
    protected abstract Producto crearProducto(String id, String nombre, long precio, int stock, Map<String, String> atributos);

    public abstract Categoria categoria();

    public abstract List<String> atributosRequeridos();
}
