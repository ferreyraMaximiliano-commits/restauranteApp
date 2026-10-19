package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Mesa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {
    //Permite insertar, modificar, leer, borrar mesas, ademas de válidar si alguna tiene pedidos asociados
    
    // Metodo para listar las mesas
    public List<Mesa> listarMesas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT id_mesa, estado, capacidad, zona FROM mesas ORDER BY id_mesa ASC";
        
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Mesa(
                    rs.getInt("id_mesa"),
                    rs.getString("estado"),
                    rs.getInt("capacidad"),
                    rs.getString("zona")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo para añadir una mesa (recibe los datos completos desde GestionMesas)
    public boolean insertar(Mesa mesa) {
        String sql = "INSERT INTO mesas (id_mesa, estado, capacidad, zona) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mesa.getIdMesa());
            ps.setString(2, mesa.getEstado());
            ps.setInt(3, mesa.getCapacidad());
            ps.setString(4, mesa.getZona());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo para modificar atributos de una mesa (recibe los datos a modificar desde GestionMesas)
    public boolean actualizar(Mesa mesa) {
        String sql = "UPDATE mesas SET estado = ?, capacidad = ?, zona = ? WHERE id_mesa = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mesa.getEstado());
            ps.setInt(2, mesa.getCapacidad());
            ps.setString(3, mesa.getZona());
            ps.setInt(4, mesa.getIdMesa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo para eliminar mesas
    public boolean eliminar(int idMesa) {
        String sql = "DELETE FROM mesas WHERE id_mesa = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Metodo que verifica si una mesa tiene pedidos asociados (historicamente)
    public boolean tienePedidosAsociados(int idMesa) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE id_mesa = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

