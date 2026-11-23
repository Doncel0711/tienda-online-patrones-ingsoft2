package tienda.modelo;

/** Rol Observer: quien quiere saber cuando un producto agotado vuelve a tener existencias. */
public interface ObservadorStock {
    void productoDisponible(Producto producto);
}
