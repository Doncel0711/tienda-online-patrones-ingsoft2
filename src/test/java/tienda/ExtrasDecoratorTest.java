package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import tienda.carrito.ItemCarrito;
import tienda.extras.EmpaqueRegalo;
import tienda.extras.EnvioExpress;
import tienda.extras.GarantiaExtendida;
import tienda.extras.TipoExtra;
import tienda.modelo.Articulo;
import tienda.modelo.Comida;
import tienda.modelo.Ropa;
import tienda.modelo.Tecnologia;

/** Pruebas del patrón Decorator. */
class ExtrasDecoratorTest {
    private final Tecnologia teclado = new Tecnologia("T2", "Teclado", 200_000, 5, "KeyPro", 12);

    @Test
    void decoradorSumaPrecioYDescripcion() {
        Articulo a = new EmpaqueRegalo(teclado);
        assertEquals(208_000, a.getPrecio());
        assertEquals("Teclado + Empaque de regalo", a.getDescripcion());
    }

    @Test
    void decoradoresSeApilanSinModificarElProducto() {
        Articulo a = new EnvioExpress(new GarantiaExtendida(teclado));
        assertEquals(200_000 + 20_000 + 15_000, a.getPrecio());
        assertEquals("Teclado + Garantía extendida + Envío express", a.getDescripcion());
        assertEquals(200_000, teclado.getPrecio(), "el producto original no cambia");
    }

    @Test
    void itemCarritoAplicaExtrasEnElOrdenDeclarado() {
        ItemCarrito item = new ItemCarrito(teclado, 2,
                EnumSet.of(TipoExtra.ENVIO_EXPRESS, TipoExtra.GARANTIA_EXTENDIDA, TipoExtra.EMPAQUE_REGALO));
        // garantía 10% sobre el precio base, luego empaque y envío
        assertEquals(200_000 + 20_000 + 8_000 + 15_000, item.getPrecioUnitario());
        assertEquals(2 * 243_000, item.getSubtotal());
    }

    @Test
    void garantiaSoloAplicaATecnologia() {
        Ropa camisa = new Ropa("R1", "Camisa", 40_000, 5, "M", "Azul");
        assertThrows(IllegalArgumentException.class, () -> new ItemCarrito(camisa, 1, List.of(TipoExtra.GARANTIA_EXTENDIDA)));
    }

    @Test
    void empaqueNoAplicaAComida() {
        Comida pan = new Comida("C1", "Pan", 3_000, 5, 300, java.time.LocalDate.of(2027, 1, 1));
        assertTrue(!TipoExtra.EMPAQUE_REGALO.esAplicable(pan));
        assertTrue(TipoExtra.ENVIO_EXPRESS.esAplicable(pan));
        assertThrows(IllegalArgumentException.class, () -> new ItemCarrito(pan, 1, List.of(TipoExtra.EMPAQUE_REGALO)));
    }
}
