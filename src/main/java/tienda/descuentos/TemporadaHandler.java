package tienda.descuentos;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;

/** Descuento de temporada: 10% en noviembre y diciembre (Black Friday y Navidad). */
public class TemporadaHandler extends DescuentoHandler {
    private final Clock reloj;

    public TemporadaHandler(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    protected void aplicar(ContextoDescuento ctx) {
        Month mes = LocalDate.now(reloj).getMonth();
        if (mes == Month.NOVEMBER || mes == Month.DECEMBER) {
            ctx.agregar("Temporada Black Friday/Navidad (10%)", ctx.getSubtotal() * 10 / 100);
        }
    }
}
