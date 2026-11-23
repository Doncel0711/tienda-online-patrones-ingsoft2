package tienda.extras;

import java.util.Objects;
import tienda.modelo.Articulo;

/**
 * Patrón Decorator - rol Decorator abstracto.
 * Envuelve un Articulo (producto u otro extra) y le suma descripción y costo.
 */
public abstract class ExtraDecorator implements Articulo {
    protected final Articulo envuelto;

    protected ExtraDecorator(Articulo envuelto) {
        this.envuelto = Objects.requireNonNull(envuelto);
    }

    public abstract String nombreExtra();

    protected abstract long costoExtra();

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion() + " + " + nombreExtra();
    }

    @Override
    public long getPrecio() {
        return envuelto.getPrecio() + costoExtra();
    }
}
