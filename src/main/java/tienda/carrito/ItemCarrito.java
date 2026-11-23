package tienda.carrito;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import tienda.extras.TipoExtra;
import tienda.modelo.Articulo;
import tienda.modelo.Producto;

/** Línea del carrito: un producto (decorado con sus extras) y una cantidad. */
public class ItemCarrito {
    private final Producto producto;
    private final Set<TipoExtra> extras;
    private final Articulo articulo;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad, Collection<TipoExtra> extras) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.extras = EnumSet.noneOf(TipoExtra.class);
        if (extras != null) {
            this.extras.addAll(extras);
        }
        Articulo decorado = producto;
        for (TipoExtra extra : this.extras) { // EnumSet respeta el orden de declaración
            if (!extra.esAplicable(producto)) {
                throw new IllegalArgumentException("El extra '" + extra.getEtiqueta() + "' no aplica a " + producto.getNombre());
            }
            decorado = extra.decorar(decorado);
        }
        this.articulo = decorado;
    }

    public Producto getProducto() {
        return producto;
    }

    public Set<TipoExtra> getExtras() {
        return java.util.Collections.unmodifiableSet(extras);
    }

    public int getCantidad() {
        return cantidad;
    }

    void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return articulo.getDescripcion();
    }

    public long getPrecioUnitario() {
        return articulo.getPrecio();
    }

    public long getSubtotal() {
        return articulo.getPrecio() * cantidad;
    }

    boolean mismoQue(Producto otro, Collection<TipoExtra> otrosExtras) {
        Set<TipoExtra> comparar = EnumSet.noneOf(TipoExtra.class);
        if (otrosExtras != null) {
            comparar.addAll(otrosExtras);
        }
        return producto.getId().equals(otro.getId()) && extras.equals(comparar);
    }
}
