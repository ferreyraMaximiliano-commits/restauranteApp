package ar.restaurante.dao;

import ar.restaurante.modelo.Pedido;
import ar.restaurante.conexion.Conexion;
import ar.restaurante.modelo.Mozo;
import ar.restaurante.modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ar.restaurante.modelo.RegistroPedido;
import ar.restaurante.modelo.Sector;

public class PedidoDAO {
    // Gestiona el ciclo de vida completo de las comandas
    
    // Crear pedido
    public boolean crearPedido(Pedido nuevoPedido) {
        String sqlPedido = "INSERT INTO pedidos (fecha, estado, id_mesa, id_mozo) VALUES (?, ?, ?, ?)";
        String sqlRegistro = "INSERT INTO registroPedidos (numero_pedido, renglon, id_producto, cantidad, nota_extra, precio_unitario, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false); // Desactiva guardado automático

            int idPedido = -1;
            try (PreparedStatement psP = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                psP.setDate(1, java.sql.Date.valueOf(nuevoPedido.getFecha()));
                psP.setString(2, nuevoPedido.getEstado());
                psP.setInt(3, nuevoPedido.getIdMesa());
                psP.setInt(4, nuevoPedido.getMozo().getIdUsuario());
                psP.executeUpdate();
                
                try (ResultSet rs = psP.getGeneratedKeys()) {
                    if (rs.next()) idPedido = rs.getInt(1);
                }
            }

            if (idPedido != -1) {
                try (PreparedStatement psR = con.prepareStatement(sqlRegistro)) {
                    int contadorRenglon = 1;
                    
                    for (RegistroPedido item : nuevoPedido.getDetalles()) {
                        psR.setInt(1, idPedido);
                        psR.setInt(2, contadorRenglon++);
                        psR.setInt(3, item.getProducto().getIdProducto());
                        psR.setInt(4, item.getCantidad());
                        psR.setString(5, item.getNotaExtra());
                        psR.setDouble(6, item.getPrecioUnitario());
                        psR.setDouble(7, item.getTotal());
                        psR.setString(8, item.getEstado());
                        psR.addBatch();
                    }
                    psR.executeBatch();
                }
                con.commit(); // Confirmación de transacción
                return true;
            }
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }

    // Listar pedidos
    public List<RegistroPedido> listarSeguimientoPedidos(int idMozo) {
        List<RegistroPedido> lista = new ArrayList<>();
        String sql = "SELECT rp.numero_pedido, ped.id_mesa, p.id_producto, p.nombre AS nombre_producto, " +
                     "       p.id_sector, s.nombre AS nombre_sector, p.precio, p.estado AS estado_prod, " +
                     "       rp.cantidad, rp.nota_extra, rp.precio_unitario, rp.total, rp.estado AS estado_item, rp.renglon " +
                     "FROM pedidos ped " +
                     "INNER JOIN registroPedidos rp ON ped.numero_pedido = rp.numero_pedido " +
                     "INNER JOIN productos p ON rp.id_producto = p.id_producto " +
                     "INNER JOIN sector s ON p.id_sector = s.id_sector " +
                     "WHERE ped.id_mozo = ? AND rp.estado NOT IN ('Entregado', 'Cancelado')";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMozo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    
                    Mozo mozoTemporal = new Mozo(
                       idMozo, null, null, null, null, 
                       "", "", null, null, 0.0
                    );
                    
                    // Instanciamos un objeto Pedido
                    Pedido pedido = new Pedido(rs.getInt("id_mesa"), mozoTemporal);
                    pedido.setNumeroPedido(rs.getInt("numero_pedido")); 
                    
                    // Instanciamos el Sector del producto
                    Sector sector = new Sector(rs.getInt("id_sector"), rs.getString("nombre_sector"));
                    
                    // Instanciamos el Producto asociado al renglón
                    Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        sector,
                        rs.getDouble("precio"),
                        rs.getString("estado_prod")
                    );
                    
                    // Construimos el modelo puro RegistroPedido y lo añadimos a la lista
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

    // Actualizar estado de un renglon individual
    public boolean actualizarEstadoRenglon(int numeroPedido, int renglon, String nuevoEstado) {
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


