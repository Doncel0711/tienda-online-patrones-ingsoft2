package tienda.descuentos;

import java.util.HashMap;
import java.util.Map;

/** Cupones: BIENVENIDO10 (10%, combinable) y MEGA30 (30%, NO combinable: anula los demás descuentos). */
public class CuponHandler extends DescuentoHandler {
    public record Cupon(String codigo, int porcentaje, boolean exclusivo) {
    }

    private final Map<String, Cupon> cupones = new HashMap<>();

    public CuponHandler() {
        registrar(new Cupon("BIENVENIDO10", 10, false));
        registrar(new Cupon("MEGA30", 30, true));
    }

    public void registrar(Cupon cupon) {
        cupones.put(cupon.codigo(), cupon);
    }

    @Override
    protected void aplicar(ContextoDescuento ctx) {
        if (ctx.getCupon().isEmpty()) {
            return;
        }
        Cupon cupon = cupones.get(ctx.getCupon());
        if (cupon == null) {
            ctx.advertir("El cupón '" + ctx.getCupon() + "' no es válido");
            return;
        }
        ctx.agregar("Cupón " + cupon.codigo() + " (" + cupon.porcentaje() + "%)", ctx.getSubtotal() * cupon.porcentaje() / 100);
        if (cupon.exclusivo()) {
            ctx.cerrar();
        }
    }
}
