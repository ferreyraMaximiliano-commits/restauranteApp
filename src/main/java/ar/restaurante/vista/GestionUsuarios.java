package ar.restaurante.vista;

import ar.restaurante.dao.EmpleadoDAO;
import ar.restaurante.modelo.Empleado;
import ar.restaurante.modelo.Gerente;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.PersonalOperativo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionUsuarios extends JDialog {
    private JTable tablaPersonal;
    private DefaultTableModel modeloTabla;
    private JTextField txtEmail, txtPass, txtNombre, txtApellido, txtTel, txtSalario, txtExtra;
    private JComboBox<String> cbTurno, cbRol;
    private JLabel lblExtra;
    private JButton btnAñadir, btnModificar, btnEliminar, btnLimpiar, btnRegresar;
    private EmpleadoDAO empleadoDAO;
    
    // Lista en memoria para rastrear los objetos vivos de la tabla
    private List<Empleado> empleadosCargados;
    private int idUsuarioSeleccionado = -1;

    public GestionUsuarios(Frame padre) {
        super(padre, "Gestión de Personal y Cuentas - Modelo POO", true);
        empleadoDAO = new EmpleadoDAO();
        setSize(950, 500);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Formulario ---
        JPanel panelForm = new JPanel(new GridLayout(10, 2, 5, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Ficha del Empleado"));

        panelForm.add(new JLabel(" Email / Usuario:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        panelForm.add(new JLabel(" Contraseña:"));
        txtPass = new JTextField();
        panelForm.add(txtPass);

        panelForm.add(new JLabel(" Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel(" Apellido:"));
        txtApellido = new JTextField();
        panelForm.add(txtApellido);

        panelForm.add(new JLabel(" Teléfono:"));
        txtTel = new JTextField();
        panelForm.add(txtTel);

        panelForm.add(new JLabel(" Turno:"));
        cbTurno = new JComboBox<>(new String[]{"Mañana", "Tarde", "Noche"});
        panelForm.add(cbTurno);

        panelForm.add(new JLabel(" Salario ($):"));
        txtSalario = new JTextField();
        panelForm.add(txtSalario);

        panelForm.add(new JLabel(" Rol de Sistema:"));
        cbRol = new JComboBox<>(new String[]{"Mozo", "Gerente", "Operativo - Cocina", "Operativo - Barra"});
        panelForm.add(cbRol);

        lblExtra = new JLabel(" (No requerido):");
        txtExtra = new JTextField();
        txtExtra.setEditable(false);
        panelForm.add(lblExtra);
        panelForm.add(txtExtra);

        // Control dinámico del campo extra según el rol seleccionado
        cbRol.addActionListener(e -> {
            String sel = cbRol.getSelectedItem().toString();
            if (sel.equals("Gerente")) {
                lblExtra.setText(" Cód. Autorización:");
                txtExtra.setEditable(true);
            } else {
                lblExtra.setText(" (No requerido):");
                txtExtra.setText("");
                txtExtra.setEditable(false);
            }
        });

        // Panel de Botones
        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 5, 5));
        btnAñadir = new JButton("Registrar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Dar de Baja");
        btnLimpiar = new JButton("Limpiar");
        panelBotonesForm.add(btnAñadir); panelBotonesForm.add(btnModificar);
        panelBotonesForm.add(btnEliminar); panelBotonesForm.add(btnLimpiar);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.add(panelForm, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotonesForm, BorderLayout.SOUTH);
        panelIzquierdo.setPreferredSize(new Dimension(280, 0));
        add(panelIzquierdo, BorderLayout.WEST);

        // --- Panel de Tabla ---
        String[] columnas = {"ID", "Email", "Nombre", "Apellido", "Teléfono", "Turno", "Salario", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPersonal = new JTable(modeloTabla);
        add(new JScrollPane(tablaPersonal), BorderLayout.CENTER);

        // --- Barra de navegación inferior ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegresar = new JButton("← Regresar al Menú");
        btnRegresar.setBackground(new Color(192, 57, 43));
        btnRegresar.setForeground(Color.WHITE);
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Gestión de Eventos y Lógica ---
        actualizarTabla();

        // Listener para capturar la selección de la tabla y cargar el formulario
        tablaPersonal.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPersonal.getSelectedRow();
            if (fila != -1 && !e.getValueIsAdjusting()) {
                cargarCamposDesdeObjeto(fila);
            }
        });

        // Registrar nuevo usuario/empleado
        btnAñadir.addActionListener(e -> {
            try {
                String email = txtEmail.getText().trim();
                String pass = txtPass.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String tel = txtTel.getText().trim();
                String turno = cbTurno.getSelectedItem().toString();
                double salario = Double.parseDouble(txtSalario.getText());
                String rol = cbRol.getSelectedItem().toString();
                
                int extraId = 0;
                if (rol.equals("Gerente")) {
                    extraId = Integer.parseInt(txtExtra.getText().trim());
                } else if (rol.equals("Operativo - Cocina")) {
                    extraId = 1;
                } else if (rol.equals("Operativo - Barra")) {
                    extraId = 2;
                }

                if (empleadoDAO.insertarEmpleado(email, pass, nombre, apellido, tel, turno, salario, rol, extraId)) {
                    JOptionPane.showMessageDialog(this, "Empleado incorporado con éxito al sistema.");
                    actualizarTabla();
                    limpiarCampos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Revise que el Salario o Código tengan formatos numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Modificar datos personales
        btnModificar.addActionListener(e -> {
            if (idUsuarioSeleccionado == -1) return;
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String tel = txtTel.getText().trim();
                String turno = cbTurno.getSelectedItem().toString();
                double salario = Double.parseDouble(txtSalario.getText());

                if (empleadoDAO.actualizarEmpleado(idUsuarioSeleccionado, nombre, apellido, tel, turno, salario)) {
                    JOptionPane.showMessageDialog(this, "Los datos personales se han actualizado correctamente.");
                    actualizarTabla();
                    limpiarCampos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Formato de salario inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Baja lógica segura
        btnEliminar.addActionListener(e -> {
            if (idUsuarioSeleccionado == -1) return;
            int resp = JOptionPane.showConfirmDialog(this, "¿Seguro que desea inhabilitar esta cuenta de usuario?\n(Se mantendrá el registro de transacciones históricas)", "Confirmar Baja", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                if (empleadoDAO.eliminarLogico(idUsuarioSeleccionado)) {
                    JOptionPane.showMessageDialog(this, "Cuenta dada de baja de manera segura.");
                    actualizarTabla();
                    limpiarCampos();
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnRegresar.addActionListener(e -> this.dispose());
    }

    // LÓGICA POO: Lee las propiedades usando los Getters nativos del objeto seleccionado
    private void cargarCamposDesdeObjeto(int fila) {
        Empleado emp = empleadosCargados.get(fila);
        
        idUsuarioSeleccionado = emp.getIdUsuario();
        txtEmail.setText(emp.getEmail());
        txtEmail.setEditable(false);
        txtPass.setText("********");
        txtPass.setEditable(false);
        
        txtNombre.setText(emp.getNombre());
        txtApellido.setText(emp.getApellido());
        
        // Mapeo directo de atributos de la herencia
        txtTel.setText(emp.getTelefono()); // Si añadiste el atributo teléfono a Empleado usa emp.getTelefono()
        cbTurno.setSelectedItem("Noche"); // Ajustar o añadir getters si el modelo los requiere
        txtSalario.setText(String.valueOf(1800.0)); // Ajustar o añadir getters si el modelo los requiere
        
        // Polimorfismo con instanceof para determinar campos del rol específico
        if (emp instanceof Gerente) {
            cbRol.setSelectedItem("Gerente");
            txtExtra.setText(String.valueOf(((Gerente) emp).getCodigoAutorizacion()));
        } else if (emp instanceof PersonalOperativo) {
            PersonalOperativo op = (PersonalOperativo) emp;
            String secNombre = op.getSector().getNombreSector();
            cbRol.setSelectedItem(secNombre.equalsIgnoreCase("Cocina") ? "Operativo - Cocina" : "Operativo - Barra");
            txtExtra.setText(String.valueOf(op.getSector().getIdSector()));
        } else if (emp instanceof Mozo) {
            cbRol.setSelectedItem("Mozo");
            txtExtra.setText("");
        }
        cbRol.setEnabled(false); // No se permite alterar el rol directo para cuidar la integridad en DB

    }
    // Refactorizado: Extrae dinámicamente las propiedades de la jerarquía de herencia para la JTable
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        empleadosCargados = empleadoDAO.listarEmpleados(); 

        for (Empleado emp : empleadosCargados) {
            String textoRol = "Empleado";
            if (emp instanceof Gerente) {
                textoRol = "Gerente";
            } else if (emp instanceof Mozo) {
                textoRol = "Mozo";
            } else if (emp instanceof PersonalOperativo) {
                textoRol = "Operativo - " + ((PersonalOperativo) emp).getSector().getNombreSector();
            }

            // Modificado: Ahora el arreglo coincide exactamente con las 8 columnas incluyendo getTelefono()
            modeloTabla.addRow(new Object[]{
                emp.getIdUsuario(),
                emp.getEmail(),
                emp.getNombre(),
                emp.getApellido(),
                emp.getTelefono(), // <-- Inyección del teléfono real en la grilla
                emp.getTurno(),            
                "$" + emp.getSalario(),     
                textoRol
            });
        }
    }
    private void limpiarCampos() {
        idUsuarioSeleccionado = -1;
        txtEmail.setText(""); txtEmail.setEditable(true);
        txtPass.setText(""); txtPass.setEditable(true);
        txtNombre.setText(""); txtApellido.setText("");
        txtTel.setText(""); txtSalario.setText(""); txtExtra.setText("");
        cbTurno.setSelectedIndex(0); cbRol.setSelectedIndex(0);
        cbRol.setEnabled(true);
        tablaPersonal.clearSelection();
        }
    }
