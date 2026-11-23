package tienda.pago;

import java.util.List;
import java.util.Map;

/** Transferencia: exige banco y una referencia de al menos 6 caracteres. Sin recargo. */
public class PagoTransferencia implements MetodoPago {
    @Override
    public String getNombre() {
        return "Transferencia";
    }

    @Override
    public List<String> camposRequeridos() {
        return List.of("banco", "referencia");
    }

    @Override
    public long recargo(long monto) {
        return 0;
    }

    @Override
    public ResultadoPago pagar(long monto, Map<String, String> datos) {
        String banco = datos == null || datos.get("banco") == null ? "" : datos.get("banco").trim();
        String ref = datos == null || datos.get("referencia") == null ? "" : datos.get("referencia").trim();
        if (banco.isEmpty()) {
            return new ResultadoPago(false, "Debes indicar el banco de origen", null);
        }
        if (ref.length() < 6) {
            return new ResultadoPago(false, "La referencia de la transferencia debe tener al menos 6 caracteres", null);
        }
        return new ResultadoPago(true, "Transferencia registrada desde " + banco, "TR-" + ref);
    }
}
