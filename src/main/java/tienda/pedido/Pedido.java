package tienda.pedido;

import java.time.LocalDateTime;
import java.util.List;
import tienda.descuentos.ResultadoDescuentos;

/** Pedido ya cerrado e inmutable: es lo que se guarda en el historial. */
public class Pedido {
    private final int numero;
    private final String cliente;
    private final List<LineaPedido> lineas;
    private final ResultadoDescuentos descuentos;
    private final String metodoPago;
    private final long recargo;
    private final long total;
    private final String referenciaPago;
    private final LocalDateTime fecha;

    public Pedido(int numero, String cliente, List<LineaPedido> lineas, ResultadoDescuentos descuentos,
                  String metodoPago, long recargo, long total, String referenciaPago, LocalDateTime fecha) {
        this.numero = numero;
        this.cliente = cliente;
        this.lineas = List.copyOf(lineas);
        this.descuentos = descuentos;
        this.metodoPago = metodoPago;
        this.recargo = recargo;
        this.total = total;
        this.referenciaPago = referenciaPago;
        this.fecha = fecha;
    }

    public int getNumero() {
        return numero;
    }

    public String getCliente() {
        return cliente;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public ResultadoDescuentos getDescuentos() {
        return descuentos;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public long getRecargo() {
        return recargo;
    }

    public long getTotal() {
        return total;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFactura() {
        return Factura.generar(this);
    }
}
