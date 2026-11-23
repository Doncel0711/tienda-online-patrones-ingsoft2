package tienda.pedido;

public record LineaPedido(String descripcion, int cantidad, long precioUnitario) {
    public long subtotal() {
        return precioUnitario * cantidad;
    }
}
