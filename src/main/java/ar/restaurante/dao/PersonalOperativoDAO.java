package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Pedido;
import ar.restaurante.modelo.Producto;
import ar.restaurante.modelo.RegistroPedido;
import ar.restaurante.modelo.Sector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalOperativoDAO {
    
    // Actua como filtro que separa las lineas de un pedido para que cada sector vea lo que le corresponde
    
    public List<RegistroPedido> listarPedidosPorSector(int idSector) {
        List<RegistroPedido> lista = new ArrayList<>();
        
        String sql = "SELECT rp.numero_pedido, ped.id_mesa, p.id_producto, p.nombre AS nombre_producto, " +
                     "       p.precio, p.estado AS estado_prod, p.id_sector, " +
                     "       rp.cantidad, rp.nota_extra, rp.precio_unitario, rp.total, rp.estado AS estado_item, rp.renglon " +
                     "FROM registroPedidos rp " +
                     "INNER JOIN pedidos ped ON rp.numero_pedido = ped.numero_pedido " +
                     "INNER JOIN productos p ON rp.id_producto = p.id_producto " +
                     "WHERE p.id_sector = ? " +
                     "  AND rp.estado NOT IN ('Listo', 'Listo para Entregar', 'Entregado', 'Cancelado') " +
                     "ORDER BY ped.numero_pedido ASC, rp.renglon ASC";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSector);
            
            try (ResultSet rs = ps.executeQuery()) {
                int contador = 0;
                while (rs.next()) {
                    contador++;
                    
                    // Instanciamos los objetos en cascada de forma segura
                    Pedido pedido = new Pedido(rs.getInt("id_mesa"), null);
                    pedido.setNumeroPedido(rs.getInt("numero_pedido"));

                    String nombreSectorTexto = (idSector == 1) ? "Cocina" : "Barra";
                    Sector sector = new Sector(idSector, nombreSectorTexto);
                    
                    Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        sector,
                        rs.getDouble("precio"),
                        rs.getString("estado_prod")
                    );

                    RegistroPedido renglon = new RegistroPedido(
                        pedido,
                        rs.getInt("renglon"),
                        producto,
                        rs.getInt("cantidad"),
                        rs.getString("nota_extra"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("total"),
                        rs.getString("estado_item")
                    );
                    
                    lista.add(renglon);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarEstadoProduccion(int numeroPedido, int renglon, String nuevoEstado) {
        String sql = "UPDATE registroPedidos SET estado = ? WHERE numero_pedido = ? AND renglon = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, numeroPedido);
            ps.setInt(3, renglon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

