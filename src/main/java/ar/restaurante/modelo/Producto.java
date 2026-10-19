package ar.restaurante.modelo;


public class Producto {
    private final int idProducto;
    private String nombre;
    private Sector sector;
    private double precio;
    private String estado;

    public Producto(int idProducto, String nombre, Sector sector, double precio, String estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.sector = sector;
        this.precio = precio;
        this.estado = estado;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
}
