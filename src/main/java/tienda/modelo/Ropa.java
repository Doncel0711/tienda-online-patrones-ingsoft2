package tienda.modelo;

public class Ropa extends Producto {
    private final String talla;
    private final String color;

    public Ropa(String id, String nombre, long precio, int stock, String talla, String color) {
        super(id, nombre, precio, stock, Categoria.ROPA);
        this.talla = talla;
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getDetalles() {
        return "Talla: " + talla + " | Color: " + color;
    }
}
