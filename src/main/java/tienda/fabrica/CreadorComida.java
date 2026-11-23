package tienda.fabrica;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import tienda.modelo.Categoria;
import tienda.modelo.Comida;
import tienda.modelo.Producto;

public class CreadorComida extends CreadorProducto {
    @Override
    protected Producto crearProducto(String id, String nombre, long precio, int stock, Map<String, String> a) {
        try {
            int peso = Integer.parseInt(a.get("pesoGramos").trim());
            LocalDate vence = LocalDate.parse(a.get("vence").trim());
            return new Comida(id, nombre, precio, stock, peso, vence);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("pesoGramos debe ser un número entero");
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("vence debe tener formato yyyy-MM-dd");
        }
    }

    @Override
    public Categoria categoria() {
        return Categoria.COMIDA;
    }

    @Override
    public List<String> atributosRequeridos() {
        return List.of("pesoGramos", "vence");
    }
}
