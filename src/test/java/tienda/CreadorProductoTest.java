package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tienda.fabrica.CreadorProducto;
import tienda.fabrica.Creadores;
import tienda.modelo.Categoria;
import tienda.modelo.Comida;
import tienda.modelo.Producto;
import tienda.modelo.Ropa;
import tienda.modelo.Tecnologia;

/** Pruebas del patrón Factory Method. */
class CreadorProductoTest {

    @Test
    void cadaCategoriaCreaSuSubclase() {
        Producto r = Creadores.para(Categoria.ROPA).crear("R", "Camisa", 50_000, 5, Map.of("talla", "M", "color", "Rojo"));
        Producto t = Creadores.para(Categoria.TECNOLOGIA).crear("T", "Mouse", 60_000, 5, Map.of("marca", "X", "mesesGarantia", "12"));
        Producto c = Creadores.para(Categoria.COMIDA).crear("C", "Galletas", 5_000, 5, Map.of("pesoGramos", "200", "vence", "2027-01-01"));
        assertInstanceOf(Ropa.class, r);
        assertInstanceOf(Tecnologia.class, t);
        assertInstanceOf(Comida.class, c);
    }

    @Test
    void productoCreadoConservaSusAtributosEspecificos() {
        Producto t = Creadores.para(Categoria.TECNOLOGIA).crear("T", "Mouse", 60_000, 5, Map.of("marca", "Logi", "mesesGarantia", "24"));
        assertEquals("Marca: Logi | Garantía: 24 meses", t.getDetalles());
    }

    @Test
    void validacionComunSeAplicaATodosLosCreadores() {
        CreadorProducto ropa = Creadores.para(Categoria.ROPA);
        Map<String, String> attrs = Map.of("talla", "M", "color", "Rojo");
        assertThrows(IllegalArgumentException.class, () -> ropa.crear("R", "Camisa", 0, 5, attrs));
        assertThrows(IllegalArgumentException.class, () -> ropa.crear("R", " ", 10, 5, attrs));
        assertThrows(IllegalArgumentException.class, () -> ropa.crear("R", "Camisa", 10, -1, attrs));
    }

    @Test
    void faltaUnAtributoObligatorio() {
        CreadorProducto ropa = Creadores.para(Categoria.ROPA);
        assertThrows(IllegalArgumentException.class, () -> ropa.crear("R", "Camisa", 10, 5, Map.of("talla", "M")));
    }

    @Test
    void atributoConFormatoInvalido() {
        CreadorProducto comida = Creadores.para(Categoria.COMIDA);
        assertThrows(IllegalArgumentException.class,
                () -> comida.crear("C", "Pan", 1_000, 1, Map.of("pesoGramos", "abc", "vence", "2027-01-01")));
        assertThrows(IllegalArgumentException.class,
                () -> comida.crear("C", "Pan", 1_000, 1, Map.of("pesoGramos", "100", "vence", "mañana")));
    }
}
