package ar.restaurante.vista;

import ar.restaurante.dao.MesaDAO;
import ar.restaurante.modelo.Mesa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionMesas extends JDialog {
    private JTable tablaMesas;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdMesa;
    private JSpinner spinCapacidad;
    private JComboBox<String> cbEstado, cbZona;
    private JButton btnAñadir, btnModificar, btnEliminar, btnLimpiar, btnRegresar;
    private MesaDAO mesaDAO;
    
    private int idMesaSeleccionada = -1;

    public GestionMesas(Frame padre) {
        super(padre, "Gestión de Mesas - Panel de Gerencia (POO)", true);
        mesaDAO = new MesaDAO();
        setSize(750, 420);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario ---
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la Mesa"));
        
        panelForm.add(new JLabel(" N° Mesa:"));
        txtIdMesa = new JTextField();
        panelForm.add(txtIdMesa);

        panelForm.add(new JLabel(" Capacidad:"));
        spinCapacidad = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        panelForm.add(spinCapacidad);

        panelForm.add(new JLabel(" Zona:"));
        cbZona = new JComboBox<>(new String[]{"Interior", "Exterior"});
        panelForm.add(cbZona);

        panelForm.add(new JLabel(" Estado:"));
        cbEstado = new JComboBox<>(new String[]{"Libre", "Ocupada", "Limpieza requerida"});
        panelForm.add(cbEstado);

        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 5, 5));
        btnAñadir = new JButton("Añadir");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        
        panelBotonesForm.add(btnAñadir);
        panelBotonesForm.add(btnModificar);
        panelBotonesForm.add(btnEliminar);
        panelBotonesForm.add(btnLimpiar);

        JPanel panelIzquierdoContenedor = new JPanel(new BorderLayout(5, 5));
        panelIzquierdoContenedor.add(panelForm, BorderLayout.CENTER);
        panelIzquierdoContenedor.add(panelBotonesForm, BorderLayout.SOUTH);
        panelIzquierdoContenedor.setPreferredSize(new Dimension(260, 0));
        add(panelIzquierdoContenedor, BorderLayout.WEST);

        // --- Panel de Tabla ---
        String[] columnas = {"N° Mesa", "Estado", "Capacidad", "Zona"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMesas = new JTable(modeloTabla);
        add(new JScrollPane(tablaMesas), BorderLayout.CENTER);

        // --- Barra Inferior ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegresar = new JButton("← Regresar al Menú");
        btnRegresar.setBackground(new Color(192, 57, 43));
        btnRegresar.setForeground(Color.WHITE);
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Lógica y Eventos ---
        actualizarTabla();

        tablaMesas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaMesas.getSelectedRow();
            if (fila != -1) {
                idMesaSeleccionada = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
                txtIdMesa.setText(String.valueOf(idMesaSeleccionada));
                txtIdMesa.setEditable(false); 
                
                cbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 1).toString());
                spinCapacidad.setValue(Integer.parseInt(modeloTabla.getValueAt(fila, 2).toString()));
                cbZona.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
            }
        });

        // Acción: Añadir construyendo el Objeto Mesa
        btnAñadir.addActionListener(e -> {
            try {
                int idMesa = Integer.parseInt(txtIdMesa.getText().trim());
                String estado = cbEstado.getSelectedItem().toString();
                int capacidad = (int) spinCapacidad.getValue();
                String zona = cbZona.getSelectedItem().toString();
                
                // Instanciamos el objeto de manera limpia
                Mesa nuevaMesa = new Mesa(idMesa, estado, capacidad, zona);
                
                if (mesaDAO.insertar(nuevaMesa)) {
                    JOptionPane.showMessageDialog(this, "Mesa añadida con éxito.");
                    actualizarTabla();
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "Número de mesa ya existente en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Formato de número inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción: Modificar inyectando el Objeto Mesa modificado
        btnModificar.addActionListener(e -> {
            if (idMesaSeleccionada == -1){
                JOptionPane.showMessageDialog(this, "Seleccione una mesa de la tabla", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String estado = cbEstado.getSelectedItem().toString();
            int capacidad = (int) spinCapacidad.getValue();
            String zona = cbZona.getSelectedItem().toString();

            // Creamos la cápsula con los cambios
            Mesa mesaModificada = new Mesa(idMesaSeleccionada, estado, capacidad, zona);

            if (mesaDAO.actualizar(mesaModificada)) {
                JOptionPane.showMessageDialog(this, "Mesa modificada con éxito.");
                actualizarTabla();
                limpiarCampos();
            }
        });

        btnEliminar.addActionListener(e -> {
            if (idMesaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una mesa de la tabla", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 1. Validación previa: Preguntamos a MySQL si la mesa está vinculada a algún pedido
            if (mesaDAO.tienePedidosAsociados(idMesaSeleccionada)) {
                JOptionPane.showMessageDialog(this, 
                    "No se puede eliminar la mesa porque tiene pedidos activos o históricos asociados en el sistema.", 
                    "Operación No Permitida", 
                    JOptionPane.WARNING_MESSAGE);
                return; // Detiene la ejecución para que no intente borrarla
            }

            // 2. Si no tiene pedidos, procedemos con la confirmación normal
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Seguro que desea eliminar la Mesa " + idMesaSeleccionada + "?", 
                "Confirmar", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (mesaDAO.eliminar(idMesaSeleccionada)) {
                    JOptionPane.showMessageDialog(this, "Mesa eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTabla();
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar la mesa.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnRegresar.addActionListener(e -> this.dispose());
    }

    // Descomprime de manera limpia las propiedades de cada objeto Mesa recuperado de MySQL
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Mesa> listaMesas = mesaDAO.listarMesas();
        for (Mesa m : listaMesas) {
            modeloTabla.addRow(new Object[]{
                m.getIdMesa(),
                m.getEstado(),
                m.getCapacidad(),
                m.getZona()
            });
        }
    }

    private void limpiarCampos() {
        idMesaSeleccionada = -1;
        txtIdMesa.setText("");
        txtIdMesa.setEditable(true);
        spinCapacidad.setValue(4);
        cbEstado.setSelectedIndex(0);
        cbZona.setSelectedIndex(0);
        tablaMesas.clearSelection();
    }
}

