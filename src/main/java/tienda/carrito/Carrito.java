package tienda.carrito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import tienda.extras.TipoExtra;
import tienda.modelo.Producto;

public class Carrito {
    private final List<ItemCarrito> items = new ArrayList<>();

    public void agregar(Producto producto, int cantidad, Collection<TipoExtra> extras) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (producto.agotado()) {
            throw new IllegalStateException(producto.getNombre() + " está agotado");
        }
        if (cantidadDe(producto) + cantidad > producto.getStock()) {
            throw new IllegalStateException("Stock insuficiente de " + producto.getNombre()
                    + ": quedan " + producto.getStock() + " y ya tienes " + cantidadDe(producto) + " en el carrito");
        }
        Optional<ItemCarrito> existente = items.stream().filter(i -> i.mismoQue(producto, extras)).findFirst();
        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + cantidad);
        } else {
            items.add(new ItemCarrito(producto, cantidad, extras));
        }
    }

    /** Cambia la cantidad de la línea en la posición indicada (0 = primera). */
    public void cambiarCantidad(int indice, int nuevaCantidad) {
        ItemCarrito item = obtener(indice);
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero (usa quitar para eliminar)");
        }
        int otrasLineas = cantidadDe(item.getProducto()) - item.getCantidad();
        if (otrasLineas + nuevaCantidad > item.getProducto().getStock()) {
            throw new IllegalStateException("Stock insuficiente de " + item.getProducto().getNombre()
                    + ": solo hay " + item.getProducto().getStock());
        }
        item.setCantidad(nuevaCantidad);
    }

    public void quitar(int indice) {
        items.remove(obtener(indice));
    }

    public void vaciar() {
        items.clear();
    }

    public int cantidadDe(Producto producto) {
        return items.stream()
                .filter(i -> i.getProducto().getId().equals(producto.getId()))
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    public long subtotal() {
        return items.stream().mapToLong(ItemCarrito::getSubtotal).sum();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public List<ItemCarrito> getItems() {
        return Collections.unmodifiableList(items);
    }

    private ItemCarrito obtener(int indice) {
        if (indice < 0 || indice >= items.size()) {
            throw new IllegalArgumentException("No existe la línea " + (indice + 1) + " del carrito");
        }
        return items.get(indice);
    }
}
