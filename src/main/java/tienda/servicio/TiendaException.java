package tienda.servicio;

/** Error de negocio con un mensaje listo para mostrar al usuario (consola o GUI). */
public class TiendaException extends RuntimeException {
    public TiendaException(String mensaje) {
        super(mensaje);
    }
}
