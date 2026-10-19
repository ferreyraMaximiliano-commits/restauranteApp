package ar.restaurante.vista;

import ar.restaurante.dao.HistorialDAO;
import ar.restaurante.modelo.RegistroPedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaHistorial extends JDialog {
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbFiltroEstado;
    private HistorialDAO historialDAO;

    public VentanaHistorial(Frame padre) {
        super(padre, "Historial General de Pedidos y Auditoría", true);
        historialDAO = new HistorialDAO();

        setSize(900, 500);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Filtros de Clasificación ---
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Clasificar y Filtrar Comandas"));
        
        cbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "Enviado", "En Preparación", "Listo", "Entregado", "Cancelado"
        });
        cbFiltroEstado.setPreferredSize(new Dimension(180, 25));
        
        panelFiltro.add(new JLabel("Clasificar por Estado:"));
        panelFiltro.add(cbFiltroEstado);
        add(panelFiltro, BorderLayout.NORTH);

        // --- Panel Central: Tabla Informativa ---
        String[] columnas = {"Pedido N°", "Fecha", "Mesa", "Mozo Atendió", "Producto", "Cant.", "Total Renglón", "Estado Actual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(22);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        // --- Barra Inferior: Navegación ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegresar = new JButton("← Regresar al Menú");
        btnRegresar.setBackground(new Color(192, 57, 43));
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.addActionListener(e -> this.dispose());
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Eventos e Inicialización ---
        actualizarHistorial("Todos"); // Carga completa inicial

        // Listener interactivo del JComboBox
        cbFiltroEstado.addActionListener(e -> {
            String estadoSeleccionado = cbFiltroEstado.getSelectedItem().toString();
            actualizarHistorial(estadoSeleccionado);
        });
    }

    private void actualizarHistorial(String estado) {
    modeloTabla.setRowCount(0); // Limpia filas actuales
    
    // Consumimos la nueva lista puramente Orientada a Objetos
    List<RegistroPedido> registros = historialDAO.obtenerHistorialPedidos(estado); 
    
    for (RegistroPedido rp : registros) {
        // Obtenemos los nombres navegando de forma segura a través de los objetos compuestos
        String nombreCompletoMozo = rp.getPedido().getMozo().getNombre() + " " + rp.getPedido().getMozo().getApellido(); // O inyectar mozo directamente si modificaste el modelo
        
        modeloTabla.addRow(new Object[]{
            rp.getPedido().getNumeroPedido(),
            rp.getPedido().getFecha().toString(),
            "Mesa " + rp.getPedido().getIdMesa(),
            nombreCompletoMozo, // O el nombre del mozo si agregaste la relación en Pedido
            rp.getProducto().getNombre(),
            rp.getCantidad(),
            "$" + rp.getTotal(),
            rp.getEstado()
        });
    }
}

}
