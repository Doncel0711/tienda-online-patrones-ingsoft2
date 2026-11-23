package tienda.descuentos;

/** Cliente frecuente: con 3 o más compras previas recibe 5% sobre el subtotal. */
public class ClienteFrecuenteHandler extends DescuentoHandler {
    public static final int COMPRAS_MINIMAS = 3;

    @Override
    protected void aplicar(ContextoDescuento ctx) {
        if (ctx.getCliente().getComprasRealizadas() >= COMPRAS_MINIMAS) {
            ctx.agregar("Cliente frecuente (5%)", ctx.getSubtotal() * 5 / 100);
        }
    }
}
