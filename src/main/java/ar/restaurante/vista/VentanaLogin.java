package ar.restaurante.vista;

import ar.restaurante.dao.LoginDAO;
import ar.restaurante.modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaLogin extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private LoginDAO loginDAO;

    public VentanaLogin() {
        loginDAO = new LoginDAO();

        // Configuración de la Ventana
        setTitle("Inicio de Sesión");
        setSize(320, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana
        setResizable(false);

        // Panel Principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Componentes del Formulario
        JLabel lblEmail = new JLabel("Correo Electrónico:");
        txtEmail = new JTextField();

        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField();

        btnIngresar = new JButton("Ingresar");

        // Agregar al layout
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(new JLabel()); // Espacio vacío
        panel.add(btnIngresar);

        add(panel);

        // Evento del Botón Ingresar
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarLogin();
            }
        });
    }

    private void ejecutarLogin() {
        String email = txtEmail.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llama al método polimórfico de la entrega anterior
        Usuario usuario = loginDAO.verificarCredenciales(email, contrasena);

        if (usuario != null) {
            // Cierra la ventana actual de login
            this.dispose(); 
            
            // Abre la nueva ventana pasando el objeto de forma polimórfica
            SwingUtilities.invokeLater(() -> {
                new VentanaPrincipal(usuario).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Usuario, contraseña incorrectos o cuenta inactiva.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}

