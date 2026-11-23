package tienda.extras;

import tienda.modelo.Articulo;

/** Garantía extendida: 10% del precio del artículo envuelto. */
public class GarantiaExtendida extends ExtraDecorator {
    public GarantiaExtendida(Articulo envuelto) {
        super(envuelto);
    }

    @Override
    public String nombreExtra() {
        return "Garantía extendida";
    }

    @Override
    protected long costoExtra() {
        return envuelto.getPrecio() * 10 / 100;
    }
}
