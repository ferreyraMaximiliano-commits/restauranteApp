package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Gerente;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.PersonalOperativo;
import ar.restaurante.modelo.Sector;
import ar.restaurante.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoginDAO {
    //Permite verificar las credenciales de un usuario y comprobar su rol dentro del sistema
    
    public Usuario verificarCredenciales(String email, String contrasena) {
        String sql = "SELECT u.id_usuario, u.fecha_registro, u.estado AS estado_usuario, " +
                     "       e.nombre, e.apellido, e.telefono, e.turno, e.salario, " +
                     "       g.codigo_autorizacion, " +
                     "       p.id_sector, " +
                     "       m.id_usuario AS es_mozo " +
                     "FROM usuarios u " +
                     "LEFT JOIN empleados e ON u.id_usuario = e.id_usuario " +
                     "LEFT JOIN gerentes g ON e.id_usuario = g.id_usuario " +
                     "LEFT JOIN personalOperativo p ON e.id_usuario = p.id_usuario " +
                     "LEFT JOIN mozos m ON e.id_usuario = m.id_usuario " +
                     "WHERE u.email = ? AND u.contrasena = ? AND u.estado = 'Activo'";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, contrasena);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Datos comunes de Usuario
                    int id = rs.getInt("id_usuario");
                    LocalDate fechaReg = rs.getDate("fecha_registro").toLocalDate();
                    String estado = rs.getString("estado_usuario");
                    
                    // Datos comunes de Empleado
                    String nombre = rs.getString("nombre");
                    
                    // Si el nombre no es nulo, significa que el usuario también es Empleado
                    if (nombre != null) {
                        String apellido = rs.getString("apellido");
                        String telefono = rs.getString("telefono");
                        String turno = rs.getString("turno");
                        double salario = rs.getDouble("salario");

                        // Comprobar si es gerente
                        int codigoAut = rs.getInt("codigo_autorizacion");
                        if (!rs.wasNull()) { // Verifica si el entero obtenido no era NULL en la base de datos
                            return new Gerente(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario, codigoAut);
                        }

                        // Comprobar si es mozo
                        rs.getInt("es_mozo");
                        if (!rs.wasNull()) {
                            return new Mozo(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario);
                        }

                        // Comprobar si es personal operativo
                        int idSector = rs.getInt("id_sector");
                        if (!rs.wasNull()) {
                            String nombreSector = (idSector == 1) ? "Cocina" : "Barra"; 
                            Sector sectorObjeto = new Sector(idSector, nombreSector);
                            
                            return new PersonalOperativo(id, email, contrasena, fechaReg, estado, nombre, apellido, telefono, turno, salario, sectorObjeto);
                        }

                        
                    }
                    
                    // Si es usuario pero no empleado
                    return null;
                    //return new Usuario(id, email, contrasena, fechaReg, estado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en el inicio de sesión: " + e.getMessage());
        }
        return null; // Credenciales inválidas o usuario inactivo
    }
}
