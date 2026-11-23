package tienda.extras;

import tienda.modelo.Articulo;

public class EnvioExpress extends ExtraDecorator {
    public static final long COSTO = 15_000;

    public EnvioExpress(Articulo envuelto) {
        super(envuelto);
    }

    @Override
    public String nombreExtra() {
        return "Envío express";
    }

    @Override
    protected long costoExtra() {
        return COSTO;
    }
}
