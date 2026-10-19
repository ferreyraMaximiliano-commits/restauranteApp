package ar.restaurante.vista;

import ar.restaurante.modelo.Empleado;
import ar.restaurante.modelo.Gerente;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.PersonalOperativo;
import ar.restaurante.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(Usuario usuarioAutenticado) {
        setTitle("Sistema de Gestión - Restaurante");
        setSize(550, 350); // Ajustamos el tamaño para acomodar los nuevos botones
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR DE INFORMACIÓN Y LOGOUT ---
        JPanel panelHeader = new JPanel(new BorderLayout(10, 10));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelHeader.setBackground(new Color(245, 247, 250)); 

        JPanel panelInfoTextos = new JPanel(new GridLayout(3, 1, 2, 2));
        panelInfoTextos.setOpaque(false);
        
        JLabel lblBienvenida = new JLabel("¡Bienvenido al Sistema!");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblEmail = new JLabel("Cuenta: " + usuarioAutenticado.getEmail());
        JLabel lblRol = new JLabel();
        lblRol.setFont(new Font("Arial", Font.ITALIC, 14));
        
        panelInfoTextos.add(lblBienvenida);
        panelInfoTextos.add(lblEmail);
        panelInfoTextos.add(lblRol);

        // BOTÓN DE CERRAR SESIÓN
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(231, 76, 60)); 
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrarSesion.setFocusable(false);

        btnCerrarSesion.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Seguro que desea cerrar sesión?", "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
            if (respuesta == JOptionPane.YES_OPTION) {
                this.dispose(); 
                SwingUtilities.invokeLater(() -> {
                    new VentanaLogin().setVisible(true);
                });
            }
        });

        panelHeader.add(panelInfoTextos, BorderLayout.CENTER);
        panelHeader.add(btnCerrarSesion, BorderLayout.EAST);

        // --- PANEL CENTRAL CON ACCIONES SEGÚN EL ROL ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // --- EVALUACIÓN DE ROLES (POLIMORFISMO DE OBJETOS) ---
        if (usuarioAutenticado instanceof Gerente) {
            Gerente gerente = (Gerente) usuarioAutenticado;
            lblRol.setText("Rol: Gerente (Cód. Aut: " + gerente.getCodigoAutorizacion() + ")");
            
            // 1. Botón Gestionar Productos
            JButton btnGestionProductos = new JButton("Gestionar Productos");
            btnGestionProductos.setBackground(new Color(41, 128, 185));
            btnGestionProductos.setForeground(Color.WHITE);
            btnGestionProductos.addActionListener(evt -> new GestionProductos(this).setVisible(true));

            // 2. Botón Gestionar Mesas
            JButton btnGestionMesas = new JButton("Gestionar Mesas");
            btnGestionMesas.setBackground(new Color(142, 68, 173)); 
            btnGestionMesas.setForeground(Color.WHITE);
            btnGestionMesas.addActionListener(evt -> new GestionMesas(this).setVisible(true));

            // 3. Botón Gestionar Usuarios
            JButton btnGestionUsuarios = new JButton("Gestionar Usuarios");
            btnGestionUsuarios.setBackground(new Color(22, 160, 133)); // Color verde esmeralda
            btnGestionUsuarios.setForeground(Color.WHITE);
            btnGestionUsuarios.addActionListener(evt -> new GestionUsuarios(this).setVisible(true));
            
            // 4. Boton Historial Pedidos
            JButton btnHistorial = new JButton("Historial de Pedidos");
            btnHistorial.setBackground(new Color(211, 84, 0)); // Color naranja oscuro de archivo
            btnHistorial.setForeground(Color.WHITE);
            btnHistorial.addActionListener(evt -> new VentanaHistorial(this).setVisible(true));

            // Agregar botones al panel del Gerente
            panelAcciones.add(btnGestionProductos);
            panelAcciones.add(btnGestionMesas);
            panelAcciones.add(btnGestionUsuarios);
            panelAcciones.add(btnHistorial);
            
            
        } else if (usuarioAutenticado instanceof Mozo) {
            Mozo mozo = (Mozo) usuarioAutenticado;
            lblRol.setText("Rol: Mozo (Empleado: " + mozo.getNombre() + " " + mozo.getApellido() + ")");
            
            // Botón gestion de pedidos
            JButton btnGestionPedidos = new JButton("Tomar / Seguir Pedidos");
            btnGestionPedidos.setBackground(new Color(39, 174, 96));
            btnGestionPedidos.setForeground(Color.WHITE);
            btnGestionPedidos.addActionListener(evt -> new GestionPedidosMozo(this, mozo.getIdUsuario()).setVisible(true));

            panelAcciones.add(btnGestionPedidos);
            
        } else if (usuarioAutenticado instanceof PersonalOperativo) {
            PersonalOperativo operativo = (PersonalOperativo) usuarioAutenticado;
            lblRol.setText("Rol: Personal Operativo (" + operativo.getSector().getNombreSector() + ")");
            
            JButton btnAbrirMonitor = new JButton("Abrir Monitor de Comandas");
            btnAbrirMonitor.setBackground(new Color(52, 152, 219));
            btnAbrirMonitor.setForeground(Color.WHITE);
            btnAbrirMonitor.addActionListener(evt -> new GestionPersonalOperativo(this, operativo).setVisible(true));

            panelAcciones.add(btnAbrirMonitor);
            
        } else if (usuarioAutenticado instanceof Empleado) {
            Empleado emp = (Empleado) usuarioAutenticado;
            lblRol.setText("Rol: Empleado Genérico (" + emp.getNombre() + ")");
        } else {
            lblRol.setText("Rol: Usuario Base");
        }

        // Unificar Layout de la ventana
        add(panelHeader, BorderLayout.NORTH);
        add(panelAcciones, BorderLayout.CENTER);
    }
}



