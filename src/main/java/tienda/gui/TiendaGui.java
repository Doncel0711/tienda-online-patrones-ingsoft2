package tienda.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import tienda.carrito.ItemCarrito;
import tienda.descuentos.LineaDescuento;
import tienda.descuentos.ResultadoDescuentos;
import tienda.extras.TipoExtra;
import tienda.modelo.Cliente;
import tienda.modelo.Dinero;
import tienda.modelo.Producto;
import tienda.pago.MetodoPago;
import tienda.pedido.Pedido;
import tienda.servicio.Tienda;
import tienda.servicio.TiendaException;

/** Interfaz gráfica sencilla (Swing). Usa exactamente el mismo núcleo (Tienda) que la consola. */
public class TiendaGui extends JFrame {
    private final Tienda tienda;
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();

    private final DefaultTableModel modeloCatalogo = soloLectura("Id", "Producto", "Categoría", "Detalles", "Precio", "Stock");
    private final JTable tablaCatalogo = new JTable(modeloCatalogo);
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final Map<TipoExtra, JCheckBox> checksExtras = new EnumMap<>(TipoExtra.class);

    private final DefaultTableModel modeloCarrito = soloLectura("#", "Descripción", "Cant.", "Precio unit.", "Subtotal");
    private final JTable tablaCarrito = new JTable(modeloCarrito);
    private final JTextField txtCupon = new JTextField(12);
    private final JTextArea areaResumen = new JTextArea(6, 30);
    private final JComboBox<MetodoPago> comboPago = new JComboBox<>();
    private final JPanel panelCamposPago = new JPanel(new GridLayout(0, 2, 4, 4));
    private final Map<String, JTextField> camposPago = new LinkedHashMap<>();

    private final JTextArea areaHistorial = new JTextArea();
    private final JTextArea areaAvisos = new JTextArea(5, 30);

    public TiendaGui(Tienda tienda) {
        super("Tienda en línea - Patrones de diseño");
        this.tienda = tienda;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Cliente:"));
        tienda.getClientes().forEach(comboCliente::addItem);
        comboCliente.addActionListener(e -> refrescarTodo());
        norte.add(comboCliente);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catálogo", panelCatalogo());
        tabs.addTab("Carrito y pago", panelCarrito());
        tabs.addTab("Historial y avisos", panelHistorial());
        tabs.addChangeListener(e -> refrescarTodo());

        add(norte, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        refrescarTodo();
    }

    // ---------- paneles ----------
    private JPanel panelCatalogo() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Cantidad:"));
        sur.add(spCantidad);
        for (TipoExtra e : TipoExtra.values()) {
            JCheckBox cb = new JCheckBox(e.getEtiqueta());
            checksExtras.put(e, cb);
            sur.add(cb);
        }
        JButton btnAgregar = new JButton("Agregar al carrito");
        btnAgregar.addActionListener(e -> accion(this::agregar));
        JButton btnAviso = new JButton("Avisarme cuando haya stock");
        btnAviso.addActionListener(e -> accion(this::suscribir));
        JButton btnRepo = new JButton("(Admin) Reponer +10");
        btnRepo.addActionListener(e -> accion(this::reponer));
        sur.add(btnAgregar);
        sur.add(btnAviso);
        sur.add(btnRepo);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCarrito() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCambiar = new JButton("Cambiar cantidad");
        btnCambiar.addActionListener(e -> accion(this::cambiarCantidad));
        JButton btnQuitar = new JButton("Quitar línea");
        btnQuitar.addActionListener(e -> accion(this::quitar));
        botones.add(btnCambiar);
        botones.add(btnQuitar);
        p.add(botones, BorderLayout.NORTH);

        JPanel sur = new JPanel(new BorderLayout(6, 6));
        JPanel cupon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cupon.add(new JLabel("Cupón:"));
        cupon.add(txtCupon);
        JButton btnCalcular = new JButton("Calcular descuentos");
        btnCalcular.addActionListener(e -> accion(this::refrescarResumen));
        cupon.add(btnCalcular);
        sur.add(cupon, BorderLayout.NORTH);

        areaResumen.setEditable(false);
        sur.add(new JScrollPane(areaResumen), BorderLayout.CENTER);

        JPanel pago = new JPanel(new BorderLayout(4, 4));
        pago.setBorder(BorderFactory.createTitledBorder("Pago"));
        tienda.getMetodosPago().forEach(comboPago::addItem);
        comboPago.addActionListener(e -> armarCamposPago());
        pago.add(comboPago, BorderLayout.NORTH);
        pago.add(panelCamposPago, BorderLayout.CENTER);
        JButton btnPagar = new JButton("Pagar");
        btnPagar.addActionListener(e -> accion(this::pagar));
        pago.add(btnPagar, BorderLayout.SOUTH);
        sur.add(pago, BorderLayout.EAST);

        p.add(sur, BorderLayout.SOUTH);
        armarCamposPago();
        return p;
    }

    private JPanel panelHistorial() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
        p.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);
        areaAvisos.setEditable(false);
        JPanel avisos = new JPanel(new BorderLayout());
        avisos.setBorder(BorderFactory.createTitledBorder("Avisos de disponibilidad"));
        avisos.add(new JScrollPane(areaAvisos), BorderLayout.CENTER);
        p.add(avisos, BorderLayout.SOUTH);
        return p;
    }

    // ---------- acciones ----------
    private void agregar() {
        Producto p = productoSeleccionado();
        Set<TipoExtra> extras = EnumSet.noneOf(TipoExtra.class);
        checksExtras.forEach((tipo, cb) -> {
            if (cb.isSelected()) {
                extras.add(tipo);
            }
        });
        tienda.agregarAlCarrito(clienteId(), p.getId(), (Integer) spCantidad.getValue(), extras);
        info("Agregado al carrito: " + p.getNombre());
    }

    private void suscribir() {
        Producto p = productoSeleccionado();
        tienda.suscribirAviso(clienteId(), p.getId());
        info("Te avisaremos cuando " + p.getNombre() + " vuelva a tener existencias.");
    }

    private void reponer() {
        tienda.reponerStock(productoSeleccionado().getId(), 10);
    }

    private void cambiarCantidad() {
        int fila = filaCarrito();
        String s = JOptionPane.showInputDialog(this, "Nueva cantidad:");
        if (s != null) {
            try {
                tienda.cambiarCantidad(clienteId(), fila, Integer.parseInt(s.trim()));
            } catch (NumberFormatException e) {
                throw new TiendaException("Escribe un número entero");
            }
        }
    }

    private void quitar() {
        tienda.quitarDelCarrito(clienteId(), filaCarrito());
    }

    private void pagar() {
        MetodoPago metodo = (MetodoPago) comboPago.getSelectedItem();
        Map<String, String> datos = new LinkedHashMap<>();
        camposPago.forEach((k, v) -> datos.put(k, v.getText()));
        Pedido pedido = tienda.pagar(clienteId(), metodo, datos, txtCupon.getText());
        txtCupon.setText("");
        JTextArea factura = new JTextArea(pedido.getFactura());
        factura.setEditable(false);
        factura.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(factura), "Pago aprobado", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Ejecuta una acción mostrando los errores de negocio en un diálogo y refrescando la pantalla. */
    private void accion(Runnable r) {
        try {
            r.run();
        } catch (TiendaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
        refrescarTodo();
    }

    // ---------- refresco ----------
    private void refrescarTodo() {
        if (comboCliente.getSelectedItem() == null) {
            return;
        }
        modeloCatalogo.setRowCount(0);
        for (Producto p : tienda.getProductos()) {
            modeloCatalogo.addRow(new Object[]{p.getId(), p.getNombre(), p.getCategoria().getNombre(), p.getDetalles(),
                    Dinero.formato(p.getPrecio()), p.agotado() ? "AGOTADO" : p.getStock()});
        }
        modeloCarrito.setRowCount(0);
        List<ItemCarrito> items = tienda.getCarrito(clienteId()).getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemCarrito it = items.get(i);
            modeloCarrito.addRow(new Object[]{i + 1, it.getDescripcion(), it.getCantidad(),
                    Dinero.formato(it.getPrecioUnitario()), Dinero.formato(it.getSubtotal())});
        }
        refrescarResumen();

        StringBuilder h = new StringBuilder();
        for (Pedido p : tienda.getHistorial(clienteId())) {
            h.append(p.getFactura()).append('\n');
        }
        areaHistorial.setText(h.length() == 0 ? "Aún no hay pedidos." : h.toString());
        Cliente c = (Cliente) comboCliente.getSelectedItem();
        areaAvisos.setText(c.getAvisos().isEmpty() ? "Sin avisos." : String.join("\n", c.getAvisos()));
    }

    private void refrescarResumen() {
        ResultadoDescuentos r = tienda.calcularResumen(clienteId(), txtCupon.getText());
        StringBuilder sb = new StringBuilder("Subtotal: " + Dinero.formato(r.subtotal()) + "\n");
        for (LineaDescuento d : r.lineas()) {
            sb.append("  - ").append(d.nombre()).append(": -").append(Dinero.formato(d.monto())).append('\n');
        }
        r.advertencias().forEach(a -> sb.append("  ! ").append(a).append('\n'));
        sb.append("Total con descuentos: ").append(Dinero.formato(r.totalConDescuento()));
        areaResumen.setText(sb.toString());
    }

    /** Los campos del pago se generan según lo que pide cada MetodoPago: la GUI no conoce los medios concretos. */
    private void armarCamposPago() {
        panelCamposPago.removeAll();
        camposPago.clear();
        MetodoPago m = (MetodoPago) comboPago.getSelectedItem();
        if (m != null) {
            for (String campo : m.camposRequeridos()) {
                JTextField tf = new JTextField(14);
                camposPago.put(campo, tf);
                panelCamposPago.add(new JLabel(campo + ":"));
                panelCamposPago.add(tf);
            }
        }
        panelCamposPago.revalidate();
        panelCamposPago.repaint();
    }

    // ---------- utilidades ----------
    private String clienteId() {
        return ((Cliente) comboCliente.getSelectedItem()).getId();
    }

    private Producto productoSeleccionado() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            throw new TiendaException("Selecciona un producto de la tabla");
        }
        return tienda.buscarProducto((String) modeloCatalogo.getValueAt(fila, 0));
    }

    private int filaCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            throw new TiendaException("Selecciona una línea del carrito");
        }
        return fila;
    }

    private void info(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Tienda", JOptionPane.INFORMATION_MESSAGE);
    }

    private static DefaultTableModel soloLectura(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void lanzar(Tienda tienda) {
        SwingUtilities.invokeLater(() -> new TiendaGui(tienda).setVisible(true));
    }
}
