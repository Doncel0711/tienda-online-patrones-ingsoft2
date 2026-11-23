package tienda.descuentos;

import java.time.Clock;
import java.time.LocalDate;
import tienda.carrito.Carrito;
import tienda.modelo.Cliente;

/** Arma y recorre la cadena de descuentos. El cupón va primero porque puede ser exclusivo. */
public class CalculadoraDescuentos {
    private final DescuentoHandler cadena;
    private final Clock reloj;

    public CalculadoraDescuentos(DescuentoHandler cadena, Clock reloj) {
        this.cadena = cadena;
        this.reloj = reloj;
    }

    public static CalculadoraDescuentos porDefecto(Clock reloj) {
        DescuentoHandler cupon = new CuponHandler();
        cupon.enlazar(new TemporadaHandler(reloj))
                .enlazar(new CantidadHandler())
                .enlazar(new ClienteFrecuenteHandler());
        return new CalculadoraDescuentos(cupon, reloj);
    }

    public ResultadoDescuentos calcular(Carrito carrito, Cliente cliente, String cupon) {
        ContextoDescuento ctx = new ContextoDescuento(carrito, cliente, cupon, LocalDate.now(reloj));
        cadena.procesar(ctx);
        return ctx.resultado();
    }
}
