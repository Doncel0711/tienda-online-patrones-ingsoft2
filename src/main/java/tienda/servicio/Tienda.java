package tienda.servicio;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tienda.carrito.Carrito;
import tienda.carrito.ItemCarrito;
import tienda.descuentos.CalculadoraDescuentos;
import tienda.descuentos.ResultadoDescuentos;
import tienda.extras.TipoExtra;
import tienda.fabrica.Creadores;
import tienda.modelo.Categoria;
import tienda.modelo.Cliente;
import tienda.modelo.Producto;
import tienda.pago.MetodoPago;
import tienda.pago.PagoContraEntrega;
import tienda.pago.PagoTarjeta;
import tienda.pago.PagoTransferencia;
import tienda.pago.ResultadoPago;
import tienda.pedido.LineaPedido;
import tienda.pedido.Pedido;

/**
 * Núcleo de la aplicación (fachada de la lógica de negocio).
 * La consola y la interfaz gráfica solo hablan con esta clase; no contiene ningún System.out ni Swing.
 */
public class Tienda {
    private final Clock reloj;
    private final CalculadoraDescuentos descuentos;
    private final Map<String, Producto> catalogo = new LinkedHashMap<>();
    private final Map<String, Cliente> clientes = new LinkedHashMap<>();
    private final Map<String, Carrito> carritos = new HashMap<>();
    private final Map<String, List<Pedido>> historial = new HashMap<>();
    private final List<MetodoPago> metodosPago = new ArrayList<>();
    private int contadorPedidos = 0;

    public Tienda() {
        this(Clock.systemDefaultZone());
    }

    public Tienda(Clock reloj) {
        this(reloj, CalculadoraDescuentos.porDefecto(reloj));
    }

    public Tienda(Clock reloj, CalculadoraDescuentos descuentos) {
        this.reloj = reloj;
        this.descuentos = descuentos;
        metodosPago.add(new PagoTarjeta());
        metodosPago.add(new PagoTransferencia());
        metodosPago.add(new PagoContraEntrega());
    }

    // ---------- catálogo ----------
    public Producto crearProducto(Categoria categoria, String id, String nombre, long precio, int stock, Map<String, String> atributos) {
        if (catalogo.containsKey(id)) {
            throw new TiendaException("Ya existe un producto con id " + id);
        }
        try {
            Producto p = Creadores.para(categoria).crear(id, nombre, precio, stock, atributos);
            catalogo.put(p.getId(), p);
            return p;
        } catch (IllegalArgumentException e) {
            throw new TiendaException(e.getMessage());
        }
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(new ArrayList<>(catalogo.values()));
    }

    public Producto buscarProducto(String id) {
        Producto p = catalogo.get(id);
        if (p == null) {
            throw new TiendaException("No existe el producto " + id);
        }
        return p;
    }

    public void reponerStock(String productoId, int cantidad) {
        try {
            buscarProducto(productoId).reponerStock(cantidad);
        } catch (IllegalArgumentException e) {
            throw new TiendaException(e.getMessage());
        }
    }

    // ---------- clientes ----------
    public Cliente registrarCliente(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
        carritos.put(cliente.getId(), new Carrito());
        historial.put(cliente.getId(), new ArrayList<>());
        return cliente;
    }

    public Cliente buscarCliente(String id) {
        Cliente c = clientes.get(id);
        if (c == null) {
            throw new TiendaException("No existe el cliente " + id);
        }
        return c;
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(new ArrayList<>(clientes.values()));
    }

    // ---------- carrito ----------
    public Carrito getCarrito(String clienteId) {
        buscarCliente(clienteId);
        return carritos.get(clienteId);
    }

    public void agregarAlCarrito(String clienteId, String productoId, int cantidad, Collection<TipoExtra> extras) {
        try {
            getCarrito(clienteId).agregar(buscarProducto(productoId), cantidad, extras);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new TiendaException(e.getMessage());
        }
    }

    public void cambiarCantidad(String clienteId, int indice, int cantidad) {
        try {
            getCarrito(clienteId).cambiarCantidad(indice, cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new TiendaException(e.getMessage());
        }
    }

    public void quitarDelCarrito(String clienteId, int indice) {
        try {
            getCarrito(clienteId).quitar(indice);
        } catch (IllegalArgumentException e) {
            throw new TiendaException(e.getMessage());
        }
    }

    public ResultadoDescuentos calcularResumen(String clienteId, String cupon) {
        return descuentos.calcular(getCarrito(clienteId), buscarCliente(clienteId), cupon);
    }

    // ---------- avisos de stock ----------
    public void suscribirAviso(String clienteId, String productoId) {
        Producto p = buscarProducto(productoId);
        if (!p.agotado()) {
            throw new TiendaException(p.getNombre() + " tiene existencias; el aviso solo aplica a productos agotados");
        }
        p.suscribir(buscarCliente(clienteId));
    }

    // ---------- pago ----------
    public List<MetodoPago> getMetodosPago() {
        return Collections.unmodifiableList(metodosPago);
    }

    public Pedido pagar(String clienteId, MetodoPago metodo, Map<String, String> datos, String cupon) {
        Cliente cliente = buscarCliente(clienteId);
        Carrito carrito = getCarrito(clienteId);
        if (carrito.estaVacio()) {
            throw new TiendaException("El carrito está vacío");
        }
        for (ItemCarrito item : carrito.getItems()) {
            Producto p = item.getProducto();
            if (carrito.cantidadDe(p) > p.getStock()) {
                throw new TiendaException("Ya no hay stock suficiente de " + p.getNombre());
            }
        }
        ResultadoDescuentos desc = descuentos.calcular(carrito, cliente, cupon);
        long recargo = metodo.recargo(desc.totalConDescuento());
        long total = desc.totalConDescuento() + recargo;

        ResultadoPago resultado = metodo.pagar(total, datos);
        if (!resultado.aprobado()) {
            throw new TiendaException("Pago rechazado: " + resultado.mensaje());
        }

        List<LineaPedido> lineas = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            item.getProducto().reducirStock(item.getCantidad());
            lineas.add(new LineaPedido(item.getDescripcion(), item.getCantidad(), item.getPrecioUnitario()));
        }
        Pedido pedido = new Pedido(++contadorPedidos, cliente.getNombre(), lineas, desc, metodo.getNombre(),
                recargo, total, resultado.referencia(), LocalDateTime.now(reloj));
        historial.get(clienteId).add(pedido);
        cliente.registrarCompra();
        carrito.vaciar();
        return pedido;
    }

    public List<Pedido> getHistorial(String clienteId) {
        buscarCliente(clienteId);
        return Collections.unmodifiableList(historial.get(clienteId));
    }
}
