package tienda.descuentos;

import java.util.List;

public record ResultadoDescuentos(long subtotal, List<LineaDescuento> lineas, long totalDescuento, List<String> advertencias) {
    public long totalConDescuento() {
        return subtotal - totalDescuento;
    }
}
