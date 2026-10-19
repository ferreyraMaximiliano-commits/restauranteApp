package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Empleado;
import ar.restaurante.modelo.Gerente;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.PersonalOperativo;
import ar.restaurante.modelo.Sector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class EmpleadoDAO {
    // Permite registrar nuevos empleados, modificar sus datos y darlos de baja
    
    // Listar Empleados
    public List<Empleado> listarEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.email, u.contrasena, u.fecha_registro, u.estado AS estado_usuario, " +
                     "       e.nombre, e.apellido, e.telefono, e.turno, e.salario, " +
                     "       g.codigo_autorizacion, " +
                     "       p.id_sector, s.nombre AS nombre_sector " +
                     "FROM usuarios u " +
                     "INNER JOIN empleados e ON u.id_usuario = e.id_usuario " +
                     "LEFT JOIN gerentes g ON e.id_usuario = g.id_usuario " +
                     "LEFT JOIN mozos m ON e.id_usuario = m.id_usuario " +
                     "LEFT JOIN personalOperativo p ON e.id_usuario = p.id_usuario " +
                     "LEFT JOIN sector s ON p.id_sector = s.id_sector " +
                     "WHERE u.estado = 'Activo' " +
                     "ORDER BY u.id_usuario ASC";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String email = rs.getString("email");
                String contrasena = rs.getString("contrasena");
                LocalDate fechaReg = rs.getDate("fecha_registro").toLocalDate();
                String estado = rs.getString("estado_usuario");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String telefono = rs.getString("telefono");
                String turno = rs.getString("turno");
                double salario = rs.getDouble("salario");

                // Comprobar si es gerente
                int codigoAut = rs.getInt("codigo_autorizacion");
                if (!rs.wasNull()) {
                    lista.add(new Gerente(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario, codigoAut));
                    continue;
                }

                // Comprobar si es personal operativo
                int idSector = rs.getInt("id_sector");
                if (!rs.wasNull()) {
                    Sector sector = new Sector(idSector, rs.getString("nombre_sector"));
                    lista.add(new PersonalOperativo(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario, sector));
                    continue;
                }

                // Si no es ninguno de los anteriores, se asume que es mozo
                lista.add(new Mozo(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    

    // Registrar empleado
    public boolean insertarEmpleado(String email, String pass, String nombre, String apellido, 
                                           String tel, String turno, double salario, String rol, int extraId) {
        String sqlUser = "INSERT INTO usuarios (email, contrasena, fecha_registro, estado) VALUES (?, ?, CURDATE(), 'Activo')";
        String sqlEmp = "INSERT INTO empleados (id_usuario, nombre, apellido, telefono, turno, salario) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false);

            int idUsuario = -1;
            try (PreparedStatement psU = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psU.setString(1, email);
                psU.setString(2, pass);
                psU.executeUpdate();
                try (ResultSet rs = psU.getGeneratedKeys()) {
                    if (rs.next()) idUsuario = rs.getInt(1);
                }
            }

            if (idUsuario != -1) {
                try (PreparedStatement psE = con.prepareStatement(sqlEmp)) {
                    psE.setInt(1, idUsuario);
                    psE.setString(2, nombre);
                    psE.setString(3, apellido);
                    psE.setString(4, tel);
                    psE.setString(5, turno);
                    psE.setDouble(6, salario);
                    psE.executeUpdate();
                }

                String sqlRol = "";
                if (rol.equals("Gerente")) {
                    sqlRol = "INSERT INTO gerentes (id_usuario, codigo_autorizacion) VALUES (?, ?)";
                } else if (rol.equals("Mozo")) {
                    sqlRol = "INSERT INTO mozos (id_usuario) VALUES (?)";
                } else {
                    sqlRol = "INSERT INTO personalOperativo (id_usuario, id_sector) VALUES (?, ?)";
                }

                try (PreparedStatement psR = con.prepareStatement(sqlRol)) {
                    psR.setInt(1, idUsuario);
                    if (!rol.equals("Mozo")) {
                        psR.setInt(2, extraId);
                    }
                    psR.executeUpdate();
                }

                con.commit();
                return true;
            }
        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
        } finally {
            if (con != null) { try { con.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        return false;
    }

    public boolean actualizarEmpleado(int idUsuario, String nombre, String apellido, String tel, String turno, double salario) {
        String sql = "UPDATE empleados SET nombre = ?, apellido = ?, telefono = ?, turno = ?, salario = ? WHERE id_usuario = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, tel);
            ps.setString(4, turno);
            ps.setDouble(5, salario);
            ps.setInt(6, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarLogico(int idUsuario) {
        String sql = "UPDATE usuarios SET estado = 'Inactivo' WHERE id_usuario = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

