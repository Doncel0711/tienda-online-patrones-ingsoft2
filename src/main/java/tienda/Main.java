package tienda;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import tienda.consola.ConsolaApp;
import tienda.gui.TiendaGui;
import tienda.servicio.CatalogoInicial;
import tienda.servicio.Tienda;

/**
 * Punto de entrada.
 *   java -cp target/classes tienda.Main            -> consola
 *   java -cp target/classes tienda.Main --gui      -> interfaz gráfica
 * Ambas interfaces usan el mismo núcleo (Tienda).
 */
public class Main {
    public static void main(String[] args) {
        Tienda tienda = CatalogoInicial.cargar(new Tienda());
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            TiendaGui.lanzar(tienda);
        } else {
            PrintStream salida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
            new ConsolaApp(tienda, System.in, salida).ejecutar();
        }
    }
}
