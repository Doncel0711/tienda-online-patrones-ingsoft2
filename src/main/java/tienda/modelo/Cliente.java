package tienda.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cliente de la tienda. También es Observer: recibe avisos de productos que vuelven a tener stock. */
public class Cliente implements ObservadorStock {
    private final String id;
    private final String nombre;
    private int comprasRealizadas;
    private final List<String> avisos = new ArrayList<>();

    public Cliente(String id, String nombre) {
        this(id, nombre, 0);
    }

    public Cliente(String id, String nombre, int comprasRealizadas) {
        this.id = id;
        this.nombre = nombre;
        this.comprasRealizadas = comprasRealizadas;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getComprasRealizadas() {
        return comprasRealizadas;
    }

    public void registrarCompra() {
        comprasRealizadas++;
    }

    public List<String> getAvisos() {
        return Collections.unmodifiableList(avisos);
    }

    public void limpiarAvisos() {
        avisos.clear();
    }

    @Override
    public void productoDisponible(Producto producto) {
        avisos.add("¡" + producto.getNombre() + " volvió a tener existencias (" + producto.getStock() + " unidades)!");
    }

    @Override
    public String toString() {
        return nombre;
    }
}
