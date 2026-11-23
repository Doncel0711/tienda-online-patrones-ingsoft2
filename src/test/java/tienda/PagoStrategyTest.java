package tienda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tienda.pago.MetodoPago;
import tienda.pago.PagoContraEntrega;
import tienda.pago.PagoTarjeta;
import tienda.pago.PagoTransferencia;
import tienda.pago.ResultadoPago;

/** Pruebas del patrón Strategy. */
class PagoStrategyTest {

    @Test
    void tarjetaValidaSeAprueba() {
        ResultadoPago r = new PagoTarjeta().pagar(100_000, Map.of("numero", "4111 1111 1111 1234", "cvv", "123"));
        assertTrue(r.aprobado());
        assertEquals("TC-1234", r.referencia());
    }

    @Test
    void tarjetaRechazaNumeroCvvYCupo() {
        PagoTarjeta t = new PagoTarjeta();
        assertFalse(t.pagar(1_000, Map.of("numero", "123", "cvv", "123")).aprobado());
        assertFalse(t.pagar(1_000, Map.of("numero", "4111111111111111", "cvv", "12")).aprobado());
        assertFalse(t.pagar(PagoTarjeta.CUPO + 1, Map.of("numero", "4111111111111111", "cvv", "123")).aprobado());
    }

    @Test
    void transferenciaExigeBancoYReferencia() {
        PagoTransferencia t = new PagoTransferencia();
        assertTrue(t.pagar(10_000, Map.of("banco", "Bancolombia", "referencia", "ABC12345")).aprobado());
        assertFalse(t.pagar(10_000, Map.of("banco", "", "referencia", "ABC12345")).aprobado());
        assertFalse(t.pagar(10_000, Map.of("banco", "Davivienda", "referencia", "123")).aprobado());
    }

    @Test
    void contraEntregaTieneRecargoYTope() {
        PagoContraEntrega c = new PagoContraEntrega();
        assertEquals(PagoContraEntrega.RECARGO, c.recargo(50_000));
        assertTrue(c.pagar(50_000, Map.of("direccion", "Calle 1 # 2-3")).aprobado());
        assertFalse(c.pagar(PagoContraEntrega.TOPE + 1, Map.of("direccion", "Calle 1 # 2-3")).aprobado());
        assertFalse(c.pagar(50_000, Map.of("direccion", " ")).aprobado());
    }

    @Test
    void cadaMedioDeclaraSusCamposParaLasInterfaces() {
        assertEquals(2, new PagoTarjeta().camposRequeridos().size());
        assertEquals(1, new PagoContraEntrega().camposRequeridos().size());
    }

    @Test
    void sePuedeUsarUnMedioNuevoSinCambiarElResto() {
        MetodoPago billetera = new MetodoPago() {
            public String getNombre() { return "Billetera"; }
            public java.util.List<String> camposRequeridos() { return java.util.List.of("telefono"); }
            public long recargo(long monto) { return 0; }
            public ResultadoPago pagar(long monto, Map<String, String> datos) {
                return new ResultadoPago(true, "ok", "BIL-1");
            }
        };
        assertTrue(billetera.pagar(1, Map.of()).aprobado());
    }
}
