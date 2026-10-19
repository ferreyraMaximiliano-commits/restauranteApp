package ar.restaurante.vista;

import ar.restaurante.dao.MesaDAO;
import ar.restaurante.modelo.Pedido;
import ar.restaurante.dao.PedidoDAO;
import ar.restaurante.dao.ProductoDAO;
import ar.restaurante.modelo.Mesa;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.Producto;
import ar.restaurante.modelo.RegistroPedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPedidosMozo extends JDialog {
    private JTable tablaActual, tablaSeguimiento;
    private DefaultTableModel modeloActual, modeloSeguimiento;
    private JComboBox<String> cbMesas, cbProductos, cbEstadosModificar;
    private JSpinner spinCantidad;
    private JTextField txtNota;
    private JButton btnAgregarItem, btnConfirmarPedido, btnCambiarEstado, btnRegresar;
    
    private PedidoDAO pedidoDAO;
    private ProductoDAO productoDAO; 
    private int idMozoAutenticado;
    
    // Objeto principal del modelo encargado de retener el estado local del pedido en construcción
    private Pedido pedidoEnConstruccion;

    private int idPedidoSeleccionado = -1;
    private int renglonSeleccionado = -1;

    public GestionPedidosMozo(Frame padre, int idMozo) {
        super(padre, "Panel de Control de Mozos - Pedidos Orientados a Objetos", true);
        this.pedidoDAO = new PedidoDAO();
        this.productoDAO = new ProductoDAO();
        this.idMozoAutenticado = idMozo;
        
        Mozo mozoSesion = new Mozo(
            this.idMozoAutenticado, null, null, null, null, 
            "", "", null, null, 0.0
        );
        // Inicializamos el objeto de cabecera con la primera mesa por defecto (Mesa 1)
        this.pedidoEnConstruccion = new Pedido(1, mozoSesion);
        
        setSize(1050, 550);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: CONTROLES ---
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cbMesas = new JComboBox<>();
        cargarComboMesas();
        cbProductos = new JComboBox<>();
        cargarComboProductos();
        spinCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        txtNota = new JTextField(12);
        btnAgregarItem = new JButton("＋ Añadir al detalle");

        panelControles.add(new JLabel("Mesa:"));
        panelControles.add(cbMesas);
        panelControles.add(new JLabel("Producto:"));
        panelControles.add(cbProductos);
        panelControles.add(new JLabel("Cantidad:"));
        panelControles.add(spinCantidad);
        panelControles.add(new JLabel("Notas:"));
        panelControles.add(txtNota);
        panelControles.add(btnAgregarItem);

        // Cambiar la mesa del objeto si el usuario cambia el ComboBox superior
        cbMesas.addActionListener(e -> {
            if (cbMesas.getSelectedItem() != null) {
                int mesaSel = Integer.parseInt((String) cbMesas.getSelectedItem());

                // 1. Instanciamos el mozo correspondiente de la sesión
                Mozo mozoSesion1 = new Mozo(this.idMozoAutenticado, null, null, null, null, "", "", null, null, 0.0);

                // 2. CORREGIDO: Asignamos el nuevo Pedido con su respectiva mesa y mozo objeto
                pedidoEnConstruccion = new Pedido(mesaSel, mozoSesion1);

                // 3. Limpiamos las filas anteriores en la tabla visual de pre-carga
                modeloActual.setRowCount(0); 
            }
        });


        // --- PANEL CENTRAL: DOS TABLAS ---
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 0));

        // Tabla Izquierda (Elementos en Construcción)
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Elementos en este Nuevo Pedido (Pre-carga)"));
        modeloActual = new DefaultTableModel(new String[]{"ID", "Producto", "Cant", "Nota", "Total"}, 0);
        tablaActual = new JTable(modeloActual);
        btnConfirmarPedido = new JButton("ENVIAR PEDIDO");
        btnConfirmarPedido.setBackground(new Color(46, 204, 113));
        btnConfirmarPedido.setForeground(Color.WHITE);
        panelIzquierdo.add(new JScrollPane(tablaActual), BorderLayout.CENTER);
        panelIzquierdo.add(btnConfirmarPedido, BorderLayout.SOUTH);

        // Tabla Derecha (Monitor SQL)
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Monitor de Sectores (Pendientes de entrega)"));
        modeloSeguimiento = new DefaultTableModel(new String[]{"Pedido N°", "Mesa", "Producto", "Sector", "Cant", "Estado", "Renglón"}, 0);
        tablaSeguimiento = new JTable(modeloSeguimiento);
        tablaSeguimiento.removeColumn(tablaSeguimiento.getColumnModel().getColumn(6)); // Ocultamos el Renglón
        
        tablaSeguimiento.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {

                // Ejecuta el comportamiento base de Swing para renderizar la celda
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Obtenemos el estado actual de la fila (está en la columna índice 5 de la JTable)
                String estado = table.getModel().getValueAt(row, 5).toString();

                if (isSelected) {
                    // Si la fila está seleccionada por el mouse, mantiene el color azul estándar de selección
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    // Si el ítem está "Listo", pintamos la fila de un color verde suave muy profesional
                    if (estado.equalsIgnoreCase("Listo")) {
                        c.setBackground(new Color(212, 239, 223)); // Verde pastel (D4EFD5)
                        c.setForeground(new Color(27, 94, 32));    // Texto verde oscuro para buen contraste
                        c.setFont(c.getFont().deriveFont(Font.BOLD)); // Texto en negrita para resaltar
                    } else {
                        // Para los demás estados (como "Enviado" o "En Preparación"), dejamos los colores limpios por defecto
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });
        panelDerecho.add(new JScrollPane(tablaSeguimiento), BorderLayout.CENTER);

        panelTablas.add(panelIzquierdo);
        panelTablas.add(panelDerecho);

        // --- PANEL LATERAL DERECHO: ACCIONES ---
        JPanel panelAcciones = new JPanel(new GridLayout(4, 1, 5, 10));
        panelAcciones.setBorder(BorderFactory.createTitledBorder("Acción Individual"));
        cbEstadosModificar = new JComboBox<>(new String[]{"Entregado", "Cancelado"});
        btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.setBackground(new Color(241, 196, 15));
        
        panelAcciones.add(new JLabel("Cambiar ítem a:"));
        panelAcciones.add(cbEstadosModificar);
        panelAcciones.add(btnCambiarEstado);

        // --- BARRA INFERIOR ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegresar = new JButton("← Regresar al Menú");
        btnRegresar.setBackground(new Color(192, 57, 43));
        btnRegresar.setForeground(Color.WHITE);
        panelInferior.add(btnRegresar);

        add(panelControles, BorderLayout.NORTH);
        add(panelTablas, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        // --- LOGICA Y ACCIONES ---
        this.actualizarTablaSeguimiento();

        // 1. Añadir al detalle (Construyendo objetos fuertemente tipados)
        btnAgregarItem.addActionListener(e -> {
            String prodSeleccionado = (String) cbProductos.getSelectedItem();
            if (prodSeleccionado == null) return;

            // 1. Descomponemos el String del JComboBox como lo hacíamos antes
            String[] partesGuion = prodSeleccionado.split(" - "); 
            int idProd = Integer.parseInt(partesGuion[0]); 

            String[] partesPrecio = partesGuion[1].split(" \\$"); 
            String nombreProd = partesPrecio[0]; 
            double precio = Double.parseDouble(partesPrecio[1]); 

            int cantidad = (int) spinCantidad.getValue();
            String nota = txtNota.getText().trim();

            // 2. CREAMOS EL OBJETO PRODUCTO REQUERIDO (Buscamos o instanciamos con los datos del combo)
            // El sector se resolverá automáticamente al enviarlo o puedes inicializarlo genérico aquí
            ar.restaurante.modelo.Sector sectorDummy = new ar.restaurante.modelo.Sector(0, ""); 
            Producto productoObjeto = new Producto(idProd, nombreProd, sectorDummy, precio, "Disponible");

            // 3. INSTANCIAMOS EL RENGLÓN PASANDO EL OBJETO PRODUCTO (Soluciona el error)
            // Asegúrate de tener este constructor en RegistroPedido: (Producto p, int cant, String nota, double precio)
            RegistroPedido renglon = new RegistroPedido(productoObjeto, cantidad, nota, precio);

            // 4. Agregamos el elemento encapsulado al objeto padre pedido
            pedidoEnConstruccion.agregarRenglon(renglon);

            // 5. Reflejamos en la vista JTable de pre-carga izquierda
            modeloActual.addRow(new Object[]{idProd, nombreProd, cantidad, nota, renglon.getTotal()});
            txtNota.setText("");
        });


        // 2. Guardar definitivamente enviando el objeto Pedido completo
        btnConfirmarPedido.addActionListener(e -> {
            if (pedidoEnConstruccion.getDetalles().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Añada algún artículo a la tabla de pre-carga izquierda primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (pedidoDAO.crearPedido(pedidoEnConstruccion)) {
                JOptionPane.showMessageDialog(this, "¡Pedido registrado con éxito desde el modelo de objetos!");
                
                // Reseteamos el objeto para la siguiente mesa
                int mesaActual = Integer.parseInt((String) cbMesas.getSelectedItem());
                
                // 2. Instanciamos el objeto Mozo requerido pasándole el ID que tenemos autenticado en la sesión
                Mozo mozoSesion2 = new Mozo(
                    this.idMozoAutenticado, null, null, null, null, 
                    "", "", null, null, 0.0
                );
                pedidoEnConstruccion = new Pedido(mesaActual, mozoSesion2);
                
                modeloActual.setRowCount(0);
                actualizarTablaSeguimiento(); 
            }
        });

        // Captura precisa de la selección
        tablaSeguimiento.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaSeguimiento.getSelectedRow();
            if (fila != -1) {
                idPedidoSeleccionado = Integer.parseInt(modeloSeguimiento.getValueAt(fila, 0).toString());
                renglonSeleccionado = Integer.parseInt(modeloSeguimiento.getValueAt(fila, 6).toString());
            }
        });

        // 3. Modificación del renglón
        btnCambiarEstado.addActionListener(e -> {
            if (idPedidoSeleccionado == -1 || renglonSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una fila específica del monitor de la derecha.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String estadoNuevo = (String) cbEstadosModificar.getSelectedItem();
            if (pedidoDAO.actualizarEstadoRenglon(idPedidoSeleccionado, renglonSeleccionado, estadoNuevo)) {
                JOptionPane.showMessageDialog(this, "El renglón se ha marcado como: " + estadoNuevo);
                idPedidoSeleccionado = -1;
                renglonSeleccionado = -1;
                actualizarTablaSeguimiento();
            }
        });

        btnRegresar.addActionListener(e -> this.dispose());
    }

    private void cargarComboProductos() {
        cbProductos.removeAllItems(); // Limpia elementos previos

        // CORREGIDO: Ahora consumimos la lista tipada de objetos Producto
        List<Producto> productos = productoDAO.listarProductos();

        for (Producto p : productos) {
            // Accedemos a las propiedades usando los métodos Getters del objeto Producto
            if (p.getEstado().equalsIgnoreCase("Disponible")) { 
                cbProductos.addItem(p.getIdProducto() + " - " + p.getNombre() + " $" + p.getPrecio());
            }
        }
    }

    
    private void cargarComboMesas() {
        cbMesas.removeAllItems();
        MesaDAO mesaDAO = new MesaDAO(); // Instanciamos el DAO de mesas
        List<Mesa> listaMesas = mesaDAO.listarMesas();

        for (Mesa m : listaMesas) {
            // Añadimos el ID de la mesa de manera dinámica
            cbMesas.addItem(String.valueOf(m.getIdMesa()));
        }
}

    private void actualizarTablaSeguimiento() {
        modeloSeguimiento.setRowCount(0);
        // Ahora consumimos directamente la lista tipada RegistroPedido
        List<RegistroPedido> datos = pedidoDAO.listarSeguimientoPedidos(idMozoAutenticado);

        for (RegistroPedido rp : datos) {
            modeloSeguimiento.addRow(new Object[]{
                rp.getPedido().getNumeroPedido(),
                rp.getPedido().getIdMesa(), 
                rp.getProducto().getNombre(),             // Navegación directa
                rp.getProducto().getSector().getNombreSector(),   // Navegación directa
                rp.getCantidad(),
                rp.getEstado(),
                rp.getRenglon()
            });
        }
    }

}



