package tienda.pago;

import java.util.List;
import java.util.Map;

/** Tarjeta: 16 dígitos, CVV de 3 dígitos y cupo simulado de $5.000.000. Sin recargo. */
public class PagoTarjeta implements MetodoPago {
    public static final long CUPO = 5_000_000;

    @Override
    public String getNombre() {
        return "Tarjeta";
    }

    @Override
    public List<String> camposRequeridos() {
        return List.of("numero", "cvv");
    }

    @Override
    public long recargo(long monto) {
        return 0;
    }

    @Override
    public ResultadoPago pagar(long monto, Map<String, String> datos) {
        String numero = valor(datos, "numero").replace(" ", "");
        String cvv = valor(datos, "cvv");
        if (!numero.matches("\\d{16}")) {
            return new ResultadoPago(false, "El número de tarjeta debe tener 16 dígitos", null);
        }
        if (!cvv.matches("\\d{3}")) {
            return new ResultadoPago(false, "El CVV debe tener 3 dígitos", null);
        }
        if (monto > CUPO) {
            return new ResultadoPago(false, "El monto supera el cupo de la tarjeta", null);
        }
        return new ResultadoPago(true, "Pago aprobado con tarjeta terminada en " + numero.substring(12),
                "TC-" + numero.substring(12));
    }

    private static String valor(Map<String, String> datos, String clave) {
        String v = datos == null ? null : datos.get(clave);
        return v == null ? "" : v.trim();
    }
}
