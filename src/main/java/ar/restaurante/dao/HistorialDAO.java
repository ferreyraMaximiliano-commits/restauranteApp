package ar.restaurante.dao;

import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {
    // Permite obtener el historial de pedidos con su informacion

    // Obtener historial de pedidos
    public List<RegistroPedido> obtenerHistorialPedidos(String estadoFiltro) {
        List<RegistroPedido> lista = new ArrayList<>();
        
        // Traemos todos los campos necesarios para dar vida a los objetos por completo
        String sql = "SELECT rp.numero_pedido, ped.fecha, ped.id_mesa, ped.id_mozo, " +
             "       emp.nombre AS nombre_mozo, emp.apellido AS apellido_mozo, " +
             "       pr.id_producto, pr.nombre AS nombre_producto, pr.precio, pr.estado AS estado_prod, " +
             "       pr.id_sector, s.nombre AS nombre_sector, " +
             "       rp.renglon, rp.cantidad, rp.nota_extra, rp.precio_unitario, rp.total, rp.estado AS estado_item " +
             "FROM registroPedidos rp " +
                     "INNER JOIN pedidos ped ON rp.numero_pedido = ped.numero_pedido " +
                     "INNER JOIN empleados emp ON ped.id_mozo = emp.id_usuario " +
                     "INNER JOIN productos pr ON rp.id_producto = pr.id_producto " +
                     "INNER JOIN sector s ON pr.id_sector = s.id_sector ";

        
        // Si no se desea ver todos los pedidos registrados se añade un WHERE a la consulta sql
        if (!estadoFiltro.equals("Todos")) {
            sql += "WHERE rp.estado = ? ";
        }
        
        // Orden de resultados de la consulta (reciente a antiguo)
        sql += "ORDER BY rp.numero_pedido DESC, rp.renglon ASC";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Selecciona el estado de los pedidos que se desea ver en el WHERE
            if (!estadoFiltro.equals("Todos")) {
                ps.setString(1, estadoFiltro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Instanciamos el Mozo
                    Mozo mozo = new Mozo(
                        rs.getInt("id_mozo"), null, null, null, null,
                        rs.getString("nombre_mozo"), rs.getString("apellido_mozo"), null, null, 0.0
                    );

                    // Le pasamos el objeto mozo al constructor del pedido
                    Pedido pedido = new Pedido(rs.getInt("id_mesa"), mozo); 
                    pedido.setNumeroPedido(rs.getInt("numero_pedido"));
                    pedido.setFecha(rs.getDate("fecha").toLocalDate());

                    // Instanciamos el sector y el producto correspondientes
                    Sector sector = new Sector(rs.getInt("id_sector"), rs.getString("nombre_sector"));
                    Producto producto = new Producto(
                        rs.getInt("id_producto"), rs.getString("nombre_producto"), 
                        sector, rs.getDouble("precio"), rs.getString("estado_prod")
                    );
                    
                    // Construimos el RegistroPedido final encapsulando toda la jerarquia
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
}

