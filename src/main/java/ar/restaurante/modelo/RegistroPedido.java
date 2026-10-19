package ar.restaurante.modelo;

public class RegistroPedido {
    private Pedido pedido;
    private int renglon;
    private Producto producto;
    private int cantidad;
    private String notaExtra;
    private double precioUnitario;
    private double total;
    private String estado;

    // Constructor pre - carga
    public RegistroPedido(Producto producto, int cantidad, String notaExtra, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.notaExtra = notaExtra;
        this.precioUnitario = precioUnitario;
        this.total = cantidad * precioUnitario;
        this.estado = "Enviado";
    }

    // Constructor completo
    public RegistroPedido(Pedido numeroPedido, int renglon, Producto producto, int cantidad, 
                          String notaExtra, double precioUnitario, double total, String estado) {
        this.pedido = numeroPedido;
        this.renglon = renglon;
        this.producto = producto;
        this.cantidad = cantidad;
        this.notaExtra = notaExtra;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.estado = estado;
    }


    public Pedido getPedido() {
        return pedido;
    }

    public void setNumeroPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public int getRenglon() {
        return renglon;
    }

    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    public Producto getProducto() {
        return producto;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNotaExtra() {
        return notaExtra;
    }

    public void setNotaExtra(String notaExtra) {
        this.notaExtra = notaExtra;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}

