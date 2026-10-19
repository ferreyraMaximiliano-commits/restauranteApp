package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Producto;
import ar.restaurante.modelo.Sector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    // Permite insertar, modificar, leer y borrar productos

    // Metodo para listar productos
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.id_sector, s.nombre AS nombre_sector, p.precio, p.estado " +
                    "FROM productos p " +
                    "INNER JOIN sector s ON p.id_sector = s.id_sector " +
                    "WHERE p.estado = 'Disponible'"; 
        
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Sector sector = new Sector(
                    rs.getInt("id_sector"),
                    rs.getString("nombre_sector")
                );

                Producto producto = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    sector,
                    rs.getDouble("precio"),
                    rs.getString("estado")
                );
                
                lista.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo para añadir productos
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, id_sector, precio, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getSector().getIdSector()); 
            ps.setDouble(3, producto.getPrecio());
            ps.setString(4, producto.getEstado());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo para modificar datos de un producto
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, id_sector = ?, precio = ?, estado = ? WHERE id_producto = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getSector().getIdSector());
            ps.setDouble(3, producto.getPrecio());
            ps.setString(4, producto.getEstado());
            ps.setInt(5, producto.getIdProducto());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo para eliminar de manera lógica un producto de la base de datos
    public boolean eliminar(int idProducto) {
        String sql = "UPDATE productos SET estado = 'Eliminado' WHERE id_producto = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
