package tienda.extras;

import tienda.modelo.Articulo;
import tienda.modelo.Categoria;
import tienda.modelo.Producto;

/**
 * Catálogo de extras disponibles y a qué categorías aplican.
 * El orden de declaración es el orden en que se aplican los decoradores
 * (la garantía va primero para calcularse solo sobre el precio del producto).
 */
public enum TipoExtra {
    GARANTIA_EXTENDIDA("Garantía extendida (10%)") {
        @Override
        public boolean esAplicable(Producto p) {
            return p.getCategoria() == Categoria.TECNOLOGIA;
        }

        @Override
        public Articulo decorar(Articulo a) {
            return new GarantiaExtendida(a);
        }
    },
    EMPAQUE_REGALO("Empaque de regalo (+$8.000)") {
        @Override
        public boolean esAplicable(Producto p) {
            return p.getCategoria() != Categoria.COMIDA;
        }

        @Override
        public Articulo decorar(Articulo a) {
            return new EmpaqueRegalo(a);
        }
    },
    ENVIO_EXPRESS("Envío express (+$15.000)") {
        @Override
        public boolean esAplicable(Producto p) {
            return true;
        }

        @Override
        public Articulo decorar(Articulo a) {
            return new EnvioExpress(a);
        }
    };

    private final String etiqueta;

    TipoExtra(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public abstract boolean esAplicable(Producto producto);

    public abstract Articulo decorar(Articulo articulo);
}
