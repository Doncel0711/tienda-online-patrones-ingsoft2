package tienda.pago;

import java.util.List;
import java.util.Map;

/**
 * Patrón Strategy - rol Strategy.
 * Cada medio de pago valida y procesa a su manera. La consola y la GUI piden los datos
 * según camposRequeridos(), así agregar un medio nuevo no obliga a tocar las interfaces.
 */
public interface MetodoPago {
    String getNombre();

    /** Nombres de los datos que el usuario debe diligenciar para este medio. */
    List<String> camposRequeridos();

    /** Cargo adicional que cobra este medio sobre el monto (0 si no tiene). */
    long recargo(long monto);

    ResultadoPago pagar(long monto, Map<String, String> datos);
}
