package tienda.descuentos;

/**
 * Patrón Chain of Responsibility - rol Handler.
 * Cada eslabón decide si su descuento aplica, lo registra en el contexto y pasa al siguiente,
 * salvo que el contexto se haya cerrado (descuento no combinable).
 */
public abstract class DescuentoHandler {
    private DescuentoHandler siguiente;

    /** Enlaza el siguiente eslabón y lo devuelve, para poder encadenar: a.enlazar(b).enlazar(c). */
    public DescuentoHandler enlazar(DescuentoHandler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public final void procesar(ContextoDescuento ctx) {
        if (ctx.estaCerrado()) {
            return;
        }
        aplicar(ctx);
        if (siguiente != null) {
            siguiente.procesar(ctx);
        }
    }

    protected abstract void aplicar(ContextoDescuento ctx);
}
