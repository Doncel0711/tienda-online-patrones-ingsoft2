package tienda.extras;

import tienda.modelo.Articulo;

public class EmpaqueRegalo extends ExtraDecorator {
    public static final long COSTO = 8_000;

    public EmpaqueRegalo(Articulo envuelto) {
        super(envuelto);
    }

    @Override
    public String nombreExtra() {
        return "Empaque de regalo";
    }

    @Override
    protected long costoExtra() {
        return COSTO;
    }
}
