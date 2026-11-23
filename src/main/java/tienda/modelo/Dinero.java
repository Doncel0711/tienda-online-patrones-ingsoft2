package tienda.modelo;

import java.util.Locale;

/** Utilidad de formato para pesos colombianos (se manejan como long, sin decimales). */
public final class Dinero {
    private static final Locale CO = Locale.forLanguageTag("es-CO");

    private Dinero() {
    }

    public static String formato(long pesos) {
        return String.format(CO, "$%,d", pesos);
    }
}
