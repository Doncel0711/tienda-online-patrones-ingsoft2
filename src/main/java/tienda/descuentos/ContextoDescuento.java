package tienda.descuentos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import tienda.carrito.Carrito;
import tienda.modelo.Cliente;

/** Datos que viajan por la cadena: quién compra, qué lleva, qué cupón trae y qué descuentos van acumulados. */
public class ContextoDescuento {
    private final Carrito carrito;
    private final Cliente cliente;
    private final String cupon;
    private final LocalDate fecha;
    private final long subtotal;
    private final List<LineaDescuento> lineas = new ArrayList<>();
    private final List<String> advertencias = new ArrayList<>();
    private boolean cerrado;

    public ContextoDescuento(Carrito carrito, Cliente cliente, String cupon, LocalDate fecha) {
        this.carrito = carrito;
        this.cliente = cliente;
        this.cupon = cupon == null ? "" : cupon.trim().toUpperCase();
        this.fecha = fecha;
        this.subtotal = carrito.subtotal();
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getCupon() {
        return cupon;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public void agregar(String nombre, long monto) {
        if (monto > 0) {
            lineas.add(new LineaDescuento(nombre, monto));
        }
    }

    public void advertir(String mensaje) {
        advertencias.add(mensaje);
    }

    /** Un descuento no combinable cierra la cadena: los siguientes ya no se evalúan. */
    public void cerrar() {
        cerrado = true;
    }

    public boolean estaCerrado() {
        return cerrado;
    }

    public ResultadoDescuentos resultado() {
        long suma = lineas.stream().mapToLong(LineaDescuento::monto).sum();
        long total = Math.min(suma, subtotal); // nunca se descuenta más que el subtotal
        return new ResultadoDescuentos(subtotal, List.copyOf(lineas), total, List.copyOf(advertencias));
    }
}
