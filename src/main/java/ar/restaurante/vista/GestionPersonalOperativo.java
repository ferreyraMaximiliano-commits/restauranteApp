package ar.restaurante.vista;

import ar.restaurante.dao.PersonalOperativoDAO;
import ar.restaurante.modelo.PersonalOperativo;
import ar.restaurante.modelo.RegistroPedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionPersonalOperativo extends JDialog {
    private JTable tablaComandas;
    private DefaultTableModel modeloComandas;
    private JButton btnPreparar, btnListo, btnRefrescar, btnRegresar;
    private PersonalOperativoDAO operativoDAO;
    private PersonalOperativo empleadoOperativo;

    private int idPedidoSeleccionado = -1;
    private int renglonSeleccionado = -1;

    public GestionPersonalOperativo(Frame padre, PersonalOperativo empleado) {
        super(padre, "Monitor de Producción - POO", true);
        this.operativoDAO = new PersonalOperativoDAO();
        this.empleadoOperativo = empleado;

        setTitle("Monitor de Producción - Sector: " + empleadoOperativo.getSector().getNombreSector());
        setSize(850, 450);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Encabezado ---
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTitulo = new JLabel("Comandas de " + empleadoOperativo.getSector().getNombreSector() + " | Operario: " + empleadoOperativo.getNombre());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // --- Tabla ---
        String[] columnas = {"Pedido N°", "Mesa", "Item / Producto", "Cantidad", "Notas", "Estado", "Renglón"};
        modeloComandas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaComandas = new JTable(modeloComandas);
        tablaComandas.setRowHeight(25);
        tablaComandas.removeColumn(tablaComandas.getColumnModel().getColumn(6)); // Oculto columna técnica
        add(new JScrollPane(tablaComandas), BorderLayout.CENTER);

        // --- Panel de Acciones (Derecha) ---
        JPanel panelAcciones = new JPanel(new GridLayout(4, 1, 10, 10));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPreparar = new JButton("En Preparación");
        btnPreparar.setBackground(new Color(230, 126, 34)); // Naranja
        btnPreparar.setForeground(Color.WHITE);

        btnListo = new JButton("Marcar como Listo");
        btnListo.setBackground(new Color(46, 204, 113)); // Verde
        btnListo.setForeground(Color.WHITE);

        btnRefrescar = new JButton("Actualizar");
        btnRefrescar.setBackground(new Color(52, 152, 219));
        btnRefrescar.setForeground(Color.WHITE);

        panelAcciones.add(btnPreparar);
        panelAcciones.add(btnListo);
        panelAcciones.add(new JSeparator());
        panelAcciones.add(btnRefrescar);
        add(panelAcciones, BorderLayout.EAST);

        // --- Barra Inferior ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(new Color(192, 57, 43));
        btnRegresar.setForeground(Color.WHITE);
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Eventos ---
        actualizarMonitor();

        tablaComandas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaComandas.getSelectedRow();
            if (fila != -1) {
                idPedidoSeleccionado = Integer.parseInt(modeloComandas.getValueAt(fila, 0).toString());
                renglonSeleccionado = Integer.parseInt(modeloComandas.getValueAt(fila, 6).toString());
            }
        });

        btnPreparar.addActionListener(e -> cambiarEstadoSeleccionado("En Preparación"));
        btnListo.addActionListener(e -> cambiarEstadoSeleccionado("Listo"));
        btnRefrescar.addActionListener(e -> actualizarMonitor());
        btnRegresar.addActionListener(e -> this.dispose());
    }

    private void cambiarEstadoSeleccionado(String estado) {
        if (idPedidoSeleccionado == -1 || renglonSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un elemento de la lista.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (operativoDAO.actualizarEstadoProduccion(idPedidoSeleccionado, renglonSeleccionado, estado)) {
            idPedidoSeleccionado = -1;
            renglonSeleccionado = -1;
            actualizarMonitor();
        }
    }

    // --- Modifica este método dentro de GestionPersonalOperativoDialog.java ---
    private void actualizarMonitor() {
        modeloComandas.setRowCount(0); // Borra filas actuales

        // Consumimos la nueva lista puramente Orientada a Objetos
        List<RegistroPedido> comandas = operativoDAO.listarPedidosPorSector(empleadoOperativo.getSector().getIdSector());

        for (RegistroPedido rp : comandas) {
            // Extraemos las propiedades de forma segura navegando a través de los objetos
            modeloComandas.addRow(new Object[]{
                rp.getPedido().getNumeroPedido(),
                "Mesa " + rp.getPedido().getIdMesa(),
                rp.getProducto().getNombre(),
                rp.getCantidad(),
                rp.getNotaExtra(),
                rp.getEstado(),  // Estado actual ("Enviado" o "En Preparación")
                rp.getRenglon()  // El renglón técnico que se mantiene oculto para poder operar
            });
        }
    }
}

