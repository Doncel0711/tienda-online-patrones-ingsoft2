package tienda.descuentos;

import tienda.carrito.ItemCarrito;

/** Descuento por cantidad: 10% sobre cada línea con 3 o más unidades. */
public class CantidadHandler extends DescuentoHandler {
    public static final int MINIMO_UNIDADES = 3;

    @Override
    protected void aplicar(ContextoDescuento ctx) {
        long monto = 0;
        for (ItemCarrito item : ctx.getCarrito().getItems()) {
            if (item.getCantidad() >= MINIMO_UNIDADES) {
                monto += item.getSubtotal() * 10 / 100;
            }
        }
        ctx.agregar("Descuento por cantidad (10% en líneas de " + MINIMO_UNIDADES + "+ unidades)", monto);
    }
}
