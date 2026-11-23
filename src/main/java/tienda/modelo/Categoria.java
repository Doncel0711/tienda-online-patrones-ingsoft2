package tienda.modelo;

public enum Categoria {
    ROPA("Ropa"),
    TECNOLOGIA("Tecnología"),
    COMIDA("Comida");

    private final String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
