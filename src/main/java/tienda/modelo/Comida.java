package tienda.modelo;

import java.time.LocalDate;

public class Comida extends Producto {
    private final int pesoGramos;
    private final LocalDate fechaVencimiento;

    public Comida(String id, String nombre, long precio, int stock, int pesoGramos, LocalDate fechaVencimiento) {
        super(id, nombre, precio, stock, Categoria.COMIDA);
        this.pesoGramos = pesoGramos;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getPesoGramos() {
        return pesoGramos;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    @Override
    public String getDetalles() {
        return "Peso: " + pesoGramos + " g | Vence: " + fechaVencimiento;
    }
}
