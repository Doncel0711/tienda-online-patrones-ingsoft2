# Tienda en Línea — Parcial de Patrones de Diseño

**Materia:** Ingeniería de Software 2
**Integrantes:**

- Laura Sthefanny Doncel Parra
- Jhonatan Snayder Forigua Tiria

Sistema de compras simulado (catálogo, carrito, descuentos, pagos, avisos de stock, historial y factura) en **Java 17+**,
con **versión de consola** y **versión con interfaz gráfica (Swing)** que usan **el mismo núcleo** (`tienda.servicio.Tienda`).
La información vive en memoria; no se usan bases de datos externas.

---

## Cómo compilar y ejecutar

Requisitos: **JDK 17 o superior**. Maven es opcional.

### Opción A — Con Maven

```bash
mvn test                      # compila y corre las pruebas unitarias
mvn package                   # genera target/tienda-online-1.0.0.jar
java -jar target/tienda-online-1.0.0.jar          # versión consola
java -jar target/tienda-online-1.0.0.jar --gui    # versión interfaz gráfica
```

### Opción B — Sin Maven (solo JDK)

Linux / macOS / Git Bash:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main -name "*.java")
java -cp out tienda.Main          # consola
java -cp out tienda.Main --gui    # interfaz gráfica
```

Windows (PowerShell):

```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main -Filter *.java).FullName
java -cp out tienda.Main          # consola
java -cp out tienda.Main --gui    # interfaz gráfica
```

> En la consola de Windows, si las tildes se ven mal, ejecuta antes `chcp 65001`.

### Datos de demostración

- Clientes: **Laura Doncel** (sin compras previas) y **Jhonatan Forigua** (3 compras previas → cliente frecuente).
- 8 productos en 3 categorías. **Audífonos inalámbricos (T1)** inicia agotado para probar el aviso de disponibilidad.
- Cupones: `BIENVENIDO10` (10 %, combinable) y `MEGA30` (30 %, **no** combinable: anula los demás descuentos).
- Tarjeta de prueba: cualquier número de 16 dígitos y CVV de 3 dígitos.

---

## Funcionalidades implementadas

| # | Funcionalidad | Requisito |
|---|---------------|-----------|
| 1 | Catálogo por categorías (ropa, tecnología, comida), cada una con atributos propios | RF01 |
| 2 | Extras por producto: garantía extendida (solo tecnología), empaque de regalo (no comida), envío express (todos) | RF02 |
| 3 | Carrito: agregar, cambiar cantidad, quitar; validación de stock | RF03 |
| 4 | Descuentos automáticos y acumulables: por cantidad, de temporada, cliente frecuente y cupones (uno exclusivo) | RF04 |
| 5 | Pago simulado con tarjeta, transferencia o contra entrega (cada uno con sus reglas y recargo) | RF05 |
| 6 | Aviso al cliente suscrito cuando un producto agotado vuelve a tener existencias | RF06 |
| 7 | Factura detallada e historial de pedidos por cliente | RF07 |
| 8 | Consola y GUI sobre el mismo núcleo | RNF01 |

## Arquitectura por paquetes

```
src/main/java/tienda
├── modelo/      Producto (abstracto), Ropa, Tecnologia, Comida, Cliente, Articulo, ObservadorStock, Dinero
├── fabrica/     CreadorProducto + CreadorRopa/Tecnologia/Comida + Creadores     (Factory Method)
├── extras/      ExtraDecorator + GarantiaExtendida/EmpaqueRegalo/EnvioExpress   (Decorator)
├── carrito/     Carrito, ItemCarrito
├── descuentos/  DescuentoHandler + Cupon/Temporada/Cantidad/ClienteFrecuente    (Chain of Responsibility)
├── pago/        MetodoPago + PagoTarjeta/PagoTransferencia/PagoContraEntrega    (Strategy)
├── pedido/      Pedido, LineaPedido, Factura
├── servicio/    Tienda (núcleo/fachada), TiendaException, CatalogoInicial
├── consola/     ConsolaApp   ┐ ambas dependen solo de servicio, nunca la una de la otra
├── gui/         TiendaGui    ┘
└── Main.java
```

## Patrones de diseño aplicados

| Patrón | Categoría | Problema que resuelve | Clases principales |
|--------|-----------|-----------------------|--------------------|
| Factory Method | Creacional | Crear productos de distintas categorías sin `if/else` por categoría | `CreadorProducto`, `CreadorRopa`, `CreadorTecnologia`, `CreadorComida` |
| Decorator | Estructural | Extras combinables que cambian precio y descripción sin una clase por combinación | `ExtraDecorator`, `GarantiaExtendida`, `EmpaqueRegalo`, `EnvioExpress` |
| Chain of Responsibility | Comportamiento | Descuentos acumulables, con uno que anula a los demás | `DescuentoHandler`, `CuponHandler`, `TemporadaHandler`, `CantidadHandler`, `ClienteFrecuenteHandler` |
| Strategy | Comportamiento | Medios de pago con validaciones y recargos distintos | `MetodoPago`, `PagoTarjeta`, `PagoTransferencia`, `PagoContraEntrega` |
| Observer | Comportamiento | Avisar a los interesados cuando un producto agotado vuelve | `Producto` (sujeto), `ObservadorStock`, `Cliente` |

## Pruebas unitarias

Hay una clase de pruebas por patrón y una de integración (JUnit 5, 40 pruebas):
`CreadorProductoTest`, `ExtrasDecoratorTest`, `DescuentosChainTest`, `PagoStrategyTest`, `StockObserverTest`, `TiendaIntegracionTest`.
Se ejecutan con `mvn test`.

## Declaración de uso de herramientas de IA

Se utilizó **Claude (Anthropic)** como apoyo para generar la base del código fuente en Java, las pruebas unitarias y este README
a partir de la propuesta y los requisitos definidos por el grupo. El grupo revisó, ejecutó y comprende el código, y puede explicar
cualquier parte del proyecto en la sustentación.

---

# Propuesta de Proyecto: Tienda en Línea

## 1. Alcance del Proyecto

### Lo que sí vamos a hacer

- **Catálogo de productos:** Vamos a manejar productos divididos por categorías (ropa, tecnología y comida). Cada categoría va a tener sus propias características (talla para ropa, garantía/marca para tecnología).
- **Carrito de compras y adicionales:** El usuario podrá agregar productos al carrito y personalizar los ítems con opciones extra (empaque de regalo, envío rápido o garantía extendida), recalculando el precio final.
- **Sistema de descuentos:** Aplicación de descuentos por cantidad, cliente frecuente, fechas especiales o cupones, permitiendo combinar algunos según las reglas.
- **Métodos de pago:** El usuario elegirá entre tarjeta, transferencia o pago contra entrega. Cada opción ejecutará sus propias validaciones.
- **Aviso de disponibilidad:** Si un producto se agota, el usuario podrá suscribirse para recibir una alerta cuando vuelva a haber stock.
- **Factura e historial:** Al finalizar la compra, el pedido se guardará en el historial del cliente y se mostrará su factura.

### Lo que NO vamos a hacer y por qué

- **Rastreo de envíos en tiempo real con mapas:** Se excluye porque la materia se enfoca en aplicar patrones de diseño en el núcleo en Java y no en conectar la aplicación con servicios logísticos externos.
- **Bases de datos externas (como MySQL o MongoDB):** La información se guardará en memoria mientras la app corre para enfocarnos en la estructura de clases y facilitar que funcione en consola y en la interfaz gráfica sin configurar servidores externos.
- **Devoluciones o reclamos:** Se dejan fuera para delimitar el tamaño del proyecto y concentrarnos en la selección de productos y el cobro.

### Las 3 partes del sistema que más van a cambiar

1. **Los descuentos:** Continuamente cambian las promociones o la forma de combinar cupones.
2. **Los métodos de pago:** Cada forma de pagar tiene sus propias reglas y validaciones.
3. **Los adicionales de los productos:** Los extras disponibles cambian según la categoría del producto.

## 2. Requisitos del Sistema

### Requisitos Funcionales

- **RF01:** El sistema debe mostrar los productos organizados por su categoría con sus detalles específicos.
- **RF02:** El sistema debe permitir agregarle opciones extra a un producto (empaque, garantía) y actualizar su precio final.
- **RF03:** El sistema debe permitir agregar, cambiar cantidades y quitar productos del carrito antes de confirmar el pago.
- **RF04:** El sistema debe aplicar los descuentos correspondientes al carrito de forma automática.
- **RF05:** El sistema debe procesar el pago simulado según la opción elegida por el usuario.
- **RF06:** El sistema debe notificar al usuario cuando un producto agotado vuelva a tener inventario.
- **RF07:** El sistema debe generar la factura con el resumen de la compra y guardarla en el historial del cliente.

### Requisitos No Funcionales

- **RNF01:** La lógica del programa debe ser independiente de la pantalla; la consola y la interfaz gráfica deben usar exactamente el mismo núcleo de código.
- **RNF02:** El código debe aplicar patrones de diseño para permitir agregar nuevos métodos de pago o descuentos sin alterar el código existente.
- **RNF03:** La aplicación se desarrollará en lenguaje Java versión 17 o superior.
- **RNF04:** Se implementarán pruebas unitarias para validar las áreas donde se aplicaron los patrones de diseño.
- **RNF05:** El desarrollo se registrará en un repositorio de GitHub con aportes visibles de ambos integrantes.
