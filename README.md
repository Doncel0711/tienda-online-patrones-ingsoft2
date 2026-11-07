# Propuesta de Proyecto: Tienda en Línea

**Materia:** Ingeniería de Software 2  
**Integrantes:**
* Laura Sthefanny Doncel Parra
* Jhonatan Snayder Forigua Tiria

---

## 1. Alcance del Proyecto

### Lo que sí vamos a hacer
* **Catálogo de productos:** Vamos a manejar productos divididos por categorías (ropa, tecnología y comida). Cada categoría va a tener sus propias características (talla para ropa, garantía/marca para tecnología).
* **Carrito de compras y adicionales:** El usuario podrá agregar productos al carrito y personalizar los ítems con opciones extra (empaque de regalo, envío rápido o garantía extendida), recalculando el precio final.
* **Sistema de descuentos:** Aplicación de descuentos por cantidad, cliente frecuente, fechas especiales o cupones, permitiendo combinar algunos según las reglas.
* **Métodos de pago:** El usuario elegirá entre tarjeta, transferencia o pago contra entrega. Cada opción ejecutará sus propias validaciones.
* **Aviso de disponibilidad:** Si un producto se agota, el usuario podrá suscribirse para recibir una alerta cuando vuelva a haber stock.
* **Factura e historial:** Al finalizar la compra, el pedido se guardará en el historial del cliente y se mostrará su factura.

### Lo que NO vamos a hacer y por qué
* **Rastreo de envíos en tiempo real con mapas:** Se excluye porque la materia se enfoca en aplicar patrones de diseño en el núcleo en Java y no en conectar la aplicación con servicios logísticos externos.
* **Bases de datos externas (como MySQL o MongoDB):** La información se guardará en memoria mientras la app corre para enfocarnos en la estructura de clases y facilitar que funcione en consola y en la interfaz gráfica sin configurar servidores externos.
* **Devoluciones o reclamos:** Se dejan fuera para delimitar el tamaño del proyecto y concentrarnos en la selección de productos y el cobro.

### Las 3 partes del sistema que más van a cambiar
El proyecto cumple con tener tres áreas con variación constante:
1. **Los descuentos:** Continuamente cambian las promociones o la forma de combinar cupones.
2. **Los métodos de pago:** Cada forma de pagar tiene sus propias reglas y validaciones.
3. **Los adicionales de los productos:** Los extras disponibles cambian según la categoría del producto.

---

## 2. Requisitos del Sistema

### Requisitos Funcionales
* **RF01:** El sistema debe mostrar los productos organizados por su categoría con sus detalles específicos.
* **RF02:** El sistema debe permitir agregarle opciones extra a un producto (empaque, garantía) y actualizar su precio final.
* **RF03:** El sistema debe permitir agregar, cambiar cantidades y quitar productos del carrito antes de confirmar el pago.
* **RF04:** El sistema debe aplicar los descuentos correspondientes al carrito de forma automática.
* **RF05:** El sistema debe procesar el pago simulado según la opción elegida por el usuario.
* **RF06:** El sistema debe notificar al usuario cuando un producto agotado vuelva a tener inventario.
* **RF07:** El sistema debe generar la factura con el resumen de la compra y guardarla en el historial del cliente.

### Requisitos No Funcionales
* **RNF01:** La lógica del programa debe ser independiente de la pantalla; la consola y la interfaz gráfica deben usar exactamente el mismo núcleo de código.
* **RNF02:** El código debe aplicar patrones de diseño para permitir agregar nuevos métodos de pago o descuentos sin alterar el código existente.
* **RNF03:** La aplicación se desarrollará en lenguaje Java versión 17 o superior.
* **RNF04:** Se implementarán pruebas unitarias para validar las áreas donde se aplicaron los patrones de diseño.
* **RNF05:** El desarrollo se registrará en un repositorio de GitHub con aportes visibles de ambos integrantes.
