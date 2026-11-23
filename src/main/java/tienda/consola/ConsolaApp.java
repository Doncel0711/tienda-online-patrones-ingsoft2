package tienda.consola;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import tienda.carrito.Carrito;
import tienda.carrito.ItemCarrito;
import tienda.descuentos.LineaDescuento;
import tienda.descuentos.ResultadoDescuentos;
import tienda.extras.TipoExtra;
import tienda.modelo.Cliente;
import tienda.modelo.Dinero;
import tienda.modelo.Producto;
import tienda.pago.MetodoPago;
import tienda.pedido.Pedido;
import tienda.servicio.Tienda;
import tienda.servicio.TiendaException;

/** Interfaz de consola. Solo lee y muestra; toda la lógica está en Tienda. */
public class ConsolaApp {
    private final Tienda tienda;
    private final Scanner in;
    private final PrintStream out;
    private Cliente actual;

    public ConsolaApp(Tienda tienda, InputStream entrada, PrintStream salida) {
        this.tienda = tienda;
        this.in = new Scanner(entrada, "UTF-8");
        this.out = salida;
    }

    public void ejecutar() {
        out.println("=== TIENDA EN LÍNEA ===");
        try {
            elegirCliente();
            boolean seguir = true;
            while (seguir) {
                seguir = menu();
            }
        } catch (NoSuchElementException fin) {
            out.println("\n(entrada finalizada)");
        }
        out.println("¡Hasta pronto!");
    }

    private void elegirCliente() {
        out.println("\nClientes registrados:");
        List<Cliente> clientes = tienda.getClientes();
        for (int i = 0; i < clientes.size(); i++) {
            out.println("  " + (i + 1) + ". " + clientes.get(i).getNombre()
                    + " (" + clientes.get(i).getComprasRealizadas() + " compras previas)");
        }
        while (actual == null) {
            int op = leerEntero("¿Quién eres? ");
            if (op >= 1 && op <= clientes.size()) {
                actual = clientes.get(op - 1);
            } else {
                out.println("Opción inválida.");
            }
        }
        out.println("Bienvenido(a), " + actual.getNombre() + ".");
    }

    private boolean menu() {
        out.println("\n--- MENÚ ---");
        out.println("1. Ver catálogo");
        out.println("2. Agregar producto al carrito");
        out.println("3. Ver carrito y descuentos");
        out.println("4. Cambiar cantidad / quitar del carrito");
        out.println("5. Pagar");
        out.println("6. Historial de pedidos");
        out.println("7. Avisarme cuando un producto agotado vuelva");
        out.println("8. Ver mis avisos");
        out.println("9. (Admin) Reponer stock");
        out.println("0. Salir");
        int op = leerEntero("Opción: ");
        try {
            switch (op) {
                case 1 -> verCatalogo();
                case 2 -> agregar();
                case 3 -> verCarrito("");
                case 4 -> modificarCarrito();
                case 5 -> pagar();
                case 6 -> historial();
                case 7 -> suscribir();
                case 8 -> avisos();
                case 9 -> reponer();
                case 0 -> {
                    return false;
                }
                default -> out.println("Opción inválida.");
            }
        } catch (TiendaException e) {
            out.println("Aviso: " + e.getMessage());
        }
        return true;
    }

    private void verCatalogo() {
        out.println("\n--- CATÁLOGO ---");
        for (Producto p : tienda.getProductos()) {
            String stock = p.agotado() ? "AGOTADO" : p.getStock() + " disp.";
            out.printf("[%s] %-24s %12s  (%s)  %s%n", p.getId(), p.getNombre(), Dinero.formato(p.getPrecio()),
                    p.getCategoria().getNombre(), stock);
            out.println("      " + p.getDetalles());
        }
    }

    private void agregar() {
        verCatalogo();
        String id = leer("\nId del producto: ").toUpperCase();
        Producto p = tienda.buscarProducto(id);
        int cantidad = leerEntero("Cantidad: ");
        Set<TipoExtra> extras = EnumSet.noneOf(TipoExtra.class);
        for (TipoExtra e : TipoExtra.values()) {
            if (e.esAplicable(p) && leer("¿Agregar " + e.getEtiqueta() + "? (s/n): ").equalsIgnoreCase("s")) {
                extras.add(e);
            }
        }
        tienda.agregarAlCarrito(actual.getId(), id, cantidad, extras);
        out.println("OK: Agregado al carrito.");
    }

    private void verCarrito(String cupon) {
        Carrito carrito = tienda.getCarrito(actual.getId());
        if (carrito.estaVacio()) {
            out.println("El carrito está vacío.");
            return;
        }
        out.println("\n--- CARRITO ---");
        List<ItemCarrito> items = carrito.getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemCarrito it = items.get(i);
            out.printf("%d. %d x %s  (%s c/u) = %s%n", i + 1, it.getCantidad(), it.getDescripcion(),
                    Dinero.formato(it.getPrecioUnitario()), Dinero.formato(it.getSubtotal()));
        }
        mostrarResumen(tienda.calcularResumen(actual.getId(), cupon));
    }

    private void mostrarResumen(ResultadoDescuentos r) {
        out.println("Subtotal: " + Dinero.formato(r.subtotal()));
        for (LineaDescuento d : r.lineas()) {
            out.println("  - " + d.nombre() + ": -" + Dinero.formato(d.monto()));
        }
        r.advertencias().forEach(a -> out.println("  ! " + a));
        out.println("Total con descuentos: " + Dinero.formato(r.totalConDescuento()));
    }

    private void modificarCarrito() {
        verCarrito("");
        if (tienda.getCarrito(actual.getId()).estaVacio()) {
            return;
        }
        int linea = leerEntero("Número de línea: ") - 1;
        int nueva = leerEntero("Nueva cantidad (0 para quitar): ");
        if (nueva == 0) {
            tienda.quitarDelCarrito(actual.getId(), linea);
            out.println("OK: Línea eliminada.");
        } else {
            tienda.cambiarCantidad(actual.getId(), linea, nueva);
            out.println("OK: Cantidad actualizada.");
        }
    }

    private void pagar() {
        if (tienda.getCarrito(actual.getId()).estaVacio()) {
            out.println("El carrito está vacío.");
            return;
        }
        String cupon = leer("Cupón (Enter si no tienes): ");
        verCarrito(cupon);
        List<MetodoPago> metodos = tienda.getMetodosPago();
        out.println("\nMedios de pago:");
        for (int i = 0; i < metodos.size(); i++) {
            out.println("  " + (i + 1) + ". " + metodos.get(i).getNombre());
        }
        int op = leerEntero("Elige el medio de pago: ");
        if (op < 1 || op > metodos.size()) {
            out.println("Opción inválida.");
            return;
        }
        MetodoPago metodo = metodos.get(op - 1);
        Map<String, String> datos = new LinkedHashMap<>();
        for (String campo : metodo.camposRequeridos()) {
            datos.put(campo, leer("  " + campo + ": "));
        }
        Pedido pedido = tienda.pagar(actual.getId(), metodo, datos, cupon);
        out.println("\nPago aprobado.\n");
        out.println(pedido.getFactura());
    }

    private void historial() {
        List<Pedido> pedidos = tienda.getHistorial(actual.getId());
        if (pedidos.isEmpty()) {
            out.println("Aún no tienes pedidos.");
            return;
        }
        for (Pedido p : pedidos) {
            out.println(p.getFactura());
        }
    }

    private void suscribir() {
        List<String> agotados = new ArrayList<>();
        for (Producto p : tienda.getProductos()) {
            if (p.agotado()) {
                agotados.add(p.getId());
                out.println("  [" + p.getId() + "] " + p.getNombre());
            }
        }
        if (agotados.isEmpty()) {
            out.println("No hay productos agotados.");
            return;
        }
        String id = leer("Id del producto agotado: ").toUpperCase();
        tienda.suscribirAviso(actual.getId(), id);
        out.println("OK: Te avisaremos cuando " + tienda.buscarProducto(id).getNombre() + " vuelva a tener existencias.");
    }

    private void avisos() {
        if (actual.getAvisos().isEmpty()) {
            out.println("No tienes avisos.");
            return;
        }
        actual.getAvisos().forEach(a -> out.println("AVISO: " + a));
        actual.limpiarAvisos();
    }

    private void reponer() {
        String id = leer("Id del producto: ").toUpperCase();
        int cantidad = leerEntero("Unidades a reponer: ");
        tienda.reponerStock(id, cantidad);
        out.println("OK: Stock actualizado.");
    }

    private String leer(String prompt) {
        out.print(prompt);
        return in.nextLine().trim();
    }

    private int leerEntero(String prompt) {
        while (true) {
            String s = leer(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                out.println("Escribe un número entero.");
            }
        }
    }
}
