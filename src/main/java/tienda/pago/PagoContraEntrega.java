package tienda.pago;

import java.util.List;
import java.util.Map;

/** Contra entrega: exige dirección, tope de $1.500.000 y cobra $6.000 de recargo. */
public class PagoContraEntrega implements MetodoPago {
    public static final long TOPE = 1_500_000;
    public static final long RECARGO = 6_000;

    @Override
    public String getNombre() {
        return "Contra entrega";
    }

    @Override
    public List<String> camposRequeridos() {
        return List.of("direccion");
    }

    @Override
    public long recargo(long monto) {
        return RECARGO;
    }

    @Override
    public ResultadoPago pagar(long monto, Map<String, String> datos) {
        String dir = datos == null || datos.get("direccion") == null ? "" : datos.get("direccion").trim();
        if (dir.isEmpty()) {
            return new ResultadoPago(false, "Debes indicar la dirección de entrega", null);
        }
        if (monto > TOPE) {
            return new ResultadoPago(false, "Contra entrega solo está disponible para compras hasta el tope permitido", null);
        }
        return new ResultadoPago(true, "Se cobrará al entregar en " + dir, "CE-" + Math.abs(dir.hashCode() % 10000));
    }
}
