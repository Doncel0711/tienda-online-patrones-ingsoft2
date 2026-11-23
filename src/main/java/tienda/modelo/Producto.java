package tienda.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Producto base del catálogo.
 * - Es el componente concreto del patrón Decorator (implementa Articulo).
 * - Es el sujeto (Subject) del patrón Observer: avisa cuando vuelve a haber stock.
 */
public abstract class Producto implements Articulo {
    private final String id;
    private final String nombre;
    private final long precioBase;
    private final Categoria categoria;
    private int stock;
    private final List<ObservadorStock> observadores = new ArrayList<>();

    protected Producto(String id, String nombre, long precioBase, int stock, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.stock = stock;
        this.categoria = categoria;
    }

    /** Atributos propios de cada categoría, en texto. */
    public abstract String getDetalles();

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getStock() {
        return stock;
    }

    public boolean agotado() {
        return stock <= 0;
    }

    @Override
    public String getDescripcion() {
        return nombre;
    }

    @Override
    public long getPrecio() {
        return precioBase;
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            throw new IllegalStateException("No se puede descontar " + cantidad + " de " + nombre + " (stock " + stock + ")");
        }
        stock -= cantidad;
    }

    /** Si el producto estaba agotado y ahora hay existencias, notifica a los suscritos. */
    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reponer debe ser positiva");
        }
        boolean estabaAgotado = agotado();
        stock += cantidad;
        if (estabaAgotado) {
            notificarObservadores();
        }
    }

    public void suscribir(ObservadorStock observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorStock observador) {
        observadores.remove(observador);
    }

    public int cantidadSuscritos() {
        return observadores.size();
    }

    private void notificarObservadores() {
        List<ObservadorStock> copia = new ArrayList<>(observadores);
        observadores.clear(); // la suscripción es de un solo aviso
        for (ObservadorStock o : copia) {
            o.productoDisponible(this);
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}
