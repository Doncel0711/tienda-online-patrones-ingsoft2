package tienda.modelo;

/**
 * Componente común: algo que tiene descripción y precio.
 * Lo implementan Producto (componente concreto) y los extras (decoradores).
 */
public interface Articulo {
    String getDescripcion();

    long getPrecio();
}
