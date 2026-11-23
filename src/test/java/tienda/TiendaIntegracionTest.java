package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tienda.extras.TipoExtra;
import tienda.pago.PagoContraEntrega;
import tienda.pago.PagoTarjeta;
import tienda.pedido.Pedido;
import tienda.servicio.CatalogoInicial;
import tienda.servicio.Tienda;
import tienda.servicio.TiendaException;

/** Flujos completos sobre el núcleo (lo que usan la consola y la GUI). */
class TiendaIntegracionTest {
    private Tienda tienda;

    @BeforeEach
    void preparar() {
        Clock octubre = Clock.fixed(Instant.parse("2026-10-10T12:00:00Z"), ZoneId.of("America/Bogota"));
        tienda = CatalogoInicial.cargar(new Tienda(octubre));
    }

    @Test
    void compraCompletaDescuentaStockGuardaHistorialYGeneraFactura() {
        tienda.agregarAlCarrito("C1", "T2", 1, List.of(TipoExtra.GARANTIA_EXTENDIDA)); // 250.000 + 25.000
        Pedido p = tienda.pagar("C1", new PagoTarjeta(), Map.of("numero", "4111111111111111", "cvv", "123"), "");

        assertEquals(275_000, p.getTotal());
        assertEquals(7, tienda.buscarProducto("T2").getStock());
        assertEquals(1, tienda.getHistorial("C1").size());
        assertTrue(tienda.getCarrito("C1").estaVacio());
        assertEquals(1, tienda.buscarCliente("C1").getComprasRealizadas());
        assertTrue(p.getFactura().contains("TOTAL A PAGAR"));
        assertTrue(p.getFactura().contains("Garantía extendida"));
    }

    @Test
    void pagoRechazadoNoToculaStockNiElCarrito() {
        tienda.agregarAlCarrito("C1", "R1", 2, List.of());
        assertThrows(TiendaException.class,
                () -> tienda.pagar("C1", new PagoTarjeta(), Map.of("numero", "1", "cvv", "1"), ""));
        assertEquals(20, tienda.buscarProducto("R1").getStock());
        assertFalse(tienda.getCarrito("C1").estaVacio());
        assertTrue(tienda.getHistorial("C1").isEmpty());
    }

    @Test
    void contraEntregaSumaRecargoAlTotal() {
        tienda.agregarAlCarrito("C1", "R1", 1, List.of()); // 45.000
        Pedido p = tienda.pagar("C1", new PagoContraEntrega(), Map.of("direccion", "Calle 10 # 5-5"), "");
        assertEquals(45_000 + PagoContraEntrega.RECARGO, p.getTotal());
    }

    @Test
    void clienteFrecuenteObtieneDescuentoEnLaCompra() {
        tienda.agregarAlCarrito("C2", "R2", 1, List.of()); // 120.000, cliente con 3 compras
        assertEquals(6_000, tienda.calcularResumen("C2", "").totalDescuento());
    }

    @Test
    void noSePuedeAgregarUnProductoAgotadoNiPasarseDelStock() {
        assertThrows(TiendaException.class, () -> tienda.agregarAlCarrito("C1", "T1", 1, List.of()));
        assertThrows(TiendaException.class, () -> tienda.agregarAlCarrito("C1", "T3", 4, List.of()));
    }

    @Test
    void carritoVacioNoSePuedePagar() {
        assertThrows(TiendaException.class,
                () -> tienda.pagar("C1", new PagoContraEntrega(), Map.of("direccion", "x"), ""));
    }

    @Test
    void avisoDeStockFuncionaDeExtremoAExtremo() {
        tienda.suscribirAviso("C1", "T1");
        assertThrows(TiendaException.class, () -> tienda.suscribirAviso("C1", "T2")); // T2 tiene stock
        tienda.reponerStock("T1", 4);
        assertEquals(1, tienda.buscarCliente("C1").getAvisos().size());
        assertEquals(0, tienda.buscarCliente("C2").getAvisos().size());
    }

    @Test
    void cambiarCantidadYQuitarLineas() {
        tienda.agregarAlCarrito("C1", "R1", 1, List.of());
        tienda.cambiarCantidad("C1", 0, 5);
        assertEquals(5, tienda.getCarrito("C1").getItems().get(0).getCantidad());
        assertThrows(TiendaException.class, () -> tienda.cambiarCantidad("C1", 0, 999));
        tienda.quitarDelCarrito("C1", 0);
        assertTrue(tienda.getCarrito("C1").estaVacio());
    }

    @Test
    void mismoProductoConMismosExtrasSeFusionaEnUnaLinea() {
        tienda.agregarAlCarrito("C1", "R1", 1, List.of());
        tienda.agregarAlCarrito("C1", "R1", 2, List.of());
        tienda.agregarAlCarrito("C1", "R1", 1, List.of(TipoExtra.EMPAQUE_REGALO));
        assertEquals(2, tienda.getCarrito("C1").getItems().size());
        assertEquals(3, tienda.getCarrito("C1").getItems().get(0).getCantidad());
    }
}
