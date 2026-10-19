package ar.restaurante.vista;

import ar.restaurante.dao.ProductoDAO;
import ar.restaurante.modelo.Producto;
import ar.restaurante.modelo.Sector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionProductos extends JDialog {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtPrecio;
    private JComboBox<String> cbSector, cbEstado;
    private JButton btnAñadir, btnModificar, btnEliminar, btnLimpiar, btnRegresar;
    private ProductoDAO productoDAO;
    
    private int idSeleccionado = -1; 


    public GestionProductos(Frame padre) {
        super(padre, "Gestión de Productos - Panel de Gerencia", true);
        productoDAO = new ProductoDAO();
        setSize(750, 450);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario (Izquierda) ---
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));

        panelForm.add(new JLabel(" Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel(" Sector:"));
        cbSector = new JComboBox<>(new String[]{"1 - Cocina", "2 - Barra"});
        panelForm.add(cbSector);

        panelForm.add(new JLabel(" Precio ($):"));
        txtPrecio = new JTextField();
        panelForm.add(txtPrecio);

        panelForm.add(new JLabel(" Estado:"));
        cbEstado = new JComboBox<>(new String[]{"Disponible", "No Disponible"});
        panelForm.add(cbEstado);

        // Botones de acción inferiores en el formulario
        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 5, 5));
        btnAñadir = new JButton("Añadir");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        
        panelBotonesForm.add(btnAñadir);
        panelBotonesForm.add(btnModificar);
        panelBotonesForm.add(btnEliminar);
        panelBotonesForm.add(btnLimpiar);
        
        // Contenedor del lado izquierdo (Formulario + Botones de Acción)
        JPanel panelIzquierdoContenedor = new JPanel(new BorderLayout(5, 5));
        panelIzquierdoContenedor.add(panelForm, BorderLayout.CENTER);
        panelIzquierdoContenedor.add(panelBotonesForm, BorderLayout.SOUTH);
        panelIzquierdoContenedor.setPreferredSize(new Dimension(250, 0));
        add(panelIzquierdoContenedor, BorderLayout.WEST);

        // --- Panel de Tabla (Centro / Derecha) ---
        String[] columnas = {"ID", "Nombre", "Sector", "Precio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloTabla);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        
        // --- Barra Inferior (Botón de Regresar) ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegresar = new JButton("← Regresar al Menú");
        btnRegresar.setBackground(new Color(192, 57, 43)); // Estilo color rojo suave/alerta
        btnRegresar.setForeground(Color.WHITE);
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Lógica y Eventos ---
        actualizarTabla();

        // Al hacer clic en una fila de la tabla, se cargan los campos
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaProductos.getSelectedRow();
            if (fila != -1) {
                // Guardamos el ID de forma oculta en la variable de clase
                idSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
                
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                String sector = modeloTabla.getValueAt(fila, 2).toString();
                cbSector.setSelectedIndex(sector.equals("Cocina") ? 0 : 1);
                txtPrecio.setText(modeloTabla.getValueAt(fila, 3).toString());
                cbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
            }
        });
        
         // Evento Regresar
        btnRegresar.addActionListener(e -> {
            this.dispose(); // Cierra esta ventana modal y regresa a la VentanaPrincipal
        });
        
        // Evento añadir
        btnAñadir.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                int idSector = cbSector.getSelectedIndex() + 1;
                String nombreSector = cbSector.getSelectedItem().toString().split(" - ")[1];
                double precio = Double.parseDouble(txtPrecio.getText());
                String estado = cbEstado.getSelectedItem().toString();

                // 1. Instanciamos el objeto Sector compuesto
                Sector sectorObjeto = new Sector(idSector, nombreSector);

                // 2. Instanciamos el objeto Producto (pasamos 0 en el ID ya que es AUTO_INCREMENT en MySQL)
                Producto nuevoProducto = new Producto(0, nombre, sectorObjeto, precio, estado);

                // 3. Enviamos el objeto completo al DAO (Soluciona el error)
                if(productoDAO.insertar(nuevoProducto)) {
                    JOptionPane.showMessageDialog(this, "Producto añadido con éxito.");
                    actualizarTabla();
                    limpiarCampos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Evento Modificar
        btnModificar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para modificar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try {
                String nombre = txtNombre.getText().trim();
                int idSector = cbSector.getSelectedIndex() + 1;
                String nombreSector = cbSector.getSelectedItem().toString().split(" - ")[1];
                double precio = Double.parseDouble(txtPrecio.getText());
                String estado = cbEstado.getSelectedItem().toString();

                // 1. Instanciamos los objetos correspondientes
                Sector sectorObjeto = new Sector(idSector, nombreSector);
                Producto productoModificado = new Producto(idSeleccionado, nombre, sectorObjeto, precio, estado);

                // 2. Enviamos el objeto actualizado al DAO
                if (productoDAO.actualizar(productoModificado)) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado con éxito.");
                    actualizarTabla();
                    limpiarCampos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, revise los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Evento Eliminar
        btnEliminar.addActionListener(e -> {
            if (idSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para eliminar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (productoDAO.eliminar(idSeleccionado)) {
                    actualizarTabla();
                    limpiarCampos();
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Borra filas actuales

        // CORREGIDO: Ahora recuperamos la lista de objetos Producto
        List<Producto> productos = productoDAO.listarProductos();

        for (Producto p : productos) {
            // Extraemos las propiedades de forma segura usando los Getters del objeto
            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombre(),
                p.getSector().getNombreSector(), // Accedemos de forma limpia al nombre del sector compuesto
                p.getPrecio(),
                p.getEstado()
            });
        }
    }


    private void limpiarCampos() {
        idSeleccionado = -1; // Reset de la variable oculta
        txtNombre.setText("");
        txtPrecio.setText("");
        cbSector.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        tablaProductos.clearSelection();
    }
}

