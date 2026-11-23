package tienda.modelo;

public class Tecnologia extends Producto {
    private final String marca;
    private final int mesesGarantia;

    public Tecnologia(String id, String nombre, long precio, int stock, String marca, int mesesGarantia) {
        super(id, nombre, precio, stock, Categoria.TECNOLOGIA);
        this.marca = marca;
        this.mesesGarantia = mesesGarantia;
    }

    public String getMarca() {
        return marca;
    }

    public int getMesesGarantia() {
        return mesesGarantia;
    }

    @Override
    public String getDetalles() {
        return "Marca: " + marca + " | Garantía: " + mesesGarantia + " meses";
    }
}
