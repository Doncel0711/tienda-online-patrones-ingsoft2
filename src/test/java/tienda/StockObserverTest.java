package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tienda.modelo.Cliente;
import tienda.modelo.ObservadorStock;
import tienda.modelo.Producto;
import tienda.modelo.Tecnologia;

/** Pruebas del patrón Observer. */
class StockObserverTest {

    @Test
    void suscritosSonAvisadosCuandoVuelveElStock() {
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 0, "X", 12);
        Cliente a = new Cliente("A", "Ana");
        Cliente b = new Cliente("B", "Beto");
        p.suscribir(a);
        p.suscribir(b);

        p.reponerStock(5);

        assertEquals(1, a.getAvisos().size());
        assertEquals(1, b.getAvisos().size());
        assertTrue(a.getAvisos().get(0).contains("Audífonos"));
    }

    @Test
    void noSeAvisaSiElProductoYaTeniaStock() {
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 3, "X", 12);
        Cliente a = new Cliente("A", "Ana");
        p.suscribir(a);
        p.reponerStock(5);
        assertTrue(a.getAvisos().isEmpty());
    }

    @Test
    void elAvisoEsDeUnaSolaVez() {
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 0, "X", 12);
        Cliente a = new Cliente("A", "Ana");
        p.suscribir(a);
        p.reponerStock(1);
        p.reducirStock(1);
        p.reponerStock(1);
        assertEquals(1, a.getAvisos().size());
        assertEquals(0, p.cantidadSuscritos());
    }

    @Test
    void noSeDuplicaLaSuscripcion() {
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 0, "X", 12);
        Cliente a = new Cliente("A", "Ana");
        p.suscribir(a);
        p.suscribir(a);
        assertEquals(1, p.cantidadSuscritos());
    }

    @Test
    void cualquierObservadorPuedeSuscribirse() {
        List<String> recibidos = new ArrayList<>();
        ObservadorStock logger = prod -> recibidos.add(prod.getNombre());
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 0, "X", 12);
        p.suscribir(logger);
        p.reponerStock(2);
        assertEquals(List.of("Audífonos"), recibidos);
    }

    @Test
    void desuscribirEvitaElAviso() {
        Producto p = new Tecnologia("T1", "Audífonos", 100_000, 0, "X", 12);
        Cliente a = new Cliente("A", "Ana");
        p.suscribir(a);
        p.desuscribir(a);
        p.reponerStock(2);
        assertTrue(a.getAvisos().isEmpty());
    }
}
