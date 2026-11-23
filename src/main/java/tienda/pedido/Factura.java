package tienda.pedido;

import java.time.format.DateTimeFormatter;
import tienda.descuentos.LineaDescuento;
import tienda.modelo.Dinero;

/** Genera el texto de la factura de un pedido. */
public final class Factura {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Factura() {
    }

    public static String generar(Pedido p) {
        StringBuilder sb = new StringBuilder();
        sb.append("================ FACTURA #").append(String.format("%04d", p.getNumero())).append(" ================\n");
        sb.append("Cliente: ").append(p.getCliente()).append('\n');
        sb.append("Fecha:   ").append(p.getFecha().format(FECHA)).append('\n');
        sb.append("--------------------------------------------\n");
        for (LineaPedido l : p.getLineas()) {
            sb.append(l.cantidad()).append(" x ").append(l.descripcion()).append('\n');
            sb.append("      ").append(Dinero.formato(l.precioUnitario())).append(" c/u  ->  ")
                    .append(Dinero.formato(l.subtotal())).append('\n');
        }
        sb.append("--------------------------------------------\n");
        sb.append("Subtotal:        ").append(Dinero.formato(p.getDescuentos().subtotal())).append('\n');
        for (LineaDescuento d : p.getDescuentos().lineas()) {
            sb.append("  - ").append(d.nombre()).append(": -").append(Dinero.formato(d.monto())).append('\n');
        }
        if (p.getDescuentos().totalDescuento() > 0) {
            sb.append("Total descuentos: -").append(Dinero.formato(p.getDescuentos().totalDescuento())).append('\n');
        }
        if (p.getRecargo() > 0) {
            sb.append("Recargo ").append(p.getMetodoPago()).append(": ").append(Dinero.formato(p.getRecargo())).append('\n');
        }
        sb.append("TOTAL A PAGAR:   ").append(Dinero.formato(p.getTotal())).append('\n');
        sb.append("Medio de pago:   ").append(p.getMetodoPago()).append(" (ref. ").append(p.getReferenciaPago()).append(")\n");
        sb.append("============================================\n");
        return sb.toString();
    }
}
