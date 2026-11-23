package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tienda.carrito.Carrito;
import tienda.descuentos.CalculadoraDescuentos;
import tienda.descuentos.ContextoDescuento;
import tienda.descuentos.DescuentoHandler;
import tienda.descuentos.ResultadoDescuentos;
import tienda.modelo.Cliente;
import tienda.modelo.Ropa;

/** Pruebas del patrón Chain of Responsibility. */
class DescuentosChainTest {
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private final Clock octubre = Clock.fixed(Instant.parse("2026-10-10T12:00:00Z"), ZONA);
    private final Clock noviembre = Clock.fixed(Instant.parse("2026-11-20T12:00:00Z"), ZONA);

    private Ropa camisa;
    private Carrito carrito;
    private final Cliente nuevo = new Cliente("N", "Nuevo");
    private final Cliente frecuente = new Cliente("F", "Frecuente", 5);

    @BeforeEach
    void preparar() {
        camisa = new Ropa("R1", "Camisa", 100_000, 50, "M", "Azul");
        carrito = new Carrito();
    }

    @Test
    void sinCondicionesNoHayDescuento() {
        carrito.agregar(camisa, 1, null);
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(octubre).calcular(carrito, nuevo, "");
        assertEquals(0, r.totalDescuento());
        assertEquals(100_000, r.totalConDescuento());
    }

    @Test
    void descuentoPorCantidadDesdeTresUnidades() {
        carrito.agregar(camisa, 3, null);
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(octubre).calcular(carrito, nuevo, "");
        assertEquals(30_000, r.totalDescuento()); // 10% de 300.000
    }

    @Test
    void descuentoDeTemporadaDependeDelMes() {
        carrito.agregar(camisa, 1, null);
        assertEquals(0, CalculadoraDescuentos.porDefecto(octubre).calcular(carrito, nuevo, "").totalDescuento());
        assertEquals(10_000, CalculadoraDescuentos.porDefecto(noviembre).calcular(carrito, nuevo, "").totalDescuento());
    }

    @Test
    void clienteFrecuenteRecibeCincoPorCiento() {
        carrito.agregar(camisa, 1, null);
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(octubre).calcular(carrito, frecuente, "");
        assertEquals(5_000, r.totalDescuento());
    }

    @Test
    void descuentosCombinablesSeAcumulan() {
        carrito.agregar(camisa, 3, null); // 300.000
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(noviembre).calcular(carrito, frecuente, "BIENVENIDO10");
        // cupón 10% + temporada 10% + cantidad 10% + frecuente 5% = 30.000+30.000+30.000+15.000
        assertEquals(105_000, r.totalDescuento());
        assertEquals(4, r.lineas().size());
    }

    @Test
    void cuponExclusivoCierraLaCadena() {
        carrito.agregar(camisa, 3, null);
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(noviembre).calcular(carrito, frecuente, "mega30");
        assertEquals(90_000, r.totalDescuento()); // solo el 30%; no se suman los demás
        assertEquals(1, r.lineas().size());
    }

    @Test
    void cuponInvalidoGeneraAdvertenciaYNoDescuenta() {
        carrito.agregar(camisa, 1, null);
        ResultadoDescuentos r = CalculadoraDescuentos.porDefecto(octubre).calcular(carrito, nuevo, "FALSO99");
        assertEquals(0, r.totalDescuento());
        assertFalse(r.advertencias().isEmpty());
    }

    @Test
    void sePuedeAgregarUnDescuentoNuevoSinTocarLosExistentes() {
        class DescuentoFijo extends DescuentoHandler {
            @Override
            protected void aplicar(ContextoDescuento ctx) {
                ctx.agregar("Descuento de prueba", 2_000);
            }
        }
        DescuentoHandler primero = new DescuentoFijo();
        primero.enlazar(new DescuentoFijo());
        carrito.agregar(camisa, 1, null);
        ResultadoDescuentos r = new CalculadoraDescuentos(primero, octubre).calcular(carrito, nuevo, "");
        assertEquals(4_000, r.totalDescuento());
        assertTrue(r.lineas().stream().allMatch(l -> l.nombre().equals("Descuento de prueba")));
    }

    @Test
    void elDescuentoTotalNuncaSuperaElSubtotal() {
        class DescuentoEnorme extends DescuentoHandler {
            @Override
            protected void aplicar(ContextoDescuento ctx) {
                ctx.agregar("Enorme", 10_000_000);
            }
        }
        carrito.agregar(camisa, 1, null);
        ResultadoDescuentos r = new CalculadoraDescuentos(new DescuentoEnorme(), octubre).calcular(carrito, nuevo, "");
        assertEquals(100_000, r.totalDescuento());
        assertEquals(0, r.totalConDescuento());
        assertEquals(List.of("Enorme"), r.lineas().stream().map(l -> l.nombre()).toList());
    }
}
